package com.slc.code.ui.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.slc.code.R;
import com.slc.code.contract.MvpContract;
import com.slc.code.easypermissions.EasyPermissions;
import com.slc.code.ui.utils.ActivityUtil;
import com.slc.code.ui.utils.DimenUtil;
import com.slc.code.ui.utils.SlcUltimateBar;
import com.slc.code.ui.views.BaseActivityDelegate;
import com.slc.code.ui.views.BaseView;


/**
 * Created by on the way on 2017/12/6.
 */

public abstract class BaseActivity<P extends MvpContract.BasePresenter> extends MvpActivity<P> implements BaseActivityDelegate, BaseView {

    protected SlcUltimateBar slcUltimateBar;//沉浸状态栏工具
    protected int barStyle = SlcUltimateBar.DARK;//是否为高亮Bar
    private ViewGroup codeRootView;//核心的根视图
    private View statusBarEmbellishView;//状态栏修饰视图

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        codeRootView = (ViewGroup) getLayoutInflater().inflate(R.layout.activity_base_code, null);
        statusBarEmbellishView = codeRootView.findViewById(R.id.view_status_bar_embellish);
        statusBarEmbellishView.getLayoutParams().height = DimenUtil.getStatusBarHeight(this);
        slcUltimateBar = new SlcUltimateBar(this);
        initBarStyle();
        Object layoutObj = setLayout();
        if (layoutObj instanceof Integer) {
            setContentView((Integer) layoutObj);
        } else if (layoutObj instanceof View) {
            setContentView((View) layoutObj);
        } else {
            throw new ClassCastException("setLayout() type must be layoutRes or View");
        }
        initViewBefore();
        onBindView(savedInstanceState);
        initViewLater();
        initPresenter();
        ActivityUtil.getInstance().addActivity(this);
    }

    /**
     * @deprecated 不推荐此类调用此方法，否则会破坏基础activity视图造成，设置根视图以转移到setLayout()方法
     */
    @Deprecated
    @Override
    public final void setContentView(View view) {
        codeRootView.addView(view, 1);
        super.setContentView(codeRootView);
    }

    /**
     * @deprecated 不推荐此类调用此方法，否则会破坏基础activity视图造成，设置根视图以转移到setLayout()方法
     */
    @Deprecated
    @Override
    public final void setContentView(int layoutResID) {
        codeRootView.addView(getLayoutInflater().inflate(layoutResID, codeRootView, false), 1);
        super.setContentView(codeRootView);
    }

    /**
     * @deprecated 不推荐此类调用此方法，否则会破坏基础activity视图造成，设置根视图以转移到setLayout()方法
     */
    @Deprecated
    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        codeRootView.addView(view, 1, params);
        super.setContentView(codeRootView);
    }

    /**
     * 设置布局
     *
     * @return 返回一个继承鱼View对象的视图或布局的资源文件
     */
    protected abstract Object setLayout();

    /**
     * 绑定试图
     *
     * @param savedInstanceState
     */
    protected abstract void onBindView(@Nullable Bundle savedInstanceState);

    /**
     * 初始化视图之前调用此方法
     */
    protected void initViewBefore() {
    }

    /**
     * 初始化试图之后调用此方法
     */
    protected void initViewLater() {
    }
    protected void initPresenter(){

    }
   /* @Override
    public SlcUltimateBar getSlcUltimateBar() {
        return slcUltimateBar;
    }*/

    @Override
    public final int getBarStyle() {
        return barStyle;
    }

    @Override
    public final void setBarStyle(int barStyle) {
        if (this.barStyle != barStyle) {
            this.barStyle = barStyle;
            syncBarStyle();
        }
    }

    /**
     * 同步syncBarStyle
     */
    protected void syncBarStyle() {
        slcUltimateBar.setImmersionBar(SlcUltimateBar.STATUS_BAR, barStyle);
        Log.i("setBarStyle", "改变" + (barStyle == SlcUltimateBar.DARK ? "DARK" : "LIGHT"));
    }

    /**
     * 初始化BarStyle
     */
    protected void initBarStyle() {
        syncBarStyle();
    }

    /**
     * 设置状态栏装饰的颜色
     */
    public void setStatusBarEmbellishViewColor(@ColorInt int color) {
        statusBarEmbellishView.setBackgroundColor(color);
    }

    /**
     * 设置状态栏装饰的drawable
     *
     * @param drawable
     */
    public void setStatusBarEmbellishViewDrawable(Drawable drawable) {
        statusBarEmbellishView.setBackgroundDrawable(drawable);
    }

    /**
     * 设置状态栏装饰的drawable
     *
     * @param drawableRes
     */
    public void setStatusBarEmbellishViewRes(@DrawableRes int drawableRes) {
        statusBarEmbellishView.setBackgroundResource(drawableRes);
    }

    /**
     * 设置状态栏装饰的颜色
     */
    public void setCodeRootViewColor(@ColorInt int color) {
        codeRootView.setBackgroundColor(color);
    }

    /**
     * 设置状态栏装饰的drawable
     *
     * @param drawable
     */
    public void setCodeRootViewDrawable(Drawable drawable) {
        codeRootView.setBackgroundDrawable(drawable);
    }

    /**
     * 设置状态栏装饰的drawable
     *
     * @param drawableRes
     */
    public void setCodeRootViewRes(@DrawableRes int drawableRes) {
        codeRootView.setBackgroundResource(drawableRes);
    }

    /**
     * 设置状态栏修饰物是否显示
     *
     * @param isShow
     */
    public void setShowStatusBarEmbellishView(boolean isShow) {
        statusBarEmbellishView.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    /**
     * 状态栏修饰物是否显示
     *
     * @return
     */
    public boolean statusBarEmbellishViewIsShow() {
        return statusBarEmbellishView.getVisibility() == View.VISIBLE;
    }
/*
    */

    /**
     * 动态权限封装
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * 设置来自fragment的连接方法
     *
     * @param fragmentName
     */
    public void setFunctionsForFragment(String fragmentName) {
        //TODO
    }
    @Override
    public int getUiTopShowMode() {
        return BaseView.UI_TOP_SHOW_MODE_NORMAL;
    }

    /**
     * 在销毁视图时，销毁Presenter对象，避免由于Presenter持有上下问环境时造成的资源浪费
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        slcUltimateBar.destroy();
        slcUltimateBar = null;
        ActivityUtil.getInstance().removeActivity(this);
    }

}
