package com.example.notebook.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.example.notebook.R
import com.example.notebook.database.NoteDatabase
import com.example.notebook.databinding.ActivityMainBinding
import com.example.notebook.repository.NoteRepository
import com.example.notebook.viewModal.NoteActivityViewModal
import com.example.notebook.viewModal.NoteActivityViewModalFactory

class MainActivity : AppCompatActivity() {

    private lateinit var noteActivityViewModal: NoteActivityViewModal
    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityMainBinding.inflate(layoutInflater)


        try {
            setContentView(binding.root)
            val noteRepository = NoteRepository(NoteDatabase(this))
            val noteActivityViewModalFactory = NoteActivityViewModalFactory(noteRepository)
            noteActivityViewModal = ViewModelProvider(this , noteActivityViewModalFactory)[NoteActivityViewModal::class.java]
        }catch (e : Exception){
            Log.d("TAG" , "Error")
        }

    }
}