package com.example.echoer.managers;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PermissionManager {
    private final AppCompatActivity mActivity;
    private final String[] mPermissions;
    private PermissionCallback mCallback;
    private ActivityResultLauncher<String[]> requestPermissionLauncher;

    // 回调接口
    public interface PermissionCallback {
        void onPermissionsGranted();
        void onPermissionsDenied(List<String> deniedPermissions);
    }

    public PermissionManager(Activity activity, String[] permissions) {
        this.mActivity = (AppCompatActivity) activity;
        this.mPermissions = permissions;
        initializePermissionLauncher();
    }

    private void initializePermissionLauncher() {
        this.requestPermissionLauncher =
                mActivity.registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), (Map<String, Boolean> result) -> {
                    List<String> deniedPermissions = new ArrayList<>();
                    for (Map.Entry<String, Boolean> entry :  result.entrySet()) {
                        if (!entry.getValue()) {
                            deniedPermissions.add(entry.getKey());
                        }
                    }

                    if (deniedPermissions.isEmpty() && mCallback != null) {
                        mCallback.onPermissionsGranted();
                    } else if (mCallback != null) {
                        mCallback.onPermissionsDenied(deniedPermissions);
                    }
                });
    }

    public void setPermissionCallback(PermissionCallback callback) {
        // 这个setter方法允许你提供一个自定义的实现，告诉PermissionManager当请求权限的操作完成后应该做什么。
        // 说白了也就是重写 PermissionCallback 这个接口中的方法
        this.mCallback = callback;
    }

    public boolean arePermissionsGranted() {
        // 遇事不决，先查一遍权限，只要有一个不对就返回false，全通过就返回true。
        // 这个方法应该是最先调用的，保证检查完整性。
        for (String permission : mPermissions) {
            if (ContextCompat.checkSelfPermission(mActivity, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public void requestPermissions() {
        if (!arePermissionsGranted()) {
//            if (mActivity.shouldShowRequestPermissionRationale())
            requestPermissionLauncher.launch(mPermissions);
        } else if (mCallback != null) {
            mCallback.onPermissionsGranted();
        }
    }

    public void guideUserToSettings() {
        if (!arePermissionsGranted()) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", mActivity.getPackageName(), null);
            intent.setData(uri);
            mActivity.startActivity(intent);
        }
    }
}
