package com.slc.code.ui.menu;

import android.content.Context;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.slc.code.R;


/**
 * Created by on the way on 2018/8/7.
 */

public class TextActionMenu extends BaseActionProviderImp {
    private TextView title;

    public TextActionMenu(Context context) {
        super(context);
    }

    @Override
    public int getActionViewLayout() {
        return R.layout.menu_view_action_text;
    }

    @Override
    protected void bindView(View rootView) {
        super.bindView(rootView);
        if (rootView != null) {
            rootView.setId(forItem.getItemId());
            title = rootView.findViewById(R.id.title);
            title.setText(forItem.getTitle());
        }
    }
}
