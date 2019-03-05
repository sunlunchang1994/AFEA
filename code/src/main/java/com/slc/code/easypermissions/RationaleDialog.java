package com.slc.code.easypermissions;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;

import com.slc.code.easypermissions.helper.Rationale;

public class RationaleDialog {
    public static void show(@NonNull Context context, int titleRes, @StringRes int msgRes, @NonNull final Rationale rationale) {
        if (Build.VERSION.SDK_INT >= 21) {
            //TODO 使用app包下面的dialog
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
            builder.setMessage(msgRes);
            if (titleRes != 0) {
                builder.setTitle(titleRes);
            }
            builder.setCancelable(false)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            rationale.ok();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            rationale.cancel();
                        }
                    })
                    .create().show();
        } else {
            //TODO 使用v7包下面的dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(msgRes);
            if (titleRes != 0) {
                builder.setTitle(titleRes);
            }
            builder.setCancelable(false)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            rationale.ok();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            rationale.cancel();
                        }
                    })
                    .create().show();
        }
    }
}
