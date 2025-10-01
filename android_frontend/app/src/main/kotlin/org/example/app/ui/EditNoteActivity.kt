package org.example.app.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import org.example.app.NotesApp
import org.example.app.R

/**
 PUBLIC_INTERFACE
 EditNoteActivity lets the user create or update a note. It handles saving and simple validation.
 */
class EditNoteActivity : AppCompatActivity() {

    private val viewModel: EditNoteViewModel by viewModels {
        EditNoteViewModelFactory((application as NotesApp).repository)
    }

    private lateinit var titleEt: EditText
    private lateinit var contentEt: EditText
    private lateinit var categorySpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_SoftMono_Launch)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_note)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        titleEt = findViewById(R.id.input_title)
        contentEt = findViewById(R.id.input_content)
        categorySpinner = findViewById(R.id.input_category)

        val categories = resources.getStringArray(R.array.categories)
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)
        categorySpinner.adapter = spinnerAdapter

        val noteId = intent.getLongExtra(EXTRA_NOTE_ID, 0L).takeIf { it != 0L }
        viewModel.note.observe(this) { note ->
            if (note != null) {
                setTitle(R.string.title_edit_note)
                titleEt.setText(note.title)
                contentEt.setText(note.content)
                val idx = categories.indexOf(note.category).takeIf { it >= 0 } ?: 0
                categorySpinner.setSelection(idx)
            } else {
                setTitle(R.string.title_new_note)
            }
        }
        viewModel.load(noteId)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_edit_note, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> { finish(); true }
            R.id.action_save -> { saveNote(); true }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun saveNote() {
        val title = titleEt.text.toString()
        val content = contentEt.text.toString()
        val category = categorySpinner.selectedItem?.toString() ?: getString(R.string.category_general)
        viewModel.save(title, content, category) {
            finish()
        }
    }

    companion object {
        const val EXTRA_NOTE_ID = "extra_note_id"
    }
}
