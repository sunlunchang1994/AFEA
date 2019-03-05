package com.slc.code.ui.views;

import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.MenuRes;
import android.support.annotation.StringRes;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.ActionMenuView;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

/**
 * Created by on the way on 2018/6/25.
 */
//TODO 此处需要增加几个方法 让子类决定展示什么样的视图 如类似简书、转转和普通的视图
public interface ToolBarViewIndependent extends ToolBarView {
    AppBarLayout getAppBarLayout();

    void setAppBarPaddingTop(int top);

    void setAppBarPaddingTopAdaptive();

    View getToolBarView();
    void setOnNavigationClickListener(OnNavigationClickListener onNavigationClickListener);
    interface OnNavigationClickListener {
        void onNavigationClick();
    }
}
