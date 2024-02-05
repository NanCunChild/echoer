package com.nancunchild.echoer.utils

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class PermissionManager(
    private val appCompatActivity: AppCompatActivity,
    private val permissionNeeded: Array<String>
    // 传入参数为需要请求的权限
) {
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>
    private var authResultActorCallBack: PermissionAuthResultActor? = null

    // 该接口用于在权限请求完成后通知调用者结果，你当然需要每次重写
    interface PermissionAuthResultActor {
        fun onPermissionsGranted()
        fun onPermissionsDenied(permissionDenied: Array<String>)
    }

    // 设置权限请求结果的回调接口
    fun setPermissionAuthResultActor(callback: PermissionAuthResultActor) {
        this.authResultActorCallBack = callback
    }

    // 初始化时设置权限请求结果监听
    init {
        setupPermissionAuthResultListener()
    }

    // 权限请求结果处理器
    private fun setupPermissionAuthResultListener() {
        requestPermissionLauncher =
            appCompatActivity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
                val deniedPermissions = result.entries.filter { !it.value }.map { it.key }

                authResultActorCallBack?.let {
                    if (deniedPermissions.isEmpty()) {
                        it.onPermissionsGranted()
                    } else {
                        it.onPermissionsDenied(deniedPermissions.toTypedArray())
                    }
                } ?: Log.w("Permission", "Callback Lacked!")
            }
    }

    // 判断是否已经拥有所请求的权限
    fun hasPermissions(): Array<String> {
        return permissionNeeded.filter {
            ContextCompat.checkSelfPermission(
                appCompatActivity,
                it
            ) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()
    }

    // 请求尚未获得的权限
    fun requestPermissions(): Boolean {
        val permissionsNotGranted = hasPermissions()
        return if (permissionsNotGranted.isNotEmpty()) {
            val permanentlyDenied = permissionsNotGranted.filter {
                !appCompatActivity.shouldShowRequestPermissionRationale(it)
            }
            /*
                    shouldShowRequestPermissionRationale()的作用:
                        1，没有申请过权限，申请就是了，所以返回false；
                        2，申请了用户拒绝了，就要提示用户，所以返回true；
                        3，已经允许了，不需要申请也不需要提示，所以返回false；
                        4，用户选择了拒绝并且不再提示，那你也不要申请了，也不要提示用户了，所以返回false；
            */

            if (permanentlyDenied.isEmpty()) {
                requestPermissionLauncher.launch(permissionsNotGranted)
                true
            } else {
                Log.d("Permission", "Permission Cannot Be Permitted In App. $permanentlyDenied")
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", appCompatActivity.packageName, null)
                intent.data = uri
                appCompatActivity.startActivity(intent)
                false
            }
        } else {
            true
        }
    }

    fun hasNotificationPermission(): Boolean {
        // 处理通知权限检查
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // ... 适当的通知渠道处理
        }
        return false
    }
}
