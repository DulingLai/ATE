package dulinglai.android.ate.model;

import dulinglai.android.ate.model.components.*;
import dulinglai.android.ate.model.entities.DialogEntity;
import dulinglai.android.ate.resources.manifest.ProcessManifest;

import soot.Scene;
import soot.SootClass;

import java.util.HashSet;
import java.util.Set;

public class App {

    private static App instance;

    private String packageName;

    private Activity launchActivity;
    private Set<SootClass> entrypoints;

    // Android components
    private Set<Activity> activities;
    private Set<Service> services;
    private Set<BroadcastReceiver> broadcastReceivers;
    private Set<ContentProvider> contentProviders;

    // Other UI elements
    private Set<Fragment> fragments;
    private Set<DialogEntity> dialogs;

    private App(){
        reset();
    }

    public static synchronized App v() {
        if (instance == null) {
            instance = new App();
        }
        return instance;
    }

    /**
     * Resets all components of the app
     */
    public void reset() {
        activities = new HashSet<>();
        fragments = new HashSet<>();
        services = new HashSet<>();
        broadcastReceivers = new HashSet<>();
        contentProviders = new HashSet<>();
    }

    /**
     * Builds the initial model of the app
     */
    public void initializeAppModel(ProcessManifest manifest) {
        this.packageName = manifest.getPackageName();
        addActivities(manifest.getActivitySet());
        setLaunchActivity(manifest.getLaunchActivity());
        addServices(manifest.getServiceSet());
        addBroadcastReceivers(manifest.getReceiverSet());
        addContentProviders(manifest.getProviderSet());

        // Load the soot classes
        activities.forEach(x -> x.setMainClass(Scene.v().getSootClassUnsafe(x.getName())));
        services.forEach(x -> x.setMainClass(Scene.v().getSootClassUnsafe(x.getName())));
        broadcastReceivers.forEach(x -> x.setMainClass(Scene.v().getSootClassUnsafe(x.getName())));
        contentProviders.forEach(x -> x.setMainClass(Scene.v().getSootClassUnsafe(x.getName())));

        // initialize entrypoints for analysis
        // Gets all components SootClasses as the entry-point for analysis
        Set<String> entryPointString = manifest.getEntryPointClasses();
        this.entrypoints = new HashSet<>(entryPointString.size());
        for (String className : entryPointString) {
            SootClass sc = Scene.v().getSootClassUnsafe(className);
            if (sc != null)
                this.entrypoints.add(sc);
        }
    }

    /* ====================================
               Getters and setters
     ======================================*/

    /**
     * Gets the package name of the app
     * @return The package name of the app
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * Gets all activities
     * @return All activities
     */
    public Set<Activity> getActivities() {
        return activities;
    }

    /**
     * Gets the specific activity by name
     * @param activityName The activity name
     * @return The activity of specific name
     */
    public Activity getActivityByName(String activityName) {
        for(Activity activity : activities){
            if (activity.getName().equals(activityName)) {
                return activity;
            }
        }
        return null;
    }

    /**
     * Adds an activity to the model
     * @param activity The activity to be added
     */
    public void addActivity(Activity activity) {
        this.activities.add(activity);
    }

    /**
     * Adds a set of activities to the model
     * @param activities The activities to be added
     */
    public void addActivities(Set<Activity> activities) {
        activities.remove(null);
        this.activities.addAll(activities);
    }

    /**
     * Gets the launch activity of the app
     * @return The launch activity
     */
    public Activity getLaunchActivity() {
        return launchActivity;
    }

    /**
     * Set the launch activity of the app
     * @param launchActivity The launch activity to be set
     */
    public void setLaunchActivity(Activity launchActivity) {
        this.launchActivity = launchActivity;
    }

    /**
     * Gets the entrypoints for analysis
     * @return The entrypoints for analysis
     */
    public Set<SootClass> getEntrypoints() {
        return entrypoints;
    }

    /**
     * Gets all services of the app
     * @return All services
     */
    public Set<Service> getServices() {
        return services;
    }

    /**
     * Adds a service to the model
     * @param service The service to be added
     */
    public void addService(Service service) {
        this.services.add(service);
    }

    /**
     * Adds a set of services to the model
     * @param services The services to be added
     */
    public void addServices(Set<Service> services) {
        services.remove(null);
        this.services.addAll(services);
    }

    /**
     * Gets all broadcast receivers of the app
     * @return All broadcast receivers of the app
     */
    public Set<BroadcastReceiver> getBroadcastReceivers() {
        return broadcastReceivers;
    }

    /**
     * Adds a broadcast receiver to the app
     * @param broadcastReceiver All broadcast receiver to be added
     */
    public void addBroadcastReceiver(BroadcastReceiver broadcastReceiver) {
        this.broadcastReceivers.add(broadcastReceiver);
    }

    /**
     * Adds a set of broadcast receivers to the model
     * @param broadcastReceivers The broadcast receivers to be added
     */
    public void addBroadcastReceivers(Set<BroadcastReceiver> broadcastReceivers) {
        broadcastReceivers.remove(null);
        this.broadcastReceivers.addAll(broadcastReceivers);
    }

    /**
     * Gets all content providers of the app
     * @return All content providers
     */
    public Set<ContentProvider> getContentProviders() {
        return contentProviders;
    }

    /**
     * Adds the content provider to the app
     * @param contentProvider The content provider to be added
     */
    public void addContentProvider(ContentProvider contentProvider) {
        this.contentProviders.add(contentProvider);
    }

    /**
     * Adds a set of content providers to the model
     * @param contentProviders The content providers to be added
     */
    public void addContentProviders(Set<ContentProvider> contentProviders) {
        contentProviders.remove(null);
        this.contentProviders.addAll(contentProviders);
    }

    /**
     * Gets all fragments of the app
     * @return All fragments
     */
    public Set<Fragment> getFragments() {
        return fragments;
    }

    /**
     * Gets a fragment by name
     * @param name The name of the fragment
     * @return The fragment of given name
     */
    public Fragment getFragmentByName(String name) {
        for (Fragment fragment : fragments) {
            if (name.equals(fragment.getName()))
                return fragment;
        }
        return null;
    }

    /**
     * Adds a fragment to the model
     * @param fragment The fragment to be added
     */
    public void addFragment(Fragment fragment) {
        this.fragments.add(fragment);
    }

    /**
     * Creates a fragment to the model
     * @param sootClass The Soot class of the fragment to be added
     * @param parentActivity The parent activity of this fragment
     */
    public void createFragment(SootClass sootClass, Activity parentActivity) {
        Fragment fragment = getFragmentByName(sootClass.getName());
        if (fragment==null)
            addFragment(new Fragment(sootClass, parentActivity));
        else if (fragment.getParentActivities()==null)
            fragment.AddParentActivity(parentActivity);
    }

    /**
     * Creates a fragment to the model
     * @param sootClass The Soot class of the fragment to be added
     */
    public void createFragment(SootClass sootClass) {
        Fragment fragment = getFragmentByName(sootClass.getName());
        if (fragment==null)
            addFragment(new Fragment(sootClass));
    }
}
