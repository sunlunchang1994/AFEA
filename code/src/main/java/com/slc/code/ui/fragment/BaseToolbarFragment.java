package com.slc.code.ui.fragment;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ActionProvider;
import android.support.v4.view.MenuItemCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.slc.code.R;
import com.slc.code.contract.MvpContract;
import com.slc.code.ui.menu.BaseActionProvider;
import com.slc.code.ui.utils.DimenUtil;
import com.slc.code.ui.utils.SlcUltimateBar;
import com.slc.code.ui.views.BaseView;
import com.slc.code.ui.views.ToolBarView;

/**
 * Created by on the way on 2017/12/6.
 */

public abstract class BaseToolbarFragment<P extends MvpContract.BasePresenter> extends BaseFragment<P> implements ToolBarView {
    protected View toolbar;//工具栏
    protected AppBarLayout appBarLayout;//工具栏布局
    protected ConstraintLayout developersContentView;//开发者父类视图

    @Override
    public final Object setLayout() {
        final CoordinatorLayout toolBarView = (CoordinatorLayout) getLayoutInflater().inflate(getBarStyle() == SlcUltimateBar.LIGHT ? R.layout.toolbar_layout_light : R.layout.toolbar_layout_dark, null);
        //TODO 下面这句代码必须加上，否则fragment布局会出现各种奇奇怪怪的不协调问题
        toolBarView.setLayoutParams(new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT, CoordinatorLayout.LayoutParams.MATCH_PARENT));
        appBarLayout = toolBarView.findViewById(R.id.appBarLayout);
        //
        toolbar = getLayoutInflater().inflate(getToolBarViewRes(), appBarLayout, false);
        appBarLayout.addView(toolbar);
        bindToolView(toolbar);
        //
        developersContentView = toolBarView.findViewById(R.id.developersContentView);

        switch (getUiTopShowMode()) {
            case BaseView.UI_TOP_SHOW_MODE_NORMAL:
                appBarLayout.setPadding(0, DimenUtil.getStatusBarHeight(getContext()), 0, 0);
                setShowStatusBarEmbellishView(false);
                ((CoordinatorLayout.LayoutParams) developersContentView.getLayoutParams()).setBehavior(new AppBarLayout.ScrollingViewBehavior());
                break;
            case BaseView.UI_TOP_SHOW_MODE_COORDINATE:
                appBarLayout.setPadding(0, 0, 0, 0);
                setShowStatusBarEmbellishView(true);
                AppBarLayout.LayoutParams toolbarLayoutParams = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
                toolbarLayoutParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
                ((CoordinatorLayout.LayoutParams) developersContentView.getLayoutParams()).setBehavior(new AppBarLayout.ScrollingViewBehavior());
                break;
            case BaseView.UI_TOP_SHOW_MODE_FADE_TO:
                appBarLayout.setPadding(0, DimenUtil.getStatusBarHeight(getContext()), 0, 0);
                setToolBarElevation(0f);
                setShowStatusBarEmbellishView(false);
                //((CoordinatorLayout.LayoutParams) developersContentView.getLayoutParams()).setBehavior(null);
                appBarLayout.getBackground().setAlpha(0);
                //TODO 以下代码是将toolBarView置于顶层
                toolBarView.removeView(appBarLayout);
                toolBarView.addView(appBarLayout);
                break;
        }

        Object developerObj = setDeveloperView();
        if (developerObj instanceof Integer) {
            developersContentView.addView(getLayoutInflater().inflate((Integer) developerObj, null),
                    new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT));
        } else if (developerObj instanceof View) {
            developersContentView.addView((View) developerObj,
                    new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT));
        } else {
            throw new ClassCastException("setLayout() type must be layoutRes or View");
        }
        return toolBarView;
    }

    /**
     * 设置开发者视图
     *
     * @return
     */
    public abstract Object setDeveloperView();

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
        getMvpContext().onBackPressed();
    }

    @Override
    public int getUiTopShowMode() {
        return BaseView.UI_TOP_SHOW_MODE_NORMAL;
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
    public void setOnMenuItemClickListener(MenuItem.OnMenuItemClickListener listener) {
        Menu menu = getMenu();
        for (int i = 0; i < menu.size(); i++) {
            ActionProvider actionProvider = MenuItemCompat.getActionProvider(menu.getItem(i));
            if (actionProvider instanceof BaseActionProvider) {
                ((BaseActionProvider)actionProvider).setOnMenuItemClickListener(listener);
            }
        }
    }
}
