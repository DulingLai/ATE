package dulinglai.android.ate.infoflow;

import soot.SootMethod;
import soot.jimple.infoflow.IInfoflow;
import soot.jimple.infoflow.sourcesSinks.manager.ISourceSinkManager;

/**
 * Common interface for specialized versions of the {@link soot.jimple.infoflow.Infoflow} class that
 * allows the data flow analysis to be run inside an existing Soot instance
 */
public interface IInPlaceInfoflow extends IInfoflow {

    public void runAnalysis(final ISourceSinkManager sourcesSinks, SootMethod entryPoint);

}
