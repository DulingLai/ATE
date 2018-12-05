package dulinglai.android.ate.data.android;

import dulinglai.android.ate.resources.androidConstants.ComponentLifecycleConstants;
import soot.Scene;
import soot.SootClass;

public class AndroidClass {

    public static final String SIG_CAR_CREATE = "<android.car.Car: android.car.Car createCar(android.content.Context,android.content.ServiceConnection)>";
    public final SootClass scContext;
    public final SootClass scBroadcastReceiver;
    public final SootClass scServiceConnection;

    public final SootClass scFragmentTransaction;
    public final SootClass scSupportFragmentTransaction;
    public final SootClass scFragmentManager;
    public final SootClass scSupportFragmentManager;
    public final SootClass scFragment;
    public final SootClass scSupportFragment;


    private static AndroidClass instance;

    private AndroidClass(){
        scContext = Scene.v().getSootClassUnsafe("android.content.Context");
        scBroadcastReceiver = Scene.v().getSootClassUnsafe(ComponentLifecycleConstants.BROADCASTRECEIVERCLASS);
        scServiceConnection = Scene.v().getSootClassUnsafe(ComponentLifecycleConstants.SERVICECONNECTIONINTERFACE);

        scFragmentTransaction = Scene.v().getSootClassUnsafe("android.app.FragmentTransaction");
        scFragmentManager = Scene.v().getSootClassUnsafe("android.app.FragmentManager");
        scFragment = Scene.v().getSootClassUnsafe(ComponentLifecycleConstants.FRAGMENTCLASS);

        scSupportFragmentTransaction = Scene.v().getSootClassUnsafe("android.support.v4.app.FragmentTransaction");
        scSupportFragmentManager = Scene.v().getSootClassUnsafe("android.support.v4.app.FragmentManager");
        scSupportFragment = Scene.v().getSootClassUnsafe(ComponentLifecycleConstants.SUPPORTFRAGMENTCLASS);
    }

    public static synchronized AndroidClass v() {
        if (instance == null) {
            instance = new AndroidClass();
        }
        return instance;
    }
}
