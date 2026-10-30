package com.example.listy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.listy.ui.lists.ListDetailScreen
import com.example.listy.ui.lists.ListSummary
import com.example.listy.ui.lists.ListsScreen
import com.example.listy.ui.lists.ListsViewModel
import com.example.listy.ui.theme.ListyTheme
import com.example.listy.ui.theme.listColor

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Uygulama hep koyu; durum çubuğu ikonları açık renk olsun
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
        )
        setContent {
            ListyTheme {
                ListyApp()
            }
        }
    }
}

@Composable
private fun ListyApp(viewModel: ListsViewModel = viewModel()) {
    val lists by viewModel.lists.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    var openListId by rememberSaveable { mutableStateOf<Long?>(null) }

    val openList = lists.find { it.id == openListId }

    if (openList == null) {
        ListsScreen(
            lists = lists.map { list ->
                ListSummary(
                    id = list.id,
                    name = list.name,
                    colorIndex = list.colorIndex,
                    openTaskCount = tasks.count { it.listId == list.id && !it.done },
                )
            },
            onOpenList = { openListId = it },
            // To Do gibi: yeni liste oluşunca direkt içine gir
            onCreateList = { name -> viewModel.createList(name)?.let { openListId = it } },
        )
    } else {
        BackHandler { openListId = null }
        ListDetailScreen(
            title = openList.name,
            color = listColor(openList.colorIndex),
            tasks = tasks.filter { it.listId == openList.id },
            onBack = { openListId = null },
            onAddTask = { viewModel.addTask(openList.id, it) },
            onToggleDone = viewModel::toggleDone,
            onToggleImportant = viewModel::toggleImportant,
        )
    }
}
