package com.slc.code.ui.weight.tool_bar;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.ActionProvider;
import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.slc.code.R;
import com.slc.code.ui.menu.BaseActionProvider;
import com.slc.code.ui.utils.DimenUtil;
import com.slc.code.ui.utils.SlcUltimateBar;
import com.slc.code.ui.views.ToolBarViewIndependent;

/**
 * Created by achang on 2018/12/18.
 */

public abstract class BaseToolBar implements ToolBarViewIndependent {
    protected Activity mActivity;
    protected int mBarStyle;
    protected AppBarLayout appBarLayout;
    protected View mToolBarView;

    protected boolean isSetNavigationIcon;
    private OnNavigationClickListener onNavigationClickListener;


    protected BaseToolBar(Activity activity, int barStyle) {
        this.mActivity = activity;
        this.mBarStyle = barStyle;
        appBarLayout = (AppBarLayout) activity.getLayoutInflater().inflate(barStyle == SlcUltimateBar.LIGHT ? R.layout.app_bar_layout_light : R.layout.app_bar_layout_dark, null);
        appBarLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mToolBarView = activity.getLayoutInflater().inflate(getToolBarViewRes(), appBarLayout, false);
        appBarLayout.addView(mToolBarView);
        bindToolView(mToolBarView);
    }

    @Override
    public void setToolBarBackgroundResource(int resId) {
        appBarLayout.setBackgroundResource(resId);
    }

    @Override
    public void setToolBarBackground(Drawable background) {
        appBarLayout.setBackgroundDrawable(background);
    }

    @Override
    public void setToolBarBackgroundColor(int color) {
        appBarLayout.setBackgroundColor(color);
        if (color == Color.TRANSPARENT) {
            appBarLayout.getBackground().setAlpha(0);
        }
    }

    @Override
    public void navigationOnClick() {
        if (this.onNavigationClickListener != null) {
            onNavigationClickListener.onNavigationClick();
            return;
        }
        mActivity.onBackPressed();
    }

    @Override
    public void setOnNavigationClickListener(OnNavigationClickListener onNavigationClickListener) {
        this.onNavigationClickListener = onNavigationClickListener;
    }

    @Override
    public void setToolBarElevation(float elevation) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && appBarLayout != null) {
            appBarLayout.setElevation(elevation);
            if (elevation == 0) {
                appBarLayout.setTargetElevation(0f);
            }
        }
    }

    @Override
    public AppBarLayout getAppBarLayout() {
        return appBarLayout;
    }

    @Override
    public View getToolBarView() {
        return mToolBarView;
    }

    @Override
    public void setAppBarPaddingTop(int top) {
        appBarLayout.setPadding(0, top, 0, 0);
    }

    @Override
    public void setAppBarPaddingTopAdaptive() {
        setAppBarPaddingTop(DimenUtil.getStatusBarHeight(mActivity));
    }

    @Override
    public void setOnMenuItemClickListener(MenuItem.OnMenuItemClickListener listener) {
        Menu menu = getMenu();
        MenuItem menuItem = null;
        ActionProvider actionProvider = null;
        for (int i = 0; i < menu.size(); i++) {
            menuItem = menu.getItem(i);
            actionProvider = MenuItemCompat.getActionProvider(menuItem);
            if (actionProvider instanceof BaseActionProvider) {
                BaseActionProvider baseActionProvider = (BaseActionProvider) actionProvider;
                baseActionProvider.setSubMenuItem(menuItem.getSubMenu());
                baseActionProvider.setOnMenuItemClickListener(listener);
            }
        }
    }
}
