package com.slc.code.ui.weight.tool_bar;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.AppCompatImageButton;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.slc.code.R;
import com.slc.code.ui.utils.SlcUltimateBar;
import com.slc.code.ui.views.ToolBarView;
import com.slc.code.ui.views.ToolBarViewIndependent;

import java.util.HashMap;
import java.util.Map;

public class CustomToolBar extends BaseToolBar {

    private AppCompatImageButton home_as_up;
    private TextView title, subTitle;
    private ActionMenuView actionMenuView;

    public static ToolBarViewIndependent getCustomToolBar(Activity activity, int barStyle) {
        return new CustomToolBar(activity, barStyle);
    }

    private CustomToolBar(Activity activity, int barStyle) {
        super(activity, barStyle);
    }

    @Override
    public void setToolBarTitle(int stringRes) {
        title.setText(stringRes);
    }

    @Override
    public void setToolBarTitle(CharSequence charSequence) {
        title.setText(charSequence);
    }

    @Override
    public void setToolBarSubTitle(int stringRes) {
        subTitle.setText(stringRes);
    }

    @Override
    public void setToolBarSubTitle(CharSequence charSequence) {
        subTitle.setText(charSequence);
    }

    @Override
    public void setNavigationIcon(int drawableRes) {
        isSetNavigationIcon = true;
        home_as_up.setImageResource(drawableRes);
    }

    @Override
    public void setNavigationIcon(Drawable drawable) {
        isSetNavigationIcon = true;
        home_as_up.setImageDrawable(drawable);
    }

    @Override
    public int getToolBarViewRes() {
        return mBarStyle == SlcUltimateBar.LIGHT ? R.layout.toolbar_view_custom_light : R.layout.toolbar_view_custom_dark;
    }

    @Override
    public void bindToolView(View toolView) {
        home_as_up = toolView.findViewById(R.id.home_as_up);
        home_as_up.setVisibility(View.GONE);
        title = toolView.findViewById(R.id.title);
        subTitle = toolView.findViewById(R.id.subTitle);
        actionMenuView = toolView.findViewById(R.id.actionMenuView);
        actionMenuView.setVisibility(View.GONE);
    }

    @Override
    public void showNavigation() {
        //TODO
        if (!isSetNavigationIcon) {
            home_as_up.setImageResource(mBarStyle == SlcUltimateBar.LIGHT ? R.drawable.ic_native_back_dark : R.drawable.ic_native_back_light);
        }
        home_as_up.setVisibility(View.VISIBLE);
        home_as_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigationOnClick();
            }
        });
    }

    @Override
    public void showNavigation(boolean isShow) {
        if (isShow) {
            showNavigation();
        } else {
            home_as_up.setVisibility(View.GONE);
        }
    }

    public TextView getTitleView() {
        return title;
    }

    public TextView getSubTitleView() {
        return subTitle;
    }

    @Override
    public void setMenuView(int menuLayout) {
        mActivity.getMenuInflater().inflate(menuLayout, actionMenuView.getMenu());
    }

    @Override
    public ActionMenuView getActionMenuView() {
        return actionMenuView;
    }

    @Override
    public void showMenuView(boolean isShow) {
        actionMenuView.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setOnMenuItemClickListener(final MenuItem.OnMenuItemClickListener listener) {
        super.setOnMenuItemClickListener(listener);
        actionMenuView.setOnMenuItemClickListener(new ActionMenuView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return listener.onMenuItemClick(item);
            }
        });
    }

    @Override
    public Menu getMenu() {
        return actionMenuView.getMenu();
    }
}
