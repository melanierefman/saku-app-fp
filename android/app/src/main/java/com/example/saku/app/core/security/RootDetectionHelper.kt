package com.example.saku.app.core.security

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import com.scottyab.rootbeer.RootBeer
import java.io.File

data class RootCheckResult(
    val isRooted: Boolean,
    val reasons: List<String> = emptyList()
)

object RootDetectionHelper {

    private const val TAG = "RootDetectionHelper"

    private val KNOWN_ROOT_PACKAGES = listOf(
        "com.topjohnwu.magisk",
        "eu.chainfire.supersu",
        "com.noshufou.android.su",
        "com.koushikdutta.superuser",
        "com.thirdparty.superuser",
        "com.yellowes.su",
        "com.kingroot.kinguser",
        "com.kingo.root",
        "com.smedialink.oneclickroot",
        "com.zhiqupk.root.global"
    )

    private val SU_PATHS = listOf(
        "/system/app/Superuser.apk",
        "/sbin/su",
        "/system/bin/su",
        "/system/xbin/su",
        "/data/local/xbin/su",
        "/data/local/bin/su",
        "/system/sd/xbin/su",
        "/system/bin/failsafe/su",
        "/data/local/su",
        "/su/bin/su"
    )

    fun checkDeviceSecurity(context: Context): RootCheckResult {
        val reasons = mutableListOf<String>()

        // 1. RootBeer library check
        try {
            val rootBeer = RootBeer(context)
            if (rootBeer.isRooted) {
                reasons.add("RootBeer mendeteksi indikasi perangkat di-root")
            }
            if (rootBeer.isRootedWithBusyBoxCheck) {
                reasons.add("RootBeer mendeteksi binary BusyBox / su")
            }
        } catch (e: Exception) {
            Log.w(TAG, "Error running RootBeer check: ${e.message}")
        }

        // 2. Check SU binary files manually
        if (checkSuBinaryExists()) {
            reasons.add("Binary 'su' terdeteksi pada sistem file")
        }

        // 3. Check Test-Keys build tags
        if (checkBuildTags()) {
            reasons.add("Build OS menggunakan test-keys tidak resmi")
        }

        // 4. Check Root Management Applications
        val installedRootApp = checkRootPackages(context)
        if (installedRootApp != null) {
            reasons.add("Aplikasi manajemen root terpasang: $installedRootApp")
        }

        val isRooted = reasons.isNotEmpty()
        if (isRooted) {
            Log.w(TAG, "Device Root Warning: ${reasons.joinToString(", ")}")
        }

        return RootCheckResult(
            isRooted = isRooted,
            reasons = reasons
        )
    }

    private fun checkSuBinaryExists(): Boolean {
        return try {
            SU_PATHS.any { path -> File(path).exists() }
        } catch (e: Exception) {
            false
        }
    }

    private fun checkBuildTags(): Boolean {
        val buildTags = Build.TAGS
        return buildTags != null && buildTags.contains("test-keys")
    }

    private fun checkRootPackages(context: Context): String? {
        val pm = context.packageManager
        for (pkg in KNOWN_ROOT_PACKAGES) {
            try {
                pm.getPackageInfo(pkg, PackageManager.GET_ACTIVITIES)
                return pkg
            } catch (_: PackageManager.NameNotFoundException) {
                // Not found, continue
            }
        }
        return null
    }
}