package dulinglai.android.ate.propagationAnalysis.intents;

import dulinglai.android.ate.iccparser.Ic3Data;
import dulinglai.android.ate.propagationAnalysis.*;
import dulinglai.android.ate.propagationAnalysis.arguments.ArgumentValueManager;
import dulinglai.android.ate.propagationAnalysis.arguments.MethodReturnValueManager;
import dulinglai.android.ate.propagationAnalysis.fields.transformers.FieldTransformerManager;
import dulinglai.android.ate.propagationAnalysis.manifest.ManifestPullParser;
import soot.PackManager;
import soot.SootMethod;
import soot.Transform;
import soot.Value;
import soot.jimple.StaticFieldRef;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Ic3Analysis extends Analysis {

    public Ic3Analysis(Ic3Config ic3Config) {
        super(ic3Config);
    }

    @Override
    protected void registerFieldTransformerFactories(Ic3Config ic3Config) {
        Timers.v().totalTimer.start();
        FieldTransformerManager.v().registerDefaultFieldTransformerFactories();
    }

    @Override
    protected void registerArgumentValueAnalyses(Ic3Config ic3Config) {
        ArgumentValueManager.v().registerDefaultArgumentValueAnalyses();
        ArgumentValueManager.v().registerArgumentValueAnalysis("classType",
                new ClassTypeValueAnalysis());
        ArgumentValueManager.v().registerArgumentValueAnalysis("path", new PathValueAnalysis());
        ArgumentValueManager.v().registerArgumentValueAnalysis("Set<path>", new PathValueAnalysis());
    }

    @Override
    protected void registerMethodReturnValueAnalyses(Ic3Config ic3Config) {
        MethodReturnValueManager.v().registerDefaultMethodReturnValueAnalyses();
    }

    @Override
    protected void initializeAnalysis(Ic3Config ic3Config) {
        // initialize the manifest
        ManifestPullParser detailedManifest = new ManifestPullParser();
        detailedManifest.loadManifestFile(ic3Config.getInput());

        // initialize IC3 result builder
        Ic3Data.Application.Builder ic3Builder = Ic3Data.Application.newBuilder();
        Map<String, Ic3Data.Application.Component.Builder> componentNameToBuilderMap =
                detailedManifest.populateProtobuf(ic3Builder);

        // start the actual analysis
        Timers.v().misc.start();

        ArgumentValueManager.v().registerArgumentValueAnalysis("context",
                new ContextValueAnalysis(ic3Config.getPackageName()));

        AndroidMethodReturnValueAnalysis.registerAndroidMethodReturnValueAnalyses(ic3Config.getPackageName());


        // TODO combine soot phases together (if possible)
        soot.G.reset();
        Map<SootMethod, Set<String>> entryPointMap = new HashMap<>();
        addSceneTransformer(ic3Config, entryPointMap);
        addEntryPointMappingSceneTransformer(ic3Config.getEntryPointClasses(), ic3Config.getCallbackMethods(), entryPointMap);
    }

    private void addSceneTransformer(Ic3Config ic3Config, Map<SootMethod, Set<String>> entryPointMap) {
        Ic3ResultBuilder resultBuilder = new Ic3ResultBuilder();
        resultBuilder.setEntryPointMap(new HashMap<>());

        // create the debug folder and file
        String debugDirPath = ic3Config.getOutput() + File.separator + "debug";
        File debugDir = new File(debugDirPath);
        if (!debugDir.exists())
            debugDir.mkdir();
        DateFormat dateFormat = new SimpleDateFormat("yy-MM-dd_HH-mm-ss");
        String fileName = dateFormat.format(new Date()) + ".txt";
        String debugFile = debugDirPath + File.separator + fileName;

        // add the transformation pack
        Transform transform = new Transform("wjtp.ifds", new PropagationSceneTransformer(resultBuilder,
                new PropagationSceneTransformerFilePrinter(debugFile, new SymbolFilter() {
                    @Override
                    public boolean filterOut(Value symbol) {
                        return symbol instanceof StaticFieldRef && ((StaticFieldRef) symbol).getField()
                                .getDeclaringClass().getName().startsWith("android.provider");
                    }
                })));
        if (PackManager.v().getPack("wjtp").get("wjtp.ifds") == null)
            PackManager.v().getPack("wjtp").add(transform);
        else {
            Iterator<?> it = PackManager.v().getPack("wjtp").iterator();
            while (it.hasNext()) {
                Object current = it.next();
                if (current instanceof Transform
                        && ((Transform) current).getPhaseName().equals("wjtp.ifds")) {
                    it.remove();
                    break;
                }

            }
            PackManager.v().getPack("wjtp").add(transform);
        }
    }

    protected void addEntryPointMappingSceneTransformer(Set<String> entryPointClasses,
                                                        Map<String, Set<String>> entryPointMapping, Map<SootMethod, Set<String>> entryPointMap) {
        String pack = AnalysisParameters.v().useShimple() ? "wstp" : "wjtp";

        Transform transform = new Transform(pack + ".epm",
                new EntryPointMappingSceneTransformer(entryPointClasses, entryPointMapping, entryPointMap));
        if (PackManager.v().getPack(pack).get(pack + ".epm") == null) {
            PackManager.v().getPack(pack).add(transform);
        } else {
            Iterator<?> it = PackManager.v().getPack(pack).iterator();
            while (it.hasNext()) {
                Object current = it.next();
                if (current instanceof Transform
                        && ((Transform) current).getPhaseName().equals(pack + ".epm")) {
                    it.remove();
                    break;
                }

            }
            PackManager.v().getPack(pack).add(transform);
        }
    }

    @Override
    protected void processResults(Ic3Config ic3Config) {

    }

    @Override
    protected void finalizeAnalysis(Ic3Config ic3Config) {

    }

    @Override
    protected void handleFatalAnalysisException(Ic3Config ic3Config, FatalAnalysisException exception) {

    }
}
