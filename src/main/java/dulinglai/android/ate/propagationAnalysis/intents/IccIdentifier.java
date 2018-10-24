package dulinglai.android.ate.propagationAnalysis.intents;

import soot.SootClass;
import soot.SootMethod;
import soot.Unit;

import java.util.Objects;

public class IccIdentifier {
    private final Unit stmt;
    private final SootMethod method;
    private final SootClass clazz;

    public IccIdentifier(Unit stmt, SootMethod method, SootClass clazz) {
        this.stmt = stmt;
        this.method = method;
        this.clazz = clazz;
    }

    /**
     * Gets the ICC statement
     * @return The ICC statement (e.g. startActivity)
     */
    public Unit getStmt() {
        return stmt;
    }

    /**
     * Gets the ICC method that encapsulate the statement
     * @return The ICC method
     */
    public SootMethod getMethod() {
        return method;
    }

    /**
     * Gets the class of the ICC statement
     * @return The class of the ICC statement
     */
    public SootClass getClazz() {
        return clazz;
    }

    @Override
    public int hashCode() {
        return Objects.hash(stmt, method, clazz);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof IccIdentifier) {
            IccIdentifier iccIdentifier = (IccIdentifier) other;
            return Objects.equals(this.stmt, iccIdentifier.stmt)
                    && Objects.equals(this.method, iccIdentifier.method)
                    && Objects.equals(this.clazz, iccIdentifier.clazz);
        }
        return false;
    }

}
