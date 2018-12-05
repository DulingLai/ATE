package dulinglai.android.ate.resources.resources;

import dulinglai.android.ate.model.App;
import dulinglai.android.ate.model.components.AbstractComponent;
import dulinglai.android.ate.resources.BaseAppTest;
import org.junit.Test;
import org.pmw.tinylog.Logger;
import soot.Scene;
import soot.SootClass;

import java.util.Set;
import java.util.stream.Collectors;

public class AndroidSootClassTest extends BaseAppTest {
    @Test
    public void testAndroidClasses(){

        main.constructInitialModel(config.getAnalysisFileConfig().getTargetAPKFile());

        App.v().getActivities().forEach(x -> x.setMainClass(Scene.v().getSootClassUnsafe(x.getName())));
        App.v().getServices().forEach(x -> x.setMainClass(Scene.v().getSootClassUnsafe(x.getName())));
        App.v().getBroadcastReceivers().forEach(x -> x.setMainClass(Scene.v().getSootClassUnsafe(x.getName())));
        App.v().getContentProviders().forEach(x -> x.setMainClass(Scene.v().getSootClassUnsafe(x.getName())));

        Set<SootClass> activities = App.v().getActivities().stream().map(AbstractComponent::getMainClass).collect(Collectors.toSet());
        Set<SootClass> services = App.v().getServices().stream().map(AbstractComponent::getMainClass).collect(Collectors.toSet());
        Set<SootClass> receivers = App.v().getBroadcastReceivers().stream().map(AbstractComponent::getMainClass).collect(Collectors.toSet());
        Set<SootClass> providers = App.v().getContentProviders().stream().map(AbstractComponent::getMainClass).collect(Collectors.toSet());

        // test
        assert !activities.isEmpty();
        assert !services.isEmpty();
        assert !receivers.isEmpty();
        assert !providers.isEmpty();

        Logger.debug("Activities: {}", activities);
        Logger.debug("Services: {}", services);
        Logger.debug("Broadcast Receivers: {}", receivers);
        Logger.debug("Content Providers: {}", providers);

        App.v().getActivities().forEach(x -> {
            String parentCompString = x.getParentCompString();
            Logger.debug("Activity: {} -> Exported: {}; Parent: {}",x.getName(), x.isExported(), parentCompString);
        });
    }
}
