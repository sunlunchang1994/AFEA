package com.slc.code.easypermissions;

import android.app.Dialog;
import android.content.DialogInterface;

public class RationaleDialogClickListener implements Dialog.OnClickListener {

    private Object mHost;
    private RationaleDialog mConfig;
    private EasyPermissions.PermissionCallbacks mCallbacks;
    private EasyPermissions.RationaleCallbacks mRationaleCallbacks;

    public RationaleDialogClickListener(Object host,
                                        RationaleDialog config,
                                        EasyPermissions.PermissionCallbacks callbacks,
                                        EasyPermissions.RationaleCallbacks rationaleCallbacks) {
        /*mHost = compatDialogFragment.getParentFragment() != null
                ? compatDialogFragment.getParentFragment()
                : compatDialogFragment.getActivity();*/
        this.mHost=host;
        mConfig = config;
        mCallbacks = callbacks;
        mRationaleCallbacks = rationaleCallbacks;
    }

    /*public RationaleDialogClickListener(RationaleDialogFragment dialogFragment,
                                        RationaleDialog config,
                                        EasyPermissions.PermissionCallbacks callbacks,
                                        EasyPermissions.RationaleCallbacks dialogCallback) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mHost = dialogFragment.getParentFragment() != null ?
                    dialogFragment.getParentFragment() :
                    dialogFragment.getActivity();
        } else {
            mHost = dialogFragment.getActivity();
        }

        mConfig = config;
        mCallbacks = callbacks;
        mRationaleCallbacks = dialogCallback;
    }*/

    @Override
    public void onClick(DialogInterface dialog, int which) {
        /*int requestCode = mConfig.requestCode;
        if (which == Dialog.BUTTON_POSITIVE) {
            String[] permissions = mConfig.permissions;
            if (mRationaleCallbacks != null) {
                mRationaleCallbacks.onRationaleAccepted(requestCode);
            }
            if (mHost instanceof Fragment) {
                PermissionHelper.newInstance((Fragment) mHost,mCallbacks).directRequestPermissions(requestCode, permissions);
            } else if (mHost instanceof android.app.Fragment) {
                PermissionHelper.newInstance((android.app.Fragment) mHost,mCallbacks).directRequestPermissions(requestCode, permissions);
            } else if (mHost instanceof Activity) {
                PermissionHelper.newInstance((Activity) mHost,mCallbacks).directRequestPermissions(requestCode, permissions);
            } else {
                throw new RuntimeException("Host must be an Activity or Fragment!");
            }
        } else {
            if (mRationaleCallbacks != null) {
                mRationaleCallbacks.onRationaleDenied(requestCode);
            }
            notifyPermissionDenied();
        }*/
    }

   /* private void notifyPermissionDenied() {
        if (mCallbacks != null) {
            mCallbacks.onPermissionsDenied(mConfig.requestCode, Arrays.asList(mConfig.permissions));
        }
    }*/
}
