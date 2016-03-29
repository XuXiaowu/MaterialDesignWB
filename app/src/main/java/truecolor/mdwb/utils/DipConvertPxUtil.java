package truecolor.mdwb.utils;

import android.content.Context;

/**
 * dip转换px单位
 * 
 * @author xiaowu
 * @date 2014-9-1
 */
public class DipConvertPxUtil {

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dipValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(pxValue / scale + 0.5f);
    }
}
