package dulinglai.android.ate.model;

import dulinglai.android.ate.model.components.*;
import dulinglai.android.ate.model.widgets.AbstractWidget;
import soot.util.HashMultiMap;
import soot.util.MultiMap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ComponentTransitionGraph {
    private List<TransitionEdge> transitionEdges;
    private Set<Activity> activitySet;
    private Set<Service> serviceSet;
    private Set<ContentProvider> contentProviderSet;
    private Set<BroadcastReceiver> broadcastReceiverSet;
    private MultiMap<AbstractComponent, TransitionEdge> outEdgeMap;
    private MultiMap<AbstractComponent, TransitionEdge> inEdgeMap;

    public ComponentTransitionGraph(Set<Activity> activitySet, Set<Service> serviceSet,
                                    Set<BroadcastReceiver> broadcastReceiverSet, Set<ContentProvider> contentProviderSet) {

        this.activitySet = activitySet;
        this.serviceSet = serviceSet;
        this.contentProviderSet = contentProviderSet;
        this.broadcastReceiverSet = broadcastReceiverSet;
        this.transitionEdges = new ArrayList<>();
        this.outEdgeMap = new HashMultiMap<>();
        this.inEdgeMap = new HashMultiMap<>();

        for (Activity activity : activitySet) {
            if (activity.getParentCompString()!=null) {
                Activity parentNode = getActivityNodeByName(activity.getParentCompString());
                if (parentNode!=null) {
                    addTransitionEdge(parentNode, activity);
                }
            }
        }
    }

    /**
     * Add a new transition edge
     * @param srcComp The source activity
     * @param tgtComp The target activity
     */
    public void addTransitionEdge(AbstractComponent srcComp, AbstractComponent tgtComp) {
        TransitionEdge newEdge = new TransitionEdge(srcComp, tgtComp);
        transitionEdges.add(newEdge);
        outEdgeMap.put(srcComp, newEdge);
        inEdgeMap.put(tgtComp, newEdge);
    }

    /**
     * Add a new transition edge
     * @param srcActivity The source activity
     * @param tgtActivity The target activity
     * @param widget The widget assigned to this edge
     */
    public void addTransitionEdge(AbstractComponent srcActivity, Activity tgtActivity, AbstractWidget widget) {
        TransitionEdge newEdge = new TransitionEdge(srcActivity, tgtActivity, widget);
        transitionEdges.add(newEdge);
        outEdgeMap.put(srcActivity, newEdge);
        inEdgeMap.put(tgtActivity, newEdge);
    }

    /**
     * Gets the component node by name
     * @param name The name to look up component node
     * @return The component node
     */
    public AbstractComponent getCompNodeByName(String name) {
        for (Activity activity : activitySet) {
            if (activity.getName().equalsIgnoreCase(name))
                return activity;
        }
        for (Service serviceNode : serviceSet) {
            if (serviceNode.getName().equalsIgnoreCase(name))
                return serviceNode;
        }
        for (ContentProvider providerNode : contentProviderSet) {
            if (providerNode.getName().equalsIgnoreCase(name))
                return providerNode;
        }
        for (BroadcastReceiver receiverNode : broadcastReceiverSet) {
            if (receiverNode.getName().equalsIgnoreCase(name))
                return receiverNode;
        }
        return null;
    }

    /**
     * Gets an activity node by name
     * @param name The name to look up activity node
     * @return The activity node
     */
    public Activity getActivityNodeByName(String name) {
        for (Activity activity : activitySet) {
            if (activity.getName().equalsIgnoreCase(name))
                return activity;
        }
        return null;
    }

    /**
     * Gets the service node by name
     * @param name The name to look up service node
     * @return The service node
     */
    public Service getServiceNodeByName(String name) {
        for (Service serviceNode : serviceSet) {
            if (serviceNode.getName().equalsIgnoreCase(name))
                return serviceNode;
        }
        return null;
    }

    /**
     * Gets the content provider node by name
     * @param name The name to look up provider node
     * @return The provider node
     */
    public ContentProvider getProviderNodeByName(String name) {
        for (ContentProvider providerNode : contentProviderSet) {
            if (providerNode.getName().equalsIgnoreCase(name))
                return providerNode;
        }
        return null;
    }

    /**
     * Gets the broadcast receiver node by name
     * @param name The name to look up broadcast receiver node
     * @return The receiver node
     */
    public BroadcastReceiver getReceiverNodeByName(String name) {
        for (BroadcastReceiver receiverNode : broadcastReceiverSet) {
            if (receiverNode.getName().equalsIgnoreCase(name))
                return receiverNode;
        }
        return null;
    }

    /**
     * Gets all activity nodes in the graph
     * @return All activity nodes
     */
    public Set<Activity> getAllActivities(){
        return activitySet;
    }

    /**
     * Gets the transition edges with given source activity node
     * @param componentNode The source activity node
     * @return The set of transition edges that contain given source activity node
     */
    public Set<TransitionEdge> getEdgeWithSrcComponent(AbstractComponent componentNode) {
        Set<TransitionEdge> edgeSet = new HashSet<>();
        for (TransitionEdge edge : transitionEdges) {
            if (edge.getSrcComp().equals(componentNode))
                edgeSet.add(edge);
        }
        return edgeSet;
    }

    /**
     * Gets the transition edges with given target component node
     * @param componentNode The target component node
     * @return The set of transition edges that contain given target component node
     */
    public Set<TransitionEdge> getEdgeWithTgtComp(AbstractComponent componentNode) {
        Set<TransitionEdge> edgeSet = new HashSet<>();
        for (TransitionEdge edge : transitionEdges) {
            if (edge.getTgtComp().equals(componentNode))
                edgeSet.add(edge);
        }
        return edgeSet;
    }

    /**
     * Gets the transition edge with given source and target component nodes
     * @param srcNode The source component node
     * @param tgtNode The target component node
     * @return The transition edge
     */
    public TransitionEdge getEdge(AbstractComponent srcNode, AbstractComponent tgtNode) {
        for (TransitionEdge edge : transitionEdges) {
            if (edge.getTgtComp().equals(tgtNode))
                if (edge.getSrcComp().equals(srcNode))
                    return edge;
        }
        return null;
    }

    /**
     * Sets the widget assigned to the transition edge with given source and target component nodes
     * @param srcNode The source component node
     * @param tgtNode The target component node
     * @param widget The widget to be assigned to this transition
     */
    public void setEdgeWidget(AbstractComponent srcNode, AbstractComponent tgtNode, AbstractWidget widget) {
        for (TransitionEdge edge : transitionEdges) {
            if (edge.getTgtComp().equals(tgtNode))
                if (edge.getSrcComp().equals(srcNode))
                    edge.setWidget(widget);
        }
    }

    /**
     * Sets the tag assigned to the transition edge with given source and target component nodes
     * @param srcNode The source component node
     * @param tgtNode The target component node
     * @param tag The tag to be assigned to this transition
     */
    public void setEdgeWidget(AbstractComponent srcNode, AbstractComponent tgtNode, EdgeTag tag) {
        for (TransitionEdge edge : transitionEdges) {
            if (edge.getTgtComp().equals(tgtNode))
                if (edge.getSrcComp().equals(srcNode))
                    edge.setEdgeTag(tag);
        }
    }

    /**
     * Gets the tag assigned to the transition edge with given source and target component nodes
     * @param srcNode The source component node
     * @param tgtNode The target component node
     * @return  The tag to be assigned to this transition
     */
    public EdgeTag setEdgeWidget(AbstractComponent srcNode, AbstractComponent tgtNode) {
        for (TransitionEdge edge : transitionEdges) {
            if (edge.getTgtComp().equals(tgtNode))
                if (edge.getSrcComp().equals(srcNode))
                    return edge.getEdgeTag();
        }
        return null;
    }

    /**
     * Gets the successor of the given component
     * @param componentNode The given component
     * @return The successor of the given component
     */
    public List<AbstractComponent> getSuccsOf(AbstractComponent componentNode) {
        List<AbstractComponent> compNodeList = new ArrayList<>();
        for (TransitionEdge edge : getEdgeWithSrcComponent(componentNode)) {
            compNodeList.add(edge.getTgtComp());
        }
        return compNodeList;
    }

    /**
     * Gets the predecessor of the given component
     * @param componentNode The given component
     * @return The predecessor of the given component
     */
    public List<AbstractComponent> getPredsOf(AbstractComponent componentNode) {
        List<AbstractComponent> compNodeList = new ArrayList<>();
        for (TransitionEdge edge : getEdgeWithTgtComp(componentNode)) {
            compNodeList.add(edge.getSrcComp());
        }
        return compNodeList;
    }

    /**
     * Get transition edge widget
     * @param edge The transition edge
     * @return The widget assigned to this transition edge
     */
    public AbstractWidget getWidgetOf(TransitionEdge edge) {
        return edge.getWidget();
    }

    /**
     * Get transition edge widget with source component
     * @param srcActivity The source component
     * @param tgtActivity The target component
     * @return The widget assigned to this transition edge
     */
    public AbstractWidget getWidgetOfActivities(Activity srcActivity, Activity tgtActivity) {
        return getEdge(srcActivity, tgtActivity).getWidget();
    }

    /**
     * Check if an activity is in the graph
     * @param activity The activity node to check
     * @return True if the given activity node can be found in the graph
     */
    public boolean isActivityInGraph(Activity activity) {
        return activitySet.contains(activity);
    }

    public List<TransitionEdge> getTransitionEdges(){ return transitionEdges; }
}
