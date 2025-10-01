package org.example.app.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData

/**
 PUBLIC_INTERFACE
 Internal, in-memory implementation of NoteDao used as a fallback when the Room
 database cannot be initialized at runtime. This storage is ephemeral and exists
 only for the lifetime of the process.
 */
internal class InMemoryNoteDao : NoteDao {

    private val lock = Any()
    private val items = mutableListOf<Note>()
    private val allLiveData = MutableLiveData<List<Note>>(emptyList())
    private var nextId = 1L

    override fun getAll(): LiveData<List<Note>> = allLiveData

    override fun getByCategory(category: String): LiveData<List<Note>> {
        val out = MediatorLiveData<List<Note>>()
        out.addSource(allLiveData) { list ->
            out.value = list
                .filter { it.category == category }
                .sortedByDescending { it.updatedAt }
        }
        return out
    }

    override suspend fun getById(id: Long): Note? = synchronized(lock) {
        items.find { it.id == id }
    }

    override suspend fun insert(note: Note): Long = synchronized(lock) {
        val id = if (note.id == 0L) nextId++ else note.id
        val newNote = if (note.id == 0L) note.copy(id = id) else note
        val idx = items.indexOfFirst { it.id == id }
        if (idx >= 0) {
            items[idx] = newNote
        } else {
            items.add(newNote)
        }
        publish()
        id
    }

    override suspend fun update(note: Note) {
        synchronized(lock) {
            val idx = items.indexOfFirst { it.id == note.id }
            if (idx >= 0) {
                items[idx] = note
            } else {
                items.add(note)
            }
            publish()
        }
    }

    override suspend fun delete(note: Note) {
        synchronized(lock) {
            items.removeAll { it.id == note.id }
            publish()
        }
    }

    override suspend fun deleteById(id: Long) {
        synchronized(lock) {
            items.removeAll { it.id == id }
            publish()
        }
    }

    private fun publish() {
        allLiveData.postValue(items.sortedByDescending { it.updatedAt })
    }
}
