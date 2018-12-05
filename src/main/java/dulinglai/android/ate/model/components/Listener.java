package dulinglai.android.ate.model.components;

import dulinglai.android.ate.analyzers.CallbackDefinition;
import soot.SootClass;

import java.util.Set;

public class Listener {
    private SootClass sootClass;
    private AbstractComponent ownerComp;
    private Set<CallbackDefinition> callbackMethods;

    public Listener(AbstractComponent ownerComp, Set<CallbackDefinition> callbackMethods) {
        this.ownerComp = ownerComp;
        this.callbackMethods = callbackMethods;
    }

    public AbstractComponent getOwnerComp(){
        return ownerComp;
    }

    public Set<CallbackDefinition> getCallbackMethods(){
        return callbackMethods;
    }
}
