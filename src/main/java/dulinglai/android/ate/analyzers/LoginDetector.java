package dulinglai.android.ate.analyzers;

import android.text.InputType;
import dulinglai.android.ate.model.components.Activity;
import dulinglai.android.ate.model.widgets.AbstractWidget;
import dulinglai.android.ate.model.widgets.EditWidget;
import dulinglai.android.ate.resources.androidConstants.EditTextInputType;
import org.pmw.tinylog.Logger;
import soot.SootClass;
import soot.util.HashMultiMap;
import soot.util.MultiMap;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class LoginDetector {

    private static final Pattern[] signUpAliases = {
            Pattern.compile(".*(?i)(create|register)[\\s_\\-]*[new]?[\\s_\\-]*(account|user|id).*"),
            Pattern.compile(".*(?i)(sign)[\\s_\\-]*up.*"),
            Pattern.compile(".*(?i)(new)[\\s_\\-]*(account|card|email).*")};

    private static final Pattern[] forgotPasswordAliases = {
            Pattern.compile(".*(?i)forgot[\\s\\_\\-]*(your)?[\\s\\_\\-]*(password|(user|account|card|client)[\\s\\_\\-]*(name|id|number|#)).*"),
            Pattern.compile(".*(?i)(trouble|help)[\\s\\_\\-]*(signing|logging)[\\s\\_\\-]*in.*"),
            Pattern.compile(".*(?i)(old|original|new|confirm)[\\s\\_\\-]*(pass|pin).*"),
            Pattern.compile(".*(?i)(reset)[\\s\\_\\-]*(pass|pin|pwd).*")};

    private static final Pattern passwordRegex = Pattern.compile(".*(?i)(pass|pin)[\\s_]*(word|code|wd).*");
    private static final Pattern userNameRegex_1 = Pattern.compile(".*(?i)(user|account|client|phone|card)[\\s_]*(name|id|number|#).*");
    private static final Pattern userNameRegex_2 = Pattern.compile(".*(?i)(log|sign)[\\s_]*in.*");

    private MultiMap<SootClass, String> potentialLoginMap;
    private List<Activity> activityList;
    private static boolean foundSignUp = false;
    private static boolean foundRecovery = false;

    private Set<Activity> potentialLoginActivity = new HashSet<>();
    private MultiMap<Activity, AbstractWidget> potentialPasswordWidget = new HashMultiMap<>();
    private MultiMap<Activity, AbstractWidget> potentialUsernameWidget = new HashMultiMap<>();

    private MultiMap<Activity, AbstractWidget> ownershipEdges;

    public LoginDetector(MultiMap<SootClass, String> potentialLoginMap, MultiMap<Activity,
            AbstractWidget> ownershipEdges, List<Activity> activityList) {
        this.potentialLoginMap = potentialLoginMap;
        this.ownershipEdges = ownershipEdges;
        this.activityList = activityList;
    }

    /**
     * Detects the login activity and widgets
     */
    public void detectPotentialLogin() {
        // To detect login widgets, we do following:
        // if the activity name matches regex
        // if the widget input type is of password type
        // if the widget properties match regex
        for (Activity activity : activityList) {
            foundRecovery = false;
            foundSignUp = false;
            boolean isLoginActivity = false;
            // read potential logins from class files
            for (SootClass sc : potentialLoginMap.keySet()) {
                if (sc.getName().equals(activity.getName())) {
                    potentialLoginActivity.add(activity);
                }
            }

            // Regex on activity name
            String activityName = activity.getName();
            if (activityName.contains("Account"))
                Logger.debug("Here");
            if (!isSignupOrRecovery(activityName, true)) {
                if (userNameRegex_1.matcher(activityName).find() || userNameRegex_2.matcher(activityName).find()) {
                    isLoginActivity = true;
                }
            }

            if (ownershipEdges.keySet().contains(activity)) {
                for (AbstractWidget widget : ownershipEdges.get(activity)) {
                    if (widget instanceof EditWidget) {
                        int widgetInputType = ((EditWidget) widget).getInputType();
                        if (widgetInputType == InputType.TYPE_TEXT_VARIATION_PASSWORD
                                || widgetInputType == EditTextInputType.textPassword
                                || widgetInputType == EditTextInputType.textWebPassword) {
                            potentialPasswordWidget.put(activity,widget);
                            isLoginActivity = true;
                        }

                        String widgetResIdString = widget.getResourceIdString();
                        String widgetText = widget.getText();
                        String widgetHint = ((EditWidget) widget).getHint();
                        String widgetContent = ((EditWidget) widget).getContentDescription();

                        if (regexSearchOnWidgetProperty(widgetResIdString, activity, widget) ||
                            regexSearchOnWidgetProperty(widgetText, activity, widget) ||
                            regexSearchOnWidgetProperty(widgetHint, activity, widget) ||
                            regexSearchOnWidgetProperty(widgetContent, activity, widget))
                            isLoginActivity = true;
                    }
                }
            }

            if (isLoginActivity) {
                if (!foundSignUp && !foundRecovery)
                    potentialLoginActivity.add(activity);
            }

        }
    }

    /**
     * Checks if the given string is a signup or account recovery string
     * @param string The string to check
     * @param isActivityName If the string to check is the activity name
     * @return True if the given string is a signup or account recovery string
     */
    private boolean isSignupOrRecovery(String string, boolean isActivityName) {
        for (Pattern signupPattern : signUpAliases) {
            if (signupPattern.matcher(string).find()) {
                if (isActivityName)
                    foundSignUp = true;
                return true;
            }
        }

        for (Pattern recoveryPattern : forgotPasswordAliases) {
            if (recoveryPattern.matcher(string).find()) {
                if (isActivityName)
                    foundRecovery = true;
                return true;
            }
        }

        return false;
    }

    private boolean regexSearchOnWidgetProperty(String searchString, Activity activity, AbstractWidget widget) {
        if (searchString!=null && !searchString.isEmpty()) {
            if (isSignupOrRecovery(searchString, false))
                return false;

            if (passwordRegex.matcher(searchString).find()) {
                potentialPasswordWidget.put(activity,widget);
                return true;
            }
            if (userNameRegex_1.matcher(searchString).find()
                    || userNameRegex_2.matcher(searchString).find()) {
                potentialUsernameWidget.put(activity,widget);
                return true;
            }
        }

        return false;
    }

    public Set<Activity> getPotentialLoginActivity() {
        return potentialLoginActivity;
    }

    public MultiMap<Activity, AbstractWidget> getPotentialPasswordWidget() {
        return potentialPasswordWidget;
    }

    public MultiMap<Activity, AbstractWidget> getPotentialUsernameWidget() {
        return potentialUsernameWidget;
    }
}
