package com.lunat.todolist.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * from task WHERE concluido = 0 ORDER BY data ASC, hora ASC")
    fun getTasks(): Flow<List<Task>>

    @Query("SELECT * from task WHERE concluido = 1 ORDER BY data ASC, hora ASC")
    fun getCompletedTasks(): Flow<List<Task>>

    @Query("SELECT * from task WHERE id = :id")
    fun getTask(id: Int): Flow<Task>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)
}
