package dulinglai.android.ate.model.entities;

import dulinglai.android.ate.data.values.ResourceValueProvider;

import java.util.ArrayList;
import java.util.List;

public class LayoutEntity extends Entity {

    private final int id;

    // TODO: remove EReg if we are not using it
    private String searchedEReg; // main LayoutReg that is created (later called: setContentView(layoutMainReg))

    private int layoutId;
    private String layoutIdString = "";
    private String layoutIdReg = ""; // reg where the ID of the layout is stored

    private String sootClassName = "";

    private List<Integer> addedLayouts;
    private List<Integer> rootLayouts;

    private boolean isFragment;
    private boolean isActivity;

    public LayoutEntity(String mainLayoutReg, int pid) {
        super(mainLayoutReg);
        addedLayouts = new ArrayList<>();
        rootLayouts = new ArrayList<>();
        id = pid;
    }

    // clones this object into new object/LayoutInfo
    public LayoutEntity clone(){
        LayoutEntity layout = new LayoutEntity(searchedEReg, ResourceValueProvider.getNewUniqueID());
        layout.setFragment(isFragment);
        layout.setActivity(isActivity);
        layout.setSootClassName(sootClassName);
        layout.setName(name);
        layout.setLayoutId(layoutId);
        layout.setLayoutIdString(layoutIdString);
        return layout;
    }

    @Override
    public boolean allValuesFound() {
        return (!layoutIdString.equals("") && layoutIdReg.equals("") && !sootClassName.equals(""));
    }

    /*
    Setters and getters
     */
    public void setFragment(boolean fragment) {
        isFragment = fragment;
    }

    public String getSootClassName() {
        return sootClassName;
    }

    public void setSootClassName(String sootClassName) {
        this.sootClassName = sootClassName;
    }

    public int getLayoutId() {
        return layoutId;
    }

    public void setLayoutId(int layoutId) {
        this.layoutId = layoutId;
    }

    public String getLayoutIdString() {
        return layoutIdString;
    }

    public void setLayoutIdString(String layoutIdString) {
        this.layoutIdString = layoutIdString;
    }

    public boolean isActivity() {
        return isActivity;
    }

    public void setActivity(boolean activity) {
        isActivity = activity;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + id;
        result = prime * result + layoutId;
        result = prime * result + ((sootClassName == null) ? 0 : sootClassName.hashCode());
        result = prime * result + ((layoutIdString == null) ? 0 : layoutIdString.hashCode());
        result = prime * result + ((layoutIdReg == null) ? 0 : layoutIdReg.hashCode());
        result = prime * result + ((rootLayouts == null) ? 0 : rootLayouts.hashCode());
        result = prime * result + ((addedLayouts == null) ? 0 : addedLayouts.hashCode());
        result = prime * result + (isFragment ? 1231 : 1237);
        result = prime * result + (isActivity ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        LayoutEntity other = (LayoutEntity) obj;
        if (id != other.id)
            return false;
        if (layoutId != other.layoutId)
            return false;
        if (sootClassName == null) {
            if (other.sootClassName != null)
                return false;
        } else if (!sootClassName.equals(other.sootClassName))
            return false;
        if (layoutIdString == null) {
            if (other.layoutIdString != null)
                return false;
        } else if (!layoutIdString.equals(other.layoutIdString))
            return false;
        if (layoutIdReg == null) {
            if (other.layoutIdReg != null)
                return false;
        } else if (!layoutIdReg.equals(other.layoutIdReg))
            return false;
        if (rootLayouts == null) {
            if (other.rootLayouts != null)
                return false;
        } else if (!rootLayouts.equals(other.rootLayouts))
            return false;
        if (addedLayouts == null) {
            if (other.addedLayouts != null)
                return false;
        } else if (!addedLayouts.equals(other.addedLayouts))
            return false;
        if (isFragment != other.isFragment)
            return false;
        return isActivity == other.isActivity;
    }
}
