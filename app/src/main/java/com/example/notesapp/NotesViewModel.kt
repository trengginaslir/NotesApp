package com.example.notesapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotesViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getInstance(application).noteDao()

    val notes: StateFlow<List<Note>> = dao.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun addNote(title: String, note: String) {
        val newTitle = title.trim()
        if (newTitle.isEmpty()) return
        viewModelScope.launch { dao.insert(Note(title = newTitle, note = note.trim())) }
    }

    fun updateNote(old: Note, newTitle: String, newNote: String) {
        val title = newTitle.trim()
        if (title.isEmpty()) return
        viewModelScope.launch { dao.update(old.copy(title = title, note = newNote.trim())) }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch { dao.delete(note) }
    }
}
