package dulinglai.android.ate.resources.intents;

import dulinglai.android.ate.SetupApplication;
import dulinglai.android.ate.config.AteConfiguration;
import dulinglai.android.ate.resources.AteTestConfig;
import org.junit.Test;

public class TransitionAnalysisTest {
    @Test
    public void testTransitionAnalysis(){
        AteConfiguration config = AteTestConfig.getConfigForTest();
        final String apkPath = config.getAnalysisFileConfig().getTargetAPKFile();
        SetupApplication main = new SetupApplication(config);

        main.initializeSoot(config);
        main.parseResources();
        main.constructInitialModel(apkPath);
        main.analyzeJimpleClasses(apkPath);
    }
}
