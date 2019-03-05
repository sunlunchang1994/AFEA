package com.slc.afea.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.slc.afea.R;
import com.slc.code.app.AppData;

public class MyToast {
    /**
     * 短时间显示Toast
     */
    public static void Short(@NonNull Context context, @StringRes int resId) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
    }

    /**
     * 短时间显示Toast
     */
    public static void Short(@NonNull Context context, CharSequence message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 长时间显示toast
     */
    public static void Long(@NonNull Context context, @StringRes int resId) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
    }

    /**
     * 长时间显示toast
     */
    public static void Long(@NonNull Context context, CharSequence message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

}
