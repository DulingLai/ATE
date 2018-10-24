package dulinglai.android.ate.resources.intents;

import dulinglai.android.ate.propagationAnalysis.intents.Ic3Analysis;
import dulinglai.android.ate.propagationAnalysis.intents.Ic3Config;
import org.junit.Test;

public class Ic3Test {
    @Test
    public void testIC3(){
        Ic3Config config = new Ic3Config();
        config.setModel("/res/model/ComponentName.model:/res/model/Intent.model:/res/model/IntentFilter.model:/res/model/PendingIntent.model");
        config.setInput("/Volumes/WD_drive/20181007_test/todo.apk");
        config.setClasspath("~/Library/Android/sdk/platforms/android-23/android.jar:/Volumes/WD_drive/20181007_test/testspace_todo/todo.apk/retargeted/retargeted/todo:");

        Ic3Analysis ic3Analysis = new Ic3Analysis(config);
        ic3Analysis.performAnalysis(config);
    }
}
