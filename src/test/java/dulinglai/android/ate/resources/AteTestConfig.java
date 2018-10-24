package dulinglai.android.ate.resources;

import dulinglai.android.ate.MainClass;
import dulinglai.android.ate.config.AteConfiguration;

public class AteTestConfig {
    public static AteConfiguration getConfigForTest() {
        AteConfiguration ateConfiguration = new AteConfiguration();
        String apkPath = "/Volumes/WD_drive/20181007_test/todo.apk";
        String[] args = new String[4];
        args[0] = "ate";
        args[1] = "-d";
        args[2] = "-i";
        args[3] = apkPath;
        MainClass main = new MainClass();
        return main.parseCommandLineArgs(args);
    }
}
