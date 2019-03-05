package com.slc.code.ui.fragment;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.AppCompatImageButton;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.slc.code.R;
import com.slc.code.contract.MvpContract;
import com.slc.code.ui.utils.SlcUltimateBar;

/**
 * Created by on the way on 2018/7/7.
 */

public abstract class CustomToolBarFragment<P extends MvpContract.BasePresenter> extends BaseToolbarFragment<P> /*implements CustomToolBarView*/ {
    private AppCompatImageButton home_as_up;
    private TextView title, subTitle;
    private ActionMenuView actionMenuView;
    private boolean isSetNavigationIcon;

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
        return getBarStyle() == SlcUltimateBar.LIGHT ? R.layout.toolbar_view_custom_light : R.layout.toolbar_view_custom_dark;
    }

    @Override
    public void bindToolView(View toolView) {
        //TODO
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
            home_as_up.setImageResource(getBarStyle() == SlcUltimateBar.LIGHT ? R.drawable.ic_native_back_dark : R.drawable.ic_native_back_light);
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

    /*@Override
    public ActionMenuView getActionMenuView() {
        return actionMenuView;
    }*/

    @Override
    public void setMenuView(int menuLayout) {
        getMvpContext().getMenuInflater().inflate(menuLayout, actionMenuView.getMenu());
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
