package dulinglai.android.ate.analyzers;

import dulinglai.android.ate.propagationAnalysis.intents.IccIdentifier;
import dulinglai.android.ate.resources.resources.LayoutFileParser;
import dulinglai.android.ate.data.values.ResourceValueProvider;
import org.pmw.tinylog.Logger;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.util.HashMultiMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * A widget analyzer that creates widgets by analyzing the jimple file
 */
public class FastJimpleAnalyzer extends AbstractJimpleAnalyzer {

    private static final String ANALYZER = "FastJimpleAnalyzer";

    public FastJimpleAnalyzer(Set<SootClass> entryPointClasses, Set<String> activityList,
                              LayoutFileParser layoutFileParser, List<IccIdentifier> iccUnitsForWidgetAnalysis)
            throws IOException {
        super(entryPointClasses, activityList, layoutFileParser, iccUnitsForWidgetAnalysis);
        this.editTextWidgetList = new ArrayList<>();
        this.clickWidgetNodeList = new ArrayList<>();
        this.ownershipEdges = new HashMultiMap<>();
    }

    @Override
    public void analyzeJimpleClasses() {
        super.analyzeJimpleClasses();
        Logger.info("[{}] Analyzing Jimple classes in FAST mode...", ANALYZER);

        // Check and add nested activities
        checkAndAddNestedActivities();

        // Find the mappings between classes and layouts
        findClassLayoutMappings();

        // Find all callbacks
        for (SootClass sc : Scene.v().getApplicationClasses()) {
            if (!sc.isConcrete())
                continue;

            for (SootMethod sm : sc.getMethods()) {
                if (sm.isConcrete()) {
                    analyzeMethodForCallbackRegistrations(sc, sm);
                    analyzeMethodForDynamicBroadcastReceiver(sm);
                    analyzeMethodForServiceConnection(sm);
                }
            }
            // Check for method overrides
            analyzeMethodOverrideCallbacks(sc);
        }

        // Find all UI callbacks and assign them to widgets
        for (SootClass callback : callbackMethods.keySet()){
            for (CallbackDefinition callbackDefinition : callbackMethods.get(callback)){
                if (callbackDefinition.getCallbackType() == CallbackDefinition.CallbackType.Widget){
                    uicallbacks.put(callback, callbackDefinition);
                }
            }
        }
    }



    @Override
    public void excludeEntryPoint(SootClass entryPoint) {
        // not supported
    }

}
