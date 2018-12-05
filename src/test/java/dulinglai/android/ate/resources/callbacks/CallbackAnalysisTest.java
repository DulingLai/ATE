package dulinglai.android.ate.resources.callbacks;

import dulinglai.android.ate.SetupApplication;
import dulinglai.android.ate.config.AteConfiguration;
import dulinglai.android.ate.resources.AteTestConfig;
import org.junit.Test;

public class CallbackAnalysisTest {
    @Test
    public void testCallbackAnalysis(){
        AteConfiguration config = AteTestConfig.getConfigForTest();
        final String apkPath = config.getAnalysisFileConfig().getTargetAPKFile();
        SetupApplication main = new SetupApplication(config);

        main.initializeSoot(config);
        main.parseResources();
        main.constructInitialModel(apkPath);
        main.analyzeJimpleClasses(apkPath);
    }
}
