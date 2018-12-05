package dulinglai.android.ate.resources;

import dulinglai.android.ate.MainClass;
import dulinglai.android.ate.config.AteConfiguration;

public class AteTestConfig {
    public static AteConfiguration getConfigForTest() {
        AteConfiguration ateConfiguration = new AteConfiguration();
        String apkPath = TestConfigConstants.APK_EBAY;
        String[] args = new String[4];
        args[0] = "ate";
        args[1] = "-d";
        args[2] = "-i";
        args[3] = apkPath;
        MainClass main = new MainClass();
        return main.parseCommandLineArgs(args);
    }

    public static AteConfiguration getConfigForTest(String apkPath) {
        AteConfiguration ateConfiguration = new AteConfiguration();
        String[] args = new String[4];
        args[0] = "ate";
        args[1] = "-d";
        args[2] = "-i";
        args[3] = apkPath;
        MainClass main = new MainClass();
        return main.parseCommandLineArgs(args);
    }
}
