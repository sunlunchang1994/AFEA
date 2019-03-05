package com.slc.code.easypermissions.helper;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.slc.code.easypermissions.EasyPermissions;

import java.util.Arrays;
import java.util.List;

/**
 * Delegate class to make permission calls based on the 'host' (Fragment, Activity, etc).
 */
public abstract class PermissionHelper<T> {
    private EasyPermissions.PermissionCallbacks mPermissionCallbacks;
    private EasyPermissions.RationaleCallbacks mRationaleCallbacks;
    private T mHost;


    public static PermissionHelper<? extends Activity> newInstance(@NonNull Activity host) {
        return newInstance(host, null,null);
    }

    public static PermissionHelper<? extends Activity> newInstance(@NonNull Activity host, EasyPermissions.PermissionCallbacks permissionCallbacks,EasyPermissions.RationaleCallbacks rationaleCallbacks) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return new LowApiPermissionsHelper<>(host, permissionCallbacks,rationaleCallbacks);
        }
        if (host instanceof AppCompatActivity)
            return new AppCompatActivityPermissionHelper((AppCompatActivity) host, permissionCallbacks,rationaleCallbacks);
        else {
            return new ActivityPermissionHelper(host, permissionCallbacks,rationaleCallbacks);
        }
    }


    public static PermissionHelper<Fragment> newInstance(@NonNull Fragment host) {
        return newInstance(host, null,null);
    }


    public static PermissionHelper<Fragment> newInstance(@NonNull Fragment host, EasyPermissions.PermissionCallbacks permissionCallbacks,EasyPermissions.RationaleCallbacks rationaleCallbacks) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return new LowApiPermissionsHelper<>(host, permissionCallbacks,rationaleCallbacks);
        }
        return new SupportFragmentPermissionHelper(host, permissionCallbacks,rationaleCallbacks);
    }

    public static PermissionHelper<android.app.Fragment> newInstance(@NonNull android.app.Fragment host) {
        return newInstance(host, null,null);
    }

    public static PermissionHelper<android.app.Fragment> newInstance(@NonNull android.app.Fragment host, EasyPermissions.PermissionCallbacks permissionCallbacks,EasyPermissions.RationaleCallbacks rationaleCallbacks) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return new LowApiPermissionsHelper<>(host, permissionCallbacks,rationaleCallbacks);
        }
        return new FrameworkFragmentPermissionHelper(host, permissionCallbacks,rationaleCallbacks);
    }

    public PermissionHelper(@NonNull T host, EasyPermissions.PermissionCallbacks permissionCallbacks, EasyPermissions.RationaleCallbacks rationaleCallbacks) {
        mHost = host;
        mPermissionCallbacks = permissionCallbacks;
        mRationaleCallbacks = rationaleCallbacks;
    }

    private boolean shouldShowRationale(@NonNull String... perms) {
        for (String perm : perms) {
            if (shouldShowRequestPermissionRationale(perm)) {
                return true;
            }
        }
        return false;
    }

    public void requestPermissions(final @StyleRes int theme,
                                   final int requestCode,
                                   final @NonNull String... perms) {
        if (shouldShowRationale(perms)) {
            //showRequestPermissionRationale(theme,requestCode,perms);
            //TODO
            if(mRationaleCallbacks!=null){
                mRationaleCallbacks.showRequestPermissionRationale(requestCode, new Rationale() {
                    @Override
                    public void ok() {
                        directRequestPermissions(requestCode, perms);
                    }

                    @Override
                    public void cancel() {
                        if (mPermissionCallbacks != null) {
                            mPermissionCallbacks.onPermissionsDenied(requestCode, Arrays.asList(perms));
                        }
                    }
                });
            }
            /*final RationaleDialog config = new RationaleDialog(theme, requestCode, perms);
            Dialog.OnClickListener clickListener = new Dialog.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    if (which == Dialog.BUTTON_POSITIVE) {
                        directRequestPermissions(requestCode, perms);
                    } else {
                        if (mPermissionCallbacks != null) {
                            mPermissionCallbacks.onPermissionsDenied(requestCode, Arrays.asList(perms));
                        }
                    }
                }
            };
            if (mHost instanceof Fragment) {
                config.createSupportDialog((getContext()), clickListener).show();
            } else if (mHost instanceof android.app.Fragment) {
                config.createFrameworkDialog(getContext(), clickListener).show();
            } else if (mHost instanceof Activity) {
                config.createFrameworkDialog(getContext(), clickListener).show();
            } else {
                throw new RuntimeException("Host must be an Activity or Fragment!");
            }*/
        } else {
            directRequestPermissions(requestCode, perms);
        }
    }


    public boolean somePermissionPermanentlyDenied(@NonNull List<String> perms) {
        for (String deniedPermission : perms) {
            if (permissionPermanentlyDenied(deniedPermission)) {
                return true;
            }
        }

        return false;
    }

    public boolean permissionPermanentlyDenied(@NonNull String perms) {
        return !shouldShowRequestPermissionRationale(perms);
    }

    public boolean somePermissionDenied(@NonNull String... perms) {
        return shouldShowRationale(perms);
    }

    @NonNull
    public T getHost() {
        return mHost;
    }

    /**
     * 获取PermissionCallbacks
     *
     * @return
     */
    public EasyPermissions.PermissionCallbacks getPermissionCallbacks() {
        return mPermissionCallbacks;
    }
    // ============================================================================
    // Public abstract methods
    // ============================================================================

    public abstract void directRequestPermissions(int requestCode, @NonNull String... perms);

    public abstract boolean shouldShowRequestPermissionRationale(@NonNull String perm);

    public abstract void showRequestPermissionRationale(@StyleRes int theme,
                                                        int requestCode,
                                                        @NonNull String... perms);

    public abstract Context getContext();

}
