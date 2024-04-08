package com.example.yossibot.files

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import androidx.core.content.ContextCompat
import com.example.yossibot.FILE_NAME
import com.example.yossibot.data.SendData
import com.google.gson.GsonBuilder
import java.io.File
import java.io.FileOutputStream

object FilesHelper {

    fun checkStoragePermissions(context: Context?): Boolean {
        return  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            //Android is 11 (R) or above
            Environment.isExternalStorageManager()
        } else {
            //Below android 11
            val write = ContextCompat.checkSelfPermission(
                context!!,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            val read = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            read == PackageManager.PERMISSION_GRANTED && write == PackageManager.PERMISSION_GRANTED
        }
    }

    fun saveToFile(sendData: SendData) : File {
        val folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
        val file = File(folder, FILE_NAME)

        lateinit var fileOutputStream: FileOutputStream

        try {
            fileOutputStream = FileOutputStream(file)
            fileOutputStream.write(GsonBuilder().create().toJson(sendData).toByteArray())
            fileOutputStream.close()
        }
        catch (e: Exception) {
            e.printStackTrace()
        }

        return file
    }

}