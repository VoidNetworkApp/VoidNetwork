package fcul.cmov.voidnetwork.ui.utils

import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import kotlinx.coroutines.delay

// emits vibration and flashlight signals
suspend fun Context.emitSynchronizedSignals(signal: String) {
    val cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        (getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    val cameraId = cameraManager.cameraIdList.firstOrNull { id ->
        try {
            cameraManager.getCameraCharacteristics(id)
                .get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
        } catch (e: CameraAccessException) {
            false
        }
    }

    if (cameraId == null) {
        Log.e("SignalEmitter", "No camera with flashlight available")
        return
    }

    try {
        for (char in signal) {
            val duration = if (char == SHORT) 200L else 500L

            cameraManager.setTorchMode(cameraId, true)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(duration)
            }

            delay(duration)
            cameraManager.setTorchMode(cameraId, false)
            delay(300L)
        }
    } catch (e: CameraAccessException) {
        Log.e("SignalEmitter", "Error accessing the camera", e)
    } finally {
        try {
            cameraManager.setTorchMode(cameraId, false)
        } catch (e: CameraAccessException) {
            Log.e("SignalEmitter", "Error turning off flashlight", e)
        }
    }
}

