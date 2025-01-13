package com.victor.worker.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import com.cherry.permissions.lib.EasyPermissions
import com.cherry.permissions.lib.annotations.AfterPermissionGranted
import com.cherry.permissions.lib.dialogs.DEFAULT_SETTINGS_REQ_CODE
import com.cherry.permissions.lib.dialogs.SettingsDialog
import com.google.android.material.snackbar.Snackbar


class MainActivity : AppCompatActivity(),OnClickListener,EasyPermissions.PermissionCallbacks,
    EasyPermissions.RationaleCallbacks {
    companion object {
        const val TAG = "MainActivity"
        const val REQUEST_CODE_LOCATION_PERMISSION = 125
        const val REQUEST_CODE_NOTIFICATION_PERMISSION = 126
    }

    private var mTvLocation: TextView? = null
    private var mBtnLocation: Button? = null
    private var mBtnNotification: Button? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mTvLocation = findViewById(R.id.mTvLocation)
        mBtnLocation = findViewById(R.id.mBtnLocation)
        mBtnNotification = findViewById(R.id.mBtnNotification)

        mBtnLocation?.setOnClickListener(this)
        mBtnNotification?.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mBtnLocation -> {
                requestLocationPermission()
            }
            R.id.mBtnNotification -> {
                requestNotificationPermission()
            }
        }
    }

    @AfterPermissionGranted(REQUEST_CODE_LOCATION_PERMISSION)
    private fun requestLocationPermission() {
        if (hasLocationPermission()) {
            // Have permissions, do things!
            showMessage("AfterPermissionGranted you have Location permissions,you can Location")
            LocationUtils.instance.getLocation(object : OnLocationListener{
                override fun OnLocation(address: String?, locationTime: String) {
                    mTvLocation?.text = "$address\n$locationTime"
                }
            })
        } else {
            // Ask for both permissions
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.permission_location_rationale_message),
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }
    @AfterPermissionGranted(REQUEST_CODE_NOTIFICATION_PERMISSION)
    private fun requestNotificationPermission() {
        Log.e(TAG,"requestNotificationPermission()......")
        if (hasNotificationPermission()) {
            Log.e(TAG,"requestNotificationPermission()......1")
            // Have permissions, do things!
            showMessage("AfterPermissionGranted you have notification permission,you can send notification")
            //5秒后发送通知
            AlarmUtil.setAlarm(this, System.currentTimeMillis() + 5000)
        } else {
            Log.e(TAG,"requestNotificationPermission()......2")
            // Ask for both permissions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.permission_notification_rationale_message),
                    REQUEST_CODE_NOTIFICATION_PERMISSION,
                    Manifest.permission.POST_NOTIFICATIONS)
            } else {
                goNotificationPermissionSetting()
            }
        }
    }

    // 跳转到应用程序设置页面
    private fun goNotificationPermissionSetting() {
        val intent = Intent("android.settings.APP_NOTIFICATION_SETTINGS")
        intent.putExtra("android.provider.extra.APP_PACKAGE", packageName)
        startActivity(intent)

    }

    private fun hasLocationPermission(): Boolean {
        return EasyPermissions.hasPermissions(this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    private fun hasNotificationPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return EasyPermissions.hasPermissions(this,
                Manifest.permission.POST_NOTIFICATIONS
            )
        }
        return checkNotificationPermission()
    }

    // 检查通知权限是否已经被授权
    private fun checkNotificationPermission(): Boolean {
        val manager = NotificationManagerCompat.from(this)
        return manager.areNotificationsEnabled()
    }

    fun showMessage(message: String) {
        Snackbar.make(window.decorView, message, Snackbar.LENGTH_LONG)
            .setAction("Action", null).show()
    }

    @SuppressLint("StringFormatMatches")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == DEFAULT_SETTINGS_REQ_CODE) {
            val yes = getString(R.string.yes)
            val no = getString(R.string.no)

            // Do something after user returned from app settings screen, like showing a Toast.
            Toast.makeText(
                this,
                getString(
                    R.string.returned_from_app_settings_to_activity,
                    if (hasLocationPermission()) yes else no,
                ),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    // ============================================================================================
    //  Implementation Permission Callbacks
    // ============================================================================================

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        Log.d(TAG, getString(R.string.log_permissions_granted, requestCode, perms.size))
        //会回调 AfterPermissionGranted注解对应方法
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        Log.d(TAG, getString(R.string.log_permissions_denied, requestCode, perms.size))

        // (Optional) Check whether the user denied any permissions and checked "NEVER ASK AGAIN."
        // This will display a dialog directing them to enable the permission in app settings.
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {

            val settingsDialogBuilder = SettingsDialog.Builder(this)

            when(requestCode) {
                REQUEST_CODE_LOCATION_PERMISSION -> {
                    settingsDialogBuilder.title = getString(
                        com.cherry.permissions.lib.R.string.title_settings_dialog,
                        "Location Permission")
                    settingsDialogBuilder.rationale = getString(
                        com.cherry.permissions.lib.R.string.rationale_ask_again,
                        "Location Permission")
                }
                REQUEST_CODE_NOTIFICATION_PERMISSION -> {
                    settingsDialogBuilder.title = getString(
                        com.cherry.permissions.lib.R.string.title_settings_dialog,
                        "Notification Permission")
                    settingsDialogBuilder.rationale = getString(
                        com.cherry.permissions.lib.R.string.rationale_ask_again,
                        "Notification Permission")
                }
            }

            settingsDialogBuilder.build().show()
        }

    }

    // ============================================================================================
    //  Implementation Rationale Callbacks
    // ============================================================================================

    override fun onRationaleAccepted(requestCode: Int) {
        Log.d(TAG, getString(R.string.log_permission_rationale_accepted, requestCode))
    }

    override fun onRationaleDenied(requestCode: Int) {
        Log.d(TAG, getString(R.string.log_permission_rationale_denied, requestCode))
    }

}