package dulinglai.android.ate.memory;

import java.util.Set;

import dulinglai.android.ate.data.soot.ConcurrentHashSet;
import dulinglai.android.ate.memory.MemoryWarningSystem.OnMemoryThresholdReached;
import dulinglai.android.ate.memory.reasons.OutOfMemoryReason;
import org.pmw.tinylog.Logger;

/**
 * FlowDroid's implementation of a handler for the memory warning system
 * 
 * @author Steven Arzt
 *
 */
public class MemoryWatcher {
	private final MemoryWarningSystem warningSystem = new MemoryWarningSystem();

	private final Set<IMemoryBoundedSolver> solvers = new ConcurrentHashSet<>();


	/**
	 * Creates a new instance of the {@link MemoryWatcher} class
	 *
	 */
	public MemoryWatcher() {
		// Register ourselves in the warning system
		warningSystem.addListener(new OnMemoryThresholdReached() {

			@Override
			public void onThresholdReached(long usedMemory, long maxMemory) {
				// We stop the data flow analysis
				forceTerminate();
				Logger.warn("Running out of memory, solvers terminated");
			}

		});
		MemoryWarningSystem.setWarningThreshold(0.9d);
	}

	/**
	 * Adds a solver that shall be terminated when the memory threshold is
	 * reached
	 * 
	 * @param solver
	 *            A solver that shall be terminated when the memory threshold is
	 *            reached
	 */
	public void addSolver(IMemoryBoundedSolver solver) {
		this.solvers.add(solver);
	}

	/**
	 * Removes the given solver from the watch list. The given solver will no
	 * longer ne notified when the memory threshold is reached.
	 * 
	 * @param solver
	 *            The solver to remove from the watch list
	 * @return True if the given solver was found in the watch list, otherwise
	 *         false
	 */
	public boolean removeSolver(IMemoryBoundedSolver solver) {
		return this.solvers.remove(solver);
	}

	/**
	 * Clears the list of solvers registered with this memory watcher
	 */
	public void clearSolvers() {
		this.solvers.clear();
	}

	/**
	 * Shuts down the memory watcher and frees all resources associated with it
	 */
	public void close() {
		clearSolvers();
		warningSystem.close();
	}

	/**
	 * Forces the termination of all registered solvers
	 */
	public void forceTerminate() {
		Runtime runtime = Runtime.getRuntime();
		long usedMem = runtime.totalMemory() - runtime.freeMemory();
		forceTerminate(new OutOfMemoryReason(usedMem));
	}

	/**
	 * Forces the termination of all registered solvers
	 */
	public void forceTerminate(ISolverTerminationReason reason) {
		for (IMemoryBoundedSolver solver : solvers)
			solver.forceTerminate(reason);
	}

}
