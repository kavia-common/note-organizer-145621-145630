package org.example.app.ui

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import org.example.app.data.Note
import org.example.app.data.NoteRepository

/**
 * PUBLIC_INTERFACE
 * EditNoteViewModel loads, edits, and saves a single note.
 */
class EditNoteViewModel(private val repository: NoteRepository) : ViewModel() {

    private val _noteId = MutableLiveData<Long?>(null)
    val noteId: LiveData<Long?> = _noteId

    private val _note = MutableLiveData<Note?>()
    val note: LiveData<Note?> = _note

    fun load(id: Long?) {
        _noteId.value = id
        if (id != null && id != 0L) {
            viewModelScope.launch {
                _note.postValue(repository.getById(id))
            }
        } else {
            _note.value = null
        }
    }

    fun save(title: String, content: String, category: String, onSaved: (Long) -> Unit) {
        viewModelScope.launch {
            val existing = _note.value
            val now = System.currentTimeMillis()
            val toSave = if (existing == null) {
                Note(title = title.trim(), content = content.trim(), category = category.trim(), createdAt = now, updatedAt = now)
            } else {
                existing.copy(
                    title = title.trim(),
                    content = content.trim(),
                    category = category.trim(),
                    updatedAt = now
                )
            }
            val id = repository.save(toSave)
            onSaved(id)
        }
    }
}

/**
 * PUBLIC_INTERFACE
 * Factory for EditNoteViewModel.
 */
class EditNoteViewModelFactory(private val repository: NoteRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditNoteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EditNoteViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
