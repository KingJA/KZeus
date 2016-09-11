package com.kingja.permission;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import lib.kingja.zeus.ZeusManager;

public class ZeusActivity extends AppCompatActivity implements ZeusManager.OnPermissionCallback {

    private ImageView iv;
    private ZeusManager zeusManager;
    private String[] permissionArr = {Manifest.permission.CAMERA, Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv = (ImageView) findViewById(R.id.iv);
        zeusManager = new ZeusManager(this);
        zeusManager.setOnPermissionCallback(this);

    }

    public void takePhoto(View view) {
        zeusManager.checkPermission(Manifest.permission.CAMERA,  true);
    }

    public void onMany(View view) {
        zeusManager.checkPermissions(permissionArr);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        zeusManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            Bitmap mImageBitmap = (Bitmap) extras.get("data");
            iv.setImageBitmap(mImageBitmap);
        }

    }

    @Override
    public void onAllow() {
        openCamera();
    }

    @Override
    public void onClose() {
        finish();
    }
}
