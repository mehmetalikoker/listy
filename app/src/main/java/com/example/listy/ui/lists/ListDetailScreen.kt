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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.lerp
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
import com.example.listy.data.ListType
import com.example.listy.data.Money
import com.example.listy.data.Task
import com.example.listy.ui.theme.CardSurface
import com.example.listy.ui.theme.ListyTheme
import com.example.listy.ui.theme.TextPrimary
import com.example.listy.ui.theme.TextSecondary

@Composable
fun ListDetailScreen(
    title: String,
    color: Color,
    type: ListType,
    tasks: List<Task>,
    onBack: () -> Unit,
    onAddTask: (title: String, amount: Long?) -> Unit,
    onToggleDone: (Long) -> Unit,
    onToggleImportant: (Long) -> Unit,
) {
    var adding by rememberSaveable { mutableStateOf(false) }
    var showCompleted by rememberSaveable { mutableStateOf(true) }

    BackHandler(enabled = adding) { adding = false }

    // Yeni eklenen en üstte
    val openTasks = tasks.filter { !it.done }.sortedByDescending { it.createdAt }
    val doneTasks = tasks.filter { it.done }.sortedByDescending { it.createdAt }
    val isPayment = type == ListType.PAYMENT

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(color, lerp(color, Color.White, 0.3f)))),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
        ) {
            IconButton(onClick = onBack, modifier = Modifier.padding(start = 8.dp, top = 8.dp)) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_back),
                    contentDescription = "Geri",
                    tint = Color.White,
                )
            }
            Text(
                text = title,
                color = Color.White,
                fontSize = 34.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            )
            if (isPayment) {
                PaymentSummary(
                    remaining = openTasks.sumOf { it.amount ?: 0 },
                    paid = doneTasks.sumOf { it.amount ?: 0 },
                )
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                items(openTasks, key = { it.id }) { task ->
                    TaskCard(task, onToggleDone, onToggleImportant)
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
                            TaskCard(task, onToggleDone, onToggleImportant)
                        }
                    }
                }
            }
        }

        if (adding) {
            AddTaskBar(
                withAmount = isPayment,
                onAdd = onAddTask,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .imePadding(),
            )
        } else {
            FloatingActionButton(
                onClick = { adding = true },
                containerColor = color,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .navigationBarsPadding()
                    .padding(20.dp),
            ) {
                Icon(painterResource(R.drawable.ic_add), contentDescription = "Görev ekle")
            }
        }
    }
}

@Composable
private fun TaskCard(
    task: Task,
    onToggleDone: (Long) -> Unit,
    onToggleImportant: (Long) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(CardSurface)
            .padding(start = 8.dp, end = 4.dp, top = 6.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = { onToggleDone(task.id) }) {
            CheckCircle(checked = task.done)
        }
        Spacer(Modifier.width(4.dp))
        Text(
            text = task.title,
            style = MaterialTheme.typography.bodyLarge,
            color = if (task.done) TextSecondary else TextPrimary,
            textDecoration = if (task.done) TextDecoration.LineThrough else null,
            modifier = Modifier.weight(1f),
        )
        task.amount?.let { amount ->
            Text(
                text = Money.format(amount),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = if (task.done) TextSecondary else TextPrimary,
                textDecoration = if (task.done) TextDecoration.LineThrough else null,
                modifier = Modifier.padding(start = 8.dp),
            )
        }
        IconButton(onClick = { onToggleImportant(task.id) }) {
            Icon(
                painter = painterResource(if (task.important) R.drawable.ic_star else R.drawable.ic_star_outline),
                contentDescription = if (task.important) "Önemli işaretini kaldır" else "Önemli olarak işaretle",
                tint = if (task.important) Color(0xFF7B8CDE) else TextSecondary,
            )
        }
    }
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
    onAdd: (title: String, amount: Long?) -> Unit,
    modifier: Modifier = Modifier,
) {
    var title by rememberSaveable { mutableStateOf("") }
    var amountText by rememberSaveable { mutableStateOf("") }
    val titleFocus = remember { FocusRequester() }
    val amountFocus = remember { FocusRequester() }
    LaunchedEffect(Unit) { titleFocus.requestFocus() }

    val amount = Money.parse(amountText)
    val canAdd = title.isNotBlank() && (!withAmount || amount != null)

    // To Do gibi: ekledikten sonra alan açık kalır, art arda görev yazılabilir
    val submit = {
        if (canAdd) {
            onAdd(title, if (withAmount) amount else null)
            title = ""
            amountText = ""
            titleFocus.requestFocus()
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(CardSurface)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
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
                imeAction = if (withAmount) ImeAction.Next else ImeAction.Done,
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
        IconButton(onClick = submit, enabled = canAdd) {
            Icon(
                painter = painterResource(R.drawable.ic_send),
                contentDescription = "Ekle",
                tint = if (canAdd) TextPrimary else TextSecondary.copy(alpha = 0.4f),
            )
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
            color = Color(0xFFC9506B),
            type = ListType.PAYMENT,
            tasks = listOf(
                Task(1, 1, "Ekstralar", 3, amount = 1_250_050),
                Task(2, 1, "EV", 2, important = true, amount = 6_000_000),
                Task(3, 1, "Kredi Kartları", 1, done = true, amount = 2_000_000),
            ),
            onBack = {},
            onAddTask = { _, _ -> },
            onToggleDone = {},
            onToggleImportant = {},
        )
    }
}
