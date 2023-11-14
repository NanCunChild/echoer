package com.example.echoer.managers;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PermissionManager {
    private final AppCompatActivity appCompatActivity;
    private ActivityResultLauncher<String[]> requestPermissionLauncher;
    private PermissionAuthResultActor authResultActorCallBack;
    private final String[] permissionNeeded; // 传入权限管理器的需求权限

    public interface PermissionAuthResultActor {
        // 该接口是用来判断用户在初次授权时的操作的
        void onPermissionsGranted();

        void onPermissionsDenied(String[] permissionDenied);
    }
    public void setPermissionAuthResultActor(PermissionAuthResultActor paraCallback){
        this.authResultActorCallBack = paraCallback;
    }

    private void PermissionAuthResultListener() {
        // 该方法负责判断用户在授权显示框中的行为：拒绝或者接受权限，在这里配置的Launcher会在用户接受或拒绝时调用
        // 一般情况下，都会是 requestPermissions 方法调用Launch，之后用回调判断，这个方法只会在这个过程中使用。
        this.requestPermissionLauncher =
                appCompatActivity.registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), (Map<String, Boolean> result) -> {
                    List<String> deniedPermissions = new ArrayList<>();
                    for (Map.Entry<String, Boolean> entry : result.entrySet()) {
                        if (!entry.getValue()) {
                            deniedPermissions.add(entry.getKey());
                        }
                    }

                    if (authResultActorCallBack != null) {
                        if (deniedPermissions.isEmpty()) {
                            authResultActorCallBack.onPermissionsGranted();
                        } else {
                            authResultActorCallBack.onPermissionsDenied( deniedPermissions.toArray(new String[0]));
                        }
                    } else {
                        Log.w("Permission", "Call Back Lacked!");
                    }
                });
    }

    public PermissionManager(AppCompatActivity activity, String[] permissionNeeded) {
        this.appCompatActivity = activity;
        this.permissionNeeded = permissionNeeded;
        PermissionAuthResultListener();
    }

    public String[] hasPermissions() {
        // 权限检查函数，检查当今是否拥有类中的权限。没有的权限会被作为字符串数组输出，所以判断时就判断是否为空就OK。
        // 这个方法应该是最先调用的，保证检查完整性。
        ArrayList<String> permissionNotGet = new ArrayList<>(); // 作为List可以快速地动态加入项
        for (String permission : permissionNeeded) {
            if (ContextCompat.checkSelfPermission(appCompatActivity, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionNotGet.add(permission);
            }
        }
        String[] formedPermissionNotGet = permissionNotGet.toArray(new String[permissionNotGet.size()]);
        return formedPermissionNotGet;
    }

    public boolean requestPermissions() {
        /*
         * TRUE:
         * 没有被永久拒绝的权限，要么所有权限都获得了；要么暂时没有，但是可以请求。
         * FALSE:
         * 权限以前被永久拒绝过，请求的机会都没了，此时跳转后是否打开了权限是未知的，回来后再过一遍权限检测才行。
         * */
        ArrayList<String> permissionPermanentDenied = new ArrayList<>();
        String[] permissionNotGet = hasPermissions();
        if (permissionNotGet.length > 0) {
            // 调用了检测权限函数，返回长度大于0表示存在没有授权的权限。
            for (String permission : permissionNotGet) {
                if (appCompatActivity.shouldShowRequestPermissionRationale(permission)) {
                    // 此处为用户永久拒绝了权限的情况，只要有一项就会引导用户进入设置
                    /*
                    shouldShowRequestPermissionRationale()的作用:
                        1，没有申请过权限，申请就是了，所以返回false；
                        2，申请了用户拒绝了，那你就要提示用户了，所以返回true；
                        3，已经允许了，不需要申请也不需要提示，所以返回false；
                        4，用户选择了拒绝并且不再提示，那你也不要申请了，也不要提示用户了，所以返回false；
                    */
                    permissionPermanentDenied.add(permission);
                }
            }

            if (permissionPermanentDenied.size() == 0) {
                // 没有被永久拒绝的权限，放心请求
                requestPermissionLauncher.launch(permissionNotGet);
                return true;
            } else {
                // 如果存在用户永久拒绝了的权限，则跳转至设置界面设置权限
                Log.d("Permission", "Permission Can Not Permitted In App."+permissionPermanentDenied);
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", appCompatActivity.getPackageName(), null);
                intent.setData(uri);
                appCompatActivity.startActivity(intent);
                return false;
            }
        } else {
            return true;
        }
    }

    public boolean hasNotificationPermission(){
        //  暂时别调用，没大搞懂
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Linked Wire";
            String description = "The wire you are linking.";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("CHANNEL_ID", name, importance);
            channel.setDescription(description);
            // 注册通道
//            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(channel);
        }

        return false;
    }
}
