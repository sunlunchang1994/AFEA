package com.slc.code.ui.menu;

import android.view.MenuItem;
import android.view.SubMenu;

/**
 * Created by achang on 2018/12/20.
 */

public interface BaseActionProvider {
    void setSubMenuItem(SubMenu subMenuItem);

    void setOnMenuItemClickListener(MenuItem.OnMenuItemClickListener onMenuItemClickListener);

}
