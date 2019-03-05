package com.slc.code.easypermissions;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.support.annotation.Size;
import android.support.annotation.StyleRes;
import android.support.v4.app.Fragment;

import com.slc.code.easypermissions.helper.PermissionHelper;

import java.util.Arrays;

/**
 * An immutable model object that holds all of the parameters associated with a permission request,
 * such as the permissions, request code, and rationale.
 *
 * @see EasyPermissions#requestPermissions(PermissionRequest)
 * @see Builder
 */
public final class PermissionRequest {
    private final PermissionHelper mHelper;
    private final String[] mPerms;
    private final int mRequestCode;
    private final int mTheme;

    private PermissionRequest(PermissionHelper helper,
                              String[] perms,
                              int requestCode,
                              int theme) {
        mHelper = helper;
        mPerms = perms.clone();
        mRequestCode = requestCode;
        mTheme = theme;
    }

    @NonNull
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    public PermissionHelper getHelper() {
        return mHelper;
    }

    @NonNull
    public String[] getPerms() {
        return mPerms.clone();
    }

    public int getRequestCode() {
        return mRequestCode;
    }

    @StyleRes
    public int getTheme() {
        return mTheme;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PermissionRequest request = (PermissionRequest) o;

        return Arrays.equals(mPerms, request.mPerms) && mRequestCode == request.mRequestCode;
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(mPerms);
        result = 31 * result + mRequestCode;
        return result;
    }

    @Override
    public String toString() {
        return "PermissionRequest{" +
                "mHelper=" + mHelper +
                ", mPerms=" + Arrays.toString(mPerms) +
                ", mRequestCode=" + mRequestCode +
                ", mTheme=" + mTheme +
                '}';
    }

    /**
     * Builder to build a permission request with variable options.
     *
     * @see PermissionRequest
     */
    public static final class Builder {
        private final PermissionHelper mHelper;
        private final int mRequestCode;
        private final String[] mPerms;

        private int mTheme = -1;

        /**
         * Construct a new permission request builder with a host, request code, and the requested
         * permissions.
         *
         * @param activity    the permission request host
         * @param requestCode request code to track this request; must be &lt; 256
         * @param perms       the set of permissions to be requested
         */
        public Builder(@NonNull Activity activity, EasyPermissions.PermissionCallbacks permissionCallbacks,EasyPermissions.RationaleCallbacks rationaleCallbacks, int requestCode,
                       @NonNull @Size(min = 1) String... perms) {
            mHelper = PermissionHelper.newInstance(activity, permissionCallbacks,rationaleCallbacks);
            mRequestCode = requestCode;
            mPerms = perms;
        }

        /**
         * @see #Builder(Activity, EasyPermissions.PermissionCallbacks, EasyPermissions.RationaleCallbacks, int, String...)
         */
        public Builder(@NonNull Fragment fragment, EasyPermissions.PermissionCallbacks permissionCallbacks, EasyPermissions.RationaleCallbacks rationaleCallbacks, int requestCode,
                       @NonNull @Size(min = 1) String... perms) {
            mHelper = PermissionHelper.newInstance(fragment, permissionCallbacks,rationaleCallbacks);
            mRequestCode = requestCode;
            mPerms = perms;
        }

        /**
         * @see #Builder(Activity, EasyPermissions.PermissionCallbacks,EasyPermissions.RationaleCallbacks, int, String...)
         */
        public Builder(@NonNull android.app.Fragment fragment, EasyPermissions.PermissionCallbacks permissionCallbacks,EasyPermissions.RationaleCallbacks rationaleCallbacks,  int requestCode,
                       @NonNull @Size(min = 1) String... perms) {
            mHelper = PermissionHelper.newInstance(fragment, permissionCallbacks,rationaleCallbacks);
            mRequestCode = requestCode;
            mPerms = perms;
        }

        /**
         * Set the theme to be used for the rationale dialog should it be shown.
         *
         * @param theme a style resource
         */
        @NonNull
        public Builder setTheme(@StyleRes int theme) {
            mTheme = theme;
            return this;
        }

        /**
         * Build the permission request.
         *
         * @return the permission request
         * @see EasyPermissions#requestPermissions(PermissionRequest)
         * @see PermissionRequest
         */
        @NonNull
        public PermissionRequest build() {
            return new PermissionRequest(
                    mHelper,
                    mPerms,
                    mRequestCode,
                    mTheme);
        }
    }
}
