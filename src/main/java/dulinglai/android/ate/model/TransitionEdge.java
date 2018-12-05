package dulinglai.android.ate.model;

import dulinglai.android.ate.model.components.AbstractComponent;
import dulinglai.android.ate.model.widgets.AbstractWidget;

public class TransitionEdge {

    private AbstractComponent srcComp;
    private AbstractComponent tgtComp;
    private AbstractWidget widget;
    private EdgeTag edgeTag;

    public TransitionEdge(AbstractComponent srcComp, AbstractComponent tgtComp) {
        this.srcComp = srcComp;
        this.tgtComp = tgtComp;
    }

    public TransitionEdge(AbstractComponent srcComp, AbstractComponent tgtComp, AbstractWidget widget) {
        this.srcComp = srcComp;
        this.tgtComp = tgtComp;
        this.widget = widget;
    }

    public String toString() {
        return "Transition Edge: " + srcComp.getName() + " ==> " + tgtComp.getName();
    }

    @Override
    public boolean equals(Object other) {
        TransitionEdge o = (TransitionEdge) other;
        if (o == null)
            return false;
        if (o.getSrcComp() != srcComp)
            return false;
        if (o.getTgtComp() != tgtComp)
            return false;
        return o.getWidget() == widget;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((srcComp == null) ? 0 : srcComp.hashCode());
        result = prime * result + ((tgtComp == null) ? 0 : tgtComp.hashCode());
        result = prime * result + ((widget == null) ? 0 : widget.hashCode());
        return result;
    }

    /**
     * Gets the source activity node of the edge
     * @return the source activity node of the edge
     */
    public AbstractComponent getSrcComp() {
        return srcComp;
    }

    /**
     * Gets the target activity node of the edge
     * @return the target activity node of the edge
     */
    public AbstractComponent getTgtComp() {
        return tgtComp;
    }

    /**
     * Gets the widget assigned to this transition
     * @return the widget assigned to this transition
     */
    public AbstractWidget getWidget() {
        return widget;
    }

    /**
     * Sets the widget assigned to this transition
     * @param widget The widget assigned to this transition
     */
    public void setWidget(AbstractWidget widget) {
        this.widget = widget;
    }

    public EdgeTag getEdgeTag() {
        return edgeTag;
    }

    public void setEdgeTag(EdgeTag edgeTag) {
        this.edgeTag = edgeTag;
    }
}
