package com.example.listy.data

enum class ListType { TODO, PAYMENT }

data class TaskList(
    val id: Long,
    val name: String,
    val createdAt: Long,
    val colorIndex: Int = 0,
    val type: ListType = ListType.TODO,
)

data class Task(
    val id: Long,
    val listId: Long,
    val title: String,
    val createdAt: Long,
    val done: Boolean = false,
    val important: Boolean = false,
    /** Ödeme listelerinde tutar, kuruş cinsinden. Yapılacaklar listesinde null. */
    val amount: Long? = null,
)
