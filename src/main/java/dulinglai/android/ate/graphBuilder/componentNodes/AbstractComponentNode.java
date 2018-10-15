package dulinglai.android.ate.graphBuilder.componentNodes;

import dulinglai.android.ate.resources.axml.AXmlNode;

import static dulinglai.android.ate.graphBuilder.NodeUtils.processNodeName;

public class AbstractComponentNode {
    public String name;

    public AbstractComponentNode(AXmlNode node, String packageName) {
        this.name = processNodeName(node, packageName);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
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

        AbstractComponentNode other = (AbstractComponentNode) obj;
        if (name == null) {
            return other.name == null;
        } else return name.equals(other.name);
    }
}
