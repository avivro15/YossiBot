package com.example.yossibot

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.yossibot.databinding.FragmentSendBinding
import com.example.yossibot.settings.viewmodel.RecipientsViewModel
import java.io.File

class SendFragment : Fragment() {

    // region Members

    private lateinit var binding: FragmentSendBinding
    private lateinit var viewModel: RecipientsViewModel

    // endregion

    // region Lifecycle

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSendBinding.inflate(layoutInflater)

//        binding.sendBtn.setOnClickListener {
//            Log.d("asdf","btnclick")
//            val sendData = SendData(
//                mutableListOf(binding.recipientsEt.text.toString()),
//                binding.titleEt.text.toString(),
//                binding.dataEt.text.toString())
//
//            intentSendFileTelegram(FilesHelper.saveToFile(sendData))
//
//            resetViews()
//        }

        return binding.root
    }

    // endregion

    // region Private methods

    /**
     * Get External uti for a file
     * @param file - the file
     */
    private fun getUri(file: File) : Uri {
        return FileProvider.getUriForFile(requireContext(), "com.example.yossibot.provider", file)
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

    /**
     * Empty all views
     */
    private fun resetViews() {
        binding.titleEt.text.clear()
        binding.dataEt.text.clear()
        binding.recipientsEt.text.clear()
    }

    // endregion
}