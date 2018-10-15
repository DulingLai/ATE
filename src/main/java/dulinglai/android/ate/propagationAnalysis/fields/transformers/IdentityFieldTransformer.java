package dulinglai.android.ate.propagationAnalysis.fields.transformers;

import dulinglai.android.ate.propagationAnalysis.fields.values.FieldValue;

/**
 * The identity field transformer, which does not modify anything.
 */
public class IdentityFieldTransformer extends FieldTransformer {
    private static final IdentityFieldTransformer instance = new IdentityFieldTransformer();

    private IdentityFieldTransformer() {
    }

    @Override
    public FieldValue apply(FieldValue fieldValue) {
        return fieldValue;
    }

    @Override
    public FieldTransformer compose(FieldTransformer secondFieldOperation) {
        return secondFieldOperation;
    }

    @Override
    public String toString() {
        return "identity";
    }

    public static IdentityFieldTransformer v() {
        return instance;
    }
}
