package com.slc.code.ui.fragment;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.slc.code.R;
import com.slc.code.contract.MvpContract;
import com.slc.code.easypermissions.EasyPermissions;
import com.slc.code.ui.activity.BaseActivity;
import com.slc.code.ui.utils.DimenUtil;
import com.slc.code.ui.utils.SlcUltimateBar;
import com.slc.code.ui.views.BaseActivityDelegate;
import com.slc.code.ui.views.BaseView;
import com.slc.code.ui.views.Functions;


/**
 * Created by on the way on 2017/12/6.
 */

public abstract class BaseFragment<P extends MvpContract.BasePresenter> extends MvpFragment<P> implements BaseView {
    protected Functions mFunctions;//方法

    protected BaseActivityDelegate mBaseActivityDelegate;
    protected int barStyle = SlcUltimateBar.DARK;

    protected ViewGroup codeRootView;//核心的根视图
    private View statusBarEmbellishView;//状态栏修饰视图

    /**
     * 设置方法
     *
     * @param functions
     */
    public void setFunctions(Functions functions) {
        this.mFunctions = functions;
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        if (mBaseActivityDelegate != null) {
            mBaseActivityDelegate.setBarStyle(getBarStyle());//初始化状态栏风格
        }
    }

    @Nullable
    @Override
    public final View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        codeRootView = (ViewGroup) getLayoutInflater().inflate(R.layout.activity_base_code, null);
        codeRootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        statusBarEmbellishView = codeRootView.findViewById(R.id.view_status_bar_embellish);
        statusBarEmbellishView.getLayoutParams().height = DimenUtil.getStatusBarHeight(getContext());

        Object layoutObj = setLayout();
        if (layoutObj instanceof Integer) {
            codeRootView.addView(inflater.inflate((Integer) layoutObj, container, false), new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        } else if (layoutObj instanceof View) {
            codeRootView.addView((View) layoutObj, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        } else {
            throw new ClassCastException("setLayout() type must be int or View");
        }
        initViewBefore();
        onBindView(savedInstanceState, codeRootView);
        initViewLater();
        initPresenter();
        return codeRootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof BaseActivity) {
            ((BaseActivity) activity).setFunctionsForFragment(getClass().getName());
        }
        if (activity instanceof BaseActivityDelegate) {
            mBaseActivityDelegate = (BaseActivityDelegate) activity;
        } else {
            throw new ClassCastException("activity type must be extends BaseActivityDelegate");
        }
        Log.i(TAG, "onAttach()");
    }

    /**
     * 设置布局
     *
     * @return 返回一个继承鱼View对象的视图或布局的资源文件
     */
    public abstract Object setLayout();

    /**
     * 绑定试图
     *
     * @param savedInstanceState
     * @param rootView
     */
    public abstract void onBindView(@Nullable Bundle savedInstanceState, View rootView);

    /**
     * 初始化视图之前调用此方法
     */
    public void initViewBefore() {

    }

    /**
     * 初始化试图之后调用此方法
     */
    public void initViewLater() {
    }
    protected void initPresenter(){

    }
    /**
     * 获取状态栏颜色
     *
     * @return
     */
    protected int getBarStyle() {
        return barStyle;
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
    @Override
    public int getUiTopShowMode() {
        return BaseView.UI_TOP_SHOW_MODE_NORMAL;
    }

    /**
     * 在销毁视图时，销毁Presenter对象，避免由于Presenter持有上下问环境时造成的资源浪费
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBaseActivityDelegate = null;
    }

}
