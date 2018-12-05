package dulinglai.android.ate.model.components;

import dulinglai.android.ate.resources.axml.AXmlNode;
import soot.SootClass;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static dulinglai.android.ate.resources.axml.AxmlUtils.processNodeName;

public class AbstractComponent {
    public String name;
    public SootClass mainClass;
    public Set<SootClass> addedClasses;

    public AbstractComponent(AXmlNode node, String packageName) {
        this.name = processNodeName(node, packageName);
    }

    /**
     * Creates the intent filters from the intent filter string
     * @param action list of action intent filters
     * @param category list of category intent filters
     * @return Set of IntentFilters
     */
    Set<IntentFilter> createIntentFilters(List<String> action, List<String> category) {
        Set<IntentFilter> intentFilters = new HashSet<>();
        if (action != null && !action.isEmpty()) {
            for (String actionFilter : action) {
                intentFilters.add(new IntentFilter(actionFilter, IntentFilter.Type.Action));
            }
        }

        if (category != null && !category.isEmpty()) {
            for (String categoryFilter : category) {
                intentFilters.add(new IntentFilter(categoryFilter, IntentFilter.Type.Category));
            }
        }
        return intentFilters;
    }

    /* =======================================
              Getters and setters
     =========================================*/
    /**
     * Gets the name of this component
     * @return The name of this component
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the component
     * @param name The component name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the main SootClass of this service
     * @return The main SootClass
     */
    public SootClass getMainClass() {
        return mainClass;
    }

    /**
     * Sets the main SootClass of this service
     * @param mainClass The main soot class
     */
    public void setMainClass(SootClass mainClass) {
        this.mainClass = mainClass;
    }

    /**
     * Gets the added SootClasses of this service
     * @return The added SootClasses of this service
     */
    public Set<SootClass> getAddedClasses() {
        return addedClasses;
    }

    /**
     * Adds the added SootClasses to this service
     * @param addedClasses The added SootClasses
     */
    public void addAddedClasses(Set<SootClass> addedClasses) {
        if (this.addedClasses == null)
            this.addedClasses = new HashSet<>();
        this.addedClasses.addAll(addedClasses);
    }

    /**
     * Sets the added SootClass of this service
     * @param addedClass The added SootClasses
     */
    public void setAddedClass(SootClass addedClass) {
        if (this.addedClasses == null)
            this.addedClasses = new HashSet<>();
        this.addedClasses.add(addedClass);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((mainClass == null) ? 0 : mainClass.hashCode());
        result = prime * result + ((addedClasses == null) ? 0 : addedClasses.hashCode());
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

        AbstractComponent other = (AbstractComponent) obj;
        if (mainClass == null) {
            if (other.mainClass != null)
                return false;
        } else if (!mainClass.equals(other.mainClass))
            return false;
        if (addedClasses == null) {
            if (other.addedClasses != null)
                return false;
        } else if (!addedClasses.equals(other.addedClasses))
            return false;
        if (name == null) {
            return other.name == null;
        } else return name.equals(other.name);
    }
}
