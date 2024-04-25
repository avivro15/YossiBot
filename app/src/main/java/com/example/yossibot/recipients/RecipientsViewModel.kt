package com.example.yossibot.recipients

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.yossibot.data.SendData
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class RecipientsViewModel(private val repository: RecipientsRepo) : ViewModel() {

    private val eventChannel = Channel<Resource>()

    val eventsFlow = eventChannel.receiveAsFlow()

    var title by mutableStateOf("")
        private set

    var data by mutableStateOf("")
        private set

    private val _uiRecipientsList = mutableStateListOf<UiRecipient>()
    val uiRecipientsList: List<UiRecipient>
        get() = _uiRecipientsList

    init {
        viewModelScope.launch {
            repository.getRecipients().collect { recipients ->
                _uiRecipientsList.clear()
                _uiRecipientsList.addAll(recipients.map {
                    UiRecipient(it.id, mutableIntStateOf(it.recipientsId), mutableStateOf(it.name))
                })
            }

        }
    }

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    private fun insertRecipient(recipient: Recipient) = viewModelScope.launch {
        repository.insertRecipient(recipient)
    }

    fun updateTitle(updatedTitle: String) {
        title = updatedTitle
    }

    fun updateMsgData(updatedData: String) {
        data = updatedData
    }

    fun onCheckedChange(isChecked: Boolean, recipient: UiRecipient) {
        _uiRecipientsList.find { it.id == recipient.id }?.let { it.isChecked.value = isChecked }
    }

    fun showRecipientDialog(recipient: UiRecipient?) {
        viewModelScope.launch {
            eventChannel.send(Resource.RecipientDialogEvent(recipient = recipient ?: UiRecipient()))
        }
    }

    fun dismissRecipientDialog() {
        viewModelScope.launch {
            eventChannel.send(Resource.RecipientDialogEvent(isVisible = false))
        }
    }

    fun saveRecipient(recipient: UiRecipient) {
        insertRecipient(Recipient(recipient.id, recipient.recipientId.value, recipient.name.value))
        dismissRecipientDialog()
    }

    fun deleteRecipient(recipient: UiRecipient) {
        val recipientToDelete = _uiRecipientsList.find { it.id == recipient.id }
        if (recipientToDelete != null) {
            viewModelScope.launch {
                repository.deleteRecipient(Recipient(
                    recipientToDelete.id,
                    recipientToDelete.recipientId.value,
                    recipientToDelete.name.value
                ))
            }
        }

        dismissRecipientDialog()
    }

    fun getCurrSendData() : SendData {
        val sendRecipient = mutableListOf<Recipient>()

        _uiRecipientsList.forEach {
            uiRecipient ->
                if (uiRecipient.isChecked.value)
                    sendRecipient.add(Recipient(uiRecipient.id,
                                                    uiRecipient.recipientId.value,
                                                    uiRecipient.name.value))
        }

        return SendData(recipients = sendRecipient, title, data)
    }
}

class RecipientsViewModelFactory(private val repository: RecipientsRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecipientsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RecipientsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

data class UiRecipient(val id: Int = 0,
                       val recipientId: MutableState<Int> = mutableIntStateOf(0),
                       val name: MutableState<String> = mutableStateOf(""),
                       val isChecked: MutableState<Boolean> = mutableStateOf(false))

sealed class Resource {
    data class RecipientDialogEvent(
        val recipient: UiRecipient? = null,
        val isVisible: Boolean = true
    ) : Resource()
    data class Success(val data: String) : Resource()
}
