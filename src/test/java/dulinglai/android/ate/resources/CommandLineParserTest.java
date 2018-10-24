package dulinglai.android.ate.resources;

import dulinglai.android.ate.config.AteConfiguration;
import org.junit.Test;
import org.pmw.tinylog.Logger;

public class CommandLineParserTest {
    @Test
    public void testCmdParser() {
        AteConfiguration config = AteTestConfig.getConfigForTest();
        // print the options for debugging
        Logger.debug("Input APK path: " + config.getAnalysisFileConfig().getTargetAPKFile());
        Logger.debug("ICC model: " + config.getIccConfig().getIccModel());
        Logger.debug("Output path: " + config.getAnalysisFileConfig().getOutputFile());
        Logger.debug("Android SDK path: " + config.getAnalysisFileConfig().getAndroidPlatformDir());
    }
}
