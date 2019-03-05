package com.slc.code.ui.activity;

import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.MenuRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.slc.code.R;
import com.slc.code.contract.MvpContract;
import com.slc.code.ui.utils.SlcUltimateBar;


/**
 * Created by ontheway on 2017/9/29.
 */

public abstract class NativeToolBarActivity<P extends MvpContract.BasePresenter> extends BaseToolBarActivity<P> {
    private boolean isSetNavigationIcon;

    @Override
    public final int getToolBarViewRes() {//TODO 写到此处
        return getBarStyle() == SlcUltimateBar.LIGHT ? R.layout.toolbar_view_native_light : R.layout.toolbar_view_native_dark;
    }

    @Override
    public final void showNavigation() {
        if (toolbar != null) {
            if (!isSetNavigationIcon) {
                getNativeToolbar().setNavigationIcon(getBarStyle() == SlcUltimateBar.LIGHT ? R.drawable.ic_native_back_dark : R.drawable.ic_native_back_light);
            }
            getNativeToolbar().setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navigationOnClick();
                }
            });
        }
    }

    @Override
    public void showNavigation(boolean isShow) {
        if (isShow) {
            showNavigation();
        } else {
            getNativeToolbar().setNavigationIcon(null);
        }
    }

    @Override
    public void bindToolView(View toolView) {
        //TODO
        getNativeToolbar().setNavigationIcon(null);
    }


    /**
     * 获取原生工具栏
     *
     * @return
     */
    protected final Toolbar getNativeToolbar() {
        return (Toolbar) toolbar;
    }

    /**
     * 设置工具栏标题文本
     *
     * @param stringRes
     */
    @Override
    public void setToolBarTitle(@StringRes int stringRes) {
        getNativeToolbar().setTitle(stringRes);
    }

    /**
     * 设置工具栏标题文本
     */
    @Override
    public void setToolBarTitle(CharSequence charSequence) {
        getNativeToolbar().setTitle(charSequence);
    }

    /**
     * 设置工具栏副标题文本
     *
     * @param stringRes
     */
    @Override
    public void setToolBarSubTitle(@StringRes int stringRes) {
        getNativeToolbar().setSubtitle(stringRes);
    }

    /**
     * 设置工具栏副标题文本
     */
    @Override
    public void setToolBarSubTitle(CharSequence charSequence) {
        getNativeToolbar().setSubtitle(charSequence);
    }

    /**
     * 设置导航栏返回按钮图标
     *
     * @param drawableRes
     */
    @Override
    public void setNavigationIcon(@DrawableRes int drawableRes) {
        isSetNavigationIcon = true;
        getNativeToolbar().setNavigationIcon(drawableRes);
    }

    /**
     * 设置导航栏返回按钮图标
     *
     * @param drawable
     */
    @Override
    public void setNavigationIcon(Drawable drawable) {
        isSetNavigationIcon = true;
        getNativeToolbar().setNavigationIcon(drawable);
    }

    @Override
    public TextView getTitleView() {
        boolean isTempText = false;
        CharSequence toolBarTitle = getNativeToolbar().getTitle();
        if (TextUtils.isEmpty(toolBarTitle)) {
            isTempText = true;
            getNativeToolbar().setTitle(getMvpContext().getPackageName());
            toolBarTitle = getMvpContext().getPackageName();
        }
        TextView titleText = getToolBarTextView(toolBarTitle);
        if (isTempText) {
            getNativeToolbar().setTitle("");
        }
        return titleText;
    }


    @Override
    public TextView getSubTitleView() {
        boolean isTempText = false;
        CharSequence toolBarSubTitle = getNativeToolbar().getSubtitle();
        if (TextUtils.isEmpty(toolBarSubTitle)) {
            isTempText = true;
            getNativeToolbar().setSubtitle(getMvpContext().getPackageName());
            toolBarSubTitle = getMvpContext().getPackageName();
        }
        TextView subtitleText = getToolBarTextView(toolBarSubTitle);
        if (isTempText) {
            getNativeToolbar().setSubtitle("");
        }
        return subtitleText;
    }

    /**
     * 根据字符串获取toolBar的控件
     *
     * @param text
     * @return
     */
    private TextView getToolBarTextView(@NonNull CharSequence text) {
        for (int i = 0; i < getNativeToolbar().getChildCount(); i++) {
            View v = getNativeToolbar().getChildAt(i);
            if (v != null && v instanceof TextView) {
                TextView t = (TextView) v;
                CharSequence textTemp = t.getText();
                if (!TextUtils.isEmpty(textTemp) && text.equals(textTemp) && t.getId() == View.NO_ID) {
                    return t;
                }
            }
        }
        return null;
    }

    @Override
    public void setMenuView(@MenuRes int menuLayout) {
        //getMenuInflater().inflate(menuLayout, getNativeToolbar().getMenu());
        getNativeToolbar().inflateMenu(menuLayout);
    }

    @Override
    public ActionMenuView getActionMenuView() {
        for (int i = 0; i < getNativeToolbar().getChildCount(); i++) {
            View v = getNativeToolbar().getChildAt(i);
            if (v != null && v instanceof ActionMenuView) {
                return (ActionMenuView) v;
            }
        }
        return null;
    }

    @Override
    public void showMenuView(boolean isShow) {
        ActionMenuView actionMenuView = getActionMenuView();
        if(actionMenuView!=null){
            actionMenuView.setVisibility(isShow ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void setOnMenuItemClickListener(final MenuItem.OnMenuItemClickListener listener) {
        super.setOnMenuItemClickListener(listener);
        getNativeToolbar().setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return listener.onMenuItemClick(item);
            }
        });
    }

    @Override
    public Menu getMenu() {
        return getNativeToolbar().getMenu();
    }
}
