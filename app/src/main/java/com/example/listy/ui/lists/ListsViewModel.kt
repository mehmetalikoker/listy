package com.example.listy.ui.lists

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.listy.data.ListRepository
import com.example.listy.data.ListType
import com.example.listy.data.Task
import com.example.listy.data.TaskList
import kotlinx.coroutines.flow.StateFlow

class ListsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ListRepository(application)

    val lists: StateFlow<List<TaskList>> = repository.lists
    val tasks: StateFlow<List<Task>> = repository.tasks

    /** Liste oluşturulursa id'sini, ad boşsa null döner. */
    fun createList(name: String, type: ListType): Long? {
        val trimmed = name.trim()
        return if (trimmed.isNotEmpty()) repository.addList(trimmed, type) else null
    }

    /** [amount] kuruş cinsinden; sadece ödeme listelerinde verilir. */
    fun addTask(listId: Long, title: String, amount: Long?, daily: Boolean) {
        val trimmed = title.trim()
        if (trimmed.isNotEmpty()) repository.addTask(listId, trimmed, amount, daily)
    }

    fun toggleDone(taskId: Long) = repository.toggleDone(taskId)

    fun toggleImportant(taskId: Long) = repository.toggleImportant(taskId)
}
