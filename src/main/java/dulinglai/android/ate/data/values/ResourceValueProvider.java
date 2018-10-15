package dulinglai.android.ate.data.values;

import org.pmw.tinylog.Logger;

import java.util.Map;

public class ResourceValueProvider {
    private final String RESOURCE_PARSER = "ResourceValueProvider";

    private final Map<Integer, String> stringResource;
    private final Map<Integer, String> resourceId;
    private final Map<String, Integer> layoutResource;

    public ResourceValueProvider(Map<Integer, String> stringResource, Map<Integer,String> resourceId,
                                 Map<String, Integer> layoutResource) {

        this.stringResource = stringResource;
        this.resourceId = resourceId;
        this.layoutResource = layoutResource;
    }

    public String getStringById(Integer id) {
        return stringResource.get(id);
    }

    public String getResourceIdString(Integer id) {
        return resourceId.get(id);
    }

    public Integer getResouceIdByString(String resourceString) {
        for (Integer key : resourceId.keySet()) {
            if (resourceId.get(key).equalsIgnoreCase(resourceString))
                return key;
        }
        return null;
    }

    public String findResourceById(Integer id) {
        String resString = getStringById(id);
        if (resString!=null)
            return resString;
        else {
            resString = getLayoutResourceString(id);
            if (resString!=null)
                return resString;
            else {
                Logger.warn("[{}] Cannot find layout ");
                return null;
            }
        }
    }

    public Integer getLayoutResourceId(String filename) {
        return layoutResource.get(filename);
    }

    public String getLayoutResourceString(Integer resId) {
        for (String layoutString : layoutResource.keySet()) {
            if (layoutResource.get(layoutString).equals(resId)) {
                return layoutString;
            }
        }
        return null;
    }

}
