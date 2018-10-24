package dulinglai.android.ate.entryPointCreators.components;

import dulinglai.android.ate.resources.androidConstants.ComponentLifecycleConstants;
import soot.SootClass;
import soot.jimple.Jimple;
import soot.jimple.NopStmt;
import soot.jimple.Stmt;

/**
 * Entry point creator for Android broadcast receiverNodes
 * 
 * @author Steven Arzt
 *
 */
public class BroadcastReceiverEntryPointCreator extends AbstractComponentEntryPointCreator {

	public BroadcastReceiverEntryPointCreator(SootClass component, SootClass applicationClass) {
		super(component, applicationClass);
	}

	@Override
	protected void generateComponentLifecycle() {
		Stmt onReceiveStmt = searchAndBuildMethod(ComponentLifecycleConstants.BROADCAST_ONRECEIVE, component, thisLocal);

		// methods
		NopStmt startWhileStmt = Jimple.v().newNopStmt();
		NopStmt endWhileStmt = Jimple.v().newNopStmt();
		body.getUnits().add(startWhileStmt);
		createIfStmt(endWhileStmt);

		addCallbackMethods();

		body.getUnits().add(endWhileStmt);
		createIfStmt(onReceiveStmt);
	}

}
