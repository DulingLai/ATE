package dulinglai.android.ate.model.entities;

import org.apache.commons.lang.StringUtils;

import java.util.HashSet;
import java.util.Set;

public abstract class Entity {

    protected String name;

    // TODO: remove text and text reg if we do not need those info
    protected Set<String> text = new HashSet<>();
    protected Set<String> textReg = new HashSet<>();
    protected boolean shouldRunOnInitMethod;

    public Entity(String reg) {
        name = reg;
        shouldRunOnInitMethod = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return String.join("#", text);
    }

    public void addText(String ptext) {
        if (!StringUtils.isBlank(ptext))
            text.add(ptext);
    }

    public String toString() {
        return "Name: " + name + "; text: " + text;
    }

    public Set<String> getTextReg() {
        return textReg;
    }

    public void addTextReg(String textReg) {
        this.textReg.add(textReg);
    }

    public void removeTextReg(String ptextReg) {
        textReg.remove(ptextReg);
    }

    public void setTextReg(Set<String> textReg) {
        this.textReg = textReg;
    }

    public void setText(Set<String> texts) {
        text.addAll(texts);
    }

    public abstract Entity clone();

    public abstract boolean allValuesFound();

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Entity other = (Entity) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (text == null) {
            if (other.text != null)
                return false;
        } else if (!text.equals(other.text))
            return false;
        if (textReg == null) {
            return other.textReg == null;
        } else return textReg.equals(other.textReg);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((text == null) ? 0 : text.hashCode());
        result = prime * result + ((textReg == null) ? 0 : textReg.hashCode());
        return result;
    }
}
