package com.slc.code.ui.utils;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;

import com.slc.code.R;

/**
 * Created by on the way on 2018/3/22.
 */

public class ColorUtil {
    public static int getColorPrimary(Context context){
        TypedValue tv = new TypedValue();
        if (context.getTheme().resolveAttribute(R.attr.colorPrimary, tv, true)) {
            return tv.data;
        }
        return Color.BLACK;
    }
    public static int getColorAccent(Context context){
        TypedValue tv = new TypedValue();
        if (context.getTheme().resolveAttribute(R.attr.colorAccent, tv, true)) {
            return tv.data;
        }
        return Color.BLACK;
    }
}
