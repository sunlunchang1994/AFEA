package com.slc.code.ui.menu;


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.LayoutRes;
import android.support.v4.view.ActionProvider;
import android.support.v4.widget.ListPopupWindowCompat;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.v7.widget.ListPopupWindow;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.slc.code.ui.baseadapter.abslistview.CommonAdapter;
import com.slc.code.ui.baseadapter.abslistview.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by achang on 2018/12/20.
 */

public abstract class BaseActionProviderImp extends ActionProvider implements BaseActionProvider {
    protected MenuItem.OnMenuItemClickListener onMenuItemClickListener;
    protected MenuItem forItem;
    private SubMenu subMenuItem;

    /**
     * Creates a new instance.
     *
     * @param context Context for accessing resources.
     */
    public BaseActionProviderImp(Context context) {
        super(context);
    }

    @Override
    public void setSubMenuItem(SubMenu subMenuItem) {
        this.subMenuItem = subMenuItem;
    }

    public void setOnMenuItemClickListener(MenuItem.OnMenuItemClickListener onMenuItemClickListener) {
        this.onMenuItemClickListener = onMenuItemClickListener;
    }

    @Override
    public boolean onPerformDefaultAction() {
        if (onMenuItemClickListener != null && forItem != null) {
            return onMenuItemClickListener.onMenuItemClick(forItem);
        } else {
            return super.onPerformDefaultAction();
        }
    }

    @Override
    public View onCreateActionView() {
        return LayoutInflater.from(getContext()).inflate(getActionViewLayout(), null);
    }

    /**
     * 获取视图
     *
     * @return
     */
    @LayoutRes
    protected abstract int getActionViewLayout();

    @Override
    public View onCreateActionView(MenuItem forItem) {
        this.forItem = forItem;
        View rootView = onCreateActionView();
        bindView(rootView);
        return rootView;
    }

    /**
     * 绑定视图
     *
     * @param rootView
     */
    protected void bindView(View rootView) {
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (subMenuItem != null && subMenuItem.size() != 0) {
                    final ListPopupWindow listPopupWindow = new ListPopupWindow(getContext());
                    listPopupWindow.setWidth(getContext().getResources().getDisplayMetrics().widthPixels / 3);
                    listPopupWindow.setHeight(ListPopupWindow.WRAP_CONTENT);
                    listPopupWindow.setAnchorView(v);
                    listPopupWindow.setModal(true);
                    final List<MenuItem> menuItemList = new ArrayList<>();
                    for (int i = 0; i < subMenuItem.size(); i++) {
                        menuItemList.add(subMenuItem.getItem(i));
                    }
                    listPopupWindow.setAdapter(new CommonAdapter<MenuItem>(getContext(), android.support.v7.appcompat.R.layout.select_dialog_item_material, menuItemList) {
                        @Override
                        protected void convert(ViewHolder holder, MenuItem menuItem, int position) {
                            holder.setText(android.R.id.text1, menuItem.getTitle());
                        }
                    });
                    listPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            listPopupWindow.dismiss();
                            if (onMenuItemClickListener != null) {
                                onMenuItemClickListener.onMenuItemClick(menuItemList.get(position));
                            }
                        }
                    });
                    listPopupWindow.show();
                } else {
                    onPerformDefaultAction();
                }
            }
        });
        rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private class MenuItem1 implements MenuItem {
        private CharSequence title;

        @Override
        public int getItemId() {
            return 0;
        }

        @Override
        public int getGroupId() {
            return 0;
        }

        @Override
        public int getOrder() {
            return 0;
        }

        @Override
        public MenuItem setTitle(CharSequence title) {
            this.title = title;
            return this;
        }

        @Override
        public MenuItem setTitle(int title) {
            return this;
        }

        @Override
        public CharSequence getTitle() {
            return title;
        }

        @Override
        public MenuItem setTitleCondensed(CharSequence title) {
            return null;
        }

        @Override
        public CharSequence getTitleCondensed() {
            return null;
        }

        @Override
        public MenuItem setIcon(Drawable icon) {
            return null;
        }

        @Override
        public MenuItem setIcon(int iconRes) {
            return null;
        }

        @Override
        public Drawable getIcon() {
            return null;
        }

        @Override
        public MenuItem setIntent(Intent intent) {
            return null;
        }

        @Override
        public Intent getIntent() {
            return null;
        }

        @Override
        public MenuItem setShortcut(char numericChar, char alphaChar) {
            return null;
        }

        @Override
        public MenuItem setNumericShortcut(char numericChar) {
            return null;
        }

        @Override
        public char getNumericShortcut() {
            return 0;
        }

        @Override
        public MenuItem setAlphabeticShortcut(char alphaChar) {
            return null;
        }

        @Override
        public char getAlphabeticShortcut() {
            return 0;
        }

        @Override
        public MenuItem setCheckable(boolean checkable) {
            return null;
        }

        @Override
        public boolean isCheckable() {
            return false;
        }

        @Override
        public MenuItem setChecked(boolean checked) {
            return null;
        }

        @Override
        public boolean isChecked() {
            return false;
        }

        @Override
        public MenuItem setVisible(boolean visible) {
            return null;
        }

        @Override
        public boolean isVisible() {
            return false;
        }

        @Override
        public MenuItem setEnabled(boolean enabled) {
            return null;
        }

        @Override
        public boolean isEnabled() {
            return false;
        }

        @Override
        public boolean hasSubMenu() {
            return false;
        }

        @Override
        public SubMenu getSubMenu() {
            return null;
        }

        @Override
        public MenuItem setOnMenuItemClickListener(OnMenuItemClickListener menuItemClickListener) {
            return null;
        }

        @Override
        public ContextMenu.ContextMenuInfo getMenuInfo() {
            return null;
        }

        @Override
        public void setShowAsAction(int actionEnum) {

        }

        @Override
        public MenuItem setShowAsActionFlags(int actionEnum) {
            return null;
        }

        @Override
        public MenuItem setActionView(View view) {
            return null;
        }

        @Override
        public MenuItem setActionView(int resId) {
            return null;
        }

        @Override
        public View getActionView() {
            return null;
        }

        @Override
        public MenuItem setActionProvider(android.view.ActionProvider actionProvider) {
            return null;
        }

        @Override
        public android.view.ActionProvider getActionProvider() {
            return null;
        }

        @Override
        public boolean expandActionView() {
            return false;
        }

        @Override
        public boolean collapseActionView() {
            return false;
        }

        @Override
        public boolean isActionViewExpanded() {
            return false;
        }

        @Override
        public MenuItem setOnActionExpandListener(OnActionExpandListener listener) {
            return null;
        }
    }
}
