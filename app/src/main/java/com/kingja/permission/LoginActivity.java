package com.kingja.permission;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    private String permission = Manifest.permission.READ_PHONE_STATE;

    private String[] permissionArray = new String[]{
            Manifest.permission.CAMERA,Manifest.permission.READ_PHONE_STATE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button btn_takkePhoto = (Button) findViewById(R.id.btn_takePhoto);
        btn_takkePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionUtils.checkPermissionArray(LoginActivity.this, permissionArray, PermissionUtils.PERMISSION_REQUEST_CODE);
            }
        });

        Button btn_readImei = (Button) findViewById(R.id.btn_readImei);
        btn_readImei.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionUtils.checkPermission(LoginActivity.this, permission, 1);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PermissionUtils.PERMISSION_REQUEST_CODE:
                if (PermissionUtils.verifyPermissions(grantResults)) {

                } else {
                    Toast.makeText(this, "WRITE_CONTACTS Denied", Toast.LENGTH_SHORT)
                            .show();
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setTitle("帮助");
                    builder.setMessage("当前应用缺少必要权限。点击设置打开权限设置页。");

                    // 拒绝, 退出应用
                    builder.setNegativeButton("退出", new DialogInterface.OnClickListener() {
                        @Override public void onClick(DialogInterface dialog, int which) {
                            //setResult(PERMISSIONS_DENIED);
                            //finish();
                        }
                    });

                    builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
                        @Override public void onClick(DialogInterface dialog, int which) {
                            startAppSettings();
                        }
                    });

                    builder.setCancelable(false);

                    builder.show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    private static final String PACKAGE_URL_SCHEME = "package:";
    // 启动应用的设置
    private void startAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse(PACKAGE_URL_SCHEME + getPackageName()));
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PermissionUtils.PERMISSION_SETTING_REQ_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {

                } else {
                    Toast.makeText(this, "not has setting permission", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
    }
}
