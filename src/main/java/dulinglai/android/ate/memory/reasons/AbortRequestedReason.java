package dulinglai.android.ate.memory.reasons;

import dulinglai.android.ate.memory.ISolverTerminationReason;

/**
 * Reason for terminating a data flow solver after an external request
 * 
 * @author Steven Arzt
 *
 */
public class AbortRequestedReason implements ISolverTerminationReason {

	@Override
	public ISolverTerminationReason combine(ISolverTerminationReason terminationReason) {
		return new MultiReason(this, terminationReason);
	}

}
