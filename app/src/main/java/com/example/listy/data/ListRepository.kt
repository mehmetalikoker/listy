package com.example.listy.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import org.json.JSONArray
import org.json.JSONObject

/**
 * Listeleri ve görevleri SharedPreferences içinde JSON olarak saklar.
 * İleride Room'a geçmek istenirse sadece bu sınıf değişir.
 */
class ListRepository(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences("listy_data", Context.MODE_PRIVATE)

    private val _lists = MutableStateFlow(loadLists())
    val lists: StateFlow<List<TaskList>> = _lists.asStateFlow()

    private val _tasks = MutableStateFlow(loadTasks())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    // Ana ekran teması; null = varsayılan koyu arka plan
    private val _homeColorIndex = MutableStateFlow(
        if (prefs.contains(KEY_HOME_COLOR)) prefs.getInt(KEY_HOME_COLOR, 0) else null
    )
    val homeColorIndex: StateFlow<Int?> = _homeColorIndex.asStateFlow()

    fun setHomeColor(colorIndex: Int) {
        _homeColorIndex.value = colorIndex
        prefs.edit().putInt(KEY_HOME_COLOR, colorIndex).apply()
    }

    /** Yeni listeyi oluşturur ve id'sini döner. */
    fun addList(name: String, type: ListType): Long {
        val now = System.currentTimeMillis()
        val list = TaskList(id = now, name = name, createdAt = now, colorIndex = _lists.value.size, type = type)
        _lists.value += list
        saveLists()
        return list.id
    }

    fun renameList(listId: Long, name: String) {
        _lists.value = _lists.value.map { if (it.id == listId) it.copy(name = name) else it }
        saveLists()
    }

    fun setListColor(listId: Long, colorIndex: Int) {
        _lists.value = _lists.value.map { if (it.id == listId) it.copy(colorIndex = colorIndex) else it }
        saveLists()
    }

    /** Listeyi içindeki görevlerle birlikte siler. */
    fun deleteList(listId: Long) {
        _lists.value = _lists.value.filterNot { it.id == listId }
        _tasks.value = _tasks.value.filterNot { it.listId == listId }
        saveLists()
        saveTasks()
    }

    fun addTask(listId: Long, title: String, amount: Long?, daily: Boolean, date: LocalDate?) {
        val now = System.currentTimeMillis()
        _tasks.value += Task(
            id = now, listId = listId, title = title, createdAt = now,
            amount = amount, daily = daily, date = date,
        )
        saveTasks()
    }

    fun toggleDone(taskId: Long) = updateTask(taskId) { it.copy(done = !it.done) }

    fun editTask(taskId: Long, title: String, amount: Long?, daily: Boolean, date: LocalDate?) =
        updateTask(taskId) { it.copy(title = title, amount = amount, daily = daily, date = date) }

    fun deleteTask(taskId: Long) {
        _tasks.value = _tasks.value.filterNot { it.id == taskId }
        saveTasks()
    }

    private fun updateTask(taskId: Long, change: (Task) -> Task) {
        _tasks.value = _tasks.value.map { if (it.id == taskId) change(it) else it }
        saveTasks()
    }

    private fun loadLists(): List<TaskList> = readArray(KEY_LISTS) { obj ->
        TaskList(
            id = obj.getLong("id"),
            name = obj.getString("name"),
            createdAt = obj.getLong("createdAt"),
            colorIndex = obj.optInt("colorIndex", 0),
            type = ListType.valueOf(obj.optString("type", ListType.TODO.name)),
        )
    }

    private fun loadTasks(): List<Task> = readArray(KEY_TASKS) { obj ->
        Task(
            id = obj.getLong("id"),
            listId = obj.getLong("listId"),
            title = obj.getString("title"),
            createdAt = obj.getLong("createdAt"),
            done = obj.optBoolean("done"),
            important = obj.optBoolean("important"),
            amount = if (obj.has("amount")) obj.getLong("amount") else null,
            daily = obj.optBoolean("daily"),
            date = if (obj.has("date")) LocalDate.parse(obj.getString("date")) else null,
        )
    }

    private fun saveLists() = writeArray(KEY_LISTS, _lists.value) {
        JSONObject()
            .put("id", it.id)
            .put("name", it.name)
            .put("createdAt", it.createdAt)
            .put("colorIndex", it.colorIndex)
            .put("type", it.type.name)
    }

    private fun saveTasks() = writeArray(KEY_TASKS, _tasks.value) {
        JSONObject()
            .put("id", it.id)
            .put("listId", it.listId)
            .put("title", it.title)
            .put("createdAt", it.createdAt)
            .put("done", it.done)
            .put("important", it.important)
            .put("daily", it.daily)
            .apply { it.amount?.let { amount -> put("amount", amount) } }
            .apply { it.date?.let { date -> put("date", date.toString()) } }
    }

    private fun <T> readArray(key: String, parse: (JSONObject) -> T): List<T> {
        val raw = prefs.getString(key, null) ?: return emptyList()
        val array = JSONArray(raw)
        return List(array.length()) { i -> parse(array.getJSONObject(i)) }
    }

    private fun <T> writeArray(key: String, items: List<T>, toJson: (T) -> JSONObject) {
        val array = JSONArray()
        items.forEach { array.put(toJson(it)) }
        prefs.edit().putString(key, array.toString()).apply()
    }

    private companion object {
        const val KEY_LISTS = "lists"
        const val KEY_TASKS = "tasks"
        const val KEY_HOME_COLOR = "homeColorIndex"
    }
}
