package com.example.listy.ui.lists

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.listy.data.ListRepository
import com.example.listy.data.ListType
import com.example.listy.data.Task
import com.example.listy.data.TaskList
import java.time.LocalDate
import kotlinx.coroutines.flow.StateFlow

class ListsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ListRepository(application)

    val lists: StateFlow<List<TaskList>> = repository.lists
    val tasks: StateFlow<List<Task>> = repository.tasks
    val homeColorIndex: StateFlow<Int?> = repository.homeColorIndex

    val lightMode: StateFlow<Boolean> = repository.lightMode

    fun setHomeColor(colorIndex: Int) = repository.setHomeColor(colorIndex)

    /** Koyu/açık görünüm seçilince ana ekran rengi kalkar, düz arka plan görünür. */
    fun setLightMode(light: Boolean) {
        repository.setLightMode(light)
        repository.setHomeColor(null)
    }

    /** Liste oluşturulursa id'sini, ad boşsa null döner. */
    fun createList(name: String, type: ListType): Long? {
        val trimmed = name.trim()
        return if (trimmed.isNotEmpty()) repository.addList(trimmed, type) else null
    }

    fun renameList(listId: Long, name: String) {
        val trimmed = name.trim()
        if (trimmed.isNotEmpty()) repository.renameList(listId, trimmed)
    }

    fun setListColor(listId: Long, colorIndex: Int) = repository.setListColor(listId, colorIndex)

    fun deleteList(listId: Long) = repository.deleteList(listId)

    fun reorderLists(ids: List<Long>) = repository.reorderLists(ids)

    fun moveTask(taskId: Long, openOrder: List<Long>) = repository.moveTask(taskId, openOrder)

    /** [amount] kuruş cinsinden; sadece ödeme listelerinde verilir. */
    fun addTask(listId: Long, title: String, amount: Long?, daily: Boolean, date: LocalDate?) {
        val trimmed = title.trim()
        if (trimmed.isNotEmpty()) repository.addTask(listId, trimmed, amount, daily, date)
    }

    fun toggleDone(taskId: Long) = repository.toggleDone(taskId)

    fun resetDone(listId: Long) = repository.resetDone(listId)

    fun editTask(taskId: Long, title: String, amount: Long?, daily: Boolean, date: LocalDate?) {
        val trimmed = title.trim()
        if (trimmed.isNotEmpty()) repository.editTask(taskId, trimmed, amount, daily, date)
    }

    fun deleteTask(taskId: Long) = repository.deleteTask(taskId)
}
