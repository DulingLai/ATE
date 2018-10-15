package dulinglai.android.ate.config;

import dulinglai.android.ate.data.android.CategoryDefinition;
import org.pmw.tinylog.Logger;
import soot.jimple.infoflow.InfoflowConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * The Singleton class used to store configuration variables
 */
public class AteConfig extends InfoflowConfiguration {

    /**
     * Configuration for analysis file location
     */
    public static class AnalysisFileConfiguration {
        private String targetAPKFile = "";
        private Integer targetApiLevel = -1;
        private String sourceSinkFile = "";
        private String androidPlatformDir = "";
        private String additionalClasspath = "";
        private String outputFile = "";

        /**
         * Copies the settings of the given configuration into this configuration object
         *
         * @param fileConfig
         *            The other configuration object
         */
        public void merge(AnalysisFileConfiguration fileConfig) {
            this.targetAPKFile = fileConfig.targetAPKFile;
            this.targetApiLevel = fileConfig.targetApiLevel;
            this.sourceSinkFile = fileConfig.sourceSinkFile;
            this.androidPlatformDir = fileConfig.androidPlatformDir;
            this.additionalClasspath = fileConfig.additionalClasspath;
            this.outputFile = fileConfig.outputFile;
        }

        /**
         * Checks whether this configuration is valid, i.e., whether there are no
         * inconsistencies and all necessary data is filled in
         *
         * @return True if this configuration is complete and valid, otherwise false
         */
        public boolean validate() {
            return targetAPKFile != null && !targetAPKFile.isEmpty() && sourceSinkFile != null
                    && !sourceSinkFile.isEmpty() && androidPlatformDir != null && !androidPlatformDir.isEmpty();
        }

        /**
         * Gets the target APK file on which the data flow analysis shall be conducted
         *
         * @return The target APK file on which the data flow analysis shall be
         *         conducted
         */
        public String getTargetAPKFile() {
            return targetAPKFile;
        }

        /**
         * Sets the target APK file on which the data flow analysis shall be conducted
         *
         * @param targetAPKFile
         *            The target APK file on which the data flow analysis shall be
         *            conducted
         */
        public void setTargetAPKFile(String targetAPKFile) {
            this.targetAPKFile = targetAPKFile;
        }

        /**
         * Gets the directory in which the Android platform JARs are located
         *
         * @return The directory in which the Android platform JARs are located
         */
        public String getAndroidPlatformDir() {
            return androidPlatformDir;
        }

        /**
         * Sets the directory in which the Android platform JARs are located
         *
         * @param androidPlatformDir
         *            The directory in which the Android platform JARs are located
         */
        public void setAndroidPlatformDir(String androidPlatformDir) {
            this.androidPlatformDir = androidPlatformDir;
        }

        /**
         * Sets the target API level
         * @param targetApiLevel The target API level to set
         */
        public void setTargetApiLevel(Integer targetApiLevel) { this.targetApiLevel = targetApiLevel; }

        /**
         * Gets the target API level
         * @return The target API level
         */
        public Integer getTargetApiLevel(){ return targetApiLevel; }

        /**
         * Gets the source and sink file
         *
         * @return The source and sink file
         */
        public String getSourceSinkFile() {
            return sourceSinkFile;
        }

        /**
         * Sets the source and sink file
         *
         * @param sourceSinkFile
         *            The source and sink file
         */
        public void setSourceSinkFile(String sourceSinkFile) {
            this.sourceSinkFile = sourceSinkFile;
        }

        /**
         * Gets the additional libraries that are required on the analysis classpath.
         * FlowDroid will automatically include the target APK file and the Android
         * platform JAR file, these need not be specified separately.
         *
         * @return The additional libraries that are required on the analysis classpath
         */
        public String getAdditionalClasspath() {
            return additionalClasspath;
        }

        /**
         * Gets the additional libraries that are required on the analysis classpath.
         * FlowDroid will automatically include the target APK file and the Android
         * platform JAR file, these need not be specified separately.
         *
         * @return The additional libraries that are required on the analysis classpath
         */
        public void setAdditionalClasspath(String additionalClasspath) {
            this.additionalClasspath = additionalClasspath;
        }

        /**
         * Gets the file into which the results of the data flow analysis shall be
         * written
         *
         * @return The target file into which the results of the data flow analysis
         *         shall be written
         */
        public String getOutputFile() {
            return outputFile;
        }

        /**
         * Sets the file into which the results of the data flow analysis shall be
         * written
         *
         * @param outputFile
         *            The target file into which the results of the data flow analysis
         *            shall be written
         */
        public void setOutputFile(String outputFile) {
            this.outputFile = outputFile;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((additionalClasspath == null) ? 0 : additionalClasspath.hashCode());
            result = prime * result + ((androidPlatformDir == null) ? 0 : androidPlatformDir.hashCode());
            result = prime * result + ((outputFile == null) ? 0 : outputFile.hashCode());
            result = prime * result + ((sourceSinkFile == null) ? 0 : sourceSinkFile.hashCode());
            result = prime * result + ((targetAPKFile == null) ? 0 : targetAPKFile.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            AnalysisFileConfiguration other = (AnalysisFileConfiguration) obj;
            if (additionalClasspath == null) {
                if (other.additionalClasspath != null)
                    return false;
            } else if (!additionalClasspath.equals(other.additionalClasspath))
                return false;
            if (androidPlatformDir == null) {
                if (other.androidPlatformDir != null)
                    return false;
            } else if (!androidPlatformDir.equals(other.androidPlatformDir))
                return false;
            if (outputFile == null) {
                if (other.outputFile != null)
                    return false;
            } else if (!outputFile.equals(other.outputFile))
                return false;
            if (sourceSinkFile == null) {
                if (other.sourceSinkFile != null)
                    return false;
            } else if (!sourceSinkFile.equals(other.sourceSinkFile))
                return false;
            if (targetAPKFile == null) {
                if (other.targetAPKFile != null)
                    return false;
            } else if (!targetAPKFile.equals(other.targetAPKFile))
                return false;
            return true;
        }
    }

    /**
     * The configuration for the inter-component data flow analysis
     */
    public static class IccConfiguration {

        private boolean iccEnabled = false;
        private String iccModel = null;
        private boolean iccResultsPurify = true;

        /**
         * Copies the settings of the given configuration into this configuration object
         *
         * @param iccConfig
         *            The other configuration object
         */
        public void merge(IccConfiguration iccConfig) {
            this.iccEnabled = iccConfig.iccEnabled;
            this.iccModel = iccConfig.iccModel;
            this.iccResultsPurify = iccConfig.iccResultsPurify;
        }

        public String getIccModel() {
            return iccModel;
        }

        public void setIccModel(String iccModel) {
            this.iccModel = iccModel;
        }

        /**
         * Gets whether inter-component data flow tracking is enabled or not
         *
         * @return True if inter-component data flow tracking is enabled, otherwise
         *         false
         */
        public boolean isIccEnabled() {
            return this.iccModel != null && !this.iccModel.isEmpty();
        }

        /**
         * Gets whether the ICC results shall be purified after the data flow
         * computation. Purification means that flows inside components are dropped if
         * the same flow is also part of an inter-component flow.
         *
         * @return True if the ICC results shall be purified, otherwise false. Note that
         *         this method also returns false if ICC processing is disabled.
         */
        public boolean isIccResultsPurifyEnabled() {
            return isIccEnabled() && iccResultsPurify;
        }

        /**
         * Sets whether the ICC results shall be purified after the data flow
         * computation. Purification means that flows inside components are dropped if
         * the same flow is also part of an inter-component flow.
         *
         * @param iccResultsPurify
         */
        public void setIccResultsPurify(boolean iccResultsPurify) {
            this.iccResultsPurify = iccResultsPurify;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + (iccEnabled ? 1231 : 1237);
            result = prime * result + ((iccModel == null) ? 0 : iccModel.hashCode());
            result = prime * result + (iccResultsPurify ? 1231 : 1237);
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            IccConfiguration other = (IccConfiguration) obj;
            if (iccEnabled != other.iccEnabled)
                return false;
            if (iccModel == null) {
                if (other.iccModel != null)
                    return false;
            } else if (!iccModel.equals(other.iccModel))
                return false;
            if (iccResultsPurify != other.iccResultsPurify)
                return false;
            return true;
        }
    }

    /**
     * The configuration for analyzing callbacks in Android apps
     *
     * @author Steven Arzt
     *
     */
    public static class CallbackConfiguration {

        private boolean enableCallbacks = true;
        private CallbackAnalyzer callbackAnalyzer = CallbackAnalyzer.Fast;
        private boolean filterThreadCallbacks = true;
        private int maxCallbacksPerComponent = 100;
        private int callbackAnalysisTimeout = 0;
        private int maxCallbackAnalysisDepth = -1;
        private CallgraphAlgorithm callgraphAlgorithm;

        /**
         * Copies the settings of the given configuration into this configuration object
         *
         * @param cbConfig
         *            The other configuration object
         */
        public void merge(CallbackConfiguration cbConfig) {
            this.enableCallbacks = cbConfig.enableCallbacks;
            this.callbackAnalyzer = cbConfig.callbackAnalyzer;
            this.filterThreadCallbacks = cbConfig.filterThreadCallbacks;
            this.maxCallbacksPerComponent = cbConfig.maxCallbacksPerComponent;
            this.callbackAnalysisTimeout = cbConfig.callbackAnalysisTimeout;
            this.maxCallbackAnalysisDepth = cbConfig.maxCallbackAnalysisDepth;
            this.callgraphAlgorithm = cbConfig.callgraphAlgorithm;
        }

        /**
         * Sets whether the taint analysis shall consider callbacks
         *
         * @param enableCallbacks
         *            True if taints shall be tracked through callbacks, otherwise false
         */
        public void setEnableCallbacks(boolean enableCallbacks) {
            this.enableCallbacks = enableCallbacks;
        }

        /**
         * Gets whether the taint analysis shall consider callbacks
         *
         * @return True if taints shall be tracked through callbacks, otherwise false
         */
        public boolean getEnableCallbacks() {
            return this.enableCallbacks;
        }

        /**
         * Sets the callback analyzer to be used in preparation for the taint analysis
         *
         * @param callbackAnalyzer
         *            The callback analyzer to be used
         */
        public void setCallbackAnalyzer(CallbackAnalyzer callbackAnalyzer) {
            this.callbackAnalyzer = callbackAnalyzer;
        }

        /**
         * Gets the callback analyzer that is being used in preparation for the taint
         * analysis
         *
         * @return The callback analyzer being used
         * @return
         */
        public CallbackAnalyzer getCallbackAnalyzer() {
            return this.callbackAnalyzer;
        }

        /**
         * Sets whether the callback analysis algorithm should follow paths that contain
         * threads. If this option is disabled, callbacks only registered in threads
         * will be missed. If it is enabled, context-insensitive callgraph algorithms
         * can lead to a high number of false positives for the callback analyzer.
         *
         * @param filterThreadCallbacks
         *            True to discover callbacks registered in threads, otherwise false
         */
        public void setFilterThreadCallbacks(boolean filterThreadCallbacks) {
            this.filterThreadCallbacks = filterThreadCallbacks;
        }

        /**
         * Gets whether the callback analysis algorithm should follow paths that contain
         * threads. If this option is disabled, callbacks only registered in threads
         * will be missed. If it is enabled, context-insensitive callgraph algorithms
         * can lead to a high number of false positives for the callback analyzer.
         *
         * @return True to discover callbacks registered in threads, otherwise false
         */
        public boolean getFilterThreadCallbacks() {
            return this.filterThreadCallbacks;
        }

        /**
         * Gets the maximum number of callbacks per component. If the callback collector
         * finds more callbacks than this number for one given component, the analysis
         * will assume that precision has degraded too much and will analyze this
         * component without callbacks.
         *
         * @return The maximum number of callbacks per component
         */
        public int getMaxCallbacksPerComponent() {
            return this.maxCallbacksPerComponent;
        }

        /**
         * Sets the maximum number of callbacks per component. If the callback collector
         * finds more callbacks than this number for one given component, the analysis
         * will assume that precision has degraded too much and will analyze this
         * component without callbacks.
         *
         * @param maxCallbacksPerComponent
         *            The maximum number of callbacks per component
         */
        public void setMaxCallbacksPerComponent(int maxCallbacksPerComponent) {
            this.maxCallbacksPerComponent = maxCallbacksPerComponent;
        }

        /**
         * Gets the timeout in seconds after which the callback analysis shall be
         * stopped. After the timeout, the data flow analysis will continue with those
         * callbacks that have been found so far.
         *
         * @return The callback analysis timeout in seconds
         */
        public int getCallbackAnalysisTimeout() {
            return this.callbackAnalysisTimeout;
        }

        /**
         * Sets the timeout in seconds after which the callback analysis shall be
         * stopped. After the timeout, the data flow analysis will continue with those
         * callbacks that have been found so far.
         *
         * @param callbackAnalysisTimeout
         *            The callback analysis timeout in seconds
         */
        public void setCallbackAnalysisTimeout(int callbackAnalysisTimeout) {
            this.callbackAnalysisTimeout = callbackAnalysisTimeout;
        }

        /**
         * Gets the maximum depth up to which the callback analyzer shall look into
         * chains of callbacks registering other callbacks. A value equal to or smaller
         * than zero indicates an infinite maximum depth.
         *
         * @return The maximum depth up to which to look into callback registration
         *         chains.
         */
        public int getMaxAnalysisCallbackDepth() {
            return this.maxCallbackAnalysisDepth;
        }

        /**
         * Sets the maximum depth up to which the callback analyzer shall look into
         * chains of callbacks registering other callbacks. A value equal to or smaller
         * than zero indicates an infinite maximum depth.
         *
         * @param maxCallbackAnalysisDepth
         *            The maximum depth up to which to look into callback registration
         *            chains.
         */
        public void setMaxAnalysisCallbackDepth(int maxCallbackAnalysisDepth) {
            this.maxCallbackAnalysisDepth = maxCallbackAnalysisDepth;
        }

        /**
         * Gets the callgraph algorithm to be used by the data flow tracker
         * @return The callgraph algorithm to be used by the data flow tracker
         */
        public CallgraphAlgorithm getCallgraphAlgorithm(){ return callgraphAlgorithm; }
        /**
         * Sets the callgraph algorithm to be used by the data flow tracker
         * @param algorithm
         *          The callgraph algorithm to be used by the data flow tracker
         */
        public void setCallgraphAlgorithm(String algorithm){ this.callgraphAlgorithm = parseCallgraphAlgorithm(algorithm); }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + callbackAnalysisTimeout;
            result = prime * result + ((callbackAnalyzer == null) ? 0 : callbackAnalyzer.hashCode());
            result = prime * result + (enableCallbacks ? 1231 : 1237);
            result = prime * result + (filterThreadCallbacks ? 1231 : 1237);
            result = prime * result + maxCallbackAnalysisDepth;
            result = prime * result + maxCallbacksPerComponent;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            CallbackConfiguration other = (CallbackConfiguration) obj;
            if (callbackAnalysisTimeout != other.callbackAnalysisTimeout)
                return false;
            if (callbackAnalyzer != other.callbackAnalyzer)
                return false;
            if (enableCallbacks != other.enableCallbacks)
                return false;
            if (filterThreadCallbacks != other.filterThreadCallbacks)
                return false;
            if (maxCallbackAnalysisDepth != other.maxCallbackAnalysisDepth)
                return false;
            if (maxCallbacksPerComponent != other.maxCallbacksPerComponent)
                return false;
            return true;
        }
    }

        private static CallgraphAlgorithm parseCallgraphAlgorithm(String algo) {
        if (algo.equalsIgnoreCase("AUTO"))
            return CallgraphAlgorithm.AutomaticSelection;
        else if (algo.equalsIgnoreCase("CHA"))
            return CallgraphAlgorithm.CHA;
        else if (algo.equalsIgnoreCase("VTA"))
            return CallgraphAlgorithm.VTA;
        else if (algo.equalsIgnoreCase("RTA"))
            return CallgraphAlgorithm.RTA;
        else if (algo.equalsIgnoreCase("SPARK"))
            return CallgraphAlgorithm.SPARK;
        else if (algo.equalsIgnoreCase("GEOM"))
            return CallgraphAlgorithm.GEOM;
        else {
            Logger.error(String.format("Invalid callgraph algorithm: %s", algo));
            throw new RuntimeException();
        }
    }

    /**
     * The default mode how the filter shall treat source or sink categories that
     * have not been configured explicitly
     *
     * @author Steven Arzt
     *
     */
    public static enum SourceSinkFilterMode {
        /**
         * Include all categories that have not been excluded explicitly
         */
        UseAllButExcluded,

        /**
         * Only include those categories that have been included explicitly and ignore
         * all others
         */
        UseOnlyIncluded
    }

    /**
     * The modes (included or excludes) that a category can have for the data flow
     * analysis
     *
     * @author Steven Arzt
     *
     */
    public static enum CategoryMode {
        /**
         * The sources and sinks from the current category shall be included in the data
         * flow analysis
         */
        Include,

        /**
         * The sources and sinks from the current category shall be excluded from the
         * data flow analysis
         */
        Exclude
    }

    /**
     * The configuration for the source and sink manager
     *
     * @author Steven Arzt
     *
     */
    public static class SourceSinkConfiguration {

        private CallbackSourceMode callbackSourceMode = CallbackSourceMode.SourceListOnly;
        private boolean enableLifecycleSources = false;
        private LayoutMatchingMode layoutMatchingMode = LayoutMatchingMode.MatchSensitiveOnly;

        private SourceSinkFilterMode sourceFilterMode = SourceSinkFilterMode.UseAllButExcluded;
        private SourceSinkFilterMode sinkFilterMode = SourceSinkFilterMode.UseAllButExcluded;

        private Map<CategoryDefinition, CategoryMode> sourceCategories = new HashMap<>();
        private Map<CategoryDefinition, CategoryMode> sinkCategories = new HashMap<>();

        /**
         * Copies the settings of the given configuration into this configuration object
         *
         * @param ssConfig
         *            The other configuration object
         */
        public void merge(SourceSinkConfiguration ssConfig) {
            this.callbackSourceMode = ssConfig.callbackSourceMode;
            this.enableLifecycleSources = ssConfig.enableLifecycleSources;
            this.layoutMatchingMode = ssConfig.layoutMatchingMode;

            this.sourceFilterMode = ssConfig.sourceFilterMode;
            this.sinkFilterMode = ssConfig.sinkFilterMode;

            this.sourceCategories.putAll(ssConfig.sourceCategories);
            this.sinkCategories.putAll(ssConfig.sinkCategories);
        }

        /**
         * Sets under which circumstances the parameters of callback methods shall be
         * treated as sources.
         *
         * @param callbackSourceMode
         *            The strategy for deciding whether a certain callback parameter is
         *            a data flow source or not
         */
        public void setCallbackSourceMode(CallbackSourceMode callbackSourceMode) {
            this.callbackSourceMode = callbackSourceMode;
        }

        /**
         * Sets under which circumstances the parameters of callback methods shall be
         * treated as sources.
         *
         * @return The strategy for deciding whether a certain callback parameter is a
         *         data flow source or not
         */
        public CallbackSourceMode getCallbackSourceMode() {
            return this.callbackSourceMode;
        }

        /**
         * Sets whether the parameters of lifecycle methods shall be considered as
         * sources
         *
         * @param enableLifecycleSources
         *            True if the parameters of lifecycle methods shall be considered as
         *            sources, otherwise false
         */
        public void setEnableLifecycleSources(boolean enableLifecycleSources) {
            this.enableLifecycleSources = enableLifecycleSources;
        }

        /**
         * Gets whether the parameters of lifecycle methods shall be considered as
         * sources
         *
         * @return True if the parameters of lifecycle methods shall be considered as
         *         sources, otherwise false
         */
        public boolean getEnableLifecycleSources() {
            return this.enableLifecycleSources;
        }

        /**
         * Sets the mode to be used when deciding whether a UI control is a source or
         * not
         *
         * @param mode
         *            The mode to be used for classifying UI controls as sources
         */
        public void setLayoutMatchingMode(LayoutMatchingMode mode) {
            this.layoutMatchingMode = mode;
        }

        /**
         * Gets the mode to be used when deciding whether a UI control is a source or
         * not
         *
         * @return The mode to be used for classifying UI controls as sources
         */
        public LayoutMatchingMode getLayoutMatchingMode() {
            return this.layoutMatchingMode;
        }

        /**
         * Gets the default mode for handling sources that have not been configured
         * explicitly
         *
         * @return The default mode for handling sources that have not been configured
         *         explicitly
         */
        public SourceSinkFilterMode getSourceFilterMode() {
            return sourceFilterMode;
        }

        /**
         * Sets the default mode for handling sources that have not been configured
         * explicitly
         *
         * @param sourceFilterMode
         *            The default mode for handling sources that have not been
         *            configured explicitly
         */
        public void setSourceFilterMode(SourceSinkFilterMode sourceFilterMode) {
            this.sourceFilterMode = sourceFilterMode;
        }

        /**
         * Gets the default mode for handling sinks that have not been configured
         * explicitly
         *
         * @return The default mode for handling sinks that have not been configured
         *         explicitly
         */
        public SourceSinkFilterMode getSinkFilterMode() {
            return sinkFilterMode;
        }

        /**
         * Sets the default mode for handling sinks that have not been configured
         * explicitly
         *
         * @param sinkFilterMode
         *            The default mode for handling sinks that have not been configured
         *            explicitly
         */
        public void setSinkFilterMode(SourceSinkFilterMode sinkFilterMode) {
            this.sinkFilterMode = sinkFilterMode;
        }

        /**
         * Gets the explicitly-configured source categories
         *
         * @return The set of source categories for which an explicit configuration has
         *         been specified
         */
        public Set<CategoryDefinition> getSourceCategories() {
            return sourceCategories.keySet();
        }

        /**
         * Gets the explicitly-configured sink categories
         *
         * @return The set of sink categories for which an explicit configuration has
         *         been specified
         */
        public Set<CategoryDefinition> getSinkCategories() {
            return sinkCategories.keySet();
        }

        /**
         * Gets all source categories defined in the configuration together with their
         * respective modes (included or excluded)
         *
         * @return The source categories defined in the configuration along with their
         *         respective modes
         */
        public Map<CategoryDefinition, CategoryMode> getSourceCategoriesAndModes() {
            return this.sourceCategories;
        }

        /**
         * Gets all sink categories defined in the configuration together with their
         * respective modes (included or excluded)
         *
         * @return The sink categories defined in the configuration along with their
         *         respective modes
         */
        public Map<CategoryDefinition, CategoryMode> getSinkCategoriesAndModes() {
            return this.sinkCategories;
        }

        /**
         * Adds a source category definition to this configuration
         *
         * @param category
         *            The category definition
         * @param mode
         *            The mode that defines whether this category shall be included or
         *            excluded
         */
        public void addSourceCategory(CategoryDefinition category, CategoryMode mode) {
            this.sourceCategories.put(category, mode);
        }

        /**
         * Adds a sink category definition to this configuration
         *
         * @param category
         *            The category definition
         * @param mode
         *            The mode that defines whether this category shall be included or
         *            excluded
         */
        public void addSinkCategory(CategoryDefinition category, CategoryMode mode) {
            this.sinkCategories.put(category, mode);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((callbackSourceMode == null) ? 0 : callbackSourceMode.hashCode());
            result = prime * result + (enableLifecycleSources ? 1231 : 1237);
            result = prime * result + ((layoutMatchingMode == null) ? 0 : layoutMatchingMode.hashCode());
            result = prime * result + ((sinkCategories == null) ? 0 : sinkCategories.hashCode());
            result = prime * result + ((sinkFilterMode == null) ? 0 : sinkFilterMode.hashCode());
            result = prime * result + ((sourceCategories == null) ? 0 : sourceCategories.hashCode());
            result = prime * result + ((sourceFilterMode == null) ? 0 : sourceFilterMode.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            SourceSinkConfiguration other = (SourceSinkConfiguration) obj;
            if (callbackSourceMode != other.callbackSourceMode)
                return false;
            if (enableLifecycleSources != other.enableLifecycleSources)
                return false;
            if (layoutMatchingMode != other.layoutMatchingMode)
                return false;
            if (sinkCategories == null) {
                if (other.sinkCategories != null)
                    return false;
            } else if (!sinkCategories.equals(other.sinkCategories))
                return false;
            if (sinkFilterMode != other.sinkFilterMode)
                return false;
            if (sourceCategories == null) {
                if (other.sourceCategories != null)
                    return false;
            } else if (!sourceCategories.equals(other.sourceCategories))
                return false;
            if (sourceFilterMode != other.sourceFilterMode)
                return false;
            return true;
        }

    }

    /**
     * Enumeration containing the supported callback analyzers
     */
    public static enum CallbackAnalyzer {
        /**
         * The highly-precise default analyzer
         */
        Default,
        /**
         * An analyzer that favors performance over precision
         */
        Fast
    }

    /**
     * Methods for deciding whether a parameter of a system callback is to be
     * treated as a source or not
     *
     * @author Steven Arzt
     *
     */
    public static enum CallbackSourceMode {
        /**
         * Callback parameters are never treated as sources
         */
        NoParametersAsSources,
        /**
         * All callback parameters are sources
         */
        AllParametersAsSources,
        /**
         * Only parameters from callback methods explicitly defined as sources are
         * treated as sources
         */
        SourceListOnly
    }

    /**
     * Possible modes for matching layout components as data flow sources
     *
     * @author Steven Arzt
     */
    public static enum LayoutMatchingMode {
        /**
         * Do not use Android layout components as sources
         */
        NoMatch,

        /**
         * Use all layout components as sources
         */
        MatchAll,

        /**
         * Only use sensitive layout components (e.g. password fields) as sources
         */
        MatchSensitiveOnly
    }

    /**
     * Enumeration containing the different ways in which Soot can be used
     *
     * @author Steven Arzt
     *
     */
    public static enum SootIntegrationMode {
        /**
         * With this option, FlowDroid initializes and configures its own Soot instance.
         * This option is the default and the best choice in most cases.
         */
        CreateNewInstace,

        /**
         * With this option, FlowDroid uses the existing Soot instance, but generates
         * its own callgraph. Note that it is the responsibility of the caller to make
         * sure that pre-existing Soot instances are configured correctly for the use
         * with FlowDroid.
         */
        UseExistingInstance,

        /**
         *
         */
        UseExistingCallgraph;

        /**
         * Gets whether this integration mode requires FlowDroid to build its own
         * callgraph
         *
         * @return True if FlowDroid must create its own callgraph, otherwise false
         */
        boolean needsToBuildCallgraph() {
            return this == SootIntegrationMode.CreateNewInstace || this == SootIntegrationMode.UseExistingInstance;
        }

    }

    private boolean oneComponentAtATime = false;

    private final CallbackConfiguration callbackConfig = new CallbackConfiguration();
    private final SourceSinkConfiguration sourceSinkConfig = new SourceSinkConfiguration();
    private final IccConfiguration iccConfig = new IccConfiguration();
    private final AnalysisFileConfiguration analysisFileConfig = new AnalysisFileConfiguration();

    private SootIntegrationMode sootIntegrationMode = SootIntegrationMode.CreateNewInstace;
    private boolean mergeDexFiles = false;

    public AteConfig() {
        // We need to adapt some of the defaults. Most people don't care about
        // this stuff, but want a faster analysis.
        this.setEnableArraySizeTainting(false);
        this.setInspectSources(false);
        this.setInspectSinks(false);
        this.setIgnoreFlowsInSystemPackages(true);
        this.setExcludeSootLibraryClasses(true);
    }

    @Override
    public void merge(InfoflowConfiguration config) {
        super.merge(config);
        if (config instanceof AteConfig) {
            AteConfig androidConfig = (AteConfig) config;
            this.oneComponentAtATime = androidConfig.oneComponentAtATime;

            this.callbackConfig.merge(androidConfig.callbackConfig);
            this.sourceSinkConfig.merge(androidConfig.sourceSinkConfig);
            this.iccConfig.merge(androidConfig.iccConfig);
            this.analysisFileConfig.merge(androidConfig.analysisFileConfig);

            this.sootIntegrationMode = androidConfig.sootIntegrationMode;
            this.mergeDexFiles = androidConfig.mergeDexFiles;
        }
    }

    /**
     * Gets the configuration that defines how callbacks shall be handled
     *
     * @return The configuration of the callback analyzer
     */
    public CallbackConfiguration getCallbackConfig() {
        return callbackConfig;
    }

    /**
     * Gets the configuration of the source/sink manager
     *
     * @return The configuration of the source/sink manager
     */
    public SourceSinkConfiguration getSourceSinkConfig() {
        return sourceSinkConfig;
    }

    /**
     * Gets the configuration for the inter-component data flow analysis
     *
     * @return The configuration for the inter-component data flow analysis
     */
    public IccConfiguration getIccConfig() {
        return this.iccConfig;
    }

    /**
     * Gets the configuration for the input files (target APK, Android platform
     * directory, etc.)
     *
     * @return The input file configuration
     */
    public AnalysisFileConfiguration getAnalysisFileConfig() {
        return analysisFileConfig;
    }

    /**
     * Sets whether FlowDroid shall analyze one component at a time instead of
     * generating one big dummy main method containing all components
     *
     * @param oneComponentAtATime
     *            True if FlowDroid shall analyze one component at a time, otherwise
     *            false
     */
    public void setOneComponentAtATime(boolean oneComponentAtATime) {
        this.oneComponentAtATime = oneComponentAtATime;
    }

    /**
     * Gets whether FlowDroid shall analyze one component at a time instead of
     * generating one biug dummy main method containing all components
     *
     * @return True if FlowDroid shall analyze one component at a time, otherwise
     *         false
     */
    public boolean getOneComponentAtATime() {
        return this.oneComponentAtATime;
    }

    /**
     * Sets how FloweDroid shall interact with the underlying Soot instance.
     * FlowDroid can either set up Soot on its own, or work with an existing
     * instance.
     *
     * @param sootIntegrationMode
     *            The integration mode that FlowDroid shall use
     */
    public void setSootIntegrationMode(SootIntegrationMode sootIntegrationMode) {
        this.sootIntegrationMode = sootIntegrationMode;
    }

    /**
     * Gets how FloweDroid shall interact with the underlying Soot instance.
     * FlowDroid can either set up Soot on its own, or work with an existing
     * instance.
     *
     * @return The integration mode that FlowDroid shall use
     */
    public SootIntegrationMode getSootIntegrationMode() {
        return this.sootIntegrationMode;
    }

    /**
     * Gets whether FlowDroid shall merge all dex files in the APK to get a full
     * picture of the app
     *
     * @return True if FlowDroid shall merge all dex files in the APK, otherwise
     *         false
     */
    public boolean getMergeDexFiles() {
        return this.mergeDexFiles;
    }

    /**
     * Sets whether FlowDroid shall merge all dex files in the APK to get a full
     * picture of the app
     *
     * @param mergeDexFiles
     *            True if FlowDroid shall merge all dex files in the APK, otherwise
     *            false
     */
    public void setMergeDexFiles(boolean mergeDexFiles) {
        this.mergeDexFiles = mergeDexFiles;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((analysisFileConfig == null) ? 0 : analysisFileConfig.hashCode());
        result = prime * result + ((callbackConfig == null) ? 0 : callbackConfig.hashCode());
        result = prime * result + ((iccConfig == null) ? 0 : iccConfig.hashCode());
        result = prime * result + (mergeDexFiles ? 1231 : 1237);
        result = prime * result + (oneComponentAtATime ? 1231 : 1237);
        result = prime * result + ((sootIntegrationMode == null) ? 0 : sootIntegrationMode.hashCode());
        result = prime * result + ((sourceSinkConfig == null) ? 0 : sourceSinkConfig.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        AteConfig other = (AteConfig) obj;
        if (analysisFileConfig == null) {
            if (other.analysisFileConfig != null)
                return false;
        } else if (!analysisFileConfig.equals(other.analysisFileConfig))
            return false;
        if (callbackConfig == null) {
            if (other.callbackConfig != null)
                return false;
        } else if (!callbackConfig.equals(other.callbackConfig))
            return false;
        if (iccConfig == null) {
            if (other.iccConfig != null)
                return false;
        } else if (!iccConfig.equals(other.iccConfig))
            return false;
        if (mergeDexFiles != other.mergeDexFiles)
            return false;
        if (oneComponentAtATime != other.oneComponentAtATime)
            return false;
        if (sootIntegrationMode != other.sootIntegrationMode)
            return false;
        if (sourceSinkConfig == null) {
            if (other.sourceSinkConfig != null)
                return false;
        } else if (!sourceSinkConfig.equals(other.sourceSinkConfig))
            return false;
        return true;
    }


    /**
     * Enumeration containing the callgraph algorithms supported for the use
     * with the data flow tracker
     */
    public enum CallgraphAlgorithm {
        AutomaticSelection, CHA, VTA, RTA, SPARK, GEOM, OnDemand
    }
//    /**
//     * Enumeration containing the supported callback analyzers
//     */
//    public enum CallbackAnalyzer {
//        /**
//         * The highly-precise default analyzer
//         */
//        Default,
//        /**
//         * An analyzer that favors performance over precision
//         */
//        Fast
//    }
//
//    // analysis related configs
//    private CallgraphAlgorithm callgraphAlgorithm = CallgraphAlgorithm.SPARK;
//    private int maxCallbacksPerComponent = 100;
//    private long maxTimeout = 0;
//    private CallbackAnalyzer callbackAnalyzer = CallbackAnalyzer.Fast;
//    private int maxCallbackAnalysisDepth = 0;
//
//    // root directory of the project
//    private String projectPath;
//    private String inputApkPath;
//    private String outputPath = "sootOutput";
//
//    private String iccModelPath;
//
//    // Android OS related configs
//    private String androidSdkPath;
//    private String androidJarPath;
//    private int androidApiLevel = 23;
//    private boolean forceAndroidJar = false;
//
//
//    // Getters and setters for configuration variables
//    /*
//    Project related
//     */
//    /**
//     * Gets the current project working directory
//     * @return The current project working directory
//     */
//    public String getProjectPath(){
//        return projectPath;
//    }
//    /**
//     * Sets the current project working directory
//     * @param projectPath
//     *          The path to current project working directory
//     */
//    public void setProjectPath(String projectPath){
//        this.projectPath = projectPath;
//    }
//
//    /**
//     * Gets the path to the input apk file
//     * @return The path to the input apk file
//     */
//    public String getInputApkPath(){
//        return inputApkPath;
//    }
//    /**
//     * Sets the path to the input apk file
//     * @param inputApkPath
//     *          The path to the input apk file
//     */
//    public void setInputApkPath(String inputApkPath){
//        this.inputApkPath = inputApkPath;
//    }
//
//    /**
//     * Gets the directory for output files
//     * @return The output path
//     */
//    public String getOutputPath(){
//        return outputPath;
//    }
//    /**
//     * Sets the directory for output files
//     * @param outputPath
//     *          The path to the output directory
//     */
//    public void setOutputPath(String outputPath){
//        this.outputPath = outputPath;
//    }
//
//    /**
//     * Gets the ICC model path
//     * @return the ICC model path
//     */
//    public String getIccModelPath() {
//        return iccModelPath;
//    }
//
//    /**
//     * Sets the ICC model path
//     * @param iccModelPath
//     *          The ICC model path to set
//     */
//    public void setIccModelPath(String iccModelPath) {
//        this.iccModelPath = iccModelPath;
//    }
//
//    /**
//     * Gets the Android SDK directory
//     * @return The directory in which Android SDK is located
//     */
//    public String getAndroidSdkPath(){
//        return androidSdkPath;
//    }
//    /**
//     * Sets the Android SDK directory
//     * @param androidSdkPath
//     *          The directory in which Android SDK is located
//     */
//    public void setAndroidSdkPath(String androidSdkPath){
//        this.androidSdkPath = androidSdkPath;
//    }
//
//    /**
//     * Gets the Android JAR Path
//     * @return The Android JAR Path
//     */
//    public String getAndroidJarPath(){
//        return androidJarPath;
//    }
//    /**
//     * Sets the path to Android JAR file
//     * @param androidJarPath
//     *          The path to Android JAR file
//     */
//    public void setAndroidJarPath(String androidJarPath){
//        this.androidJarPath = androidJarPath;
//    }
//
//    /**
//     * Gets the current Android API Level setting
//     * @return The current Android API level setting
//     */
//    public int getAndroidApiLevel(){
//        return androidApiLevel;
//    }
//    /**
//     * Sets the path to Android JAR file
//     * @param androidApiLevel
//     *          The target Android API level
//     */
//    public void setAndroidApiLevel(int androidApiLevel){
//        this.androidApiLevel = androidApiLevel;
//    }
//
//    /**
//     * Gets force android jar setting
//     * @return The current force android jar setting
//     */
//    public boolean getForceAndroidJar() {
//        return forceAndroidJar;
//    }
//    /**
//     * Sets force android jar setting
//     * @param force_android_jar
//     *          The target setting for forcing android jar
//     */
//    public void setForceAndroidJar(boolean force_android_jar) {
//        this.forceAndroidJar = force_android_jar;
//    }
//
//
//    /*
//    Analysis related
//    */
//
//    /**
//     * Gets the maximum number of callbacks modeled for each component
//     * @return The maximum number of callbacks modeled for each component
//     */
//    public int getMaxCallbacksPerComponent() {
//        return maxCallbacksPerComponent;
//    }
//
//    /**
//     * Sets the maximum number of callbacks modeled for each component
//     * @param maxCallbacksPerComponent
//     *          The maximum number of callbacks modeled for each component
//     */
//    public void setMaxCallbacksPerComponent(int maxCallbacksPerComponent) {
//        this.maxCallbacksPerComponent = maxCallbacksPerComponent;
//    }
//
//    /**
//     * Gets the maximum timeout during callback analysis
//     * @return The maximum timeout during callback analysis
//     */
//    public long getMaxTimeout(){
//        return maxTimeout;
//    }
//    /**
//     * Sets the maximum timeout during callback analysis
//     * @param maxTimeout
//     *          The maximum timeout during callback analysis
//     */
//    public void setMaxTimeout(long maxTimeout){
//        this.maxTimeout = maxTimeout;
//    }
//
//    /**
//     * Gets the maximum callback resolve depth during callback analysis
//     * @return The maximum callback resolve depth during callback analysis
//     */
//    public int getMaxCallbackAnalysisDepth(){
//        return maxCallbackAnalysisDepth;
//    }
//    /**
//     * Sets the maximum callback resolve depth during callback analysis
//     * @param maxCallbackAnalysisDepth
//     *          The maximum callback resolve depth during callback analysis
//     */
//    public void setMaxCallbackAnalysisDepth(int maxCallbackAnalysisDepth){
//        this.maxCallbackAnalysisDepth = maxCallbackAnalysisDepth;
//    }
//
//    /**
//     * Gets the type of analyzer used during callback analysis
//     * @return The type of analyzer used during callback analysis
//     */
//    public CallbackAnalyzer getCallbackAnalyzer() {
//        return callbackAnalyzer;
//    }
//
//    /**
//     * Sets the type of analyzer used during callback analysis
//     * @param callbackAnalyzer
//     *          The type of analyzer used during callback analysis
//     */
//    public void setCallbackAnalyzer(CallbackAnalyzer callbackAnalyzer) {
//        this.callbackAnalyzer = callbackAnalyzer;
//    }
//
//
//    // enum parsers
//
//    // TODO remove this section if not in use
//    // Variables for paths to Android and APKs
//    public final static String ANDROID_JAR = "/Users/dulinglai/Library/Android/sdk/platforms/";
//    public final static String APKTOOL_JAR = "/Users/dulinglai/Documents/Study/ResearchProjects/login/scripts/libs/apktool.jar";
//
//    // Results file for debugging
//    public final static String RESULT_FILE = "/Users/dulinglai/Documents/Study/ResearchProjects/login/scripts/oauth_login_detector/tmp/oauth_result.txt";
//    public final static String TEMP_FOLDER = "./tmp/";
}
