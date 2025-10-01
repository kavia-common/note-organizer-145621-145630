package org.example.app.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.switchMap
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
import org.example.app.data.Note
import org.example.app.data.NoteRepository

/**
 * PUBLIC_INTERFACE
 * MainViewModel exposes list of notes and category filtering.
 */
class MainViewModel(private val repository: NoteRepository) : ViewModel() {

    private val _category = MutableLiveData<String?>(null)
    val category: LiveData<String?> = _category

    val notes: LiveData<List<Note>> = _category.switchMap { cat: String? ->
        if (cat.isNullOrBlank()) repository.getAll() else repository.getByCategory(cat)
    }

    fun setCategory(category: String?) {
        _category.value = category
    }

    fun deleteNote(id: Long) {
        viewModelScope.launch {
            repository.deleteById(id)
        }
    }
}

/**
 * PUBLIC_INTERFACE
 * Factory for MainViewModel.
 */
class MainViewModelFactory(private val repository: NoteRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
