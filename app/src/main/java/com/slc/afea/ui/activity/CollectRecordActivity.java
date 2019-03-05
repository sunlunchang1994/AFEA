package com.slc.afea.ui.activity;

import android.app.DatePickerDialog;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import com.slc.afea.R;
import com.slc.afea.contract.CollectRecordContract;
import com.slc.afea.model.record.CollectRecordModelImp;
import com.slc.afea.presenter.CollectRecordPresenterImp;
import com.slc.afea.ui.adapter.CollectRecordAdapter;
import com.slc.code.ui.activity.NativeToolBarActivity;
import com.slc.code.ui.utils.DimenUtil;

import java.util.Calendar;

/**
 * Created by achang on 2019/1/15.
 */

public class CollectRecordActivity extends NativeToolBarActivity<CollectRecordContract.CollectRecordPresenter>
        implements SwipeRefreshLayout.OnRefreshListener, CollectRecordContract.CollectRecordView, MenuItem.OnMenuItemClickListener {
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private CollectRecordAdapter adapter;

    @Override
    protected void onBindView(@Nullable Bundle savedInstanceState) {
        showNavigation();
        setToolBarTitle(R.string.label_collect_record);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        View ll_title = getLayoutInflater().inflate(R.layout.head_collect_record, null);
        AppBarLayout.LayoutParams toolbarLayoutParams = new AppBarLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DimenUtil.dip2px(getMvpContext(), 36));
        toolbarLayoutParams.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED);
        appBarLayout.addView(ll_title, toolbarLayoutParams);
        setMenuView(R.menu.menu_collect_record);
        showMenuView(true);
        setOnMenuItemClickListener(this);
        TextView subTitleView = getSubTitleView();
        subTitleView.setGravity(subTitleView.getGravity() | Gravity.CENTER_VERTICAL);
        subTitleView.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_action_right_black, 0);
        subTitleView.setOnClickListener(
                l -> new DatePickerDialog(getMvpContext(), (DatePicker view, int year, int month, int dayOfMonth) -> {
                    getPresenter().getCalendar().set(year, month, dayOfMonth, 0, 0);
                    swipeRefreshLayout.setRefreshing(true);
                    upoDateSubTitle(year + "-" + getPresenter().intToStringOfDibit(month + 1)
                            + "-" + getPresenter().intToStringOfDibit(dayOfMonth));
                    getPresenter().getCollectRecordDataByDate();
                }, getPresenter().getCalendar().get(Calendar.YEAR), getPresenter().getCalendar().get(Calendar.MONTH),
                        getPresenter().getCalendar().get(Calendar.DAY_OF_MONTH)).show()
        );
    }

    @Override
    public void upoDateSubTitle(String subTitle) {
        setToolBarSubTitle(subTitle);
    }

    @Override
    public void upoDateTitle(String title) {
        setToolBarTitle(title);
    }

    @Override
    public int getUiTopShowMode() {
        return UI_TOP_SHOW_MODE_COORDINATE;
    }

    @Override
    public Object setDeveloperView() {
        return R.layout.activity_collect_record;
    }

    @Override
    protected void initPresenter() {
        super.initPresenter();
        swipeRefreshLayout.setRefreshing(true);
        new CollectRecordPresenterImp(this).start();
    }

    @Override
    public void onRefresh() {
        getPresenter().getCollectRecordDataByDate();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_empty:
                break;
            case R.id.action_sort_date_positive:
                getPresenter().sort(CollectRecordModelImp.SORT_DATE_POSITIVE);
                break;
            case R.id.action_sort_date_negative:
                getPresenter().sort(CollectRecordModelImp.SORT_DATE_NEGATIVE);
                break;
            case R.id.action_sort_number_few_more:
                getPresenter().sort(CollectRecordModelImp.SORT_NUMBER_FEW_MORE);
                break;
            case R.id.action_sort_number_more_few:
                getPresenter().sort(CollectRecordModelImp.SORT_NUMBER_MORE_FEW);
                break;
        }
        return false;
    }

    @Override
    public void refresh() {
        if (adapter == null) {
            adapter = new CollectRecordAdapter(getMvpContext(), getPresenter().getDateList());
            recyclerView.setAdapter(adapter);
        } else
            adapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }

}
