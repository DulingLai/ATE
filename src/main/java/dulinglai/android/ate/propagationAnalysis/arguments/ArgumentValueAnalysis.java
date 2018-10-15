package dulinglai.android.ate.propagationAnalysis.arguments;

import soot.Unit;
import soot.Value;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;

import java.util.Collections;
import java.util.Set;

/**
 * An argument value analysis. Subclasses indicate how to analyze a certain argument type. They
 * should override:
 * <ul>
 * <li>{@link #computeVariableValues(Value, Stmt)} to compute the argument value at a given
 * statement for a given Value.</li>
 * <li>{@link #computeInlineArgumentValues(String[])} to compute an inline argument value from a
 * string representation.</li>
 * <li>{@link #getTopValue()} to indicate how an unknown argument value should be represented.</li>
 * </ul>
 *
 * It is also possible to override {@link #computeArgumentValues(Argument, Unit)} if the argument
 * value computation requires access to the corresponding {@link Argument} object.
 */
public abstract class ArgumentValueAnalysis {
    /**
     * Computes the possible argument values for a given statement and a given argument.
     *
     * By default this simply calls {@link #computeArgumentValues(Argument, Unit)}.
     *
     * @param argument An {@link Argument}.
     * @param callSite A call statement.
     * @return The set of possible values for the argument.
     */
    public Set<Object> computeArgumentValues(Argument argument, Unit callSite) {
        if (argument.getArgnum() == null) {
            return null;
        }

        Stmt stmt = (Stmt) callSite;
        if (!stmt.containsInvokeExpr()) {
            throw new RuntimeException("Statement " + stmt + " does not contain an invoke expression");
        }
        InvokeExpr invokeExpr = stmt.getInvokeExpr();
        int argnum = argument.getArgnum()[0];
        Value value;
        if (argnum == -1) {
            if (invokeExpr instanceof InstanceInvokeExpr) {
                value = ((InstanceInvokeExpr) invokeExpr).getBase();
            } else {
                throw new RuntimeException("Invoke expression has no base: " + invokeExpr);
            }
        } else {
            value = stmt.getInvokeExpr().getArg(argnum);
        }

        return computeVariableValues(value, stmt);
    }

    /**
     * Computes the possible values of a variable at a given statement.
     *
     * @param value The variable for which possible values should be computed.
     * @param callSite A call statement.
     * @return The set of possible values for the variable.
     */
    public abstract Set<Object> computeVariableValues(Value value, Stmt callSite);

    /**
     * Computes a set of inline values from a string representation.
     *
     * @param inlineValues An array of strings.
     * @return The set of inline values.
     */
    public abstract Set<Object> computeInlineArgumentValues(String[] inlineValues);

    /**
     * Returns the representation of an unknown argument value.
     *
     * @return The representation of an unknown argument value.
     */
    public abstract Object getTopValue();
}
