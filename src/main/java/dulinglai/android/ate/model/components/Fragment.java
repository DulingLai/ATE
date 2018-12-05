package dulinglai.android.ate.model.components;

import dulinglai.android.ate.model.widgets.AbstractWidget;
import soot.SootClass;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Fragment {

    private final SootClass sootClass;
    private final String name;

    private Set<Listener> listeners;
    private Set<AbstractWidget> widgets;

    private Integer resourceId;
    private Set<Activity> parentActivities;

    public Fragment(SootClass sootClass, Activity parentActivity) {
        this.name = sootClass.getName();
        this.sootClass = sootClass;
        this.parentActivities = new HashSet<>(Collections.singletonList(parentActivity));
    }

    public Fragment(SootClass sootClass) {
        this.name = sootClass.getName();
        this.sootClass = sootClass;
    }

    /*
    Getters and setters
     */
    public SootClass getSootClass() {
        return sootClass;
    }

    public Set<Listener> getListeners() {
        return listeners;
    }

    public Set<AbstractWidget> getWidgets() {
        return widgets;
    }

    public Integer getResourceId() {
        return resourceId;
    }

    public Set<Activity> getParentActivities() {
        return parentActivities;
    }

    /**
     * Adds a listener to the fragment
     * @param listener The listener to be added
     */
    public void addListener(Listener listener) {
        this.listeners.add(listener);
    }

    /**
     * Adds a widget to the fragment
     * @param widget The widget to be added
     */
    public void addWidget(AbstractWidget widget) {
        this.widgets.add(widget);
    }

    /**
     * Sets the resource id of the fragment
     * @param resourceId The resource id of the fragment
     */
    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }

    /**
     * Adds the parent activity to the fragment. This method adds another activity
     * @param parentActivity The parent activity
     */
    public void AddParentActivity(Activity parentActivity) {
        this.parentActivities.add(parentActivity);
    }

    /**
     * Gets the name of the fragment
     * @return The name of this fragment (String)
     */
    public String getName() {
        return name;
    }

    @Override
    public String toString(){
        return sootClass.getName();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((sootClass == null) ? 0 : sootClass.hashCode());
        result = prime * result + ((listeners == null) ? 0 : listeners.hashCode());
        result = prime * result + ((widgets == null) ? 0 : widgets.hashCode());
        result = prime * result + ((resourceId == null) ? 0 : resourceId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;

        Fragment other = (Fragment) obj;

        if (listeners == null) {
            if (other.listeners != null)
                return false;
        } else if (!listeners.equals(other.listeners))
            return false;

        if (parentActivities == null) {
            if (other.parentActivities != null)
                return false;
        } else if (!parentActivities.equals(other.parentActivities))
            return false;

        if (widgets == null) {
            if (other.widgets != null)
                return false;
        } else if (!widgets.equals(other.widgets))
            return false;

        if (!resourceId.equals(other.resourceId))
            return false;
        return sootClass.equals(other.sootClass);
    }
}
