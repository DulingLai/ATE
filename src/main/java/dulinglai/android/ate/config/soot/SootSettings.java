package dulinglai.android.ate.config.soot;

import dulinglai.android.ate.config.AteConfiguration;
import dulinglai.android.ate.utils.androidUtils.LibraryClassPatcher;
import org.pmw.tinylog.Logger;
import soot.G;
import soot.PackManager;
import soot.Scene;
import soot.options.Options;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;

public class SootSettings {
    private static final String TAG = "SOOT";

    public static void initializeSoot(AteConfiguration config){
        Logger.info("[{}] Initializing Soot...",TAG);

        final String androidJar = config.getAnalysisFileConfig().getAndroidPlatformDir();
        final String apkFileLocation = config.getAnalysisFileConfig().getTargetAPKFile();

        Logger.debug("Using Android Jar: {}", androidJar);
        Logger.debug("Input APK file: {}", apkFileLocation);

        // clean up soot instance
        G.reset();
        Options.v().set_force_overwrite(true);

        Options.v().set_no_bodies_for_excluded(true);
        Options.v().set_allow_phantom_refs(true);       // allow soot to create phantom ref for unknown classes
        Options.v().set_output_format(Options.output_format_none);      //output as none
        //prefer Android APK files// -src-prec apk
        Options.v().set_src_prec(Options.src_prec_apk_class_jimple);
        Options.v().set_whole_program(true);
        Options.v().set_process_dir(Collections.singletonList(apkFileLocation));        // set target APK
        if (isForceAndroidJar(androidJar))
            Options.v().set_force_android_jar(androidJar);
        else
            Options.v().set_android_jars(androidJar);

        Options.v().set_keep_line_number(false);
        Options.v().set_keep_offset(false);
        Options.v().set_throw_analysis(Options.throw_analysis_dalvik);
        Options.v().set_process_multiple_dex(true);     // enable analysis on multi-dex APKs
        Options.v().set_ignore_resolution_errors(true);
        SootHelper.setSootExcludeLibs(Options.v());
        Options.v().set_soot_classpath(getClasspath(androidJar, apkFileLocation, config.getAnalysisFileConfig().getAdditionalClasspath()));

        soot.Main.v().autoSetOptions();
        // Configure the callgraph algorithm
        SootHelper.setSootCallgraphAlgorithm(config.getCallbackConfig().getCallgraphAlgorithm());

        // phase options (used in IC3)
        Options.v().setPhaseOption("cd.spark", "on");
        Options.v().setPhaseOption("jb.ulp","off");
        Options.v().setPhaseOption("jb.uce", "remove-unreachable-traps:true");
        Options.v().setPhaseOption("cg", "trim-clinit:false");

        // Add basic classes
        Logger.info("[{}] Loading dex files...",TAG);
        SootHelper.loadSootClasses();
        Scene.v().loadNecessaryClasses();

        // Make sure that we have valid Jimple bodies
        PackManager.v().getPack("wjpp").apply();

        // Patch the callgraph to support additional edges. We do this now,
        // because during callback discovery, the context-insensitive callgraph
        // algorithm would flood us with invalid edges.
        LibraryClassPatcher patcher = new LibraryClassPatcher();
        patcher.patchLibraries();

        Logger.info("[{}] Complete soot initialization...",TAG);
    }

    /**
     * Builds the classpath for this analysis
     *
     * @return The classpath to be used for the taint analysis
     */
    private static String getClasspath(String androidJar, String apkFileLocation, String additionalClasspath) {
        String classpath = isForceAndroidJar(androidJar)? Scene.v().getAndroidJarPath(androidJar, apkFileLocation) : androidJar;
        if (additionalClasspath != null && !additionalClasspath.isEmpty())
            classpath += File.pathSeparator + additionalClasspath;
        Logger.debug("[{}] Soot classpath: " + classpath, TAG);
        return classpath;
    }

    /**
     * Checks if we force to select a specific Android platform
     * @param androidJar The path to android Jar to check
     * @return True if we force to select a specific Android platform
     */
    public static boolean isForceAndroidJar(String androidJar) {
        return Files.isDirectory(Paths.get(androidJar));
    }
}
