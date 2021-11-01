package com.lunat.todolist

import androidx.lifecycle.*
import com.lunat.todolist.data.Task
import com.lunat.todolist.data.TaskDao
import kotlinx.coroutines.launch

class TaskViewModel(private val taskDao: TaskDao) : ViewModel() {

    val allTasks: LiveData<List<Task>> = taskDao.getTasks().asLiveData()
    val completedTasks: LiveData<List<Task>> = taskDao.getCompletedTasks().asLiveData()

    fun retrieveTask(id: Int): LiveData<Task> {
        return taskDao.getTask(id).asLiveData()
    }

    private fun updateTask(task: Task) {
        viewModelScope.launch {
            taskDao.update(task)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskDao.delete(task)
        }
    }

    private fun getUpdatedTaskEntry(
        taskId: Int,
        taskTitle: String,
        taskDetalhes: String,
        taskData: String,
        taskHora: String,
        taskCompleted: Boolean
    ): Task {
        return Task(
            id = taskId,
            title = taskTitle,
            detalhes = taskDetalhes,
            data = taskData,
            hora = taskHora,
            completed = taskCompleted
        )
    }

    fun updateTask(
        taskId: Int,
        taskTitle: String,
        taskDetalhes: String,
        taskData: String,
        taskHora: String,
        taskCompleted: Boolean
    ) {
        val updatedTask = getUpdatedTaskEntry(taskId, taskTitle, taskDetalhes, taskData, taskHora, taskCompleted)
        updateTask(updatedTask)
    }

    fun addNewTask(taskTitle: String, taskDetalhes: String, taskData: String, taskHora: String, taskCompleted: Boolean) {
        val newTask = getNewTaskEntry(taskTitle, taskDetalhes, taskData, taskHora, taskCompleted)
        insertTask(newTask)
    }

    private fun insertTask(task: Task) {
        viewModelScope.launch {
            taskDao.insert(task)
        }
    }

    fun isEntryValid(taskTitle: String, /*taskDetalhes: String,*/ taskData: String, taskHora: String): Boolean {
        if (taskTitle.isBlank() || taskData.isBlank() || taskHora.isBlank()) {
            return false
        }
        return true
    }

    private fun getNewTaskEntry(taskTitle: String, taskDetalhes: String, taskData: String, taskHora: String, taskCompleted: Boolean): Task {
        return Task(
            title = taskTitle,
            detalhes = taskDetalhes,
            data = taskData,
            hora = taskHora,
            completed = taskCompleted
        )
    }
}

class TaskViewModelFactory(private val taskDao: TaskDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(taskDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
