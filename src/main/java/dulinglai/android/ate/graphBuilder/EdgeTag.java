package dulinglai.android.ate.graphBuilder;

import dulinglai.android.ate.graphBuilder.componentNodes.AbstractComponentNode;

public class EdgeTag {

    private AbstractComponentNode prevComp;

    public EdgeTag(AbstractComponentNode prevComp) {
        this.prevComp = prevComp;
    }

    public AbstractComponentNode getPrevComp() {
        return prevComp;
    }
}
