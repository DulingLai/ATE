package dulinglai.android.ate.resources.resources;

import dulinglai.android.ate.SetupApplication;
import dulinglai.android.ate.config.AteConfiguration;
import dulinglai.android.ate.resources.AteTestConfig;
import dulinglai.android.ate.resources.manifest.ProcessManifest;
import org.junit.Test;
import org.pmw.tinylog.Logger;

public class ManifestParserTest {
    @Test
    public void testManifest(){
        AteConfiguration config = AteTestConfig.getConfigForTest();
        SetupApplication main = new SetupApplication(config);
        main.collectComponentNodes(config.getAnalysisFileConfig().getTargetAPKFile());

        ProcessManifest manifest = main.getManifest();
        Logger.debug("Package Name: {}", manifest.getPackageName());
        Logger.debug("Application Name: {}", manifest.getApplicationName());
        Logger.debug("Activities: {}", manifest.getAllActivityClasses());
    }
}
