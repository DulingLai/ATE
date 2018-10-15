package dulinglai.android.ate.propagationAnalysis.arguments;

import java.io.Serializable;

public class Field implements Serializable {
    private String name;
    private String type;

    /**
     * Constructor.
     *
     * @param name The field name.
     * @param type The field type, which should be one of the types registered using
     *          {@link ArgumentValueManager#registerArgumentValueAnalysis}.
     */
    public Field(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return name + " (" + type + ")";
    }
}
