package dulinglai.android.ate.analyzers;

import dulinglai.android.ate.model.App;
import dulinglai.android.ate.model.components.Activity;
import dulinglai.android.ate.model.entities.DialogEntity;
import dulinglai.android.ate.model.entities.LayoutEntity;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootClass;

import java.util.*;
import java.util.stream.Collectors;

public class UiAnalysisTransformer extends SceneTransformer {

    private final int timeout;
    private final int numThread;
    private final int maxMethodDepth;

    private final Set<SootClass> androidWidgetClasses;
    private final List<SootClass> widgetImplementers;

    // UI elements
    private final Set<DialogEntity> dialogs = Collections.synchronizedSet(new HashSet<>());
    private final Map<Integer, LayoutEntity> layouts = Collections.synchronizedMap(new HashMap<>());

    public UiAnalysisTransformer(int timeout, int numThread, int maxMethodDepth) {
        this.timeout = timeout;
        this.numThread = numThread;
        this.maxMethodDepth = maxMethodDepth;

        this.androidWidgetClasses = Scene.v().getClasses().stream().filter(x -> x.getName().startsWith("android.") &&
                !x.getName().startsWith("android.view.") && x.isInterface()
                && !x.getName().endsWith("Listener")).collect(Collectors.toSet());

        this.widgetImplementers = new ArrayList<>();
        for (SootClass widgetClass : androidWidgetClasses) {
            this.widgetImplementers.addAll(Scene.v().getActiveHierarchy().getImplementersOf(widgetClass));
        }
    }

    @Override
    protected void internalTransform(String phaseName, Map<String, String> options) {
        // load all soot classes
        Set<SootClass> activities = App.v().getActivities().stream().map(Activity::getName).filter(x -> !Scene.v().getSootClass(x).isPhantom()).
                map(x -> Scene.v().getSootClass(x)).filter(x -> x.getName().startsWith(App.v().getPackageName())).collect(Collectors.toSet());
    }
}
