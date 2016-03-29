package truecolor.mdwb.utils;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.text.style.UnderlineSpan;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import truecolor.mdwb.R;
import truecolor.mdwb.apps.DetailActivity;
import truecolor.mdwb.apps.MaterialDesignWBApps;
import truecolor.mdwb.model.EmotionsResult;
import truecolor.mdwb.model.Oauth2AccessToken;

/**
 * Created by xiaowu on 15/8/14.
 */
public class Utils {

    public static Oauth2AccessToken accessToken;
    public static com.weibo.sdk.android.Oauth2AccessToken WEIBO_ACCESS_TOKEN;

    public static int SCREEN_WIDTH;
    public static int SCREEN_HEIGHT;

    /**
     * From代号转换汉字
     * @param str From代号
     * @return 来自哪种终端
     * @date 2014-5-15
     */
    public static String StrToFrom(String str, Context context)
    {
        String returnStr="";
        if(str.equals("0"))
        {;
            returnStr = context.getResources().getString(R.string.form_web);
        }
        if(str.equals("1"))
        {
            returnStr = context.getResources().getString(R.string.form_phone_web);
        }
        if(str.equals("2"))
        {
            returnStr = context.getResources().getString(R.string.form_android);
        }
        if(str.equals("3"))
        {
            returnStr = context.getResources().getString(R.string.form_iphone);
        }
        if(str.equals("4"))
        {
            returnStr = context.getResources().getString(R.string.form_ipad);
        }
        return returnStr;
    }

    public static int getInt(String str, int defaultValue) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException ignore) {
        }
        return defaultValue;
    }

    public static int getNumberDigit(int number){
        int digit = 1;
        for(;;){
            int result = number / 10;
            if(result == 0){
                break;
            }
            number /= 10;
            digit++;
        }
        return digit;
    }

    public static void startActivity(Activity activity, Class<?> clazz){
        Intent intent = new Intent();
        intent.setClass(activity, clazz);
        activity.startActivity(intent);
//        activity.overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    public static void startActivityForResult(Activity activity, Class<?> clazz){
        Intent intent = new Intent();
        intent.setClass(activity, clazz);
//        activity.startActivityForResult(intent, Constant.EXIT_CODE);
//        activity.overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    public final static String changeDateToString(long time){
        Date date = new Date(time * 1000);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = format.format(date);
        return dateStr;
    }

    public final static String changeTimeToString(long time){
        Date date = new Date(time * 1000);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = format.format(date);
        return dateStr;
    }

    public final static String changeTimeToDay(long time){
        int day = (int) (time / 3600 / 24);
        int hour = (int) (time / 3600 % 24);
        StringBuffer sb = new StringBuffer();
        sb.append(day);
        if(hour != 0) {
            sb.append(".");
            sb.append(hour);
        }
        return sb.toString();
    }

    public static void setWeiboAccessToken(String mToken, String mRefreshToken, long mExpiresTime){
        WEIBO_ACCESS_TOKEN = new com.weibo.sdk.android.Oauth2AccessToken();
        WEIBO_ACCESS_TOKEN.setToken(mToken);
        WEIBO_ACCESS_TOKEN.setRefreshToken(mRefreshToken);
        WEIBO_ACCESS_TOKEN.setExpiresTime(mExpiresTime);
    }

    public static com.weibo.sdk.android.Oauth2AccessToken getWeiboAccessToken(){
        return WEIBO_ACCESS_TOKEN;
    }

    public static void setDisplayResolution(WindowManager windowManager) {
        Display display = windowManager.getDefaultDisplay();
        SCREEN_WIDTH = display.getWidth();
        SCREEN_HEIGHT = display.getHeight();
    }

    public static int getScreenWidth(){
        return SCREEN_WIDTH;
    }

    public static int getScreenHeight(){
        return SCREEN_HEIGHT;
    }

    public static void copy(String content, Context context)
    {
        ClipboardManager cmb = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(content.trim());
    }

    public static Editable getEmotionsContent(TextView textView, String content, Context context){
        int starts;
        int end;
        String topStr;
        String emotionStr;
        textView.setText("");
        Editable editable = textView.getEditableText();
        if(content.indexOf("[", 0) != -1 && content.indexOf("]", 2) != -1){

            do {
                starts = content.indexOf("[", 0);
                if (starts > 0) {
                    topStr = content.substring(0, starts);
                    end = content.indexOf("]", starts + 2);
                    editable.append(topStr);
                } else {
                    end = content.indexOf("]", 2);
                }
                emotionStr = content.substring(starts, end + 1);
                SpannableString ss = txtToImg(emotionStr, context);
                editable.append(ss);
                content = content.substring(end + 1, content.length());
            } while(content.indexOf("[", 0) != -1 && content.indexOf("]", 2) != -1);
            editable.append(content);
        }else {
            editable.append(content);
        }
        return editable;
    }

    public static SpannableString txtToImg(String content, Context context){
        List<EmotionsResult> emotionsList = MaterialDesignWBApps.EMOTIONS;
        SpannableString ss = new SpannableString(content);
        int starts = 0;
        int end = 0;

        if (emotionsList == null){
           return ss;
        }

        if(content.indexOf("[", starts) != -1 && content.indexOf("]", end) != -1){
            starts = content.indexOf("[", starts);
            end = content.indexOf("]", end);
            String phrase = content.substring(starts,end + 1);
            String imageName = "";
            for (EmotionsResult emotions : emotionsList) {
                if (emotions.getValue().equals(phrase)) {
                    imageName = emotions.getUrl();
                    imageName = imageName.substring(0, imageName.length() - 4);
                    break;
                }
            }

            try {
                Class mipmapClass = R.mipmap.class;
                Field field = mipmapClass.getDeclaredField(imageName);
                int res= field.getInt(imageName);
                Drawable drawable = context.getResources().getDrawable(res);
                if (drawable != null) {
                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                    ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
                    ss.setSpan(span, starts, end + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }
        return ss;
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int getActionBarHeight(Context context) {
        int result = 0;
        TypedValue tv = new TypedValue();
        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            result = TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());
        }
        return result;
    }



}
