package dulinglai.android.ate;

import dulinglai.android.ate.config.AteConfiguration;
import dulinglai.android.ate.utils.FileUtils;
import org.apache.commons.cli.*;
import org.pmw.tinylog.Configurator;
import org.pmw.tinylog.Level;
import org.pmw.tinylog.Logger;

public class MainClass {

    private final Options options = new Options();

    // Files
    private static final String INPUT_APK_PATH_CONFIG = "i";
    private static final String OUTPUT_APK_PATH_CONFIG = "o";
    private static final String ICC_MODEL_CONFIG = "m";

    // Analysis Config
    private static final String CG_ALGO = "SPARK";
    private static final String MAX_CALLBACK = "cb";
    private static final String MAX_TIMEOUT = "t";

    // Android
    private static final String ANDROID_SDK_PATH_CONFIG = "s";
    private static final String ANDROID_API_LEVEL_CONFIG = "l";

    // Program Config
    private static final String DEBUG_CONFIG = "d";
    private static final String HELP_CONFIG = "h";
    private static final String VERSION_CONFIG = "v";

    public MainClass(){
        setupCmdOptions();
    }

    /**
     *  setup the command line parser
     */
    private void setupCmdOptions() {
        // command line options
        Option input = Option.builder(INPUT_APK_PATH_CONFIG).required(true).longOpt("input").hasArg(true).desc("input apk path (required)").build();
        Option output = Option.builder(OUTPUT_APK_PATH_CONFIG).required(false).longOpt("output").hasArg(true).desc("output directory (default to \"sootOutput\")").build();
        Option sdkPath = Option.builder(ANDROID_SDK_PATH_CONFIG).required(false).longOpt("sdk").hasArg(true).desc("path to android sdk (default value can be set in config file)").build();
        Option apiLevel = Option.builder(ANDROID_API_LEVEL_CONFIG).required(false).type(Number.class).longOpt("api").hasArg(true).desc("api level (default to 23)").build();
        Option iccModel = Option.builder(ICC_MODEL_CONFIG).required(false).longOpt("model").hasArg(true).desc("icc model (default to match the package name").build();
        Option maxCallback = Option.builder(MAX_CALLBACK).required(false).type(Number.class).hasArg(true).desc("the maximum number of callbacks modeled for each component (default to 20)").build();
        Option cgAlgo = Option.builder(CG_ALGO).required(false).hasArg(true).desc("callgraph algorithm to use (AUTO, CHA, VTA, RTA, SPARK, GEOM); default: AUTO").build();
        Option timeOut = Option.builder(MAX_TIMEOUT).required(false).hasArg(true).desc("maximum timeout during callback analysis in seconds (default: 60)").build();
        Option debug = new Option(DEBUG_CONFIG, "debug", false, "debug mode (default disabled)");
        Option help = new Option(HELP_CONFIG, "help", false, "print the help message");
        Option version = new Option( VERSION_CONFIG,"version", false,"print version info" );

        // add the options
        options.addOption(input);
        options.addOption(output);
        options.addOption(sdkPath);
        options.addOption(apiLevel);
        options.addOption(iccModel);
        options.addOption(cgAlgo);
        options.addOption(timeOut);
        options.addOption(maxCallback);
        options.addOption(debug);
        options.addOption(help);
        options.addOption(version);
    }

    public static void main(String[] args) {
        MainClass mainClass = new MainClass();
        mainClass.run(args);
    }

    /**
     * Parse the command line options
     * @param args The command line arguments
     * @return The configuration for ATE
     */
    public AteConfiguration parseCommandLineArgs(String[] args) {
        // Initial check for the number of arguments
        final HelpFormatter formatter = new HelpFormatter();
        if (args.length == 0) {
            formatter.printHelp("ate [OPTIONS]", options, true);
            System.exit(1);
        }

        // instance of the config obj
        AteConfiguration config = new AteConfiguration();

        // parse the command line options
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);

            // display the help message if option is specified
            if (cmd.hasOption("h") || cmd.hasOption("help")) {
                formatter.printHelp("ate [OPTIONS]", options, true);
                System.exit(1);
            }

            // display version info and exit
            if (cmd.hasOption(VERSION_CONFIG) || cmd.hasOption("version")) {
                System.out.println("ate " + getClass().getPackage().getImplementationVersion());
                System.exit(1);
            }

            // parse the options to configs
            parseOptions(cmd, config);
        } catch (ParseException e) {
            // print the error message
            Logger.error(e.getMessage());
            formatter.printHelp("ate", options, true);
            System.exit(1);
        }

        return config;
    }

    /**
     * Parse the command line arguments.
     *
     * @param args the command line arguments passed from main().
     */
    private void run(String[] args) {
        // Initialize the command line options
        AteConfiguration config = parseCommandLineArgs(args);

        // Setup application for analysis
        SetupApplication app = new SetupApplication(config);

        // run the analysis
        app.runAnalysis();
        System.exit(0);
    }

    private void parseOptions(CommandLine cmd, AteConfiguration config) {
//        // Set the project path configuration variables in the config obj
//        config.setProjectPath(System.getProperty("user.dir"));

        // Set apk path and output path
        if (cmd.hasOption(INPUT_APK_PATH_CONFIG) || cmd.hasOption("input")) {
            String apkFile = cmd.getOptionValue(INPUT_APK_PATH_CONFIG);
            if (apkFile != null && !apkFile.isEmpty())
                config.getAnalysisFileConfig().setTargetAPKFile(apkFile);
        } else{
            Logger.error("ERROR: Input APK path is required!");
            System.exit(1);
        }

        if (cmd.hasOption(OUTPUT_APK_PATH_CONFIG) || cmd.hasOption("output")) {
            String outputPath = cmd.getOptionValue(OUTPUT_APK_PATH_CONFIG);
            if (outputPath != null && !outputPath.isEmpty())
                config.getAnalysisFileConfig().setOutputFile(outputPath);
        }

        if (cmd.hasOption(ICC_MODEL_CONFIG) || cmd.hasOption("model")) {
            String iccModelPath = cmd.getOptionValue(ICC_MODEL_CONFIG);
            if (iccModelPath != null && !iccModelPath.isEmpty())
                config.getIccConfig().setIccModel(iccModelPath);
        }

        // Android SDK
        if (cmd.hasOption(ANDROID_SDK_PATH_CONFIG) || cmd.hasOption("sdk")) {
            String adkPath = cmd.getOptionValue(ANDROID_SDK_PATH_CONFIG);
            if (adkPath != null && !adkPath.isEmpty())
                config.getAnalysisFileConfig().setAndroidPlatformDir(adkPath);
        }

        if (cmd.hasOption(ANDROID_API_LEVEL_CONFIG) || cmd.hasOption("api")) {
            config.getAnalysisFileConfig().setTargetApiLevel(Integer.parseInt(cmd.getOptionValue(ANDROID_API_LEVEL_CONFIG)));
        }

        // analysis setting
        if (cmd.hasOption(MAX_CALLBACK))
            config.getCallbackConfig().setMaxCallbacksPerComponent(Integer.parseInt(cmd.getOptionValue(MAX_CALLBACK)));

        if (cmd.hasOption(CG_ALGO))
            config.getCallbackConfig().setCallgraphAlgorithm(cmd.getOptionValue(CG_ALGO));

        if (cmd.hasOption(MAX_TIMEOUT)) {
            //TODO add max timeout for analysis
        }



        // log level setting (debug/info/production)
        if (cmd.hasOption(DEBUG_CONFIG) || cmd.hasOption("debug"))
            Configurator.currentConfig().formatPattern("[{level}] {class_name}.{method}(): {message}").level(Level.DEBUG).activate();
        else
            Configurator.currentConfig().formatPattern("{level}: {message}").activate();

        // load the config file
        String configFilePath = "config.properties";
        FileUtils.loadConfigFile(configFilePath, config);

        // validate command line options
        if (!FileUtils.validateFile(config.getAnalysisFileConfig().getOutputFile())){
            Logger.error("Wrong output path!");
            System.exit(1);
        }
        if (!FileUtils.validateFile(config.getAnalysisFileConfig().getTargetAPKFile())){
            Logger.error("Wrong input apk path!");
            System.exit(1);
        }
        if (!FileUtils.validateFile(config.getAnalysisFileConfig().getAndroidPlatformDir())){
            Logger.error("Wrong android SDK path!");
            System.exit(1);
        }

        // set the android JAR
        Integer targetApiLevel = config.getAnalysisFileConfig().getTargetApiLevel();
        if (targetApiLevel != -1) {
            String sdkPath = config.getAnalysisFileConfig().getAndroidPlatformDir();
            config.getAnalysisFileConfig().setAndroidPlatformDir(sdkPath + "/platforms/android-" + targetApiLevel + "/android.jar");
        }

    }
}
