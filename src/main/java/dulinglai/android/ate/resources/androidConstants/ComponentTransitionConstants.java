package dulinglai.android.ate.resources.androidConstants;

import java.util.Arrays;
import java.util.List;

public class ComponentTransitionConstants {

    /*
     * ============================== START ACTIVITY ==========================================
     */
    private static final String START_ACTIVITY_1 = "void startActivity(android.content.Intent)";
    private static final String START_ACTIVITY_2 = "void startActivity(android.content.Intent,android.os.Bundle)";
    private static final String START_ACTIVITY_FOR_RESULT_1 = "void startActivityForResult(android.content.Intent,int)";
    private static final String START_ACTIVITY_FOR_RESULT_2 = "void startActivityForResult(android.content.Intent,int,android.os.Bundle)";
    private static final String START_ACTIVITY_FROM_CHILD_2 = "void startActivityFromChild(android.app.Activity,android.content.Intent,int,android.os.Bundle)";
    private static final String START_ACTIVITY_FROM_CHILD_1 = "void startActivityFromChild(android.app.Activity,android.content.Intent,int)";
    private static final String START_ACTIVITY_FROM_FRAGMENT_1 = "void startActivityFromFragment(android.app.Fragment,android.content.Intent,int)";
    private static final String START_ACTIVITY_FROM_FRAGMENT_2 = "void startActivityFromFragment(android.app.Fragment,android.content.Intent,int,android.os.Bundle)";
    private static final String START_ACTIVITY_IF_NEEDED_1 = "void startActivityIfNeeded(android.content.Intent,int)";
    private static final String START_ACTIVITY_IF_NEEDED_2 = "void startActivityIfNeeded(android.content.Intent,int,android.os.Bundle)";
    private static final String START_ACTIVITIES_1 = "void startActivities(android.content.Intent[])";
    private static final String START_ACTIVITIES_2 = "void startActivities(android.content.Intent[],android.os.Bundle)";


    /*
     * ============================== START SERVICE ==========================================
     */
    private static final String START_SERVICE_1 = "android.content.ComponentName startService(android.content.Intent)";
    private static final String START_FOREGROUND_SERVICE_1 = "android.content.ComponentName startForegroundService(android.content.Intent)";

    /*
     * ============================== METHODS SUMMARY ==========================================
     */
    private static final String[] iccMethods = { START_ACTIVITY_1, START_ACTIVITY_2, START_ACTIVITY_FOR_RESULT_1,
            START_ACTIVITY_FOR_RESULT_2, START_ACTIVITY_FROM_CHILD_1, START_ACTIVITY_FROM_CHILD_2,
            START_ACTIVITY_FROM_FRAGMENT_1, START_ACTIVITY_FROM_FRAGMENT_2, START_ACTIVITY_IF_NEEDED_1,
            START_ACTIVITY_IF_NEEDED_2, START_ACTIVITIES_1, START_ACTIVITIES_2};
    private static final List<String> iccMethodsList = Arrays.asList(iccMethods);

    public static class ClickListener {
        public static final String CLICKLISTENER = "android.view.View$OnClickListener";
        public static final String CLICKLISTENER_ONCLICK = "void onClick(android.view.View)";
    }

    /*
     * ============================== GETTERS ==========================================
     */

    public static List<String> getIccMethodsList() {
        return iccMethodsList;
    }
}
