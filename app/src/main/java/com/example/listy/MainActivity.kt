package com.example.listy

import android.app.Activity
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
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.core.view.WindowCompat
import kotlinx.coroutines.delay
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.listy.data.isCompleted
import com.example.listy.data.remainingAmount
import com.example.listy.ui.about.AboutScreen
import com.example.listy.ui.lists.ListDetailScreen
import com.example.listy.ui.lists.ListSummary
import com.example.listy.ui.lists.ListsScreen
import com.example.listy.ui.lists.ListsViewModel
import com.example.listy.ui.theme.ListyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Varsayılan koyu; açık görünümde ikon rengi ListyApp içinde ayarlanır
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
        )
        setContent {
            val viewModel: ListsViewModel = viewModel()
            val lightMode by viewModel.lightMode.collectAsState()
            ListyTheme(light = lightMode) {
                // Soğuk açılışta 2 sn açılış görseli; ekran dönünce tekrar gösterilmez
                var showSplash by rememberSaveable { mutableStateOf(true) }
                LaunchedEffect(Unit) {
                    delay(2000)
                    showSplash = false
                }
                if (showSplash) SplashScreen() else ListyApp(viewModel, lightMode)
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
private fun ListyApp(viewModel: ListsViewModel, lightMode: Boolean) {
    val lists by viewModel.lists.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    val homeColorIndex by viewModel.homeColorIndex.collectAsState()
    var openListId by rememberSaveable { mutableStateOf<Long?>(null) }
    var showAbout by rememberSaveable { mutableStateOf(false) }

    val openList = lists.find { it.id == openListId }

    // Beyaz zeminde durum çubuğu ikonları koyu, renkli/koyu zeminde açık
    val lightBars = lightMode && (showAbout || (openList == null && homeColorIndex == null))
    val view = LocalView.current
    SideEffect {
        val window = (view.context as Activity).window
        WindowCompat.getInsetsController(window, view).apply {
            isAppearanceLightStatusBars = lightBars
            isAppearanceLightNavigationBars = lightBars
        }
    }

    if (showAbout) {
        BackHandler { showAbout = false }
        AboutScreen(onBack = { showAbout = false })
    } else if (openList == null) {
        ListsScreen(
            colorIndex = homeColorIndex,
            onChangeColor = viewModel::setHomeColor,
            lightMode = lightMode,
            onChangeLightMode = viewModel::setLightMode,
            onOpenAbout = { showAbout = true },
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
            onReorderLists = viewModel::reorderLists,
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
            onAddTask = { title, amount, daily, date ->
                viewModel.addTask(openList.id, title, amount, daily, date)
            },
            onToggleDone = viewModel::toggleDone,
            onResetDone = { viewModel.resetDone(openList.id) },
            onEditTask = viewModel::editTask,
            onDeleteTask = viewModel::deleteTask,
            onMoveTask = viewModel::moveTask,
        )
    }
}
