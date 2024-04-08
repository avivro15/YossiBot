package com.example.yossibot

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.example.yossibot.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.io.FileOutputStream

const val FILE_NAME = "yossiTemp.txt"
const val STORAGE_PERMISSION_CODE = 23

class MainActivity : AppCompatActivity() {

    private val storageActivityResultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                //Android is 11 (R) or above
                if (Environment.isExternalStorageManager()) {
                    //Manage External Storage Permissions Granted
                    Log.d("TAG", "onActivityResult: Manage External Storage Permissions Granted")
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Storage Permissions Denied",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                //Below android 11
            }
        }

    private lateinit var binding : ActivityMainBinding

    // region Lifecycle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!FilesHelper.checkStoragePermissions(this)) {
            requestForStoragePermissions()
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.sendBtn.setOnClickListener {
            Snackbar.make(it, "Message is sending - ${binding.userInputEt.text.toString()}",
                Snackbar.LENGTH_SHORT)
                    .show()
            intentSendFileTelegram(saveToFile(binding.userInputEt.text.toString()))

//            intentMessageTelegram(binding.userInputEt.text.toString())
//            intentMessageTelegram(binding.userInputEt.text.toString())

            binding.userInputEt.setText("")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty()) {
                val write = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val read = grantResults[1] == PackageManager.PERMISSION_GRANTED
                if (read && write) {
                    Toast.makeText(
                        this@MainActivity,
                        "Storage Permissions Granted",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Storage Permissions Denied",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    // endregion

    // region private methods

    /**
     * Save file to
     * @param data - data to be sent
     */
    private fun saveToFile(data: String) : File {
        val folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)

        val file = File(folder, FILE_NAME)

        lateinit var fileOutputStream: FileOutputStream

        try {
            fileOutputStream = FileOutputStream(file)
            fileOutputStream.write(data.toByteArray())
            fileOutputStream.close()
        }
        catch (e: Exception) {
            e.printStackTrace()
        }

        return file
    }

    /**
     * Get External uti for a file
     * @param file - the file
     */
    private fun getUri(file: File) : Uri {
        return FileProvider.getUriForFile(this, "com.example.yossibot.provider", file)
    }

    /**
     * Intent to send a telegram message
     * @param msg
     */
    private fun intentMessageTelegram(msg: String?) {
        val appName = "org.telegram.messenger"
        val myIntent = Intent(Intent.ACTION_SEND)
        myIntent.type = "text/plain"
        myIntent.setPackage(appName)
        myIntent.putExtra(Intent.EXTRA_TEXT, msg)
        this.startActivity(Intent.createChooser(myIntent, "Share with"))

    }

    /**
     * Request storage permissions
     */
    private fun requestForStoragePermissions() {
        //Android is 11 (R) or above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                val uri = Uri.fromParts("package", this.packageName, null)
                intent.data = uri
                storageActivityResultLauncher.launch(intent)
            } catch (e: Exception) {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                storageActivityResultLauncher.launch(intent)
            }
        } else {
            //Below android 11
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                STORAGE_PERMISSION_CODE
            )
        }
    }

    /**
     * Send file to telegram via intent
     */
    private fun intentSendFileTelegram(file: File) {
        val appName = "org.telegram.messenger"
        val myIntent = Intent(Intent.ACTION_SEND)
        myIntent.type = "text/*"
        myIntent.setPackage(appName)
        myIntent.putExtra(Intent.EXTRA_STREAM, getUri(file))
        this.startActivity(myIntent)
    }

    // endregion

}