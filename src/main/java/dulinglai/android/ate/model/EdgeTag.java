package dulinglai.android.ate.model;

import dulinglai.android.ate.model.components.AbstractComponent;

public class EdgeTag {

    private AbstractComponent prevComp;

    public EdgeTag(AbstractComponent prevComp) {
        this.prevComp = prevComp;
    }

    public AbstractComponent getPrevComp() {
        return prevComp;
    }
}
