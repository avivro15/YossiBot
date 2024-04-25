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
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.example.yossibot.databinding.ActivityMainBinding
import com.example.yossibot.files.FilesHelper
import com.example.yossibot.recipients.RecipientsViewModel
import com.example.yossibot.recipients.RecipientsViewModelFactory
import com.example.yossibot.recipients.Resource
import com.example.yossibot.ui.Form
import com.example.yossibot.ui.RecipientDialog
import com.example.yossibot.ui.RecipientsList
import kotlinx.coroutines.launch
import java.io.File

const val FILE_NAME = "yossiTemp.txt"
const val STORAGE_PERMISSION_CODE = 23

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val recipientsViewModel: RecipientsViewModel by viewModels {
        RecipientsViewModelFactory((application as Application).repository)
    }

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

    // region Lifecycle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        recipientsViewModel.insert(Recipient(0,12,"name"))


        if (!FilesHelper.checkStoragePermissions(this)) {
            requestForStoragePermissions()
        }

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val composeView = binding.composeView
        composeView.apply {
            // Dispose of the Composition when the view's LifecycleOwner
            // is destroyed
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val recipients = recipientsViewModel.uiRecipientsList
                val event = rememberSaveable {
                    mutableStateOf<Resource?>(null)
                }

                LaunchedEffect(key1 = Unit) {
                    lifecycleScope.launch {
                        recipientsViewModel.eventsFlow.collect { receivedEvent ->
                            event.value = receivedEvent

                        }
                    }
                }

                Surface {
                    if (event.value != null) {
                        when(event.value) {
                            is Resource.RecipientDialogEvent -> {
                                val dialogEvent = event.value as Resource.RecipientDialogEvent
                                if (dialogEvent.isVisible && dialogEvent.recipient != null) {
                                    RecipientDialog(
                                        recipient = dialogEvent.recipient,
                                        onConfirm = {recipientsViewModel.saveRecipient(it)},
                                        onDismiss = {recipientsViewModel.dismissRecipientDialog()},
                                        onDelete = {recipientsViewModel.deleteRecipient(it)}
                                    )
                                }
                            }

                            is Resource.Success -> TODO()
                            null -> TODO()
                        }
                    }
                    Column(modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Top) {
                        RecipientsList(recipients = recipients,
                            onCheckChangeListener = {isChecked, recipient ->
                                recipientsViewModel.onCheckedChange(isChecked, recipient)},
                            openDialogListener = {recipient ->
                                recipientsViewModel.showRecipientDialog(recipient)})

                        Form(viewmodel = recipientsViewModel)
                        
                        Button(content = { Text(text = "Send To Bot")} ,onClick = {
                            intentSendFileTelegram(FilesHelper.saveToFile(recipientsViewModel.getCurrSendData()))
                        })
                    }
                }
            }
        }
    }

//    @SuppressLint("UnrememberedMutableState")
//    @Preview
//    @Composable
//    fun RecipientsListPreview() {
//        Surface {
//            Column {
//                RecipientsList(recipients = listOf(
//                    UiRecipient(10, mutableIntStateOf(10), mutableStateOf("asdf")),
//                    UiRecipient(10, mutableIntStateOf(10), mutableStateOf("asdf"))),
//                    onCheckChangeListener = {a,b ->  Log.d("aaa", "pressed")}) }
//            }
//        }


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