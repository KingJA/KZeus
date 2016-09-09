package lib.kingja.zeus;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Description：TODO
 * Create Time：2016/9/9 13:43
 * Author:KingJA
 * Email:kingjavip@gmail.com
 */
public class ZeusManager {
    private Activity activity;
    private String requirePermission;
    private int mRequestCode;
    private OnPermissionCallback onPermissionCallback;
    private static final Map<String, String> PERMISSIONS;
    private static final String SENSORS = "传感器";
    private static final String CALENDAR = "日历";
    private static final String CAMERA = "相机";
    private static final String CONTACTS = "通讯录";
    private static final String LOCATION = "地理位置";
    private static final String MICROPHONE = "麦克风";
    private static final String PHONE = "电话";
    private static final String SMS = "短信";
    private static final String STORAGE = "存储";


    static {
        PERMISSIONS = new HashMap<>();
        PERMISSIONS.put("android.permission.CAMERA", CAMERA);

        PERMISSIONS.put("android.permission.ACCESS_FINE_LOCATION", LOCATION);
        PERMISSIONS.put("android.permission.ACCESS_COARSE_LOCATION", LOCATION);

        PERMISSIONS.put("android.permission.READ_EXTERNAL_STORAGE", STORAGE);
        PERMISSIONS.put("android.permission.WRITE_EXTERNAL_STORAGE", STORAGE);

        PERMISSIONS.put("android.permission.READ_CALENDAR", CALENDAR);
        PERMISSIONS.put("android.permission.WRITE_CALENDAR", CALENDAR);

        PERMISSIONS.put("android.permission.READ_CONTACTS", CONTACTS);
        PERMISSIONS.put("android.permission.WRITE_CONTACTS", CONTACTS);
        PERMISSIONS.put("android.permission.GET_ACCOUNTS", CONTACTS);

        PERMISSIONS.put("android.permission.RECORD_AUDIO", MICROPHONE);

        PERMISSIONS.put("android.permission.READ_PHONE_STATE", PHONE);
        PERMISSIONS.put("android.permission.CALL_PHONE", PHONE);
        PERMISSIONS.put("android.permission.READ_CALL_LOG", PHONE);
        PERMISSIONS.put("android.permission.WRITE_CALL_LOG", PHONE);
        PERMISSIONS.put("com.android.voicemail.permission.ADD_VOICEMAIL", PHONE);
        PERMISSIONS.put("android.permission.USE_SIP", PHONE);
        PERMISSIONS.put("android.permission.PROCESS_OUTGOING_CALLS", PHONE);

        PERMISSIONS.put("android.permission.BODY_SENSORS", SENSORS);

        PERMISSIONS.put("android.permission.SEND_SMS", SMS);
        PERMISSIONS.put("android.permission.RECEIVE_SMS", SMS);
        PERMISSIONS.put("android.permission.READ_SMS", SMS);
        PERMISSIONS.put("android.permission.RECEIVE_WAP_PUSH", SMS);
        PERMISSIONS.put("android.permission.RECEIVE_MMS", SMS);
        PERMISSIONS.put("android.permission.READ_CELL_BROADCASTS", SMS);
    }



    public ZeusManager(Activity activity) {
        this.activity = activity;

    }

    public void checkPermission(String requirePermission, int requestCode) {
        this.requirePermission = requirePermission;
        this.mRequestCode = requestCode;

        if (Build.VERSION.SDK_INT >= 23) {
            if (isDenyed(activity, requirePermission)) {
                if (!activity.shouldShowRequestPermissionRationale(requirePermission)) {
                    showAllowDialog();
                    return;
                }
                ActivityCompat.requestPermissions(activity, new String[]{requirePermission}, mRequestCode);
                return;
            } else {
                onPermissionCallback.onAllow();
            }
        } else {
            onPermissionCallback.onAllow();
        }
    }

    private boolean isDenyed(Activity activity, String requirePermission) {
        return ContextCompat.checkSelfPermission(activity, requirePermission) != PackageManager.PERMISSION_GRANTED;
    }

    public void setOnPermissionCallback(OnPermissionCallback onPermissionCallback) {
        this.onPermissionCallback = onPermissionCallback;
    }

    public interface OnPermissionCallback {
        void onAllow();
    }

    private void showAllowDialog() {
        new AlertDialog.Builder(activity)
                .setMessage("需要开启\"" + PERMISSIONS.get(requirePermission) + "\"权限")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(activity, new String[]{requirePermission}, mRequestCode);
                    }
                })
                .setNegativeButton("取消", null)
                .create()
                .show();
    }

    /**
     * 跳转到应用详情页面
     */
    private void startAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", activity.getPackageName(), null));
        activity.startActivity(intent);
    }

    /**
     * 权限请求被拒绝
     */
    public void showDenyedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("当前应用缺少必要权限\n请点击\"设置\"-\"权限\"打开全选\n最后点击两次返回按钮即可返回应用");
        builder.setNegativeButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.finish();
            }
        });
        builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startAppSettings();
            }
        });
        builder.show();
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == mRequestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onPermissionCallback.onAllow();
            } else {
                showDenyedDialog();
            }
        }
    }

    public  void checkPermissions(String[] permissionArr, int requestCode) {
        this.mRequestCode = requestCode;
        String[] permissions = getDenyedPermissions(permissionArr);
        if (permissions.length > 0) {
            ActivityCompat.requestPermissions(activity, new String[]{requirePermission}, mRequestCode);
        }
    }

    private String[] getDenyedPermissions(String[] permissionArr) {
        ArrayList<String> permiList = new ArrayList<>();
        for (String p : permissionArr) {
            if (isDenyed(activity, p)) {
                permiList.add(p);
            }
        }

        return permiList.toArray(new String[permiList.size()]);
    }
}