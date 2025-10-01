package org.example.app.data

import androidx.lifecycle.LiveData

/**
 * PUBLIC_INTERFACE
 * Repository mediating between DAO and ViewModels.
 */
class NoteRepository(private val dao: NoteDao) {

    /**
     * PUBLIC_INTERFACE
     * Returns all notes, sorted by updated time.
     */
    fun getAll(): LiveData<List<Note>> = dao.getAll()

    /**
     * PUBLIC_INTERFACE
     * Returns notes filtered by category.
     */
    fun getByCategory(category: String): LiveData<List<Note>> = dao.getByCategory(category)

    /**
     * PUBLIC_INTERFACE
     * Returns a single note by id or null.
     */
    suspend fun getById(id: Long): Note? = dao.getById(id)

    /**
     * PUBLIC_INTERFACE
     * Saves a note (insert or update). Returns id for inserts.
     */
    suspend fun save(note: Note): Long {
        return if (note.id == 0L) {
            dao.insert(note)
        } else {
            dao.update(note)
            note.id
        }
    }

    /**
     * PUBLIC_INTERFACE
     * Deletes the note by id.
     */
    suspend fun deleteById(id: Long) = dao.deleteById(id)
}
