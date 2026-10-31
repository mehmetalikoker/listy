package com.example.listy.ui.lists

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.listy.R
import com.example.listy.data.DailyPeriod
import com.example.listy.data.DateInput
import com.example.listy.data.ListType
import com.example.listy.data.Money
import com.example.listy.data.Task
import com.example.listy.data.dailyShare
import com.example.listy.data.isCompleted
import com.example.listy.data.paidAmount
import com.example.listy.data.remainingAmount
import java.time.LocalDate
import com.example.listy.ui.theme.CardSurface
import com.example.listy.ui.theme.DeleteRed
import com.example.listy.ui.theme.EditYellow
import com.example.listy.ui.theme.ListThemes
import com.example.listy.ui.theme.ListyTheme
import com.example.listy.ui.theme.TextPrimary
import com.example.listy.ui.theme.TextSecondary
import com.example.listy.ui.theme.listColor
import com.example.listy.ui.theme.listFabColor

@Composable
fun ListDetailScreen(
    title: String,
    colorIndex: Int,
    type: ListType,
    tasks: List<Task>,
    onBack: () -> Unit,
    onChangeColor: (Int) -> Unit,
    onAddTask: (title: String, amount: Long?, daily: Boolean, date: LocalDate?) -> Unit,
    onToggleDone: (Long) -> Unit,
    onEditTask: (id: Long, title: String, amount: Long?, daily: Boolean, date: LocalDate?) -> Unit,
    onDeleteTask: (Long) -> Unit,
) {
    var adding by rememberSaveable { mutableStateOf(false) }
    var showCompleted by rememberSaveable { mutableStateOf(true) }
    var editingId by rememberSaveable { mutableStateOf<Long?>(null) }
    var deletingId by rememberSaveable { mutableStateOf<Long?>(null) }
    var pickingTheme by rememberSaveable { mutableStateOf(false) }
    val color = listColor(colorIndex)

    BackHandler(enabled = adding) { adding = false }

    val today = LocalDate.now()
    val isPayment = type == ListType.PAYMENT
    val isReminder = type == ListType.REMINDER
    // Yeni eklenen en üstte; hatırlatıcıda tarihi en yakın olan en üstte
    val openTasks = tasks.filter { !it.isCompleted(today) }.sortedByDescending { it.createdAt }
        .let { if (isReminder) it.sortedBy { task -> task.date } else it }
    val doneTasks = tasks.filter { it.isCompleted(today) }.sortedByDescending { it.createdAt }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
        ) {
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp)) {
                IconButton(onClick = onBack) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_back),
                        contentDescription = "Geri",
                        tint = Color.White,
                    )
                }
                Spacer(Modifier.weight(1f))
                IconButton(onClick = { pickingTheme = true }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_palette),
                        contentDescription = "Tema",
                        tint = Color.White,
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f, fill = false),
                )
                if (isPayment) {
                    // Ay sonuna kalan gün; ayın son günündeysek sonraki ayın sonuna
                    Text(
                        text = "Ay sonuna ${DailyPeriod.of(today).daysLeft} gün",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.25f))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                    )
                }
            }
            if (isPayment) {
                PaymentSummary(
                    remaining = tasks.sumOf { it.remainingAmount(today) },
                    paid = tasks.sumOf { it.paidAmount(today) },
                )
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                items(openTasks, key = { it.id }) { task ->
                    TaskCard(
                        task = task,
                        today = today,
                        onToggleDone = onToggleDone,
                        onEdit = { editingId = task.id },
                        onDelete = { deletingId = task.id },
                    )
                }
                if (doneTasks.isNotEmpty()) {
                    item(key = "completed-header") {
                        CompletedHeader(
                            count = doneTasks.size,
                            expanded = showCompleted,
                            onClick = { showCompleted = !showCompleted },
                        )
                    }
                    if (showCompleted) {
                        items(doneTasks, key = { it.id }) { task ->
                            TaskCard(
                                task = task,
                                today = today,
                                onToggleDone = onToggleDone,
                                onEdit = { editingId = task.id },
                                onDelete = { deletingId = task.id },
                            )
                        }
                    }
                }
            }
        }

        if (adding) {
            AddTaskBar(
                withAmount = isPayment,
                withDate = isReminder,
                onAdd = onAddTask,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .imePadding(),
            )
        } else {
            AddFab(
                onClick = { adding = true },
                containerColor = listFabColor(colorIndex),
                contentDescription = "Görev ekle",
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .navigationBarsPadding(),
            )
        }
    }

    if (pickingTheme) {
        ThemeDialog(
            selectedIndex = colorIndex.mod(ListThemes.size),
            onSelect = onChangeColor,
            onDismiss = { pickingTheme = false },
        )
    }

    tasks.find { it.id == editingId }?.let { task ->
        EditTaskDialog(
            task = task,
            withAmount = isPayment,
            withDate = isReminder,
            onDismiss = { editingId = null },
            onSave = { newTitle, amount, daily, date ->
                onEditTask(task.id, newTitle, amount, daily, date)
                editingId = null
            },
        )
    }

    tasks.find { it.id == deletingId }?.let { task ->
        AlertDialog(
            onDismissRequest = { deletingId = null },
            title = { Text("Görev silinsin mi?") },
            text = { Text("\"${task.title}\" kalıcı olarak silinecek.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteTask(task.id)
                        deletingId = null
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = DeleteRed),
                ) { Text("Sil") }
            },
            dismissButton = {
                TextButton(onClick = { deletingId = null }) { Text("İptal") }
            },
        )
    }
}


@Composable
private fun TaskCard(
    task: Task,
    today: LocalDate,
    onToggleDone: (Long) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    val completed = task.isCompleted(today)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(CardSurface)
            .padding(start = 8.dp, end = 4.dp, top = 6.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (task.daily) {
            // Günlük ödeme kendiliğinden azalır, elle işaretlenmez
            Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(R.drawable.ic_daily),
                    contentDescription = "Günlük ödeme",
                    tint = TextSecondary,
                )
            }
        } else if (task.date != null) {
            // Hatırlatıcı tamamlanmaz; işaret yerine zil
            Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(R.drawable.ic_reminder),
                    contentDescription = "Hatırlatıcı",
                    tint = TextSecondary,
                )
            }
        } else {
            IconButton(onClick = { onToggleDone(task.id) }) {
                CheckCircle(checked = task.done)
            }
        }
        Spacer(Modifier.width(4.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.bodyLarge,
                color = if (completed) TextSecondary else TextPrimary,
                textDecoration = if (completed) TextDecoration.LineThrough else null,
            )
            if (task.daily && task.amount != null) {
                val daysLeft = DailyPeriod.of(today).daysLeft
                Text(
                    text = "Günlük ${Money.format(task.dailyShare(today))} · " +
                        "${Money.format(task.amount)} · $daysLeft gün kaldı",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                )
            }
            task.date?.let { date ->
                // Günü geçmiş ve yapılmamış hatırlatıcı kırmızı
                Text(
                    text = DateInput.formatLong(date, today),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (!completed && date < today) DeleteRed else TextSecondary,
                )
            }
        }
        task.amount?.let { amount ->
            Text(
                text = Money.format(if (task.daily) task.remainingAmount(today) else amount),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = if (completed) TextSecondary else TextPrimary,
                textDecoration = if (completed) TextDecoration.LineThrough else null,
                modifier = Modifier.padding(start = 8.dp),
            )
        }
        Spacer(Modifier.width(4.dp))
        IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
            Icon(
                painter = painterResource(R.drawable.ic_edit),
                contentDescription = "Düzenle",
                tint = EditYellow,
                modifier = Modifier.size(20.dp),
            )
        }
        IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
            Icon(
                painter = painterResource(R.drawable.ic_delete),
                contentDescription = "Sil",
                tint = DeleteRed,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
private fun EditTaskDialog(
    task: Task,
    withAmount: Boolean,
    withDate: Boolean,
    onDismiss: () -> Unit,
    onSave: (title: String, amount: Long?, daily: Boolean, date: LocalDate?) -> Unit,
) {
    var title by rememberSaveable { mutableStateOf(task.title) }
    var amountText by rememberSaveable {
        mutableStateOf(task.amount?.let { Money.format(it).removeSuffix(" TL") } ?: "")
    }
    var daily by rememberSaveable { mutableStateOf(task.daily) }
    var dateText by rememberSaveable { mutableStateOf(task.date?.let(DateInput::formatShort) ?: "") }

    val amount = Money.parse(amountText)
    val date = DateInput.parse(dateText)
    val canSave = title.isNotBlank() && (!withAmount || amount != null) && (!withDate || date != null)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Görevi düzenle") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Görev") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    modifier = Modifier.fillMaxWidth(),
                )
                if (withAmount) {
                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { amountText = it },
                        label = { Text("Tutar (TL)") },
                        singleLine = true,
                        isError = amountText.isNotEmpty() && amount == null,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { daily = !daily }
                            .padding(end = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Checkbox(checked = daily, onCheckedChange = { daily = it })
                        Text("Günlük", style = MaterialTheme.typography.bodyMedium)
                    }
                }
                if (withDate) {
                    OutlinedTextField(
                        value = dateText,
                        onValueChange = { dateText = it },
                        label = { Text("Tarih (gg.aa.yyyy)") },
                        singleLine = true,
                        isError = dateText.isNotEmpty() && date == null,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(title, if (withAmount) amount else null, withAmount && daily, if (withDate) date else null)
                },
                enabled = canSave,
            ) { Text("Kaydet") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("İptal") }
        },
    )
}

@Composable
internal fun ThemeDialog(
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tema") },
        text = {
            // Seçim anında uygulanır; arka plan değişimi dialog arkasında görünür
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ListThemes.withIndex().chunked(4).forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        row.forEach { (index, theme) ->
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(theme.color)
                                    .then(
                                        if (index == selectedIndex) {
                                            Modifier.border(3.dp, Color.White, CircleShape)
                                        } else {
                                            Modifier
                                        }
                                    )
                                    .clickable { onSelect(index) },
                                contentAlignment = Alignment.Center,
                            ) {
                                if (index == selectedIndex) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_check),
                                        contentDescription = theme.name,
                                        tint = Color.White,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Tamam") }
        },
    )
}

@Composable
private fun CheckCircle(checked: Boolean) {
    if (checked) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(Color(0xFF8E99A8)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_check),
                contentDescription = "Tamamlanmadı olarak işaretle",
                tint = CardSurface,
                modifier = Modifier.size(16.dp),
            )
        }
    } else {
        Box(
            modifier = Modifier
                .size(24.dp)
                .border(2.dp, TextSecondary, CircleShape),
        )
    }
}

@Composable
private fun CompletedHeader(count: Int, expanded: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(Color.Black.copy(alpha = 0.25f))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(if (expanded) R.drawable.ic_expand else R.drawable.ic_collapse),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(20.dp),
        )
        Spacer(Modifier.width(12.dp))
        Text("Tamamlandı  $count", color = Color.White, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun PaymentSummary(remaining: Long, paid: Long) {
    Row(
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(Color.Black.copy(alpha = 0.25f))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text("Kalan", color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.labelLarge)
            Text(
                text = Money.format(remaining),
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text("Ödenen", color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.labelLarge)
            Text(Money.format(paid), color = Color.White, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = "Toplam ${Money.format(remaining + paid)}",
                color = Color.White.copy(alpha = 0.8f),
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun AddTaskBar(
    withAmount: Boolean,
    withDate: Boolean,
    onAdd: (title: String, amount: Long?, daily: Boolean, date: LocalDate?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var title by rememberSaveable { mutableStateOf("") }
    var amountText by rememberSaveable { mutableStateOf("") }
    var daily by rememberSaveable { mutableStateOf(false) }
    var dateText by rememberSaveable { mutableStateOf("") }
    val titleFocus = remember { FocusRequester() }
    val amountFocus = remember { FocusRequester() }
    LaunchedEffect(Unit) { titleFocus.requestFocus() }

    val amount = Money.parse(amountText)
    val date = DateInput.parse(dateText)
    val canAdd = title.isNotBlank() && (!withAmount || amount != null) && (!withDate || date != null)

    // To Do gibi: ekledikten sonra alan açık kalır, art arda görev yazılabilir
    val submit = {
        if (canAdd) {
            onAdd(title, if (withAmount) amount else null, withAmount && daily, if (withDate) date else null)
            title = ""
            amountText = ""
            dateText = ""
            daily = false
            titleFocus.requestFocus()
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(CardSurface)
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .border(2.dp, TextSecondary, CircleShape),
            )
            Spacer(Modifier.width(16.dp))
            BarTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = "Görev ekle",
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = if (withAmount || withDate) ImeAction.Next else ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onNext = { amountFocus.requestFocus() },
                    onDone = { submit() },
                ),
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(titleFocus),
            )
            if (withAmount) {
                Spacer(Modifier.width(12.dp))
                BarTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    placeholder = "Tutar (TL)",
                    isError = amountText.isNotEmpty() && amount == null,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions(onDone = { submit() }),
                    modifier = Modifier
                        .width(110.dp)
                        .focusRequester(amountFocus),
                )
            }
            if (withDate) {
                Spacer(Modifier.width(12.dp))
                BarTextField(
                    value = dateText,
                    onValueChange = { dateText = it },
                    placeholder = "gg.aa.yyyy",
                    isError = dateText.isNotEmpty() && date == null,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions(onDone = { submit() }),
                    modifier = Modifier
                        .width(110.dp)
                        .focusRequester(amountFocus),
                )
            }
            IconButton(onClick = submit, enabled = canAdd) {
                Icon(
                    painter = painterResource(R.drawable.ic_send),
                    contentDescription = "Ekle",
                    tint = if (canAdd) TextPrimary else TextSecondary.copy(alpha = 0.4f),
                )
            }
        }
        if (withAmount) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { daily = !daily }
                    .padding(end = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    checked = daily,
                    onCheckedChange = { daily = it },
                    colors = CheckboxDefaults.colors(uncheckedColor = TextSecondary),
                )
                Text(
                    text = if (daily) "Günlük · ay sonundan ay sonuna her gün düşer" else "Günlük",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                )
            }
        }
    }
}

@Composable
private fun BarTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
) {
    Box(modifier = modifier) {
        if (value.isEmpty()) {
            Text(placeholder, color = TextSecondary, style = MaterialTheme.typography.bodyLarge)
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = if (isError) Color(0xFFFF8A80) else TextPrimary,
            ),
            cursorBrush = SolidColor(TextPrimary),
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Preview
@Composable
private fun ListDetailPreview() {
    ListyTheme {
        ListDetailScreen(
            title = "Ödemeler",
            colorIndex = 0,
            type = ListType.PAYMENT,
            tasks = listOf(
                Task(1, 1, "Ekstralar", 3, amount = 1_250_050),
                Task(2, 1, "EV", 2, important = true, amount = 6_000_000, daily = true),
                Task(3, 1, "Kredi Kartları", 1, done = true, amount = 2_000_000),
            ),
            onBack = {},
            onChangeColor = {},
            onAddTask = { _, _, _, _ -> },
            onToggleDone = {},
            onEditTask = { _, _, _, _, _ -> },
            onDeleteTask = {},
        )
    }
}
