package com.slc.code.ui.views;

import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.MenuRes;
import android.support.annotation.StringRes;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.ActionMenuView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

/**
 * Created by on the way on 2018/6/25.
 */
//TODO 此处需要增加几个方法 让子类决定展示什么样的视图 如类似简书、转转和普通的视图
public interface ToolBarView {

    /**
     * 设置工具栏标题文本
     *
     * @param stringRes
     */
    void setToolBarTitle(@StringRes int stringRes);

    /**
     * 设置工具栏标题文本
     */
    void setToolBarTitle(CharSequence charSequence);

    /**
     * 设置工具栏副标题文本
     *
     * @param stringRes
     */
    void setToolBarSubTitle(@StringRes int stringRes);

    /**
     * 设置工具栏副标题文本
     */
    void setToolBarSubTitle(CharSequence charSequence);

    /**
     * 设置导航栏返回按钮图标
     *
     * @param drawableRes
     */
    void setNavigationIcon(@DrawableRes int drawableRes);

    /**
     * 设置导航栏返回按钮图标
     *
     * @param drawable
     */
    void setNavigationIcon(Drawable drawable);

    /**
     * 获取工具栏视图
     *
     * @return layoutResId
     */
    int getToolBarViewRes();

    /**
     * 绑定工具栏试图
     *
     * @param toolView
     */
    void bindToolView(View toolView);

    /**
     * 设置ToolBar背景
     *
     * @param resId
     */
    void setToolBarBackgroundResource(@DrawableRes int resId);

    /**
     * 设置ToolBar背景
     */
    void setToolBarBackground(Drawable background);

    /**
     * 设置ToolBar背景
     */
    void setToolBarBackgroundColor(@ColorInt int color);

    /**
     * 导航按钮点击事件
     */
    void navigationOnClick();

    /**
     * 显示导航按钮
     */
    void showNavigation();

    void showNavigation(boolean isShow);

    /**
     * 设置ToolBar阴影
     *
     * @param elevation
     */
    void setToolBarElevation(float elevation);

    /**
     * 获取title的textView
     *
     * @return
     */
    TextView getTitleView();

    /**
     * 获取SubTitle的textView
     *
     * @return
     */
    TextView getSubTitleView();

    /**
     * 设置菜单视图
     *
     * @param menuLayout
     */
    void setMenuView(@MenuRes int menuLayout);

    /**
     * 获取菜单事件的视图
     * @return
     */
    ActionMenuView getActionMenuView();

    /**
     * 显示菜单视图
     *
     * @param isShow
     */
    void showMenuView(boolean isShow);

    /**
     * 设置菜单监听事件
     *
     * @param listener
     */
    void setOnMenuItemClickListener(MenuItem.OnMenuItemClickListener listener);

    /**
     * 获取菜单
     *
     * @return
     */
    Menu getMenu();
}
