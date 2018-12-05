package dulinglai.android.ate.resources.resources;

import dulinglai.android.ate.model.App;
import dulinglai.android.ate.resources.BaseAppTest;
import org.junit.Test;
import org.pmw.tinylog.Logger;

public class LayoutParserTest extends BaseAppTest {
    @Test
    public void testManifest(){
        // construct initial model
        main.constructInitialModel(config.getAnalysisFileConfig().getTargetAPKFile());

        // test the layout file parser
        LayoutFileParser lfp = new LayoutFileParser(App.v().getPackageName());
        lfp.parseLayoutFileDirect(config.getAnalysisFileConfig().getTargetAPKFile());
        Logger.debug("here");
    }
}
