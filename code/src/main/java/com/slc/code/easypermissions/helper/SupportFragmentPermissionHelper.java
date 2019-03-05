package com.slc.code.easypermissions.helper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.slc.code.easypermissions.EasyPermissions;

/**
 * Permissions helper for {@link Fragment} from the support library.
 */
class SupportFragmentPermissionHelper extends BaseSupportPermissionsHelper<Fragment> {

    public SupportFragmentPermissionHelper(@NonNull Fragment host,EasyPermissions.PermissionCallbacks permissionCallbacks,EasyPermissions.RationaleCallbacks rationaleCallbacks) {
        super(host,permissionCallbacks,rationaleCallbacks);
    }

    @Override
    public void directRequestPermissions(int requestCode, @NonNull String... perms) {
        getHost().requestPermissions(perms, requestCode);
    }

    @Override
    public boolean shouldShowRequestPermissionRationale(@NonNull String perm) {
        return getHost().shouldShowRequestPermissionRationale(perm);
    }

    @Override
    public Context getContext() {
        return getHost().getActivity();
    }
}
