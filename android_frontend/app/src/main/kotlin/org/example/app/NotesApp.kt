package org.example.app

import android.app.Application
import org.example.app.data.AppDatabase
import org.example.app.data.NoteRepository

/**
 * PUBLIC_INTERFACE
 * NotesApp initializes singletons for Room database and repository.
 */
class NotesApp : Application() {

    lateinit var database: AppDatabase
        private set

    lateinit var repository: NoteRepository
        private set

    override fun onCreate() {
        super.onCreate()
        database = AppDatabase.getInstance(this)
        repository = NoteRepository(database.noteDao())
    }
}
