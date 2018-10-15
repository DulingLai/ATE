package dulinglai.android.ate.propagationAnalysis.intents;

import java.util.Map;
import java.util.Set;

public class Ic3Config {
    private String model;
    private String compiledModel;
    private String classpath;
    private String input;
    private String output;
    private boolean traverseModeled = false;

    private String packageName;
    private Map<String, Set<String>> callbackMethods;
    private Set<String> entryPointClasses;

    /**
     * Gets the path to the directory containing the model.
     *
     * @return The path to the model.
     */
    public String getModel() {
        return model;
    }

    /**
     * Sets the path to the directory containing the model. This should be a single directory
     * containing all COAL specifications.
     *
     * @param model The path to the model.
     *
     * @see #getModel()
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * Gets the path to the compiled model.
     *
     * @return The path to the compiled model.
     *
     * @see #setCompiledModel
     */
    public String getCompiledModel() {
        return compiledModel;
    }

    /**
     * Sets the path to the compiled model.
     *
     * @param compiledModel The path to the compiled model.
     *
     * @see #getCompiledModel
     */
    public void setCompiledModel(String compiledModel) {
        this.compiledModel = compiledModel;
    }

    /**
     * Gets the classpath for the analysis.
     *
     * @return The classpath for the analysis.
     *
     * @see #setClasspath
     */
    public String getClasspath() {
        return classpath;
    }

    /**
     * Sets the classpath for the analysis. The directories and jar files on the classpath should
     * contain all classes that are referenced by the input classes but through which propagation
     * should not be performed. For example, this includes library or framework classes. Multiple
     * paths should be separated with <code>:</code>.
     *
     * @param classpath The classpath for the analysis.
     *
     * @see #getClasspath
     */
    public void setClasspath(String classpath) {
        this.classpath = classpath;
    }

    /**
     * Gets the input directory or file for the analysis.
     *
     * @return The input directory or file for the analysis.
     *
     * @see #setInput
     */
    public String getInput() {
        return input;
    }

    /**
     * Sets the input directories or files for the analysis. They should contain all classes through
     * which the propagation should be performed. Multiple entries should be separated with
     * <code>:</code>.
     *
     * @param input The input directory or file
     *
     * @see #getInput
     */
    public void setInput(String input) {
        this.input = input;
    }

    /**
     * Gets the output directory or file for the analysis.
     *
     * @return The output directory or file
     *
     * @see #setOutput
     */
    public String getOutput() {
        return output;
    }

    /**
     * Sets the output directory or file for the analysis.
     *
     * @param output The output directory or file.
     *
     * @see #getOutput
     */
    public void setOutput(String output) {
        this.output = output;
    }

    /**
     * Sets the flag that determines if propagation should be done through the modeled classes.
     *
     * @param traverseModeled The value of the flag.
     */
    public void setTraverseModeled(boolean traverseModeled) {
        this.traverseModeled = traverseModeled;
    }

    /**
     * Determines whether the propagation should be done through the modeled classes. This is
     * equivalent to adding the modeled classes to the list of analysis classes.
     *
     * @return True if the propagation should be performed through the analysis classes.
     */
    public boolean traverseModeled() {
        return traverseModeled;
    }

    public String getPackageName() {
        return packageName;
    }

    public Map<String, Set<String>> getCallbackMethods() {
        return callbackMethods;
    }

    public Set<String> getEntryPointClasses() {
        return entryPointClasses;
    }
}
