package dulinglai.android.ate.resources.resources.controls;

import dulinglai.android.ate.resources.axml.AXmlAttribute;
import dulinglai.android.ate.resources.axml.flags.InputType;
import pxb.android.axml.AxmlVisitor;
import soot.SootClass;

import java.util.Collection;
import java.util.Map;

/**
 * EditText control in Android
 *
 * @author Steven Arzt
 *
 */
public class EditTextControl extends AndroidLayoutControl {

    private int inputType;
    private boolean isPassword;
    private String contentDescription;
    private String hint;
    private Collection<Integer> inputTypeFlags;

    EditTextControl(SootClass viewClass) {
        super(viewClass);
    }

    public EditTextControl(int id, SootClass viewClass) {
        super(id, viewClass);
    }

    public EditTextControl(int id, SootClass viewClass, Map<String, Object> additionalAttributes) {
        super(id, viewClass, additionalAttributes);
    }

    /**
     * Sets the type of this input (text, password, etc.)
     *
     * @param inputType
     *            The input type
     */
    void setInputType(int inputType) {
        this.inputType = inputType;
        this.inputTypeFlags = InputType.getFlags(inputType);
    }

    /**
     * Returns true if the input satiesfies all specified types
     * @see InputType
     * @param type the types to check
     */
    public boolean satisfiesInputType(int... type) {
        if (inputTypeFlags == null)
            return false;
        for (int i : type)
            if (!inputTypeFlags.contains(i))
                return false;
        return true;
    }

    /**
     * Gets the type of this input (text, password, etc.)
     *
     * @return The input type
     */
    public int getInputType() {
        return inputType;
    }

    /**
     * Gets the text of this edit control
     *
     * @return The text of this edit control
     */
    public String getText() {
        return text;
    }

    /**
     * Gets the content description associated with this edit control
     * @return The content description
     */
    public String getContentDescription() {
        return contentDescription;
    }

    /**
     * Gets the hint associated with this edit control
     * @return The hint
     */
    public String getHint() {
        return hint;
    }

    @Override
    protected void handleAttribute(AXmlAttribute<?> attribute, boolean loadOptionalData) {
        final String attrName = attribute.getName().trim();
        final int type = attribute.getType();

        // Collect attributes of this widget
        if (attrName.equals("id")) {
            if (type == AxmlVisitor.TYPE_INT_HEX)
                id = (Integer) attribute.getValue();
        }
        else if (attrName.equals("inputType") && type == AxmlVisitor.TYPE_INT_HEX) {
            setInputType((Integer) attribute.getValue());
        }
        else if (attrName.equals("password")) {
            if (type == AxmlVisitor.TYPE_INT_HEX)
                isPassword = ((Integer) attribute.getValue()) != 0; // -1 for
                // true, 0
                // for false
            else if (type == AxmlVisitor.TYPE_INT_BOOLEAN)
                isPassword = (Boolean) attribute.getValue();
            else
                throw new RuntimeException("Unknown representation of boolean data type");
        }
        else if (attrName.equals("text")) {
            if (type == AxmlVisitor.TYPE_INT_HEX) {
                text = String.valueOf(attribute.getValue());
            } else if (type == AxmlVisitor.TYPE_STRING)
                text = (String) attribute.getValue();
        }
        else if (attrName.equals("contentDescription")) {
            if (type == AxmlVisitor.TYPE_INT_HEX)
                contentDescription = String.valueOf(attribute.getValue());
            else if (type == AxmlVisitor.TYPE_STRING)
                contentDescription = (String) attribute.getValue();
        }
        else if (attrName.equals("hint")){
            if (type == AxmlVisitor.TYPE_INT_HEX)
                hint = String.valueOf(attribute.getValue());
            else if (type == AxmlVisitor.TYPE_STRING)
                hint = (String) attribute.getValue();
        }
        else
            super.handleAttribute(attribute, loadOptionalData);
    }

    public boolean isSensitive() {
        if (isPassword)
            return true;

        if (satisfiesInputType(InputType.numberPassword))
            return true;
        if (satisfiesInputType(InputType.textVisiblePassword))
            return true;
        if (satisfiesInputType(InputType.textWebPassword))
            return true;
        if (satisfiesInputType(InputType.textPassword))
            return true;

        return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + inputType;
        result = prime * result + (isPassword ? 1231 : 1237);
        result = prime * result + ((text == null) ? 0 : text.hashCode());
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
        EditTextControl other = (EditTextControl) obj;
        if (inputType != other.inputType)
            return false;
        if (isPassword != other.isPassword)
            return false;
        if (text == null) {
            return other.text == null;
        } else return text.equals(other.text);
    }
}
