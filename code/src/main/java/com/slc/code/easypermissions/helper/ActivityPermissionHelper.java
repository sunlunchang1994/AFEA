package com.slc.code.easypermissions.helper;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.slc.code.easypermissions.EasyPermissions;

/**
 * Permissions helper for {@link Activity}.
 */
class ActivityPermissionHelper extends BaseFrameworkPermissionsHelper<Activity> {

    public ActivityPermissionHelper(Activity host,EasyPermissions.PermissionCallbacks permissionCallbacks,EasyPermissions.RationaleCallbacks rationaleCallbacks) {
        super(host,permissionCallbacks,rationaleCallbacks);
    }

    @Override
    public void directRequestPermissions(int requestCode, @NonNull String... perms) {
        ActivityCompat.requestPermissions(getHost(), perms, requestCode);
    }

    @Override
    public boolean shouldShowRequestPermissionRationale(@NonNull String perm) {
        return ActivityCompat.shouldShowRequestPermissionRationale(getHost(), perm);
    }

    @Override
    public Context getContext() {
        return getHost();
    }
}
