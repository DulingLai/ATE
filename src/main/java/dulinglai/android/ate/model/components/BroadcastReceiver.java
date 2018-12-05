package dulinglai.android.ate.model.components;

import dulinglai.android.ate.resources.axml.AXmlNode;

public class BroadcastReceiver extends AbstractComponent {


    public BroadcastReceiver(AXmlNode node, String packageName) {
        super(node, packageName);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        return super.hashCode();
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

        BroadcastReceiver other = (BroadcastReceiver) obj;

        if (name == null) {
            return other.name == null;
        } else if (other.name == null) {
            return false;
        } else return name.equals(other.name);
    }
}
