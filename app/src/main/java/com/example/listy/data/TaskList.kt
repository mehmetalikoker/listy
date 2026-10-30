package com.example.listy.data

data class TaskList(
    val id: Long,
    val name: String,
    val createdAt: Long,
    val colorIndex: Int = 0,
)

data class Task(
    val id: Long,
    val listId: Long,
    val title: String,
    val createdAt: Long,
    val done: Boolean = false,
    val important: Boolean = false,
)
