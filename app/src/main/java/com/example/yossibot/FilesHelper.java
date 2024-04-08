package com.example.yossibot;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class FilesHelper {

    public static boolean checkStoragePermissions(Context context){
        boolean isPermissionsGranted;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            //Android is 11 (R) or above
            isPermissionsGranted =  Environment.isExternalStorageManager();
        }
        else {
            //Below android 11
            int write = ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE);
            int read = ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE);

            isPermissionsGranted =  (read == PackageManager.PERMISSION_GRANTED)
                    && (write == PackageManager.PERMISSION_GRANTED);
        }

        return isPermissionsGranted;
    }
}
