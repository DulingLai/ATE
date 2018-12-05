package dulinglai.android.ate.model.components;

import dulinglai.android.ate.resources.axml.AXmlNode;

import static dulinglai.android.ate.resources.axml.AxmlUtils.processAuthorities;

public class ContentProvider extends AbstractComponent {
    private String authorities;

    public ContentProvider(AXmlNode node, String packageName) {
        super(node, packageName);
        this.authorities = processAuthorities(node);
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
        int result = super.hashCode();
        result = prime * result + ((authorities == null) ? 0 : authorities.hashCode());
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

        ContentProvider other = (ContentProvider) obj;

        if (name == null) {
            return other.name == null;
        }
        if (authorities == null) {
            return  other.authorities == null;
        } else if (other.authorities == null) {
            return false;
        } else {
            if (!authorities.equals(other.authorities))
                return false;
            return name.equals(other.name);
        }
    }
}
