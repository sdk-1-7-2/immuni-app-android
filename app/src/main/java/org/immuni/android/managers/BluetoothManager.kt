package org.immuni.android.managers

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import kotlinx.coroutines.*
import org.immuni.android.ImmuniApplication
import org.immuni.android.R
import org.immuni.android.toast
import org.immuni.android.workers.BLEForegroundServiceWorker
import org.koin.core.KoinComponent

class BluetoothManager(val context: Context) : KoinComponent {
    private val bluetoothAdapter: BluetoothAdapter by lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    fun adapter(): BluetoothAdapter {
        return bluetoothAdapter
    }

    fun isBluetoothEnabled(): Boolean {
        return bluetoothAdapter.isEnabled ?: false
    }

    fun isBluetoothSupported(): Boolean {
        return !context.packageManager.missingSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)
    }

    fun openBluetoothSettings(fragment: Fragment, requestCode: Int = REQUEST_ENABLE_BT) {
        bluetoothAdapter.takeIf { !it.isEnabled }?.apply {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            fragment.startActivityForResult(enableBtIntent, requestCode)
        }
    }

    fun openBluetoothSettings(activity: Activity, requestCode: Int = REQUEST_ENABLE_BT) {
        bluetoothAdapter.takeIf { !it.isEnabled }?.apply {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            activity.startActivityForResult(enableBtIntent, requestCode)
        }
    }

    fun scheduleBLEWorker(appContext: Context) {

        // check if the hardware support BLE
        if(!isBluetoothSupported()) {
            toast(context.getString(R.string.ble_not_supported_by_this_device))
            return
        }

        GlobalScope.launch (Dispatchers.Default) {
            val workManager = WorkManager.getInstance(appContext)

            // first check if it is already running
            var restart = true
            try {
                withTimeout(5000) {
                    val infoList = workManager.getWorkInfosByTag(BLEForegroundServiceWorker.TAG)
                    val infos = infoList.get()
                    if(infos.any { it.state == WorkInfo.State.RUNNING }) {
                        Log.d(TAG, "### Skip BLEForegroundServiceWorker restarter, since it is already running.")
                        restart = false
                    }
                }
            } catch (e: Exception) { }

            if(!restart) return@launch

            Log.d(TAG, "### Restarting BLEForegroundServiceWorker.")

            workManager.cancelAllWorkByTag(BLEForegroundServiceWorker.TAG)

            // let the previous worker stop before restarting it
            delay(2000)

            val notificationWork = OneTimeWorkRequestBuilder<BLEForegroundServiceWorker>().addTag(BLEForegroundServiceWorker.TAG)
            workManager.enqueue(notificationWork.build())
        }
    }

    companion object {
        const val TAG = "BluetoothManager"
        const val REQUEST_ENABLE_BT = 978
    }

    private fun PackageManager.missingSystemFeature(name: String): Boolean = !hasSystemFeature(name)
}
