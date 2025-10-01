package org.example.app.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import org.example.app.NotesApp
import org.example.app.R
import org.example.app.data.Note

/**
 PUBLIC_INTERFACE
 MainActivity shows a list of notes with a Soft Mono minimalist UI, provides a navigation drawer to filter by category and access settings, and a FAB to create a new note.
 */
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: View
    private lateinit var fab: FloatingActionButton
    private lateinit var adapter: NotesAdapter

    private val viewModel: MainViewModel by lazy {
        MainViewModelFactory((application as NotesApp).repository).create(MainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(org.example.app.R.style.Theme_SoftMono_Launch)
        super.onCreate(savedInstanceState)
        setContentView(org.example.app.R.layout.activity_main)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(org.example.app.R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(org.example.app.R.id.drawer_layout)
        navigationView = findViewById(org.example.app.R.id.nav_view)
        recyclerView = findViewById(org.example.app.R.id.notes_list)
        emptyView = findViewById(org.example.app.R.id.empty_state)
        fab = findViewById(org.example.app.R.id.fab_add)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            org.example.app.R.string.navigation_drawer_open, org.example.app.R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navigationView.setNavigationItemSelectedListener(this)

        adapter = NotesAdapter(
            onClick = { note ->
                val intent = Intent(this, EditNoteActivity::class.java)
                intent.putExtra(EditNoteActivity.EXTRA_NOTE_ID, note.id)
                startActivity(intent)
            },
            onLongPress = { note ->
                viewModel.deleteNote(note.id)
                true
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        viewModel.notes.observe(this, Observer { list ->
            adapter.submit(list ?: emptyList())
            emptyView.visibility = if (list.isNullOrEmpty()) View.VISIBLE else View.GONE
        })

        fab.setOnClickListener {
            startActivity(Intent(this, EditNoteActivity::class.java))
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_all -> viewModel.setCategory(null)
            R.id.nav_general -> viewModel.setCategory(getString(R.string.category_general))
            R.id.nav_personal -> viewModel.setCategory(getString(R.string.category_personal))
            R.id.nav_work -> viewModel.setCategory(getString(R.string.category_work))
            R.id.nav_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}

private class NotesAdapter(
    val onClick: (Note) -> Unit,
    val onLongPress: (Note) -> Boolean
) : RecyclerView.Adapter<NotesAdapter.VH>() {

    private val items = mutableListOf<Note>()

    fun submit(data: List<Note>) {
        items.clear()
        items.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(org.example.app.R.layout.item_note, parent, false)
        return VH(view, onClick, onLongPress)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position])
    }

    class VH(itemView: View, val onClick: (Note) -> Unit, val onLongPress: (Note) -> Boolean) :
        RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.note_title)
        private val content: TextView = itemView.findViewById(R.id.note_content)
        private val meta: TextView = itemView.findViewById(R.id.note_meta)

        fun bind(note: Note) {
            title.text = note.title.ifBlank { "(Untitled)" }
            content.text = note.content
            val updated = android.text.format.DateFormat.format("MMM d, h:mm a", note.updatedAt)
            meta.text = "${note.category} • $updated"
            itemView.setOnClickListener { onClick(note) }
            itemView.setOnLongClickListener { onLongPress(note) }
        }
    }
}
