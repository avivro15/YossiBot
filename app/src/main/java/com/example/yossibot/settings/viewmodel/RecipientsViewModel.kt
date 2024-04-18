package com.example.yossibot.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.yossibot.settings.model.Recipient
import com.example.yossibot.settings.model.RecipientsRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class RecipientsViewModel(private val repository: RecipientsRepo) : ViewModel() {

    val allRecipient: Flow<List<Recipient>> = repository.allRecipients

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(recipient: Recipient) = viewModelScope.launch {
        repository.insertRecipient(recipient)
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