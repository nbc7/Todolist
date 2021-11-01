package com.lunat.todolist.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "titulo")
    val title: String,
    @ColumnInfo(name = "detalhes")
    val detalhes: String,
    @ColumnInfo(name = "data")
    val data: String,
    @ColumnInfo(name = "hora")
    val hora: String,
    @ColumnInfo(name = "concluido")
    val completed: Boolean
)