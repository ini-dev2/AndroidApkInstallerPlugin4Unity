# Unity APK Installer Plugin

A lightweight Android plugin for Unity that allows you to programmatically install APK files on Android devices (including Android 8.0+ where unknown app installation permission is required).

This plugin handles:
- Checking and requesting the **"Install unknown apps"** permission (Android 8.0+)
- Properly generating a content URI using `FileProvider` (Android 7.0+ compatibility)
- Safely launching the APK installer intent

Ideal for Unity apps that download and install updates or additional modules (e.g. AAB splits, expansion files, or self-updating apps).

## Features

- Supports Android 6.0 (API 23) and above
- Automatically requests `REQUEST_INSTALL_PACKAGES` permission when needed
- Uses `FileProvider` to avoid `FileUriExposedException` on Android 7.0+
- Clean and simple API
- No external dependencies

## Requirements

- Unity 2018.4 or later
- Android target SDK ≥ 26 (recommended)
- Properly configured `FileProvider` in your AndroidManifest.xml

## Example unity project
https://github.com/ini-dev2/UnityAndroidApkInstallerPluginSample

## Setup

### 1. Add the plugin to Unity

Place the following files into your Unity project's `Assets/Plugins/Android/` folder:

- `APKInstallerPlugin.java` (or compiled `.aar`/`.jar`)
- `AndroidManifest.xml` (merged or custom)

### 2. Required AndroidManifest.xml configuration

Add this inside the `<manifest>` tag (usually in a custom AndroidManifest.xml in `Plugins/Android`):

```xml
<!-- Android 10 < -->
	<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"
        android:maxSdkVersion="28" />
	<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
        android:maxSdkVersion="28" />

	<!-- Android 11+ -->
	<uses-permission android:name="android.permission.MANAGE_EXTERNAL_STORAGE"
        android:minSdkVersion="30"
        tools:ignore="ScopedStorage" />

	<!-- Android 8.0+ -->
	<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />



<application
    android:allowBackup="true"
    ... >

    <!-- Add FileProvider for sharing APK files securely -->
    <provider
                android:name="androidx.core.content.FileProvider"
                android:authorities="${applicationId}.fileprovider"
                android:exported="false"
                android:grantUriPermissions="true">
			<meta-data
				android:name="android.support.FILE_PROVIDER_PATHS"
				android:resource="@xml/file_paths" />
		</provider>

</application>
```
This allows sharing files from external storage, internal files, and cache directories.

## 3. Usage in c#

```C#
using UnityEngine;

public class APKUpdater : MonoBehaviour
{
    private AndroidJavaObject installerPlugin;

    void Start()
    {
        if (Application.platform == RuntimePlatform.Android)
        {
            installerPlugin = new AndroidJavaObject("com.nemajor.unityapkinstaller.APKInstallerPlugin", 
                new AndroidJavaClass("com.unity3d.player.UnityPlayer").GetStatic<AndroidJavaObject>("currentActivity"));
        }
    }

    public void InstallAPK(string apkPath)
    {
        if (installerPlugin != null)
        {
            installerPlugin.Call("installApk", apkPath);
        }
    }

    // Important: Handle permission result in your Activity's onActivityResult equivalent
    // Unity: Use a separate Android plugin or Unity's PermissionCallbacks to handle result
}
Handling Permission Result
The plugin uses startActivityForResult with request code 100.
You must forward the result from Unity's Android activity. Example using a common pattern:
C#// In your main AndroidJavaProxy or Activity plugin
public override void onActivityResult(int requestCode, int resultCode, Intent data)
{
    if (requestCode == 100)
    {
        // Permission screen closed — now retry installation if needed
        // You may want to retry the install here
    }
}


Tip: Many developers re-call installApk() after returning from settings if the permission is now granted.
Example Paths
C#// Persistent data path (recommended)
string apkPath = Application.persistentDataPath + "/update.apk";

// Or cache
string apkPath = Application.temporaryCachePath + "/myapp_update.apk";
Limitations
```

On Android 8.0+, user must manually allow "Install unknown apps" for your app
No built-in download functionality (use UnityWebRequest or other solutions)
You are responsible for forwarding onActivityResult if you need to retry after permission grant

## License
MIT License
Feel free to use, modify, and distribute.
## Author
ini-dev2 - [GitHub Profile](https://github.com/ini-dev2)
Made with ❤️ for the Unity community
