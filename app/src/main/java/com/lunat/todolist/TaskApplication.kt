package com.lunat.todolist

import android.app.Application
import com.lunat.todolist.data.TaskRoomDatabase

class TaskApplication : Application() {
    val database: TaskRoomDatabase by lazy { TaskRoomDatabase.getDatabase(this) }
}
