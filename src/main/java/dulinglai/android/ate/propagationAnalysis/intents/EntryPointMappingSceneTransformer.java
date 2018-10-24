/*
 * Copyright (C) 2015 The Pennsylvania State University and the University of Wisconsin
 * Systems and Internet Infrastructure Security Laboratory
 *
 * Author: Damien Octeau
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dulinglai.android.ate.propagationAnalysis.intents;

import dulinglai.android.ate.propagationAnalysis.AnalysisParameters;
import dulinglai.android.ate.propagationAnalysis.PropagationTimers;
import dulinglai.android.ate.propagationAnalysis.Timers;
import dulinglai.android.ate.resources.androidConstants.ComponentLifecycleConstants;
import org.pmw.tinylog.Level;
import org.pmw.tinylog.Logger;
import soot.*;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.jimple.toolkits.callgraph.ReachableMethods;

import java.util.*;

public class EntryPointMappingSceneTransformer extends SceneTransformer {

    private static SootClass activityClass = null;
    private static SootClass serviceClass = null;
    private static SootClass gcmBaseIntentServiceClass = null;
    private static SootClass receiverClass = null;
    private static SootClass providerClass = null;
    private static SootClass applicationClass = null;

    private final Set<String> entryPointClasses;
    private final Map<String, Set<String>> callbackMethods;
    private final Map<SootMethod, Set<String>> entryPointMap;
    private final Set<SootMethod> visitedEntryPoints = new HashSet<>();

    public EntryPointMappingSceneTransformer(Set<String> entryPointClasses,
                                             Map<String, Set<String>> callbackMethods, Map<SootMethod, Set<String>> entryPointMap) {
        this.entryPointClasses = entryPointClasses;
        this.callbackMethods = callbackMethods;
        this.entryPointMap = entryPointMap;
    }

    @Override
    protected void internalTransform(String phaseName, @SuppressWarnings("rawtypes") Map options) {
        PropagationTimers.v().totalTimer.start();
        dulinglai.android.ate.propagationAnalysis.Timers.v().entryPointMapping.start();

        // Set<String> signatures = new HashSet<>();

        Map<SootMethod, Set<String>> entryPointMap = this.entryPointMap;
        if (Logger.getLevel() == Level.DEBUG) {
            Set<String> difference = new HashSet<>(this.callbackMethods.keySet());
            difference.removeAll(entryPointClasses);
            if (difference.size() == 0) {
                Logger.debug("Difference size is 0");
            } else {
                Logger.debug("Difference is " + difference);
            }
        }

        // Set<String> lifecycleMethods = new HashSet<>();
        // lifecycleMethods.addAll(AndroidEntryPointConstants.getActivityLifecycleMethods());
        // lifecycleMethods.addAll(AndroidEntryPointConstants.getApplicationLifecycleMethods());
        // lifecycleMethods.addAll(AndroidEntryPointConstants.getBroadcastLifecycleMethods());
        // lifecycleMethods.addAll(AndroidEntryPointConstants.getContentproviderLifecycleMethods());
        // lifecycleMethods.addAll(AndroidEntryPointConstants.getServiceLifecycleMethods());
        activityClass = Scene.v().getSootClass(ComponentLifecycleConstants.ACTIVITYCLASS);
        serviceClass = Scene.v().getSootClass(ComponentLifecycleConstants.SERVICECLASS);
        gcmBaseIntentServiceClass =
                Scene.v().getSootClass(ComponentLifecycleConstants.GCMBASEINTENTSERVICECLASS);
        receiverClass = Scene.v().getSootClass(ComponentLifecycleConstants.BROADCASTRECEIVERCLASS);
        providerClass = Scene.v().getSootClass(ComponentLifecycleConstants.CONTENTPROVIDERCLASS);
        applicationClass = Scene.v().getSootClass(ComponentLifecycleConstants.APPLICATIONCLASS);

        Logger.debug(this.callbackMethods.toString());

        for (String entryPoint : entryPointClasses) {
            // if (!entryPointClasses.contains(entryPoint)) {
            // System.err.println("Warning: " + entryPoint + " is not an entry point");
            // continue;
            // }

            SootClass entryPointClass = Scene.v().getSootClass(entryPoint);
            List<MethodOrMethodContext> callbacks = new ArrayList<>();

            // Add methods for component.
            boolean knownComponentType = addLifecycleMethods(entryPointClass, callbacks);

            for (SootMethod method : entryPointClass.getMethods()) {
                String methodName = method.getName();
                if (methodName.equals(SootMethod.constructorName)
                        || methodName.equals(SootMethod.staticInitializerName) || !knownComponentType) {
                    callbacks.add(method);
                }
            }

            Set<String> callbackMethodStrings = this.callbackMethods.get(entryPoint);

            if (callbackMethodStrings != null) {
                for (String callbackMethodString : callbackMethodStrings) {
                    if (!Scene.v().containsMethod(callbackMethodString)) {
                        Logger.warn("Warning: " + callbackMethodString + " is not in scene");
                        continue;
                    }

                    SootMethod method = Scene.v().getMethod(callbackMethodString);
                    // Add constructors for callbacks.
                    for (SootMethod potentialInit : method.getDeclaringClass().getMethods()) {
                        if (potentialInit.isPrivate()) {
                            continue;
                        }
                        String name = potentialInit.getName();
                        if (name.equals(SootMethod.constructorName)) {
                            addConstructorStack(potentialInit, callbacks);
                        } else if (name.equals(SootMethod.staticInitializerName)) {
                            callbacks.add(potentialInit);
                        }
                    }
                    callbacks.add(method);
                }
            }

            Logger.debug(callbacks.toString());

            ReachableMethods reachableMethods =
                    new ReachableMethods(Scene.v().getCallGraph(), callbacks.iterator(), null);
            reachableMethods.update();

            for (Iterator<MethodOrMethodContext> iter = reachableMethods.listener(); iter.hasNext(); ) {
                SootMethod method = iter.next().method();
                if (!AnalysisParameters.v().isAnalysisClass(method.getDeclaringClass().getName())) {
                    continue;
                }

                Set<String> entryPoints = entryPointMap.computeIfAbsent(method, k -> new HashSet<>());

                entryPoints.add(entryPoint);
            }
        }

        if (Logger.getLevel() == Level.DEBUG) {
            Logger.debug("Entry points: ");
            Logger.debug(entryPointMap.toString());

            CallGraph cg = Scene.v().getCallGraph();

            Iterator<Edge> it = cg.listener();
            StringBuilder stringBuilder = new StringBuilder("Call graph:\n");
            while (it.hasNext()) {
                soot.jimple.toolkits.callgraph.Edge e = it.next();
                stringBuilder.append("" + e.src() + e.srcStmt() + " =" + e.kind() + "=> " + e.tgt() + "\n");
            }
            Logger.debug(stringBuilder.toString());
        }

        Timers.v().entryPointMapping.end();
        PropagationTimers.v().totalTimer.end();
    }

    private boolean addLifecycleMethods(SootClass entryPointClass,
                                        List<MethodOrMethodContext> callbacks) {
        boolean result = true;
        Hierarchy hierarchy = Scene.v().getActiveHierarchy();

        if (hierarchy.isClassSubclassOf(entryPointClass, activityClass)) {
            addLifecycleMethodsHelper(entryPointClass,
                    ComponentLifecycleConstants.getActivityLifecycleMethods(), callbacks);
        } else if (hierarchy.isClassSubclassOf(entryPointClass, gcmBaseIntentServiceClass)) {
            addLifecycleMethodsHelper(entryPointClass,
                    ComponentLifecycleConstants.getGCMIntentServiceMethods(), callbacks);
        } else if (hierarchy.isClassSubclassOf(entryPointClass, serviceClass)) {
            addLifecycleMethodsHelper(entryPointClass,
                    ComponentLifecycleConstants.getServiceLifecycleMethods(), callbacks);
        } else if (hierarchy.isClassSubclassOf(entryPointClass, receiverClass)) {
            addLifecycleMethodsHelper(entryPointClass,
                    ComponentLifecycleConstants.getBroadcastLifecycleMethods(), callbacks);
        } else if (hierarchy.isClassSubclassOf(entryPointClass, providerClass)) {
            addLifecycleMethodsHelper(entryPointClass,
                    ComponentLifecycleConstants.getContentproviderLifecycleMethods(), callbacks);
        } else if (hierarchy.isClassSubclassOf(entryPointClass, applicationClass)) {
            addLifecycleMethodsHelper(entryPointClass,
                    ComponentLifecycleConstants.getApplicationLifecycleMethods(), callbacks);
        } else {
            System.err.println("Unknown entry point type: " + entryPointClass);
            result = false;
        }

        return result;
    }

    private void addLifecycleMethodsHelper(SootClass entryPointClass, List<String> lifecycleMethods,
                                           List<MethodOrMethodContext> callbacks) {
        for (String lifecycleMethod : lifecycleMethods) {
            SootMethod method = findMethod(entryPointClass, lifecycleMethod);
            if (method != null) {
                callbacks.add(method);
            }
        }
    }

    /**
     * Finds a method with the given signature in the given class or one of its super classes
     *
     * @param currentClass The current class in which to start the search
     * @param subsignature The subsignature of the method to find
     * @return The method with the given signature if it has been found, otherwise null
     */
    protected SootMethod findMethod(SootClass currentClass, String subsignature) {
        if (currentClass.declaresMethod(subsignature)) {
            return currentClass.getMethod(subsignature);
        }
        if (currentClass.hasSuperclass()) {
            return findMethod(currentClass.getSuperclass(), subsignature);
        }
        return null;
    }

    private void addConstructorStack(SootMethod method, List<MethodOrMethodContext> callbacks) {
        if (visitedEntryPoints.contains(method)) {
            return;
        }

        callbacks.add(method);
        visitedEntryPoints.add(method);

        for (Type type : method.getParameterTypes()) {
            String typeString = type.toString();
            if (AnalysisParameters.v().isAnalysisClass(typeString)) {
                if (Scene.v().containsClass(typeString)) {
                    SootClass sootClass = Scene.v().getSootClass(typeString);
                    for (SootMethod sootMethod : sootClass.getMethods()) {
                        if (sootMethod.getName().equals(SootMethod.constructorName)) {
                            addConstructorStack(sootMethod, callbacks);
                        }
                    }
                } else {
                    Logger.warn("Warning: " + typeString + " is not in scene");
                }
            }
        }
    }
}
