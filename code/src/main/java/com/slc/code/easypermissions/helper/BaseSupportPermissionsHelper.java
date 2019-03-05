package com.slc.code.easypermissions.helper;

import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;

import com.slc.code.easypermissions.EasyPermissions;

/**
 * Implementation of {@link PermissionHelper} for Support Library host classes.
 */
public abstract class BaseSupportPermissionsHelper<T> extends PermissionHelper<T> {

    private static final String TAG = "BSPermissionsHelper";

    public BaseSupportPermissionsHelper(@NonNull T host, EasyPermissions.PermissionCallbacks permissionCallbacks,EasyPermissions.RationaleCallbacks rationaleCallbacks) {
        super(host, permissionCallbacks,rationaleCallbacks);
    }

    @Override
    public void showRequestPermissionRationale(@StyleRes int theme,
                                               int requestCode,
                                               @NonNull String... perms) {
        //TODO
    }
}
