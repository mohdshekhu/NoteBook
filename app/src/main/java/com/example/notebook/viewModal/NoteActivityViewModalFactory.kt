package com.example.notebook.viewModal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.notebook.repository.NoteRepository

class NoteActivityViewModalFactory(private val repository: NoteRepository)
    :ViewModelProvider.NewInstanceFactory()
{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return NoteActivityViewModal(repository) as T
    }
}