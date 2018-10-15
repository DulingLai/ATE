package dulinglai.android.ate.propagationAnalysis;

import dulinglai.android.ate.propagationAnalysis.values.BasePropagationValue;
import heros.solver.IDESolver;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.toolkits.ide.icfg.JimpleBasedInterproceduralCFG;

/**
 * A solver for the MVMF constant propagation problem.
 */
public class PropagationSolver extends
        IDESolver<Unit, Value, SootMethod, BasePropagationValue, JimpleBasedInterproceduralCFG> {

    public PropagationSolver(PropagationProblem propagationProblem) {
        super(propagationProblem);
    }

}
