package com.example.notebook.adapters

import androidx.recyclerview.widget.DiffUtil
import com.example.notebook.modal.Note

class DiffUtilCallback :DiffUtil.ItemCallback<Note>(){
    override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
        return oldItem.id==newItem.id
    }

    override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
        return oldItem.id == newItem.id
    }


}