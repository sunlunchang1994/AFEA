package com.slc.code.easypermissions.helper;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.support.annotation.NonNull;

import com.slc.code.easypermissions.EasyPermissions;

/**
 * Permissions helper for {@link Fragment} from the framework.
 */
class FrameworkFragmentPermissionHelper extends BaseFrameworkPermissionsHelper<Fragment> {

    public FrameworkFragmentPermissionHelper(@NonNull Fragment host,EasyPermissions.PermissionCallbacks permissionCallbacks,EasyPermissions.RationaleCallbacks rationaleCallbacks) {
        super(host,permissionCallbacks,rationaleCallbacks);
    }

    @Override
    @SuppressLint("NewApi")
    public void directRequestPermissions(int requestCode, @NonNull String... perms) {
        getHost().requestPermissions(perms, requestCode);
    }

    @Override
    @SuppressLint("NewApi")
    public boolean shouldShowRequestPermissionRationale(@NonNull String perm) {
        return getHost().shouldShowRequestPermissionRationale(perm);
    }

    @Override
    public Context getContext() {
        return getHost().getActivity();
    }
}
