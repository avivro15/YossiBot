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
import java.io.IOException
import java.util.UUID

const val TEMP_FOLDER_NAME = "tempYossi"

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

    fun clearTempFolder() {
        for (file in getTempFolder().listFiles()!!) {
            file.delete()
        }
    }

    /**
     * Create temp folder if not exists
     * @return the Temp folder
     */
    private fun getTempFolder() : File {
        // Create the "temp" folder within Documents
        val tempFolder = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
            TEMP_FOLDER_NAME
        )

        // Check if the folder already exists
        if (!tempFolder.exists()) {
            try {
                tempFolder.mkdirs()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        return tempFolder
    }

    fun saveToFile(sendData: SendData) : File {
        val folder = getTempFolder()
        val file = File(folder, UUID.randomUUID().toString() + FILE_NAME)

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