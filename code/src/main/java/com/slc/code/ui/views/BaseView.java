package com.slc.code.ui.views;

import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;

/**
 * Created by on the way on 2018/7/6.
 */

public interface BaseView {
    //视图顶部展示方式
    public static final int UI_TOP_SHOW_MODE_NORMAL = 0;//正常
    public static final int UI_TOP_SHOW_MODE_COORDINATE = 1;//协调
    public static final int UI_TOP_SHOW_MODE_FADE_TO = 2;//透明渐变
    /**
     * 设置状态栏装饰的颜色
     */
    public void setStatusBarEmbellishViewColor(@ColorInt int color);

    /**
     * 设置状态栏装饰的drawable
     *
     * @param drawable
     */
    public void setStatusBarEmbellishViewDrawable(Drawable drawable);

    /**
     * 设置状态栏装饰的drawable
     *
     * @param drawableRes
     */
    public void setStatusBarEmbellishViewRes(@DrawableRes int drawableRes);
    /**
     * 设置状态栏装饰的颜色
     */
    public void setCodeRootViewColor(@ColorInt int color);

    /**
     * 设置状态栏装饰的drawable
     *
     * @param drawable
     */
    public void setCodeRootViewDrawable(Drawable drawable);

    /**
     * 设置状态栏装饰的drawable
     *
     * @param drawableRes
     */
    public void setCodeRootViewRes(@DrawableRes int drawableRes);
    /**
     * 设置状态栏修饰物是否显示
     *
     * @param isShow
     */
    public void setShowStatusBarEmbellishView(boolean isShow);

    /**
     * 状态栏修饰物是否显示
     *
     * @return
     */
    public boolean statusBarEmbellishViewIsShow();

    /**
     * 设置ui顶部展示模式
     */
    int getUiTopShowMode();

}
