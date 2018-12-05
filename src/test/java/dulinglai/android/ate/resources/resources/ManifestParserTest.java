package dulinglai.android.ate.resources.resources;

import dulinglai.android.ate.model.App;
import dulinglai.android.ate.resources.BaseAppTest;
import dulinglai.android.ate.resources.manifest.ProcessManifest;
import org.junit.Test;
import org.pmw.tinylog.Logger;

public class ManifestParserTest extends BaseAppTest {
    @Test
    public void testManifest(){
        main.constructInitialModel(config.getAnalysisFileConfig().getTargetAPKFile());
        ProcessManifest manifest = main.getManifest();
        Logger.debug("Package Name: {}", manifest.getPackageName());
        Logger.debug("Application Name: {}", manifest.getApplicationName());
        Logger.debug("Activities: {}", manifest.getAllActivityClasses());

        Logger.debug("Activities in model: {}", App.v().getActivities());
        Logger.debug("Launch activity in model: {}", App.v().getLaunchActivity());
    }
}
