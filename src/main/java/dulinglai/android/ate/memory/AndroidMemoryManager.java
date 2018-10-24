package dulinglai.android.ate.memory;

import java.util.Set;

import soot.RefType;
import soot.SootClass;
import soot.Type;

/**
 * Specialized implementation of the memory manager interface for Android
 *
 * @author Steven Arzt
 *
 */
public class AndroidMemoryManager extends FlowDroidMemoryManager {

    private final Set<SootClass> components;
    private boolean componentFilterApplied = false;

    /**
     * Creates a new instance of the {@link AndroidMemoryManager} class
     *
     * @param tracingEnabled True if performance tracing data shall be recorded
     * @param erasePathData  Specifies whether data for tracking paths (current
     *                       statement, corresponding call site) shall be erased.
     * @param components     The set of classes that are Android components
     */
    public AndroidMemoryManager(boolean tracingEnabled,
                                PathDataErasureMode erasePathData,
                                Set<SootClass> components) {
        super(tracingEnabled, erasePathData);
        this.components = components;
    }

    /**
     * Checks whether the given type should be filtered out from the taint analysis
     *
     * @param tp The type to check
     * @return True if the given type shall be filtered out from the taint analysis,
     * otherwise false
     */
    private boolean isFilteredSystemType(Type tp) {
		/*
		if (tp != null && tp instanceof RefType) {
			RefType rt = (RefType) tp;
			if (rt.getSootClass().getName().equals("android.view.View"))
				return true;
		}
		*/
        return false;
    }

    /**
     * Checks whether the given type refers to an Android component
     *
     * @param tp The type to check
     * @return True if the given type refers to one of the components declared
     * in the app, otherwise false
     */
    private boolean isComponentType(Type tp) {
        if (tp != null && tp instanceof RefType) {
            RefType rt = (RefType) tp;
            if (components.contains(rt.getSootClass())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets whether taints have been filtered out because they tainted whole
     * components
     *
     * @return True if taints that referenced whole components were filtered
     * out, otherwise false
     */
    public boolean getComponentFilterApplied() {
        return this.componentFilterApplied;
    }
}
