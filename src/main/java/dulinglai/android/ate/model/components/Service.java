package dulinglai.android.ate.model.components;

import dulinglai.android.ate.resources.axml.AXmlNode;

import java.util.HashSet;
import java.util.Set;

import static dulinglai.android.ate.resources.axml.AxmlUtils.processIntentFilter;

public class Service extends AbstractComponent {
    private Set<IntentFilter> intentFilters;
    private Set<Listener> listeners;

    public Service(AXmlNode node, String packageName) {
        super(node, packageName);
        this.intentFilters = createIntentFilters(processIntentFilter(node, IntentFilter.Type.Action),
                processIntentFilter(node, IntentFilter.Type.Category));

        listeners = new HashSet<>();
    }

    /* ==================================================
                    Getters and setters
       ==================================================*/
    /**
     * Gets the intent filters
     * @return All intent filters
     */
    public Set<IntentFilter> getIntentFilters() {
        return intentFilters;
    }

    /**
     * Adds intent filters to this service
     * @param intentFilters Intent filters to be added
     */
    public void addIntentFilter(Set<IntentFilter> intentFilters) {
        this.intentFilters.addAll(intentFilters);
    }

    /**
     * Adds an intent filter to this service
     * @param intentFilter Intent filter to be added
     */
    public void addIntentFilter(IntentFilter intentFilter) {
        this.intentFilters.add(intentFilter);
    }

    /**
     * Gets the listeners
     * @return All listeners implemented in the service
     */
    public Set<Listener> getListeners() {
        return listeners;
    }

    /**
     * Adds listeners to this service
     * @param listeners The listeners to be added
     */
    public void addListeners(Set<Listener> listeners) {
        this.listeners.addAll(listeners);
    }

    /**
     * Adds a listener to this service
     * @param listener THe listener to be added
     */
    public void addListener(Listener listener) {
        this.listeners.add(listener);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((intentFilters == null) ? 0 : intentFilters.hashCode());
        result = prime * result + ((listeners == null) ? 0 : listeners.hashCode());
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

        Service other = (Service) obj;
        if (intentFilters == null) {
            if (other.intentFilters != null)
                return false;
        } else if (!intentFilters.equals(other.intentFilters))
            return false;
        if (listeners == null) {
            if (other.listeners != null)
                return false;
        } else if (!listeners.equals(other.listeners))
            return false;
        if (name == null) {
            return other.name == null;
        } else
        return name.equals(other.name);
    }
}
