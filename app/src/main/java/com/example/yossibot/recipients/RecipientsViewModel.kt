package com.example.yossibot.recipients

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.yossibot.data.SendData
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class RecipientsViewModel(private val repository: RecipientsRepo) : ViewModel() {


    private val eventChannel = Channel<Resource>()

    private val title = mutableStateOf("")

    private val data = mutableStateOf("")

    val uiState = UiState(eventChannel = eventChannel.receiveAsFlow(), title, data)

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
        title.value = updatedTitle
    }

    fun updateMsgData(updatedData: String) {
        data.value = updatedData
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
                    sendRecipient.add(Recipient(0,
                                                    uiRecipient.recipientId.value,
                                                    ""))
        }

        return SendData(
            recipients = sendRecipient,
            title.value,
            data.value
        )
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

data class UiState(
    val eventChannel: Flow<Resource>,
    val title: State<String>,
    val data: State<String>
)

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
