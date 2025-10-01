package org.example.app

import android.app.Application
import android.util.Log
import org.example.app.data.AppDatabase
import org.example.app.data.InMemoryNoteDao
import org.example.app.data.NoteRepository

/**
 * PUBLIC_INTERFACE
 * NotesApp initializes singletons for the data layer. It prefers a Room-backed
 * repository and gracefully falls back to an in-memory repository if Room cannot
 * be initialized (e.g., missing annotation processor in this environment).
 */
class NotesApp : Application() {

    // Database is optional; app works with in-memory fallback if Room init fails.
    var database: AppDatabase? = null
        private set

    lateinit var repository: NoteRepository
        private set

    override fun onCreate() {
        super.onCreate()

        repository = try {
            database = AppDatabase.getInstance(this)
            NoteRepository(database!!.noteDao())
        } catch (t: Throwable) {
            Log.e(
                "NotesApp",
                "Failed to initialize Room database. Falling back to in-memory storage.",
                t
            )
            NoteRepository(InMemoryNoteDao())
        }
    }
}
