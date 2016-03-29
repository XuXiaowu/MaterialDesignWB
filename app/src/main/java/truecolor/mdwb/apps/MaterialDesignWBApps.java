package truecolor.mdwb.apps;

import android.app.Application;
import android.content.Context;

import java.util.List;

import truecolor.mdwb.model.EmotionsResult;
import truecolor.mdwb.model.FriendsTimelineResult;

/**
 * Created by xiaowu on 15/11/19.
 */
public class MaterialDesignWBApps extends Application {

    public static List<EmotionsResult> EMOTIONS;

    public static Context getAppContext(){
        return getAppContext();
    }

    public static FriendsTimelineResult.FriendsTimeline EXTRA_STATUS;
}
