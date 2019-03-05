package com.slc.code.ui.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

/**
 * 终极版状态栏和导航栏工具，app界面美化神器！！！
 * Created by on the way on 2018/6/28.
 */

public class SlcUltimateBar {
    private Activity mActivity;
    public static final int STATUS_BAR = 1;//状态栏
    public static final int NAVIGATION_BAR = 2;//导航栏
    private final int statusBarAndNavigationBar = STATUS_BAR | NAVIGATION_BAR;
    public static final int DARK = 0, LIGHT = 1;//暗黑和高亮风格
    private View mStatusBarTintView;//状态栏视图
    private View mNavBarTintView;//导航栏视图

    public SlcUltimateBar(Activity activity) {
        this.mActivity = activity;
    }

    /**
     * 设置通知栏显示或隐藏
     *
     * @param visibility
     * @return
     */
    public SlcUltimateBar setStatusBarVisibility(boolean visibility) {
        if (visibility) {
            WindowManager.LayoutParams attr = mActivity.getWindow().getAttributes();
            attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            mActivity.getWindow().setAttributes(attr);
            mActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            mActivity.getWindow().setAttributes(lp);
            mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        return this;
    }

    /**
     * 设置状态栏和导航栏的颜色
     * 默认方法 状态栏和导航栏都会被设置
     *
     * @param color 颜色值
     * @return
     */
    @Deprecated
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public SlcUltimateBar setColorBar(@ColorInt int color) {
        return setColorBar(color, 0);
    }

    /**
     * 设置状态栏和导航栏颜色
     * 默认方法 状态栏和导航栏都会被设置
     *
     * @param color 颜色值
     * @param alpha 透明度0~255之间
     * @return
     */
    @Deprecated
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public SlcUltimateBar setColorBar(@ColorInt int color, int alpha) {
        return setColorBar(color, alpha, statusBarAndNavigationBar);
    }

    /**
     * 设置状态栏和导航栏颜色
     * 由用户选择颜色值、透明度、和位置
     *
     * @param color    颜色值
     * @param alpha    透明度
     * @param location 位置（位置可选的值有STATUS_BAR、NAVIGATION_BAR、STATUS_BAR|NAVIGATION_BAR）
     * @return
     */
    @Deprecated
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public SlcUltimateBar setColorBar(@ColorInt int color, int alpha, int location) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = mActivity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            int alphaColor = alpha == 0 ? color : calculateColor(color, alpha);
            switch (location) {
                case statusBarAndNavigationBar:
                    setStatusBarColor(alphaColor);
                case NAVIGATION_BAR:
                    setNavigationBarColor(alphaColor);
                    break;
                case STATUS_BAR:
                    setStatusBarColor(alphaColor);
                    break;
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = mActivity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            int alphaColor = alpha == 0 ? color : calculateColor(color, alpha);
            switch (location) {
                case statusBarAndNavigationBar:
                    setStatusBarColor(alphaColor);
                case NAVIGATION_BAR:
                    if (navigationBarExist()) {
                        setNavigationBarColor(alphaColor);
                        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                    }
                    break;
                case STATUS_BAR:
                    setStatusBarColor(alphaColor);
                    break;
            }
            setRootView(mActivity, true);
        }
        return this;
    }

    /**
     * 设置状态栏颜色
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void setStatusBarColor(int color) {
        Window window = mActivity.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(color);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ViewGroup decorView = (ViewGroup) window.getDecorView();
            if (mStatusBarTintView == null) {
                decorView.addView(createStatusBarView(mActivity, color));
            } else {
                mStatusBarTintView.setBackgroundColor(color);
            }
        }
    }

    /**
     * 设置导航栏颜色
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void setNavigationBarColor(int color) {
        Window window = mActivity.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setNavigationBarColor(color);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ViewGroup decorView = (ViewGroup) window.getDecorView();
            if (mStatusBarTintView == null) {
                decorView.addView(createNavBarView(mActivity, color));
            } else {
                mNavBarTintView.setBackgroundColor(color);
            }
        }
    }

    /**
     * 设置透明沉浸状态栏
     * 默认方法 状态栏和导航栏全部沉浸，风格为暗黑
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void setImmersionBar() {
        setTransparentBar(Color.TRANSPARENT, 0, statusBarAndNavigationBar, DARK);
    }

    /**
     * 设置透明沉浸状态栏
     * 由用户设置状态栏和导航栏是否沉浸，风格为暗黑
     *
     * @param location 位置（可选择的为STATUS_BAR、NAVIGATION_BAR、STATUS_BAR|NAVIGATION_BAR）
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void setImmersionBar(int location) {
        setTransparentBar(Color.TRANSPARENT, 0, location, DARK);
    }

    /**
     * 设置透明沉浸状态栏
     * 由用户设置状态栏和导航栏是否沉浸，风格为暗黑
     *
     * @param location 位置（可选择的为STATUS_BAR、NAVIGATION_BAR、STATUS_BAR|NAVIGATION_BAR）
     * @param style    状态栏风格 （可选择为DARK、LIGHT设置为DARK时状态栏字体为白色、设置为LIGHT时状态栏字体为黑色，该参数只有当系统api版本大于等于23时才有效）
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void setImmersionBar(int location, int style) {
        setTransparentBar(Color.TRANSPARENT, 0, location, style);
    }

    /**
     * 设置沉浸状态栏
     *
     * @param color    状态栏颜色
     * @param alpha    颜色透明度
     * @param location 位置（可选择的为STATUS_BAR、NAVIGATION_BAR、STATUS_BAR|NAVIGATION_BAR）
     * @param style    状态栏风格 （可选择为DARK、LIGHT 设置为DARK时状态栏字体为白色、设置为LIGHT时状态栏字体为黑色，该参数只有当系统api版本大于等于23时才有效）
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void setTransparentBar(@ColorInt int color, int alpha, int location, int style) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = mActivity.getWindow();
            View decorView = window.getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            switch (location) {
                case statusBarAndNavigationBar:
                case NAVIGATION_BAR:
                    option = option | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
                    break;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                switch (style) {
                    case DARK:
                        break;
                    case LIGHT:
                        option = option | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR/*|View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR*/;
                        break;
                }
            }
            decorView.setSystemUiVisibility(option);

            int finalColor = alpha == 0 ? Color.TRANSPARENT : Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
            switch (location) {
                case statusBarAndNavigationBar:
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M && finalColor == 0 && style == LIGHT) {
                        setStatusBarColor(Color.parseColor("#66000000"));
                    } else {
                        setStatusBarColor(finalColor);
                    }
                case NAVIGATION_BAR:
                    setNavigationBarColor(finalColor);
                    break;
                case STATUS_BAR:
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M && finalColor == 0 && style == LIGHT) {
                        setStatusBarColor(Color.parseColor("#66000000"));
                    } else {
                        setStatusBarColor(finalColor);
                    }
                    break;
            }
            /*window.setStatusBarColor(finalColor);
            window.setNavigationBarColor(finalColor);*/
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = mActivity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            int finalColor = alpha == 0 ? Color.TRANSPARENT :
                    Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
            switch (location) {
                case statusBarAndNavigationBar:
                    setStatusBarColor(finalColor);
                case NAVIGATION_BAR:
                    if (navigationBarExist()) {
                        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
                        setNavigationBarColor(finalColor);
                    }
                    break;
                case STATUS_BAR:
                    setStatusBarColor(finalColor);
                    break;
            }
            //setRootView(mActivity, true);
        }
    }


    /**
     * 导航栏是否存在
     *
     * @return true为存在，false为不存在
     */
    private boolean navigationBarExist() {
        WindowManager windowManager = mActivity.getWindowManager();
        Display d = windowManager.getDefaultDisplay();

        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            d.getRealMetrics(realDisplayMetrics);
        }
        int realHeight = realDisplayMetrics.heightPixels;
        int realWidth = realDisplayMetrics.widthPixels;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        d.getMetrics(displayMetrics);

        int displayHeight = displayMetrics.heightPixels;
        int displayWidth = displayMetrics.widthPixels;

        return (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
    }

    /**
     * 计算颜色
     * 根据颜色和透明度计算最终颜色值
     *
     * @param color 颜色
     * @param alpha 透明度
     * @return 最终颜色
     */
    @ColorInt
    private int calculateColor(@ColorInt int color, int alpha) {
        float a = 1 - alpha / 255f;
        int red = color >> 16 & 0xff;
        int green = color >> 8 & 0xff;
        int blue = color & 0xff;
        red = (int) (red * a + 0.5);
        green = (int) (green * a + 0.5);
        blue = (int) (blue * a + 0.5);
        return 0xff << 24 | red << 16 | green << 8 | blue;
    }

    /**
     * 获取根视图
     *
     * @param activity
     * @param fit
     */
    private void setRootView(Activity activity, boolean fit) {
        ViewGroup parent = activity.findViewById(android.R.id.content);
        for (int i = 0, count = parent.getChildCount(); i < count; i++) {
            View childView = parent.getChildAt(i);
            if (childView instanceof ViewGroup) {
                childView.setFitsSystemWindows(fit);
                ((ViewGroup) childView).setClipToPadding(fit);
            }
        }
    }

    /**
     * 创建状态栏视图
     *
     * @param context
     * @param color
     * @return
     */
    private View createStatusBarView(Context context, @ColorInt int color) {
        if (mStatusBarTintView != null) {
            return mStatusBarTintView;
        }
        mStatusBarTintView = new View(context);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams
                (FrameLayout.LayoutParams.MATCH_PARENT, DimenUtil.getStatusBarHeight(context));
        params.gravity = Gravity.TOP;
        mStatusBarTintView.setLayoutParams(params);
        mStatusBarTintView.setBackgroundColor(color);
        return mStatusBarTintView;
    }

    /**
     * 创建导航栏视图
     *
     * @param context
     * @param color
     * @return
     */
    private View createNavBarView(Context context, @ColorInt int color) {
        if (mNavBarTintView != null) {
            return mNavBarTintView;
        }
        mNavBarTintView = new View(context);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams
                (FrameLayout.LayoutParams.MATCH_PARENT, DimenUtil.getNavigationHeight(context));
        params.gravity = Gravity.BOTTOM;
        mNavBarTintView.setLayoutParams(params);
        mNavBarTintView.setBackgroundColor(color);
        return mNavBarTintView;
    }

    public void destroy() {
        mStatusBarTintView = null;
        mNavBarTintView = null;
        mActivity = null;
    }

    /**
     * 获取状态栏高度
     *
     * @param context
     * @return
     */
    /*public static int getStatusBarHeight(Context context) {
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }*/

    /**
     * 获取导航栏高度
     *
     * @param context
     * @return
     */
    /*public static int getNavigationHeight(Context context) {
        int resourceId = context.getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }*/

}
