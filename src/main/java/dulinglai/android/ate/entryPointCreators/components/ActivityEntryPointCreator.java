package dulinglai.android.ate.entryPointCreators.components;

import dulinglai.android.ate.entryPointCreators.SimulatedCodeElementTag;
import dulinglai.android.ate.resources.androidConstants.ComponentLifecycleConstants;
import dulinglai.android.ate.utils.androidUtils.LibraryClassPatcher;
import heros.TwoElementSet;
import soot.*;
import soot.javaToJimple.LocalGenerator;
import soot.jimple.*;
import soot.util.MultiMap;

import java.util.*;

/**
 * Entry point creator for Android activityNodes
 *
 * @author Steven Arzt
 */
public class ActivityEntryPointCreator extends AbstractComponentEntryPointCreator {

    private final MultiMap<SootClass, String> activityLifecycleCallbacks;
    private final Map<SootClass, SootField> callbackClassToField;
    private final Set<SootClass> fragmentClasses;

    protected SootField resultIntentField = null;

    public ActivityEntryPointCreator(SootClass component, SootClass applicationClass,
                                     MultiMap<SootClass, String> activityLifecycleCallbacks, Set<SootClass> fragmentClasses,
                                     Map<SootClass, SootField> callbackClassToField) {
        super(component, applicationClass);
        this.activityLifecycleCallbacks = activityLifecycleCallbacks;
        this.fragmentClasses = fragmentClasses;
        this.callbackClassToField = callbackClassToField;
    }

    @Override
    protected void generateComponentLifecycle() {
        Set<SootClass> currentClassSet = Collections.singleton(component);
        final Body body = mainMethod.getActiveBody();

        Set<SootClass> referenceClasses = new HashSet<>();
        if (applicationClass != null)
            referenceClasses.add(applicationClass);
        if (this.activityLifecycleCallbacks != null)
            for (SootClass callbackClass : this.activityLifecycleCallbacks.keySet())
                referenceClasses.add(callbackClass);
        if (this.fragmentClasses != null)
            for (SootClass callbackClass : this.fragmentClasses)
                referenceClasses.add(callbackClass);
        referenceClasses.add(component);

        // Get the application class
        Local applicationLocal = null;
        if (applicationClass != null) {
            applicationLocal = generator.generateLocal(RefType.v("android.app.Application"));
            SootClass scApplicationHolder = LibraryClassPatcher.createOrGetApplicationHolder();
            body.getUnits().add(Jimple.v().newAssignStmt(applicationLocal,
                    Jimple.v().newStaticFieldRef(scApplicationHolder.getFieldByName("application").makeRef())));
            localVarsForClasses.put(applicationClass, applicationLocal);
        }

        // Get the callback classes
        for (SootClass sc : callbackClassToField.keySet()) {
            Local callbackLocal = generator.generateLocal(RefType.v(sc));
            body.getUnits().add(Jimple.v().newAssignStmt(callbackLocal,
                    Jimple.v().newStaticFieldRef(callbackClassToField.get(sc).makeRef())));
            localVarsForClasses.put(sc, callbackLocal);
        }

        // Create the instances of the fragment classes
        if (fragmentClasses != null && !fragmentClasses.isEmpty()) {
            NopStmt beforeCbCons = Jimple.v().newNopStmt();
            body.getUnits().add(beforeCbCons);

            createClassInstances(fragmentClasses);

            // Jump back to overapproximate the order in which the
            // constructors are called
            createIfStmt(beforeCbCons);
        }

        // 1. onCreate:
        {
            searchAndBuildMethod(ComponentLifecycleConstants.ACTIVITY_ONCREATE, component, thisLocal);
            for (SootClass callbackClass : this.activityLifecycleCallbacks.keySet()) {
                searchAndBuildMethod(ComponentLifecycleConstants.ACTIVITYLIFECYCLECALLBACK_ONACTIVITYCREATED,
                        callbackClass, localVarsForClasses.get(callbackClass), currentClassSet);
            }
        }

        // Adding the lifecycle of the Fragments that belong to this Activity:
        // iterate through the fragments detected in the CallbackAnalyzer
        if (fragmentClasses != null && !fragmentClasses.isEmpty()) {
            for (SootClass scFragment : fragmentClasses) {
                // Get a class local
                Local fragmentLocal = localVarsForClasses.get(scFragment);
                Set<Local> tempLocals = new HashSet<>();
                if (fragmentLocal == null) {
                    fragmentLocal = generateClassConstructor(scFragment, body, new HashSet<SootClass>(),
                            referenceClasses, tempLocals);
                    if (fragmentLocal == null)
                        continue;
                    localVarsForClasses.put(scFragment, fragmentLocal);
                }

                // The onAttachFragment() callbacks tells the activity that a
                // new fragment was attached
                TwoElementSet<SootClass> classAndFragment = new TwoElementSet<SootClass>(component, scFragment);
                Stmt afterOnAttachFragment = Jimple.v().newNopStmt();
                createIfStmt(afterOnAttachFragment);
                searchAndBuildMethod(ComponentLifecycleConstants.ACTIVITY_ONATTACHFRAGMENT, component, thisLocal,
                        classAndFragment);
                body.getUnits().add(afterOnAttachFragment);

                // Render the fragment lifecycle
                generateFragmentLifecycle(scFragment, fragmentLocal, component);

                // Get rid of the locals
                body.getUnits().add(Jimple.v().newAssignStmt(fragmentLocal, NullConstant.v()));
                for (Local tempLocal : tempLocals)
                    body.getUnits().add(Jimple.v().newAssignStmt(tempLocal, NullConstant.v()));
            }
        }

        // 2. onStart:
        Stmt onStartStmt;
        {
            onStartStmt = searchAndBuildMethod(ComponentLifecycleConstants.ACTIVITY_ONSTART, component, thisLocal);
            for (SootClass callbackClass : this.activityLifecycleCallbacks.keySet()) {
                Stmt s = searchAndBuildMethod(ComponentLifecycleConstants.ACTIVITYLIFECYCLECALLBACK_ONACTIVITYSTARTED,
                        callbackClass, localVarsForClasses.get(callbackClass), currentClassSet);
                if (onStartStmt == null)
                    onStartStmt = s;
            }

            // If we don't have an onStart method, we need to create a
            // placeholder so that we
            // have somewhere to jump
            if (onStartStmt == null)
                body.getUnits().add(onStartStmt = Jimple.v().newNopStmt());

        }
        // onRestoreInstanceState is optional, the system only calls it if a
        // state has previously been stored.
        {
            Stmt afterOnRestore = Jimple.v().newNopStmt();
            createIfStmt(afterOnRestore);
            searchAndBuildMethod(ComponentLifecycleConstants.ACTIVITY_ONRESTOREINSTANCESTATE, component, thisLocal,
                    currentClassSet);
            body.getUnits().add(afterOnRestore);
        }
        searchAndBuildMethod(ComponentLifecycleConstants.ACTIVITY_ONPOSTCREATE, component, thisLocal);

        // 3. onResume:
        Stmt onResumeStmt = Jimple.v().newNopStmt();
        body.getUnits().add(onResumeStmt);
        {
            searchAndBuildMethod(ComponentLifecycleConstants.ACTIVITY_ONRESUME, component, thisLocal);
            for (SootClass callbackClass : this.activityLifecycleCallbacks.keySet()) {
                searchAndBuildMethod(ComponentLifecycleConstants.ACTIVITYLIFECYCLECALLBACK_ONACTIVITYRESUMED,
                        callbackClass, localVarsForClasses.get(callbackClass), currentClassSet);
            }
        }
        searchAndBuildMethod(ComponentLifecycleConstants.ACTIVITY_ONPOSTRESUME, component, thisLocal);

        // Scan for other entryPoints of this class:
        if (this.callbacks != null && !this.callbacks.isEmpty()) {
            NopStmt startWhileStmt = Jimple.v().newNopStmt();
            NopStmt endWhileStmt = Jimple.v().newNopStmt();
            body.getUnits().add(startWhileStmt);
            createIfStmt(endWhileStmt);

            // Add the callbacks
            addCallbackMethods();

            body.getUnits().add(endWhileStmt);
            createIfStmt(startWhileStmt);
        }

        // 4. onPause:
        searchAndBuildMethod(ComponentLifecycleConstants.ACTIVITY_ONPAUSE, component, thisLocal);
        for (SootClass callbackClass : this.activityLifecycleCallbacks.keySet()) {
            searchAndBuildMethod(ComponentLifecycleConstants.ACTIVITYLIFECYCLECALLBACK_ONACTIVITYPAUSED, callbackClass,
                    localVarsForClasses.get(callbackClass), currentClassSet);
        }
        searchAndBuildMethod(ComponentLifecycleConstants.ACTIVITY_ONCREATEDESCRIPTION, component, thisLocal);
        searchAndBuildMethod(ComponentLifecycleConstants.ACTIVITY_ONSAVEINSTANCESTATE, component, thisLocal);
        for (SootClass callbackClass : this.activityLifecycleCallbacks.keySet()) {
            searchAndBuildMethod(ComponentLifecycleConstants.ACTIVITYLIFECYCLECALLBACK_ONACTIVITYSAVEINSTANCESTATE,
                    callbackClass, localVarsForClasses.get(callbackClass), currentClassSet);
        }

        // goTo Stop, Resume or Create:
        // (to stop is fall-through, no need to add)
        createIfStmt(onResumeStmt);
        // createIfStmt(onCreateStmt); // no, the process gets killed in between

        // 5. onStop:
        Stmt onStop = searchAndBuildMethod(ComponentLifecycleConstants.ACTIVITY_ONSTOP, component, thisLocal);
        boolean hasAppOnStop = false;
        for (SootClass callbackClass : this.activityLifecycleCallbacks.keySet()) {
            Stmt onActStoppedStmt = searchAndBuildMethod(
                    ComponentLifecycleConstants.ACTIVITYLIFECYCLECALLBACK_ONACTIVITYSTOPPED, callbackClass,
                    localVarsForClasses.get(callbackClass), currentClassSet);
            hasAppOnStop |= onActStoppedStmt != null;
        }
        if (hasAppOnStop && onStop != null)
            createIfStmt(onStop);

        // goTo onDestroy, onRestart or onCreate:
        // (to restart is fall-through, no need to add)
        NopStmt stopToDestroyStmt = Jimple.v().newNopStmt();
        createIfStmt(stopToDestroyStmt);
        // createIfStmt(onCreateStmt); // no, the process gets killed in between

        // 6. onRestart:
        searchAndBuildMethod(ComponentLifecycleConstants.ACTIVITY_ONRESTART, component, thisLocal);
        createIfStmt(onStartStmt); // jump to onStart(), fall through to
        // onDestroy()

        // 7. onDestroy
        body.getUnits().add(stopToDestroyStmt);
        searchAndBuildMethod(ComponentLifecycleConstants.ACTIVITY_ONDESTROY, component, thisLocal);
        for (SootClass callbackClass : this.activityLifecycleCallbacks.keySet()) {
            searchAndBuildMethod(ComponentLifecycleConstants.ACTIVITYLIFECYCLECALLBACK_ONACTIVITYDESTROYED,
                    callbackClass, localVarsForClasses.get(callbackClass), currentClassSet);
        }
    }

    /**
     * Generates the lifecycle for an Android Fragment class
     *
     * @param currentClass The class for which to build the fragment lifecycle
     * @param classLocal   The local referencing an instance of the current class
     */
    private void generateFragmentLifecycle(SootClass currentClass, Local classLocal, SootClass activity) {
        NopStmt endFragmentStmt = Jimple.v().newNopStmt();
        createIfStmt(endFragmentStmt);

        // 1. onAttach:
        Stmt onAttachStmt = searchAndBuildMethod(ComponentLifecycleConstants.FRAGMENT_ONATTACH, currentClass, classLocal,
                Collections.singleton(activity));
        if (onAttachStmt == null)
            body.getUnits().add(onAttachStmt = Jimple.v().newNopStmt());

        // 2. onCreate:
        Stmt onCreateStmt = searchAndBuildMethod(ComponentLifecycleConstants.FRAGMENT_ONCREATE, currentClass,
                classLocal);
        if (onCreateStmt == null)
            body.getUnits().add(onCreateStmt = Jimple.v().newNopStmt());

        // 3. onCreateView:
        Stmt onCreateViewStmt = searchAndBuildMethod(ComponentLifecycleConstants.FRAGMENT_ONCREATEVIEW, currentClass,
                classLocal);
        if (onCreateViewStmt == null)
            body.getUnits().add(onCreateViewStmt = Jimple.v().newNopStmt());

        Stmt onViewCreatedStmt = searchAndBuildMethod(ComponentLifecycleConstants.FRAGMENT_ONVIEWCREATED, currentClass,
                classLocal);
        if (onViewCreatedStmt == null)
            body.getUnits().add(onViewCreatedStmt = Jimple.v().newNopStmt());

        // 0. onActivityCreated:
        Stmt onActCreatedStmt = searchAndBuildMethod(ComponentLifecycleConstants.FRAGMENT_ONACTIVITYCREATED,
                currentClass, classLocal);
        if (onActCreatedStmt == null)
            body.getUnits().add(onActCreatedStmt = Jimple.v().newNopStmt());

        // 4. onStart:
        Stmt onStartStmt = searchAndBuildMethod(ComponentLifecycleConstants.FRAGMENT_ONSTART, currentClass, classLocal);
        if (onStartStmt == null)
            body.getUnits().add(onStartStmt = Jimple.v().newNopStmt());

        // 5. onResume:
        Stmt onResumeStmt = Jimple.v().newNopStmt();
        body.getUnits().add(onResumeStmt);
        searchAndBuildMethod(ComponentLifecycleConstants.FRAGMENT_ONRESUME, currentClass, classLocal);

        // 6. onPause:
        searchAndBuildMethod(ComponentLifecycleConstants.FRAGMENT_ONPAUSE, currentClass, classLocal);
        createIfStmt(onResumeStmt);

        // 7. onSaveInstanceState:
        searchAndBuildMethod(ComponentLifecycleConstants.FRAGMENT_ONSAVEINSTANCESTATE, currentClass, classLocal);

        // 8. onStop:
        searchAndBuildMethod(ComponentLifecycleConstants.FRAGMENT_ONSTOP, currentClass, classLocal);
        createIfStmt(onCreateViewStmt);
        createIfStmt(onStartStmt);

        // 9. onDestroyView:
        searchAndBuildMethod(ComponentLifecycleConstants.FRAGMENT_ONDESTROYVIEW, currentClass, classLocal);
        createIfStmt(onCreateViewStmt);

        // 10. onDestroy:
        searchAndBuildMethod(ComponentLifecycleConstants.FRAGMENT_ONDESTROY, currentClass, classLocal);

        // 11. onDetach:
        searchAndBuildMethod(ComponentLifecycleConstants.FRAGMENT_ONDETACH, currentClass, classLocal);
        createIfStmt(onAttachStmt);

        body.getUnits().add(Jimple.v().newAssignStmt(classLocal, NullConstant.v()));
        body.getUnits().add(endFragmentStmt);
    }

    @Override
    protected void createAdditionalFields() {
        super.createAdditionalFields();

        // Create a name for a field for the result intent of this component
        String fieldName = "ipcResultIntent";
        int fieldIdx = 0;
        while (component.declaresFieldByName(fieldName))
            fieldName = "ipcResultIntent_" + fieldIdx++;

        // Create the field itself
        resultIntentField = Scene.v().makeSootField(fieldName, RefType.v("android.content.Intent"), Modifier.PUBLIC);
        component.addField(resultIntentField);
    }

    @Override
    protected void createAdditionalMethods() {
        createGetIntentMethod();
        createSetIntentMethod();
        createSetResultMethod();
    }

    /**
     * Creates an implementation of getIntent() that returns the intent from our ICC
     * model
     */
    private void createGetIntentMethod() {
        // We need to create an implementation of "getIntent". If there is already such
        // an implementation, we don't touch it.
        if (component.declaresMethod("android.content.Intent getIntent()"))
            return;

        Type intentType = RefType.v("android.content.Intent");
        SootMethod sm = Scene.v().makeSootMethod("getIntent", Collections.emptyList(), intentType,
                Modifier.PUBLIC);
        component.addMethod(sm);
        sm.addTag(SimulatedCodeElementTag.TAG);

        JimpleBody b = Jimple.v().newBody(sm);
        sm.setActiveBody(b);
        b.insertIdentityStmts();

        LocalGenerator localGen = new LocalGenerator(b);
        Local lcIntent = localGen.generateLocal(intentType);
        b.getUnits().add(Jimple.v().newAssignStmt(lcIntent,
                Jimple.v().newInstanceFieldRef(b.getThisLocal(), intentField.makeRef())));
        b.getUnits().add(Jimple.v().newReturnStmt(lcIntent));
    }

    /**
     * Creates an implementation of setIntent() that writes the given intent into
     * the correct field
     */
    private void createSetIntentMethod() {
        // We need to create an implementation of "getIntent". If there is already such
        // an implementation, we don't touch it.
        if (component.declaresMethod("void setIntent(android.content.Intent)"))
            return;

        Type intentType = RefType.v("android.content.Intent");
        SootMethod sm = Scene.v().makeSootMethod("setIntent", Collections.singletonList(intentType), VoidType.v(),
                Modifier.PUBLIC);
        component.addMethod(sm);
        sm.addTag(SimulatedCodeElementTag.TAG);

        JimpleBody b = Jimple.v().newBody(sm);
        sm.setActiveBody(b);
        b.insertIdentityStmts();

        Local lcIntent = b.getParameterLocal(0);
        b.getUnits().add(Jimple.v()
                .newAssignStmt(Jimple.v().newInstanceFieldRef(b.getThisLocal(), intentField.makeRef()), lcIntent));
        b.getUnits().add(Jimple.v().newReturnVoidStmt());
    }

    /**
     * Creates an implementation of setResult() that writes the given intent into
     * the correct field
     */
    private void createSetResultMethod() {
        // We need to create an implementation of "getIntent". If there is already such
        // an implementation, we don't touch it.
        if (component.declaresMethod("void setResult(int,android.content.Intent)"))
            return;

        Type intentType = RefType.v("android.content.Intent");
        List<Type> params = new ArrayList<>();
        params.add(IntType.v());
        params.add(intentType);
        SootMethod sm = Scene.v().makeSootMethod("setResult", params, VoidType.v(), Modifier.PUBLIC);
        component.addMethod(sm);
        sm.addTag(SimulatedCodeElementTag.TAG);

        JimpleBody b = Jimple.v().newBody(sm);
        sm.setActiveBody(b);
        b.insertIdentityStmts();

        Local lcIntent = b.getParameterLocal(1);
        b.getUnits().add(Jimple.v().newAssignStmt(
                Jimple.v().newInstanceFieldRef(b.getThisLocal(), resultIntentField.makeRef()), lcIntent));
        b.getUnits().add(Jimple.v().newReturnVoidStmt());

        // Activity.setResult() is final. We need to change that
        SootMethod smSetResult = Scene.v()
                .grabMethod("<android.app.Activity: void setResult(int,android.content.Intent)>");
        if (smSetResult != null && smSetResult.getDeclaringClass().isApplicationClass())
            smSetResult.setModifiers(smSetResult.getModifiers() & ~Modifier.FINAL);
    }

    @Override
    protected void reset() {
        super.reset();

        component.removeField(resultIntentField);
        resultIntentField = null;
    }

    @Override
    public ComponentEntryPointInfo getComponentInfo() {
        ActivityEntryPointInfo activityInfo = new ActivityEntryPointInfo(mainMethod);
        activityInfo.setIntentField(intentField);
        activityInfo.setResultIntentField(resultIntentField);
        return activityInfo;
    }

}
