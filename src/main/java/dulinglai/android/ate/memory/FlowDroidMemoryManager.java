package dulinglai.android.ate.memory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import dulinglai.android.ate.data.soot.AccessPath;
import org.pmw.tinylog.Logger;

/**
 * Memory manager implementation for FlowDroid
 * 
 * @author Steven Arzt
 *
 */
public class FlowDroidMemoryManager {
	private ConcurrentMap<AccessPath, AccessPath> apCache = new ConcurrentHashMap<>();
	private AtomicInteger reuseCounter = new AtomicInteger();

	private final boolean tracingEnabled;
	private final PathDataErasureMode erasePathData;
	private boolean useAbstractionCache = false;

	/**
	 * Supported modes that define which path tracking data shall be erased and
	 * which shall be kept
	 */
	public enum PathDataErasureMode {
		/**
		 * Keep all path tracking data.
		 */
		EraseNothing,
		/**
		 * Keep only those path tracking items that are necessary for context- sensitive
		 * path reconstruction.
		 */
		KeepOnlyContextData,
		/**
		 * Erase all path tracking data.
		 */
		EraseAll
	}

	/**
	 * Constructs a new instance of the AccessPathManager class
	 */
	public FlowDroidMemoryManager() {
		this(false, PathDataErasureMode.EraseNothing);
	}

	/**
	 * Constructs a new instance of the AccessPathManager class
	 * 
	 * @param tracingEnabled True if performance tracing data shall be recorded
	 * @param erasePathData  Specifies whether data for tracking paths (current
	 *                       statement, corresponding call site) shall be erased.
	 */
	public FlowDroidMemoryManager(boolean tracingEnabled, PathDataErasureMode erasePathData) {
		this.tracingEnabled = tracingEnabled;
		this.erasePathData = erasePathData;

		Logger.info("Initializing memory manager...");
		if (this.tracingEnabled)
			Logger.info("FDMM: Tracing enabled. This may negatively affect performance.");
		if (this.erasePathData != PathDataErasureMode.EraseNothing)
			Logger.info("FDMM: Path data erasure enabled");
	}

	/**
	 * Gets the cached equivalent of the given access path
	 * 
	 * @param ap The access path for which to get the cached equivalent
	 * @return The cached equivalent of the given access path
	 */
	private AccessPath getCachedAccessPath(AccessPath ap) {
		AccessPath oldAP = apCache.putIfAbsent(ap, ap);
		if (oldAP == null)
			return ap;

		// We can re-use an old access path
		if (tracingEnabled && oldAP != ap)
			reuseCounter.incrementAndGet();
		return oldAP;
	}

}
