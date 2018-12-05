package dulinglai.android.ate.resources;

import dulinglai.android.ate.SetupApplication;
import dulinglai.android.ate.config.AteConfiguration;
import org.junit.Before;

public class BaseAppTest {
    public SetupApplication main;
    public AteConfiguration config;

    @Before
    public void initializeTest() {
        main = setupApp();
    }

    /**
     * Setup the application
     * @return The manifest
     */
    public SetupApplication setupApp() {
        config = AteTestConfig.getConfigForTest();
        SetupApplication main = new SetupApplication(config);
        return main;
    }
}
