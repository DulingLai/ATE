package dulinglai.android.ate;

import dulinglai.android.ate.analyzers.*;
import dulinglai.android.ate.analyzers.filters.AlienFragmentFilter;
import dulinglai.android.ate.analyzers.filters.AlienHostComponentFilter;
import dulinglai.android.ate.analyzers.filters.ApplicationCallbackFilter;
import dulinglai.android.ate.analyzers.filters.UnreachableConstructorFilter;
import dulinglai.android.ate.config.AteConfiguration;
import dulinglai.android.ate.config.soot.SootSettings;
import dulinglai.android.ate.entryPointCreators.AndroidEntryPointCreator;
import dulinglai.android.ate.model.ComponentTransitionGraph;
import dulinglai.android.ate.model.App;
import dulinglai.android.ate.model.TransitionEdge;
import dulinglai.android.ate.model.components.*;
import dulinglai.android.ate.model.widgets.AbstractWidget;
import dulinglai.android.ate.model.widgets.ClickWidget;
import dulinglai.android.ate.model.widgets.EditWidget;
import dulinglai.android.ate.iccparser.Ic3Provider;
import dulinglai.android.ate.iccparser.IccInstrumenter;
import dulinglai.android.ate.iccparser.IccLink;
import dulinglai.android.ate.memory.IMemoryBoundedSolver;
import dulinglai.android.ate.memory.MemoryWatcher;
import dulinglai.android.ate.memory.TimeoutWatcher;
import dulinglai.android.ate.propagationAnalysis.intents.IccIdentifier;
import dulinglai.android.ate.resources.manifest.ProcessManifest;
import dulinglai.android.ate.resources.resources.ARSCFileParser;
import dulinglai.android.ate.resources.resources.LayoutFileParser;
import dulinglai.android.ate.resources.resources.controls.AndroidLayoutControl;
import dulinglai.android.ate.resources.resources.controls.EditTextControl;
import dulinglai.android.ate.resources.resources.controls.GenericLayoutControl;
import dulinglai.android.ate.data.values.ResourceValueProvider;
import dulinglai.android.ate.utils.androidUtils.ClassUtils;
import dulinglai.android.ate.utils.androidUtils.SystemClassHandler;

import heros.solver.Pair;
import org.pmw.tinylog.Logger;
import org.xmlpull.v1.XmlPullParserException;

import soot.*;
import soot.util.HashMultiMap;
import soot.util.MultiMap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class SetupApplication {
    // Logging tags
    private static final String RESOURCE_PARSER = "ResourceParser";
    private static final String CALLBACK_ANALYZER = "CallbackAnalyzer";
    private static final String CLASS_ANALYZER = "JimpleAnalyzer";
    private static final String ICC_PARSER = "IccParser";
    private static final String GRAPH_BUILDER = "GraphBuilder";

    // Configuration
    private AteConfiguration config;

    // Android manifest
    // TODO: the manifest may be converted to local
    private ProcessManifest manifest;

    // Entry points related
    private AndroidEntryPointCreator entryPointCreator = null;
    private MultiMap<SootClass, SootClass> fragmentClasses = new HashMultiMap<>();

    // Callbacks
    private MultiMap<SootClass, CallbackDefinition> callbackMethods = new HashMultiMap<>();

    // Layouts
    private MultiMap<SootClass, Integer> layoutClasses = new HashMultiMap<>();
    private MultiMap<SootClass, SootClass> baseactivityMapping = new HashMultiMap<>();

    // Widget Nodes
//    private List<EditWidget> editWidgetNodeList;
//    private List<ClickWidget> clickWidgetNodeList;

    // Edges
    private MultiMap<SootClass, AbstractWidget> ownershipEdgesClasses = new HashMultiMap<>();
    private MultiMap<Activity, AbstractWidget> ownershipEdges = new HashMultiMap<>();

//    // Login detection
//    private MultiMap<SootClass, String> potentialLoginMap = new HashMultiMap<>();
//    private Set<ActivityNode> potentialLoginActivity = new HashSet<>();
//    private MultiMap<ActivityNode, AbstractWidgetNode> potentialPasswordWidget = new HashMultiMap<>();
//    private MultiMap<ActivityNode, AbstractWidgetNode> potentialUsernameWidget = new HashMultiMap<>();

    // Component transition graph
    private IccInstrumenter iccInstrumenter = null;
    private Map<TransitionEdge, AbstractWidget> widgetEdgeMap;
    private ComponentTransitionGraph componentTransitionGraph;

    /**
     * Default constructor
     * @param config The configuration file
     */
    public SetupApplication(AteConfiguration config) {
        // Setup analysis config
        this.config = config;

        String platformDir = config.getAnalysisFileConfig().getAndroidPlatformDir();
        if (platformDir == null || platformDir.isEmpty())
            throw new RuntimeException("Android platform directory not specified");

        initializeSoot(config);
    }

    /**
     * Initialize the setting for Soot
     * @param config The configuration
     */
    public void initializeSoot(AteConfiguration config) {
        // Setup Soot for analysis
        SootSettings.initializeSoot(config);
    }

    /**
     * Parse the string resources ("en" only) and other resource ids of the resource packages
     */
    public void parseResources() {
        final String targetApk = config.getAnalysisFileConfig().getTargetAPKFile();

        // Parse Resources - collect resource ids
        Logger.info("[{}] Parsing ARSC files for resources mapping...", RESOURCE_PARSER);
        long beforeARSC = System.nanoTime();

        try {
            // parse the ARSC files
            ARSCFileParser resources = new ARSCFileParser();
            resources.parse(targetApk);
            List<ARSCFileParser.ResPackage> resPackages = resources.getPackages();

            // Setup a resource value provider
            ResourceValueProvider.v().initializeResources(resPackages);

            Logger.info("[{}] DONE - Resource parsing took " + (System.nanoTime() - beforeARSC) / 1E9 + " seconds.", RESOURCE_PARSER);
        }  catch (IOException e) {
            Logger.error("[{}] ERROR - Unable to parse app resources! Please make sure the APK is valid!", RESOURCE_PARSER);
        }
    }

    /**
     * Parse the apk manifest file for entry point classes and component nodes
     * @param targetApk The target apk file to parse
     */
    public void constructInitialModel(String targetApk) {
        Logger.info("[{}] Collecting components from manifest...", RESOURCE_PARSER);
        try {
            this.manifest = new ProcessManifest(targetApk);
            // build the initial model
            App.v().initializeAppModel(manifest);
        } catch (IOException|XmlPullParserException e) {
            Logger.error("[{}] ERROR - Failed to parse the manifest: {}!", RESOURCE_PARSER, e.getMessage());
        }

        // Print the initial model details
        Integer numActivities = manifest.getNumActivity();
        Integer numServices = manifest.getNumService();
        Integer numProviders = manifest.getNumProvider();
        Integer numReceivers = manifest.getNumReceiver();
        Integer totalNumComp = numActivities + numServices + numProviders + numReceivers;
        Logger.info("[{}] DONE - Collected {} components:", RESOURCE_PARSER, totalNumComp);
        Logger.info("                   {} activities; {} services; {} content providers; {} broadcast receivers",
                numActivities, numServices, numProviders, numReceivers);
    }

    /**
     * On-demand activity transition analysis
     */
    public void analyzeJimpleClasses(String apkPath) {
        // Collect XML-based widgets
        Logger.info("[{}] Collecting XML-based widgets ...", RESOURCE_PARSER);
        LayoutFileParser layoutFileParser = new LayoutFileParser(manifest.getPackageName());
        layoutFileParser.parseLayoutFileDirect(apkPath);
        Logger.info("[{}] ... End collecting XML-based widgets ...", RESOURCE_PARSER);


        // Analyze the class files
        Logger.info("[{}] Analyzing the class files ...", CLASS_ANALYZER);
        processJimpleClasses(layoutFileParser, null);
        Logger.info("[{}] ... End analyzing the class files ...", CLASS_ANALYZER);
    }

    /**
     * Collects the widgets from Jimple files
     * @param layoutFileParser The layout file parser
     * @param iccUnitsForWidgetAnalysis The units for widgets analysis
     */
    private void processJimpleClasses(LayoutFileParser layoutFileParser,
                                      List<IccIdentifier> iccUnitsForWidgetAnalysis) {
        try {
            processJimpleClassesDefault(layoutFileParser, iccUnitsForWidgetAnalysis);
        } catch (IOException ex) {
            Logger.error("[{}] Failed to analyze the jimple class: {}", CLASS_ANALYZER, ex.getMessage());
        }
    }

    /**
     * Perform the whole analysis
     */
    public void runAnalysis() {
        final String apkPath = config.getAnalysisFileConfig().getTargetAPKFile();
        // Parse resources files
        parseResources();

        /*
         Step 1. Collect component nodes
          */

        /*
        Step 2. Collect callbacks
         */
        Logger.info("[{}] Collecting callbacks...", CALLBACK_ANALYZER);

        /*
        Step 2. Load IC3 data
         */
        Logger.info("[{}] Loading the ICC Model...", ICC_PARSER);
        if (config.getIccConfig().getIccModel() == null || config.getIccConfig().getIccModel().isEmpty())
            config.getIccConfig().setIccModel("icc_models" + File.separator + manifest.getPackageName() + ".txt");
        String iccModel = config.getIccConfig().getIccModel();
        if (!Files.exists(Paths.get(iccModel))) {
            Logger.warn("Could not load the ICC model! Applying on-demand intent analysis...");
        }

        Ic3Provider provider = new Ic3Provider(config.getIccConfig().getIccModel());
        List<IccLink> iccLinks = provider.getIccLinks();
        Logger.info("[{}] ...End Loading the ICC Model", ICC_PARSER);

        /*
        Step 3. Build the initial activity transition graph (no widgets attached)
         */
        List<IccIdentifier> iccUnitsForWidgetAnalysis = buildComponentTransitionGraph(iccLinks,
                manifest);

//        ResultWriter.writeComponentTransitionGraph(componentTransitionGraph,
//                config.getAnalysisFileConfig().getOutputFile(), manifest.getPackageName());

        /*
        Step 4. Parse XML-based widgets
         */
        Logger.info("[{}] Collecting XML-based widgets ...", RESOURCE_PARSER);
        LayoutFileParser layoutFileParser = new LayoutFileParser(manifest.getPackageName());
        layoutFileParser.parseLayoutFileDirect(apkPath);
        Logger.info("[{}] ... End collecting XML-based widgets ...", RESOURCE_PARSER);

        /*
        Step 5. Analyze the class files
         */
//        Logger.info("[{}] Analyzing the class files ...", CLASS_ANALYZER);
//        processJimpleClasses(null, layoutFileParser, iccUnitsForWidgetAnalysis);
//        Logger.info("[{}] ... End analyzing the class files ...", CLASS_ANALYZER);

        /*
        Step 6. Combine the XML-based widgets with the programmatically set widgets and properties
         */
        Logger.info("[{}] Combining the data from XML and class files ...", CLASS_ANALYZER);
        combineClassAndXMLData(layoutFileParser.getUserControls());
        addWidgetToEdge();


        // write the results
        ResultWriter.writeComponentTransitionGraph(componentTransitionGraph,
                config.getAnalysisFileConfig().getOutputFile(), manifest.getPackageName());

        ResultWriter.writeActivityListToFile(manifest.getActivitySet(), manifest.getPackageName(),
                config.getAnalysisFileConfig().getOutputFile());

//        /*
//        Step 7. Detect potential login activity and login widgets
//         */
//        Logger.info("[{}] Detecting potential login widgets ...", LOGIN_DETECTOR);
//        LoginDetector loginDetector = new LoginDetector(potentialLoginMap, ownershipEdges, activityNodeList);
//        loginDetector.detectPotentialLogin();
//        potentialLoginActivity = loginDetector.getPotentialLoginActivity();
//        potentialUsernameWidget = loginDetector.getPotentialUsernameWidget();
//        potentialPasswordWidget = loginDetector.getPotentialPasswordWidget();


        /*
        Step 8. Build execution path
         */
//        MultiMap<SootClass, Set<SootClass>> loginExecutionPath = buildExecutionPath(activityTransitionGraph,potentialLoginActivity);

        // Debug logging
//        String packageName = manifest.getPackageName();
//        ResultWriter writer = new ResultWriter(packageName, config.getAnalysisFileConfig().getOutputFile());
//        for (ActivityNode activity : potentialLoginActivity) {
//            try {
//                writer.appendStringToResultFile("loginDetectionTest.txt", activity.getName());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }

    /**
     * Assigns widgets to transition edges
     */
    private void addWidgetToEdge() {
        for (TransitionEdge edge : widgetEdgeMap.keySet()) {
            AbstractComponent src = edge.getSrcComp();
            AbstractComponent tgt = edge.getTgtComp();
            componentTransitionGraph.setEdgeWidget(src, tgt, widgetEdgeMap.get(edge));
        }
    }

//    private MultiMap<SootClass, Set<SootClass>> buildExecutionPath(ActivityTransitionGraph activityTransitionGraph, Set<ActivityNode> potentialLoginActivity) {
//        MultiMap<SootClass, Set<SootClass>> loginExecutionPath = new HashMultiMap<>();
//        for (ActivityNode activityNode : potentialLoginActivity) {
//            SootClass activityClass = Scene.v().getSootClassUnsafe(activityNode.getName());
//            if (activityTransitionGraph.isActivityInHierarchy(activityClass)){
//                Set<SootClass> launchableActivities = new HashSet<>();
//                for (String launchableActString : manifest.getLaunchActivity()) {
//                    launchableActivities.add(Scene.v().getSootClassUnsafe(launchableActString));
//                }
//                loginExecutionPath.putAll(resolveExecutionPath(activityTransitionGraph, activityClass, launchableActivities));
//            } else {
//                Logger.warn("[{}] Failed to find the login activity");
//            }
//        }
//        return loginExecutionPath;
//    }

    /**
     * builds the component transition graph based on the ICC links and all component nodes
     * @param iccLinks The ICC links from IC3
     * @param manifest The manifest containing all component nodes
     * @return the list of ICC methods for widgets analysis
     */
    private List<IccIdentifier> buildComponentTransitionGraph(List<IccLink> iccLinks, ProcessManifest manifest) {
        Logger.info("[{}] Building component transition graph ...", GRAPH_BUILDER);
        Set<Activity> activitySet = manifest.getActivitySet();
        Set<Service> serviceSet = manifest.getServiceSet();
        Set<BroadcastReceiver> receiverSet = manifest.getReceiverSet();
        Set<ContentProvider> providerSet = manifest.getProviderSet();

        ClassUtils classUtils = new ClassUtils();
        List<IccIdentifier>  iccIdentifiers = new ArrayList<>();
//        MultiMap<SootClass, Pair<TransitionEdge, SootMethod>> iccPairForJimpleAnalysis = new HashMultiMap<>();

                // Setup activity hierarchy
        componentTransitionGraph = new ComponentTransitionGraph(activitySet, serviceSet, receiverSet, providerSet);

        // Resolve the activity links
        for (IccLink iccLink : iccLinks) {
            SootClass destClass = iccLink.getDestinationC();
            SootClass sourceClass = iccLink.getFromC();
            ClassUtils.ComponentType destType = classUtils.getComponentType(destClass);
            ClassUtils.ComponentType sourceType = classUtils.getComponentType(sourceClass);

            /*
            Connecting component nodes to component nodes
             */
            AbstractComponent srcComp = componentTransitionGraph.getCompNodeByName(sourceClass.getName());
            AbstractComponent tgtComp = componentTransitionGraph.getCompNodeByName(destClass.getName());
            if (srcComp!=null && tgtComp!=null) {
                componentTransitionGraph.addTransitionEdge(srcComp, tgtComp);
                Unit iccUnit = iccLink.getFromU();
                SootMethod iccMethod = iccLink.getFromSM();
                iccIdentifiers.add(new IccIdentifier(iccUnit, iccMethod, sourceClass));
//                iccPairForJimpleAnalysis.put(sourceClass, new Pair<>(componentTransitionGraph.getEdge(srcComp, tgtComp), iccMethod));
                Logger.debug("{} -> {}", sourceClass, destClass);
                Logger.debug("Unit: {} -> Method: {}", iccUnit, iccMethod);
            }
            else
                Logger.warn("[WARN] Failed to find component {} -> {}", sourceClass.getName(), destClass.getName());
        }

        return iccIdentifiers;
    }

    /**
     * Prepares the graphs for Jimple class analysis
     * @param component The component to start analysis
     */
    private void prepareJimpleAnalysis(SootClass component) {
        // Construct new call-graph
        resetCallgraph();
        createMainMethod(component);
        constructCallgraphInternal();
    }

    private void combineClassAndXMLData(MultiMap<String, AndroidLayoutControl> userControls) {
        // Add resource IDs for activity nodes
        for (SootClass layoutClass : layoutClasses.keySet()) {
            for (Activity activity : manifest.getActivitySet()) {
                if (activity.getName()==null)
                    Logger.warn("[Activity Node] Missing activity name...");
                else if (activity.getName().equals(layoutClass.getName())) {
//                    activity.setResourceId(layoutClasses.get(layoutClass));

                    Set<AbstractWidget> widgetNodeSet = ownershipEdgesClasses.get(layoutClass);
                    if (widgetNodeSet!=null && !widgetNodeSet.isEmpty()) {
                        ownershipEdges.putAll(activity, widgetNodeSet);
                    }
                }
            }
        }
        // collectXmlWidgets
        collectXmlWidgets(userControls);
    }

    /**
     * Collect the widgets from xml files
     * @param userControls The user controls found from xml files.
     *                     We are only interested in EditText widgets and Clickable widgets
     */
    private void collectXmlWidgets(MultiMap<String, AndroidLayoutControl> userControls) {
        for (String layoutKeyFull : userControls.keySet()) {
            String layoutKey = layoutKeyFull.substring(layoutKeyFull.lastIndexOf('/') + 1, layoutKeyFull.lastIndexOf('.'));
            Integer resId = ResourceValueProvider.v().getLayoutResourceId(layoutKey);
            if (resId != null) {
                for (Activity activity : manifest.getActivitySet()) {
//                    for (Integer activityResId : activity.getResourceId()) {
//                        if (activityResId.equals(resId)) {
//                            Set<AbstractWidget> oldWidgetNodeSet = ownershipEdges.get(activity);
//                            Set<AbstractWidget> newWidgetNodeSet = createWidgetNodeForUserControls(userControls.get(layoutKeyFull), oldWidgetNodeSet);
//                            if (oldWidgetNodeSet != null && !oldWidgetNodeSet.isEmpty()) {
//                                for (AbstractWidget widget : newWidgetNodeSet) {
//                                    oldWidgetNodeSet.removeIf(oldWidget -> oldWidget.getResourceId() == widget.getResourceId());
//                                }
//                                newWidgetNodeSet.addAll(oldWidgetNodeSet);
//                            }
//                            ownershipEdges.putAll(activity, newWidgetNodeSet);
//                        }
//                    }
                }
            } else
                Logger.warn("[GraphBuilder] Cannot find the resource id for {}", layoutKeyFull);
        }
    }

    private Set<AbstractWidget> createWidgetNodeForUserControls(Set<AndroidLayoutControl> androidLayoutControls,
                                                                Set<AbstractWidget> javaWidgets) {
        Set<AbstractWidget> newWidgetSet = new HashSet<>();
        for (AndroidLayoutControl usercontrol : androidLayoutControls) {
            if (usercontrol instanceof EditTextControl) {
                int resId = usercontrol.getID();
                AbstractWidget newEditText = null;
                if (resId!=-1) {
                    for (AbstractWidget javaWidget : javaWidgets) {
                        if (javaWidget.getResourceId() == resId) {
                            newEditText = createEditTextForUserControl((EditTextControl)usercontrol, (EditWidget) javaWidget);
                        }
                    }
                }

                if (newEditText==null)
                    newEditText = createEditTextForUserControl((EditTextControl)usercontrol);

                newWidgetSet.add(newEditText);

            } else {
                int resId = usercontrol.getID();
                AbstractWidget newClickWidget = null;

                if (resId!=-1) {
                    for (AbstractWidget javaWidget : javaWidgets) {
                        if (javaWidget.getResourceId() == resId) {
                            newClickWidget = createClickWidgetForUserControl((GenericLayoutControl) usercontrol, javaWidget);
                        }
                    }
                }

                // if we do not have a click listener, the new click widget would be null
                // then we check if we can solely use the XML widget to recreate this widget node
                if (newClickWidget == null)
                    newClickWidget = createClickWidgetForUserControl((GenericLayoutControl) usercontrol);

                if (newClickWidget != null) {
                    if (newClickWidget instanceof ClickWidget)
                        newWidgetSet.add(newClickWidget);
                    else if (newClickWidget instanceof EditWidget)
                        newWidgetSet.add(newClickWidget);
                }
            }
        }
        return newWidgetSet;
    }

    /**
     * Creates edit text widget out of given user control
     * @param usercontrol The user control
     * @return The edit text widget created
     */
    private EditWidget createEditTextForUserControl(EditTextControl usercontrol) {
        int resId = usercontrol.getID();
        int inputType = usercontrol.getInputType();

        String text = checkStringResource(usercontrol.getText());
        String contentDescription = checkStringResource(usercontrol.getContentDescription());
        String hint = checkStringResource(usercontrol.getHint());

        if (resId!=-1) {
            String resString = ResourceValueProvider.v().getResourceIdString(resId);
            return new EditWidget(resId,resString,text,contentDescription,hint,inputType);
        } else {
            return new EditWidget(resId, text, contentDescription, hint, inputType);
        }
    }

    /**
     * Creates edit text widget out of given user control, and Java-implemented EditText
     * @param usercontrol The user control
     * @return The edit text widget created
     */
    private EditWidget createEditTextForUserControl(EditTextControl usercontrol, EditWidget javaEditText) {
        int resId = usercontrol.getID();
        String text = null;
        String contentDescription = null;
        String hint = null;
        int inputType=-1;
        if (javaEditText.getInputType()!=-1)
            inputType = javaEditText.getInputType();
        else if (usercontrol.getInputType()!=-1)
            inputType = usercontrol.getInputType();

        if (javaEditText.getText() != null)
            text = checkStringResource(javaEditText.getText());
        else if (usercontrol.getText() != null)
            text = checkStringResource(usercontrol.getText());

        if (javaEditText.getContentDescription() != null)
            contentDescription = checkStringResource(javaEditText.getContentDescription());
        else if (usercontrol.getContentDescription() != null)
            contentDescription = checkStringResource(usercontrol.getContentDescription());

        if (javaEditText.getHint() != null)
            hint = checkStringResource(javaEditText.getHint());
        else if (usercontrol.getHint() != null)
            hint = checkStringResource(usercontrol.getHint());

        if (resId!=-1) {
            String resString = ResourceValueProvider.v().getResourceIdString(resId);
            return new EditWidget(resId,resString,text,contentDescription,hint,inputType);
        } else {
            return new EditWidget(resId, text, contentDescription, hint, inputType);
        }
    }

    /**
     * Creates click widget out of given user control
     * @param usercontrol The user control
     * @return The click widget created
     */
    private ClickWidget createClickWidgetForUserControl(GenericLayoutControl usercontrol) {
        int resId = usercontrol.getID();
        String text = checkStringResource(usercontrol.getText());
        ClickWidget.EventType eventType;
        String clickListener = usercontrol.getClickListener();

        if (clickListener!=null) {
            eventType = ClickWidget.EventType.Click;
            if (resId!=-1) {
                String resString = ResourceValueProvider.v().getLayoutResourceString(resId);
                return new ClickWidget(resId, resString, text, eventType, clickListener);
            } else {
                return new ClickWidget(resId, text, eventType, clickListener);
            }
        }
        return null;
    }

    /**
     * Creates click widget out of given user control
     * @param usercontrol The user control
     * @return The click widget created
     */
    private AbstractWidget createClickWidgetForUserControl(GenericLayoutControl usercontrol,
                                                           AbstractWidget javaWidget) {
        int resId = usercontrol.getID();
        String text = null;
        ClickWidget.EventType eventType;
        String clickListener;

        // We have a discrepancy between Java and XML widgets, we will use Java widgets' type - EditText instead
        if (javaWidget instanceof EditWidget) {
            EditWidget javaEditText = (EditWidget) javaWidget;
            String contentDescription = null;
            String hint = null;
            int inputType=-1;
            if (javaEditText.getInputType()!=-1)
                inputType = javaEditText.getInputType();

            if (javaEditText.getText() != null)
                text = checkStringResource(javaEditText.getText());
            else if (usercontrol.getText() != null)
                text = checkStringResource(usercontrol.getText());

            if (javaEditText.getContentDescription() != null)
                contentDescription = checkStringResource(javaEditText.getContentDescription());

            if (javaEditText.getHint() != null)
                hint = checkStringResource(javaEditText.getHint());

            if (resId!=-1) {
                String resString = ResourceValueProvider.v().getResourceIdString(resId);
                return new EditWidget(resId,resString,text,contentDescription,hint,inputType);
            } else {
                return new EditWidget(resId, text, contentDescription, hint, inputType);
            }
        }

        if (javaWidget.getText()!=null)
            text = checkStringResource(javaWidget.getText());
        else
            text = checkStringResource(usercontrol.getText());

        if (((ClickWidget)javaWidget).getInputType()!= ClickWidget.EventType.None) {
            eventType = ((ClickWidget) javaWidget).getInputType();
            clickListener = ((ClickWidget) javaWidget).getClickListener();
        }
        else if (usercontrol.getClickListener()!=null) {
            eventType = ClickWidget.EventType.Click;
            clickListener = usercontrol.getClickListener();
        } else
            return null;

        if (resId!=-1) {
            String resString = ResourceValueProvider.v().getLayoutResourceString(resId);
            return new ClickWidget(resId, resString, text, eventType, clickListener);
        } else {
            return new ClickWidget(resId, text, eventType, clickListener);
        }
    }

    /**
     * Checks the string resources if the given string is a resource id
     * @param text The string to check
     * @return The string found in string resource if the given string is a resource id,
     * else returns the original string
     */
    private String checkStringResource(String text) {
        if (text != null) {
            if (text.matches("-?\\d+")) {
                int resId = Integer.parseInt(text);
                String newText = ResourceValueProvider.v().getStringById(resId);
                if (newText != null)
                    return newText;
                else
                    return text;
            } else
                return text;
        } else
            return null;
    }

    /**
     * Collect the widget nodes in a fast mode.
     * This method prefers performance over precision and scans the code including unreachable methods.
     *
     * @param layoutFileParser
     *            The layout file parser to be used for analyzing UI controls
     * @throws IOException
     *             Thrown if a required configuration cannot be read
     */
//    private void processJimpleClassesFast(LayoutFileParser layoutFileParser,
//                                          Set<SootClass> entryPointClasses,
//                                          List<IccIdentifier> iccUnitsForWidgetAnalysis) throws IOException {
//        // Construct initial callgraph
//        resetCallgraph();
//        createMainMethod(null);
//        constructCallgraphInternal();
//
//        // Collect the callback interfaces implemented in the app's source code
//        AbstractJimpleAnalyzer jimpleAnalyzer = new FastJimpleAnalyzer(entryPointClasses,
//                manifest.getAllActivityClasses(), layoutFileParser, resourceValueProvider, iccUnitsForWidgetAnalysis);
//        jimpleAnalyzer.analyzeJimpleClasses();
//
//        // Get the layout class maps
//        this.layoutClasses = jimpleAnalyzer.getLayoutClasses();
//        this.baseactivityMapping = jimpleAnalyzer.getBaseActivityMapping();
//
//        // Collect the results
//        this.callbackMethods.putAll(jimpleAnalyzer.getCallbackMethods());
//        this.entrypoints.addAll(jimpleAnalyzer.getDynamicManifestComponents());
//
//        // Collect XML-based callback methods
//        collectXmlBasedCallbackMethods(layoutFileParser, jimpleAnalyzer);
//
//        // Reconstruct the final callgraph and ICFG
//        resetCallgraph();
//        createMainMethod(null);
//        constructCallgraphInternal();
//        jimpleAnalyzer.constructIcfg();
//
//        // Collect Java widget properties
//        jimpleAnalyzer.findWidgetsMappings();
////        IInPlaceInfoflow infoflow = createInfoflow();
//        widgetEdgeMap = jimpleAnalyzer.resolveIccMethodToWidget();
//
//        // Get the nodes set collected from Jimple files
//        this.editWidgetNodeList = jimpleAnalyzer.getEditTextWidgetList();
//        this.clickWidgetNodeList = jimpleAnalyzer.getClickWidgetNodeList();
//        this.ownershipEdgesClasses = jimpleAnalyzer.getOwnershipEdges();
//        this.potentialLoginMap = jimpleAnalyzer.getPotentialLoginMap();
//    }

//    /**
//     * Instantiates and configures the data flow engine
//     *
//     * @return A properly configured instance of the {@link soot.jimple.infoflow.Infoflow} class
//     */
//    private IInPlaceInfoflow createInfoflow() {
//        // Some sanity checks
//        if (entryPointCreator == null)
//            throw new RuntimeException("No entry point available");
//        if (entryPointCreator.getComponentToEntryPointInfo() == null)
//            throw new RuntimeException("No information about component entry points available");
//
//        // Get the component lifecycle methods
//        Collection<SootMethod> lifecycleMethods = Collections.emptySet();
//        if (entryPointCreator != null) {
//            ComponentEntryPointCollection entryPoints = entryPointCreator.getComponentToEntryPointInfo();
//            if (entryPoints != null)
//                lifecycleMethods = entryPoints.getLifecycleMethods();
//        }
//
//        // Initialize and configure the data flow tracker
//        IInPlaceInfoflow info = createInfoflowInternal(lifecycleMethods);
//        if (ipcManager != null)
//            info.setIPCManager(ipcManager);
//        info.setConfig(config);
//        info.setTaintWrapper(taintWrapper);
//        info.setTaintPropagationHandler(taintPropagationHandler);
//        info.setBackwardsPropagationHandler(backwardsPropagationHandler);
//
//        // We use a specialized memory manager that knows about Android
//        info.setMemoryManagerFactory(new IMemoryManagerFactory() {
//
//            @Override
//            public IMemoryManager<Abstraction, Unit> getMemoryManager(boolean tracingEnabled,
//                                                                      FlowDroidMemoryManager.PathDataErasureMode erasePathData) {
//                return new AndroidMemoryManager(tracingEnabled, erasePathData, entrypoints);
//            }
//
//        });
//        info.setMemoryManagerFactory(null);
//
//        // Inject additional post-processors
//        info.setPostProcessors(Collections.singleton(new PostAnalysisHandler() {
//
//            @Override
//            public InfoflowResults onResultsAvailable(InfoflowResults results, IInfoflowCFG cfg) {
//                // Purify the ICC results if requested
//                final AteConfig.IccConfiguration iccConfig = config.getIccConfig();
//                if (iccConfig.isIccResultsPurifyEnabled())
//                    results = IccResults.clean(cfg, results);
//
//                return results;
//            }
//
//        }));
//
//        return info;
//    }

//    /**
//     * Creates the data flow engine on which to run the analysis. Derived classes
//     * can override this method to use other data flow engines.
//     *
//     * @param lifecycleMethods The set of Android lifecycle methods to consider
//     * @return The data flow engine
//     */
//    protected IInPlaceInfoflow createInfoflowInternal(Collection<SootMethod> lifecycleMethods) {
//        final String androidJar = config.getAnalysisFileConfig().getAndroidPlatformDir();
//        return new InPlaceInfoflow(androidJar, forceAndroidJar, cfgFactory, lifecycleMethods);
//    }


    /**
     * Releases the callgraph and all intermediate objects associated with it
     */
    private void resetCallgraph() {
        Scene.v().releaseCallGraph();
        Scene.v().releasePointsToAnalysis();
        Scene.v().releaseReachableMethods();
        G.v().resetSpark();
    }


    private void createMainMethod(SootClass component){
        this.entryPointCreator = createEntryPointCreator(component);
        SootMethod dummyMainMethod = entryPointCreator.createDummyMain();
        Scene.v().setEntryPoints(Collections.singletonList(dummyMainMethod));
        if (!dummyMainMethod.getDeclaringClass().isInScene())
            Scene.v().addClass(dummyMainMethod.getDeclaringClass());

        // addClass() declares the given class as a library class. We need to fix it.
        dummyMainMethod.getDeclaringClass().setApplicationClass();
    }

    /**
     * Creates the {@link AndroidEntryPointCreator} instance which will later create
     * the dummy main method for the analysis
     *
     * @return The {@link AndroidEntryPointCreator} responsible for generating the
     *         dummy main method
     */
    private AndroidEntryPointCreator createEntryPointCreator(SootClass component) {
        Set<SootClass> components = App.v().getEntrypoints();

        // If we we already have an entry point creator, clean up the leftovers from previous runs
        if (entryPointCreator == null)
            entryPointCreator = new AndroidEntryPointCreator(manifest, components);
        else {
            entryPointCreator.removeGeneratedMethods(false);
            entryPointCreator.reset();
        }

        MultiMap<SootClass, SootMethod> callbackMethodSigs = new HashMultiMap<>();
        if (component == null) {
            // Get all callbacks for all components
            for (SootClass sc : this.callbackMethods.keySet()) {
                Set<CallbackDefinition> callbackDefs = this.callbackMethods.get(sc);
                if (callbackDefs != null)
                    for (CallbackDefinition cd : callbackDefs)
                        callbackMethodSigs.put(sc, cd.getTargetMethod());
            }
        } else {
            // Get the callbacks for the current component only
            for (SootClass sc : components) {
                Set<CallbackDefinition> callbackDefs = this.callbackMethods.get(sc);
                if (callbackDefs != null)
                    for (CallbackDefinition cd : callbackDefs)
                        callbackMethodSigs.put(sc, cd.getTargetMethod());
            }
        }

        entryPointCreator.setCallbackFunctions(callbackMethodSigs);
        entryPointCreator.setFragments(fragmentClasses);
        entryPointCreator.setComponents(components);
        return entryPointCreator;
    }

    /**
     * Triggers the callgraph construction in Soot
     */
    private void constructCallgraphInternal() {
        // Construct the actual callgraph
        Logger.info("Constructing the callgraph...");

//        if (iccInstrumenter==null)
//            iccInstrumenter = createIccInstrumenter();
//        iccInstrumenter.onBeforeCallgraphConstruction();

        PackManager.v().getPack("cg").apply();

        // Make sure that we have a hierarchy
        Scene.v().getOrMakeFastHierarchy();
    }

    /**
     * Collects the XML-based callback methods, e.g., Button.onClick() declared in
     * layout XML files
     *
     * @param lfp
     *            The layout file parser
     * @param jimpleClass
     *            The analysis class that gives us a mapping between layout IDs and
     *            components
     * @return True if at least one new callback method has been added, otherwise
     *         false
     */
    private boolean collectXmlBasedCallbackMethods(LayoutFileParser lfp, AbstractJimpleAnalyzer jimpleClass) {
        SootMethod smViewOnClick = Scene.v()
                .grabMethod("<android.view.View$OnClickListener: void onClick(android.view.View)>");

        // Collect the XML-based callback methods
        boolean hasNewCallback = false;
        for (final SootClass callbackClass : jimpleClass.getLayoutClasses().keySet()) {
            if (jimpleClass.isExcludedEntryPoint(callbackClass))
                continue;

            Set<Integer> classIds = jimpleClass.getLayoutClasses().get(callbackClass);
            for (Integer classId : classIds) {
                final String layoutFileName = ResourceValueProvider.v().getLayoutResourceString(classId);
                if (layoutFileName!=null) {
                    // Add the callback methods for the given class
                    Set<String> callbackMethods = lfp.getCallbackMethods().get(layoutFileName);
                    if (callbackMethods != null) {
                        for (String methodName : callbackMethods) {
                            final String subSig = "void " + methodName + "(android.view.View)";

                            // The callback may be declared directly in the
                            // class or in one of the superclasses
                            SootClass currentClass = callbackClass;
                            while (true) {
                                SootMethod callbackMethod = currentClass.getMethodUnsafe(subSig);
                                if (callbackMethod != null) {
                                    if (this.callbackMethods.put(callbackClass,
                                            new CallbackDefinition(callbackMethod, smViewOnClick, CallbackDefinition.CallbackType.Widget)))
                                        hasNewCallback = true;
                                    break;
                                }
                                SootClass sclass = currentClass.getSuperclassUnsafe();
                                if (sclass == null) {
                                    Logger.error(String.format("Callback method %s not found in class %s", methodName,
                                            callbackClass.getName()));
                                    break;
                                }
                                currentClass = sclass;
                            }
                        }
                    }

                    // Add the fragments for this class
                    Set<SootClass> fragments = lfp.getFragments().get(layoutFileName);
                    if (fragments != null)
                        for (SootClass fragment : fragments)
                            if (fragmentClasses.put(callbackClass, fragment))
                                hasNewCallback = true;

                    // For user-defined views, we need to emulate their
                    // callbacks
                    Set<AndroidLayoutControl> controls = lfp.getUserControls().get(layoutFileName);
                    if (controls != null) {
                        for (AndroidLayoutControl lc : controls)
                            if (!SystemClassHandler.isClassInSystemPackage(lc.getViewClass().getName()))
                                registerCallbackMethodsForView(callbackClass, lc);
                    }
                } else
                    Logger.error("Unexpected resource type for layout class");
            }
        }

        // Collect the fragments, merge the fragments created in the code with
        // those declared in Xml files
        if (fragmentClasses.putAll(jimpleClass.getFragmentClasses())) // Fragments
            // declared
            // in
            // code
            hasNewCallback = true;

        return hasNewCallback;
    }

    /**
     * Registers the callback methods in the given layout control so that they are
     * included in the dummy main method
     *
     * @param callbackClass
     *            The class with which to associate the layout callbacks
     * @param lc
     *            The layout control whose callbacks are to be associated with the
     *            given class
     */
    private void registerCallbackMethodsForView(SootClass callbackClass, AndroidLayoutControl lc) {
        // Ignore system classes
        if (SystemClassHandler.isClassInSystemPackage(callbackClass.getName()))
            return;

        // Get common Android classes
        SootClass scView = Scene.v().getSootClass("android.view.View");

        // Check whether the current class is actually a view
        if (!Scene.v().getOrMakeFastHierarchy().canStoreType(lc.getViewClass().getType(), scView.getType()))
            return;

        // There are also some classes that implement interesting callback
        // methods.
        // We model this as follows: Whenever the user overwrites a method in an
        // Android OS class, we treat it as a potential callback.
        SootClass sc = lc.getViewClass();
        Map<String, SootMethod> systemMethods = new HashMap<>(10000);
        for (SootClass parentClass : Scene.v().getActiveHierarchy().getSuperclassesOf(sc)) {
            if (parentClass.getName().startsWith("android."))
                for (SootMethod sm : parentClass.getMethods())
                    if (!sm.isConstructor())
                        systemMethods.put(sm.getSubSignature(), sm);
        }

        // Scan for methods that overwrite parent class methods
        for (SootMethod sm : sc.getMethods()) {
            if (!sm.isConstructor()) {
                SootMethod parentMethod = systemMethods.get(sm.getSubSignature());
                if (parentMethod != null)
                    // This is a real callback method
                    this.callbackMethods.put(callbackClass,
                            new CallbackDefinition(sm, parentMethod, CallbackDefinition.CallbackType.Widget));
            }
        }
    }

//    /**
//     * Gets the components to analyze. If the given component is not null, we assume
//     * that only this component and the application class (if any) shall be
//     * analyzed. Otherwise, all components are to be analyzed.
//     *
//     * @param component
//     *            A component class name to only analyze this class and the
//     *            application class (if any), or null to analyze all classes.
//     * @return The set of classes to analyze
//     */
//    private Set<SootClass> getComponentsToAnalyze(SootClass component) {
//        if (component == null)
//            return this.entrypoints;
//        else {
//            // We always analyze the application class together with each
//            // component as there might be interactions between the two
//            Set<SootClass> components = new HashSet<>(2);
//            components.add(component);
//
//            String applicationName = this.manifest.getApplicationName();
//            if (applicationName != null && !applicationName.isEmpty())
//                components.add(Scene.v().getSootClassUnsafe(applicationName));
//            return components;
//        }
//    }

    /**
     * Inverts the given {@link MultiMap}. The keys become values and vice versa
     *
     * @param original
     *            The map to invert
     * @return An inverted copy of the given map
     */
    private <K, V> MultiMap<K, V> invertMap(MultiMap<V, K> original) {
        MultiMap<K, V> newTag = new HashMultiMap<>();
        for (V key : original.keySet())
            for (K value : original.get(key))
                newTag.put(value, key);
        return newTag;
    }

    /**
     * Creates the ICC instrumentation class
     *
     * @return An instance of the class for instrumenting the app's code for
     *         inter-component communication
     */
    protected IccInstrumenter createIccInstrumenter() {
        IccInstrumenter iccInstrumenter;
        iccInstrumenter = new IccInstrumenter(config.getIccConfig().getIccModel(),
                entryPointCreator.getGeneratedMainMethod().getDeclaringClass(),
                entryPointCreator.getComponentToEntryPointInfo());
        return iccInstrumenter;
    }


    /**
     * Process the jimple files to build a model of the app.
     * Calculates the set of callback methods declared in the XML resource files or
     * the app's source code
     *
     * @param layoutFileParser
     *            The layout file parser to be used for analyzing UI controls
     * @throws IOException
     *             Thrown if a required configuration cannot be read
     */
    private void processJimpleClassesDefault(LayoutFileParser layoutFileParser,
                                             List<IccIdentifier> iccUnitsForWidgetAnalysis) throws IOException {
//        // cleanup the callgraph
//        resetCallgraph();
//
//        // Make sure that we don't have any leftovers from previous runs
//        PackManager.v().getPack("wjtp").remove("wjtp.lfp");
//        PackManager.v().getPack("wjtp").remove("wjtp.ajc");
//
//        // Gets the classes as entrypoints
//        Set<SootClass> entryPointClasses = getComponentsToAnalyze(null);
//
//        // Collect the callback interfaces implemented in the app's
//        // source code. Note that the filters should know all components to
//        // filter out callbacks even if the respective component is only
//        // analyzed later.
//        AbstractJimpleAnalyzer callbackAnalyzer = new DefaultJimpleAnalyzer(entryPointClasses,
//                manifest.getAllActivityClasses(), layoutFileParser,
//                iccUnitsForWidgetAnalysis);
//
//        callbackAnalyzer.addCallbackFilter(new AlienHostComponentFilter(entrypoints));
//        callbackAnalyzer.addCallbackFilter(new ApplicationCallbackFilter(entrypoints));
//        callbackAnalyzer.addCallbackFilter(new UnreachableConstructorFilter());
//        callbackAnalyzer.analyzeJimpleClasses();
//
//        // Find the XML-based callbacks files.
//        layoutFileParser.parseLayoutFile(config.getAnalysisFileConfig().getTargetAPKFile());
//
//        // Watch the callback collection algorithm's memory consumption
//        MemoryWatcher memoryWatcher = null;
//        TimeoutWatcher timeoutWatcher = null;
//        if (callbackAnalyzer instanceof IMemoryBoundedSolver) {
//            memoryWatcher = new MemoryWatcher();
//            memoryWatcher.addSolver((IMemoryBoundedSolver) callbackAnalyzer);
//
//            // Make sure that we don't spend too much time in the callback
//            // analysis
//            if (config.getCallbackConfig().getCallbackAnalysisTimeout() > 0) {
//                timeoutWatcher = new TimeoutWatcher(config.getCallbackConfig().getCallbackAnalysisTimeout());
//                timeoutWatcher.addSolver((IMemoryBoundedSolver) callbackAnalyzer);
//                timeoutWatcher.start();
//            }
//        }
//
//        try {
//            int depthIdx = 0;
//            boolean hasChanged = true;
//            boolean isInitial = true;
//            while (hasChanged) {
//                hasChanged = false;
//
//                // Check whether the solver has been aborted in the meantime
//                if (callbackAnalyzer instanceof IMemoryBoundedSolver) {
//                    if (((IMemoryBoundedSolver) callbackAnalyzer).isKilled())
//                        break;
//                }
//
//                createMainMethod(null);
//
//                // Since the gerenation of the main method can take some time,
//                // we check again whether we need to stop.
//                if (callbackAnalyzer instanceof IMemoryBoundedSolver) {
//                    if (((IMemoryBoundedSolver) callbackAnalyzer).isKilled())
//                        break;
//                }
//
//                if (!isInitial) {
//                    // Reset the callgraph
//                    resetCallgraph();
//                    // We only want to parse the layout files once
//                    PackManager.v().getPack("wjtp").remove("wjtp.lfp");
//                }
//                isInitial = false;
//
//                // Run the soot-based operations
//                constructCallgraphInternal();
//                if (!Scene.v().hasCallGraph())
//                    throw new RuntimeException("No callgraph in Scene even after creating one. That's very sad "
//                            + "and should never happen.");
//                PackManager.v().getPack("wjtp").apply();
//
//                // Creating all callgraph takes time and memory. Check whether
//                // the solver has been aborted in the meantime
//                if (callbackAnalyzer instanceof IMemoryBoundedSolver) {
//                    if (((IMemoryBoundedSolver) callbackAnalyzer).isKilled()) {
//                        Logger.warn("Aborted callback collection because of low memory");
//                        break;
//                    }
//                }
//
//                // Collect the results of the soot-based phases
//                if (this.callbackMethods.putAll(callbackAnalyzer.getCallbackMethods()))
//                    hasChanged = true;
//
//                if (this.entrypoints.addAll(callbackAnalyzer.getDynamicManifestComponents()))
//                    hasChanged = true;
//
//                if (this.fragmentClasses.putAll(callbackAnalyzer.getFragmentClasses()))
//                    hasChanged = true;
//
//                // Collect the XML-based callback methods
//                if (collectXmlBasedCallbackMethods(layoutFileParser, callbackAnalyzer))
//                    hasChanged = true;
//
//                // Avoid callback overruns. If we are beyond the callback limit
//                // for one entry point, we may not collect any further callbacks
//                // for that entry point.
//                if (config.getCallbackConfig().getMaxCallbacksPerComponent() > 0) {
//                    for (Iterator<SootClass> componentIt = this.callbackMethods.keySet().iterator(); componentIt
//                            .hasNext();) {
//                        SootClass callbackComponent = componentIt.next();
//                        if (this.callbackMethods.get(callbackComponent).size() > config.getCallbackConfig().getMaxCallbacksPerComponent()) {
//                            componentIt.remove();
//                            callbackAnalyzer.excludeEntryPoint(callbackComponent);
//                        }
//                    }
//                }
//
//                // Check depth limiting
//                depthIdx++;
//                if (config.getCallbackConfig().getMaxCallbacksPerComponent() > 0 &&
//                        depthIdx >= config.getCallbackConfig().getMaxCallbacksPerComponent())
//                    break;
//            }
//        } catch (Exception ex) {
//            Logger.error("Could not calculate callback methods", ex);
//            throw ex;
//        } finally {
//            // Shut down the watchers
//            if (timeoutWatcher != null)
//                timeoutWatcher.stop();
//            if (memoryWatcher != null)
//                memoryWatcher.close();
//        }
//
//        // Filter out callbacks that belong to fragments that are not used by
//        // the host activity
//        AlienFragmentFilter fragmentFilter = new AlienFragmentFilter(invertMap(fragmentClasses));
//        fragmentFilter.reset();
//        for (Iterator<Pair<SootClass, CallbackDefinition>> cbIt = this.callbackMethods.iterator(); cbIt.hasNext();) {
//            Pair<SootClass, CallbackDefinition> pair = cbIt.next();
//
//            // Check whether the filter accepts the given mapping
//            if (!fragmentFilter.accepts(pair.getO1(), pair.getO2().getTargetMethod()))
//                cbIt.remove();
//            else if (!fragmentFilter.accepts(pair.getO1(), pair.getO2().getTargetMethod().getDeclaringClass())) {
//                cbIt.remove();
//            }
//        }
//
//        // Avoid callback overruns
//        if (config.getCallbackConfig().getMaxCallbacksPerComponent() > 0) {
//            for (Iterator<SootClass> componentIt = this.callbackMethods.keySet().iterator(); componentIt.hasNext();) {
//                SootClass callbackComponent = componentIt.next();
//                if (this.callbackMethods.get(callbackComponent).size() > config.getCallbackConfig().getMaxCallbacksPerComponent())
//                    componentIt.remove();
//            }
//        }
//
//        // Make sure that we don't retain any weird Soot phases
//        PackManager.v().getPack("wjtp").remove("wjtp.lfp");
//        PackManager.v().getPack("wjtp").remove("wjtp.ajc");
//
//        // get the layout class maps
//        this.layoutClasses = callbackAnalyzer.getLayoutClasses();
//
//        // Warn the user if we had to abort the callback analysis early
//        boolean abortedEarly = false;
//        if (callbackAnalyzer instanceof IMemoryBoundedSolver) {
//            if (((IMemoryBoundedSolver) callbackAnalyzer).isKilled()) {
//                Logger.warn("Callback analysis aborted early due to time or memory exhaustion");
//                abortedEarly = true;
//            }
//        }
//        if (!abortedEarly)
//            Logger.info("Callback analysis completed...");
    }

    /**
     * Gets the manifest file
     * @return The manifest file
     */
    public ProcessManifest getManifest() {
        return manifest;
    }

//    @Override
//    public void setTaintWrapper(ITaintPropagationWrapper taintWrapper) {
//        this.taintWrapper = taintWrapper;
//    }
//
//    @Override
//    public ITaintPropagationWrapper getTaintWrapper() {
//        return null;
//    }
}
