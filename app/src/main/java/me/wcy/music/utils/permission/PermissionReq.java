package me.wcy.music.utils.permission;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

/**
 * 运行时权限<br>
 * <p>
 * group:android.permission-group.CONTACTS<br>
 * permission:android.permission.WRITE_CONTACTS<br>
 * permission:android.permission.GET_ACCOUNTS<br>
 * permission:android.permission.READ_CONTACTS<br>
 * <p>
 * group:android.permission-group.PHONE<br>
 * permission:android.permission.READ_CALL_LOG<br>
 * permission:android.permission.READ_PHONE_STATE<br>
 * permission:android.permission.CALL_PHONE<br>
 * permission:android.permission.WRITE_CALL_LOG<br>
 * permission:android.permission.USE_SIP<br>
 * permission:android.permission.PROCESS_OUTGOING_CALLS<br>
 * permission:com.android.voicemail.permission.ADD_VOICEMAIL<br>
 * <p>
 * group:android.permission-group.CALENDAR<br>
 * permission:android.permission.READ_CALENDAR<br>
 * permission:android.permission.WRITE_CALENDAR<br>
 * <p>
 * group:android.permission-group.CAMERA<br>
 * permission:android.permission.CAMERA<br>
 * <p>
 * group:android.permission-group.SENSORS<br>
 * permission:android.permission.BODY_SENSORS<br>
 * <p>
 * group:android.permission-group.LOCATION<br>
 * permission:android.permission.ACCESS_FINE_LOCATION<br>
 * permission:android.permission.ACCESS_COARSE_LOCATION<br>
 * <p>
 * group:android.permission-group.STORAGE<br>
 * permission:android.permission.READ_EXTERNAL_STORAGE<br>
 * permission:android.permission.WRITE_EXTERNAL_STORAGE<br>
 * <p>
 * group:android.permission-group.MICROPHONE<br>
 * permission:android.permission.RECORD_AUDIO<br>
 * <p>
 * group:android.permission-group.SMS<br>
 * permission:android.permission.READ_SMS<br>
 * permission:android.permission.RECEIVE_WAP_PUSH<br>
 * permission:android.permission.RECEIVE_MMS<br>
 * permission:android.permission.RECEIVE_SMS<br>
 * permission:android.permission.SEND_SMS<br>
 * permission:android.permission.READ_CELL_BROADCASTS<br>
 */
public class PermissionReq {
    private static int sRequestCode = 0;
    private static SparseArray<PermissionResult> sResultArray = new SparseArray<>();

    private Object mObject;
    private String[] mPermissions;
    private PermissionResult mResult;

    private PermissionReq(Object object) {
        mObject = object;
    }

    public static PermissionReq with(@NonNull Activity activity) {
        return new PermissionReq(activity);
    }

    public static PermissionReq with(@NonNull Fragment fragment) {
        return new PermissionReq(fragment);
    }

    public PermissionReq permissions(@NonNull String... permissions) {
        mPermissions = permissions;
        return this;
    }

    public PermissionReq result(@Nullable PermissionResult result) {
        mResult = result;
        return this;
    }

    public void request() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            if (mResult != null) {
                mResult.onGranted();
            }
            return;
        }

        Activity activity = getActivity(mObject);
        if (activity == null) {
            throw new IllegalArgumentException(mObject.getClass().getName() + " is not supported");
        }

        List<String> deniedPermissionList = getDeniedPermissions(activity, mPermissions);
        if (deniedPermissionList.isEmpty()) {
            if (mResult != null) {
                mResult.onGranted();
            }
            return;
        }

        int requestCode = genRequestCode();
        String[] deniedPermissions = deniedPermissionList.toArray(new String[deniedPermissionList.size()]);
        ActivityCompat.requestPermissions(activity, deniedPermissions, requestCode);
        sResultArray.put(requestCode, mResult);
    }

    public static void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionResult result = sResultArray.get(requestCode);

        if (result == null) {
            return;
        }

        sResultArray.remove(requestCode);

        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                result.onDenied();
                return;
            }
        }
        result.onGranted();
    }

    private static List<String> getDeniedPermissions(Context context, String[] permissions) {
        List<String> deniedPermissionList = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                deniedPermissionList.add(permission);
            }
        }
        return deniedPermissionList;
    }

    private static Activity getActivity(Object object) {
        if (object != null) {
            if (object instanceof Activity) {
                return (Activity) object;
            } else if (object instanceof Fragment) {
                return ((Fragment) object).getActivity();
            }
        }
        return null;
    }

    private static int genRequestCode() {
        return ++sRequestCode;
    }
}
