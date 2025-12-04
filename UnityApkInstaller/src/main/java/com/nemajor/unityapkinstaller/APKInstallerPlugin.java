package com.nemajor.unityapkinstaller;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import androidx.core.content.FileProvider;

import java.io.File;

public class APKInstallerPlugin {

    private final Activity activity;

    public APKInstallerPlugin(Activity activity) {
        this.activity = activity;
    }

    public void installApk(String apkFilePath) {
        File apkFile = new File(apkFilePath);

        if (!apkFile.exists()) {
            return;
        }

        // Проверяем и запрашиваем разрешение если нужно
        if (checkAndRequestInstallPermission()) {
            return; // Разрешение запрошено, ждем результата
        }

        // Если разрешение есть, продолжаем установку
        executeInstallation(apkFile);
    }

    /**
     * Проверяет и запрашивает разрешение на установку
     * @return true если разрешение было запрошено, false если оно уже есть
     */
    public boolean checkAndRequestInstallPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!activity.getPackageManager().canRequestPackageInstalls()) {
                // Запрашиваем разрешение
                Intent permissionIntent = new Intent(
                        android.provider.Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                        Uri.parse("package:" + activity.getPackageName())
                );
                activity.startActivityForResult(permissionIntent, 100);
                return true; // Разрешение запрошено
            }
        }
        return false; // Разрешение уже есть или не требуется
    }

    /**
     * Выполняет установку APK (основная логика)
     */
    private void executeInstallation(File apkFile) {
        try {
            Uri apkUri;

            // Для Android 7.0+ используем FileProvider
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                apkUri = FileProvider.getUriForFile(
                        activity,
                        activity.getPackageName() + ".fileprovider",
                        apkFile
                );
            } else {
                // Для старых версий используем обычный file:// URI
                apkUri = Uri.fromFile(apkFile);
            }

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            activity.startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}