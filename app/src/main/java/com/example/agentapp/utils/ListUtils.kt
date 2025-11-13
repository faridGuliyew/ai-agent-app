package com.example.agentapp.utils

import android.R.attr.data
import com.example.agentapp.presentation.Note

fun List<Note>.findByTitle(title: String?) : Note? {
    return find { it.title.uppercase() == title.toString().uppercase() }
}