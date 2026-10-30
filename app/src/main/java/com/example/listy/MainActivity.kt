package com.example.listy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import kotlinx.coroutines.delay
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.listy.data.isCompleted
import com.example.listy.data.remainingAmount
import com.example.listy.ui.lists.ListDetailScreen
import com.example.listy.ui.lists.ListSummary
import com.example.listy.ui.lists.ListsScreen
import com.example.listy.ui.lists.ListsViewModel
import com.example.listy.ui.theme.ListyTheme

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
                // Soğuk açılışta 2 sn açılış görseli; ekran dönünce tekrar gösterilmez
                var showSplash by rememberSaveable { mutableStateOf(true) }
                LaunchedEffect(Unit) {
                    delay(2000)
                    showSplash = false
                }
                if (showSplash) SplashScreen() else ListyApp()
            }
        }
    }
}

@Composable
private fun SplashScreen() {
    Image(
        painter = painterResource(R.drawable.splash),
        contentDescription = "Listy",
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize(),
    )
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
                    type = list.type,
                    openTaskCount = tasks.count { it.listId == list.id && !it.isCompleted() },
                    remainingAmount = tasks
                        .filter { it.listId == list.id }
                        .sumOf { it.remainingAmount() },
                )
            },
            onOpenList = { openListId = it },
            onRenameList = viewModel::renameList,
            onDeleteList = viewModel::deleteList,
            // To Do gibi: yeni liste oluşunca direkt içine gir
            onCreateList = { name, type -> viewModel.createList(name, type)?.let { openListId = it } },
        )
    } else {
        BackHandler { openListId = null }
        ListDetailScreen(
            title = openList.name,
            colorIndex = openList.colorIndex,
            type = openList.type,
            tasks = tasks.filter { it.listId == openList.id },
            onBack = { openListId = null },
            onChangeColor = { viewModel.setListColor(openList.id, it) },
            onAddTask = { title, amount, daily -> viewModel.addTask(openList.id, title, amount, daily) },
            onToggleDone = viewModel::toggleDone,
            onEditTask = viewModel::editTask,
            onDeleteTask = viewModel::deleteTask,
        )
    }
}
