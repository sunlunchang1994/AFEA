/*
 * Copyright Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.slc.code.easypermissions;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Size;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.slc.code.easypermissions.helper.PermissionHelper;
import com.slc.code.easypermissions.helper.Rationale;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility to request and check System permissions for apps targeting Android M (API &gt;= 23).
 */
public class EasyPermissions {
    private static Map<Integer, PermissionCallbacks> permissionCallbacksMap = new HashMap<>();

    /**
     * Callback interface to receive the results of {@code EasyPermissions.requestPermissions()}
     * calls.
     */
    public interface PermissionCallbacks {

        void onPermissionsGranted(int requestCode, @NonNull List<String> perms);

        void onPermissionsDenied(int requestCode, @NonNull List<String> perms);
    }

    /**
     * Callback interface to receive button clicked events of the rationale dialog
     */
    public interface RationaleCallbacks {
        void showRequestPermissionRationale(int requestCode, @NonNull Rationale rationale);
    }

    private static final String TAG = "EasyPermissions";

    /**
     * Check if the calling context has a set of permissions.
     *
     * @param context the calling context.
     * @param perms   one ore more permissions, such as {@link Manifest.permission#CAMERA}.
     * @return true if all permissions are already granted, false if at least one permission is not
     * yet granted.
     * @see Manifest.permission
     */
    public static boolean hasPermissions(@NonNull Context context,
                                         @Size(min = 1) @NonNull String... perms) {
        // Always return true for SDK < M, let the system deal with the permissions
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Log.w(TAG, "hasPermissions: API version < M, returning true by default");

            // DANGER ZONE!!! Changing this will break the library.
            return true;
        }

        // Null context may be passed if we have detected Low API (less than M) so getting
        // to this point with a null context should not be possible.
        if (context == null) {
            throw new IllegalArgumentException("Can't check permissions for null context");
        }

        for (String perm : perms) {
            if (ContextCompat.checkSelfPermission(context, perm)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }

        return true;
    }

    /**
     * Request a set of permissions, showing a rationale if the system requests it.
     *
     * @param host        requesting context.
     *                    will be displayed if the user rejects the request the first time.
     * @param requestCode request code to track this request, must be &lt; 256.
     * @param perms       a set of permissions to be requested.
     * @see Manifest.permission
     */
    public static void requestPermissions(
            @NonNull Activity host, PermissionCallbacks permissionCallbacks,
            RationaleCallbacks rationaleCallbacks,
            int requestCode, @Size(min = 1) @NonNull String... perms) {
        requestPermissions(
                new PermissionRequest.Builder(host, permissionCallbacks, rationaleCallbacks, requestCode, perms)
                        .build());
    }

    /**
     * Request permissions from a Support Fragment with standard OK/Cancel buttons.
     */
    public static void requestPermissions(
            @NonNull Fragment host, PermissionCallbacks permissionCallbacks,
            RationaleCallbacks rationaleCallbacks,
            int requestCode, @Size(min = 1) @NonNull String... perms) {
        requestPermissions(
                new PermissionRequest.Builder(host, permissionCallbacks, rationaleCallbacks, requestCode, perms)
                        .build());
    }

    /**
     * Request permissions from a standard Fragment with standard OK/Cancel buttons.
     */
    public static void requestPermissions(
            @NonNull android.app.Fragment host, PermissionCallbacks permissionCallbacks,
            RationaleCallbacks rationaleCallbacks,
            int requestCode, @Size(min = 1) @NonNull String... perms) {
        requestPermissions(
                new PermissionRequest.Builder(host, permissionCallbacks, rationaleCallbacks, requestCode, perms)
                        .build());
    }

    /**
     * Request a set of permissions.
     *
     * @param request the permission request
     * @see PermissionRequest
     */
    public static void requestPermissions(PermissionRequest request) {
        permissionCallbacksMap.put(request.getRequestCode(), request.getHelper().getPermissionCallbacks());
        if (hasPermissions(request.getHelper().getContext(), request.getPerms())) {
            notifyAlreadyHasPermissions(request.getRequestCode(), request.getPerms());
            return;
        }

        // Request permissions
        request.getHelper().requestPermissions(
                request.getTheme(),
                request.getRequestCode(),
                request.getPerms());
    }

    /**
     * Handle the result of a permission request, should be called from the calling {@link
     * Activity}'s {@link ActivityCompat.OnRequestPermissionsResultCallback#onRequestPermissionsResult(int,
     * String[], int[])} method.
     * <p>
     *
     * @param requestCode  requestCode argument to permission result callback.
     * @param permissions  permissions argument to permission result callback.
     * @param grantResults grantResults argument to permission result callback.
     */
    public static void onRequestPermissionsResult(int requestCode,
                                                  @NonNull String[] permissions,
                                                  @NonNull int[] grantResults) {
        // Make a collection of granted and denied permissions from the request.
        List<String> granted = new ArrayList<>();
        List<String> denied = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            String perm = permissions[i];
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                granted.add(perm);
            } else {
                denied.add(perm);
            }
        }
        PermissionCallbacks presentPermissionCallbacks = permissionCallbacksMap.get(requestCode);
        //TODO
        if (!granted.isEmpty() && presentPermissionCallbacks != null) {
            presentPermissionCallbacks.onPermissionsGranted(requestCode, granted);

        }
        // Report denied permissions, if any.
        if (!denied.isEmpty() && presentPermissionCallbacks != null) {
            presentPermissionCallbacks.onPermissionsDenied(requestCode, denied);
        }
        permissionCallbacksMap.remove(requestCode);
    }

    /**
     * Check if at least one permission in the list of denied permissions has been permanently
     * denied (user clicked "Never ask again").
     * <p>
     * <b>Note</b>: Due to a limitation in the information provided by the Android
     * framework permissions API, this method only works after the permission
     * has been denied and your app has received the onPermissionsDenied callback.
     * Otherwise the library cannot distinguish permanent denial from the
     * "not yet denied" case.
     *
     * @param host              context requesting permissions.
     * @param deniedPermissions list of denied permissions, usually from {@link
     *                          PermissionCallbacks#onPermissionsDenied(int, List)}
     * @return {@code true} if at least one permission in the list was permanently denied.
     */
    public static boolean somePermissionPermanentlyDenied(@NonNull Activity host, @NonNull List<String> deniedPermissions) {
        return PermissionHelper.newInstance(host)
                .somePermissionPermanentlyDenied(deniedPermissions);
    }

    /**
     * @see #somePermissionPermanentlyDenied(Activity, List)
     */
    public static boolean somePermissionPermanentlyDenied(@NonNull Fragment host,
                                                          @NonNull List<String> deniedPermissions) {
        return PermissionHelper.newInstance(host)
                .somePermissionPermanentlyDenied(deniedPermissions);
    }

    /**
     * @see #somePermissionPermanentlyDenied(Activity, List).
     */
    public static boolean somePermissionPermanentlyDenied(@NonNull android.app.Fragment host,
                                                          @NonNull List<String> deniedPermissions) {
        return PermissionHelper.newInstance(host)
                .somePermissionPermanentlyDenied(deniedPermissions);
    }

    /**
     * Check if a permission has been permanently denied (user clicked "Never ask again").
     *
     * @param host             context requesting permissions.
     * @param deniedPermission denied permission.
     * @return {@code true} if the permissions has been permanently denied.
     */
    public static boolean permissionPermanentlyDenied(@NonNull Activity host,
                                                      @NonNull String deniedPermission) {
        return PermissionHelper.newInstance(host).permissionPermanentlyDenied(deniedPermission);
    }

    /**
     * @see #permissionPermanentlyDenied(Activity, String)
     */
    public static boolean permissionPermanentlyDenied(@NonNull Fragment host,
                                                      @NonNull String deniedPermission) {
        return PermissionHelper.newInstance(host).permissionPermanentlyDenied(deniedPermission);
    }

    /**
     * @see #permissionPermanentlyDenied(Activity, String).
     */
    public static boolean permissionPermanentlyDenied(@NonNull android.app.Fragment host, @NonNull String deniedPermission) {
        return PermissionHelper.newInstance(host).permissionPermanentlyDenied(deniedPermission);
    }

    /**
     * See if some denied permission has been permanently denied.
     *
     * @param host  requesting context.
     * @param perms array of permissions.
     * @return true if the user has previously denied any of the {@code perms} and we should show a
     * rationale, false otherwise.
     */
    public static boolean somePermissionDenied(@NonNull Activity host, @NonNull String... perms) {
        return PermissionHelper.newInstance(host).somePermissionDenied(perms);
    }

    /**
     * @see #somePermissionDenied(Activity, String...)
     */
    public static boolean somePermissionDenied(@NonNull Fragment host, @NonNull String... perms) {
        return PermissionHelper.newInstance(host).somePermissionDenied(perms);
    }

    /**
     * @see #somePermissionDenied(Activity, String...)
     */
    public static boolean somePermissionDenied(@NonNull android.app.Fragment host, @NonNull String... perms) {
        return PermissionHelper.newInstance(host).somePermissionDenied(perms);
    }

    /**
     * Run permission callbacks on an object that requested permissions but already has them by
     * simulating {@link PackageManager#PERMISSION_GRANTED}.
     *
     * @param requestCode the permission request code.
     * @param perms       a list of permissions requested.
     */
    private static void notifyAlreadyHasPermissions(int requestCode, @NonNull String[] perms) {
        int[] grantResults = new int[perms.length];
        for (int i = 0; i < perms.length; i++) {
            grantResults[i] = PackageManager.PERMISSION_GRANTED;
        }

        onRequestPermissionsResult(requestCode, perms, grantResults);
    }
}
