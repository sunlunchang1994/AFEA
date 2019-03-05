package com.slc.code.ui.utils;

import android.content.Context;
import android.util.TypedValue;

import com.slc.code.R;

/**
 * Created by on the way on 2017/11/8.
 */

public class DimenUtil {
    private static int status_bar_height, navigation_bar_height;

    /**
     * /**
     * 将sp值转换为px值，保证文字大小不变
     * （DisplayMetrics类中属性scaledDensity）
     *
     * @param context
     * @param spValue
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     * （DisplayMetrics类中属性scaledDensity）
     *
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     * （DisplayMetrics类中属性density）
     *
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param context
     * @param dipValue
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 获取状态栏高度
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        if (status_bar_height != 0)
            return status_bar_height;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        status_bar_height = context.getResources().getDimensionPixelSize(resourceId);
        return status_bar_height;
    }

    /**
     * 获取导航栏高度
     *
     * @param context
     * @return
     */
    public static int getNavigationHeight(Context context) {
        if (navigation_bar_height != 0)
            return navigation_bar_height;
        int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        navigation_bar_height = context.getResources().getDimensionPixelSize(resourceId);
        return navigation_bar_height;
    }

    /**
     * 获取工具栏高度
     *
     * @param context
     * @return
     */
    public static int getToolBarHeight(Context context) {
        TypedValue tv = new TypedValue();
        if (context.getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)) {
            System.out.print("actionBarSize");
            return TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());
        }
        return dip2px(context, 56);
    }

}
