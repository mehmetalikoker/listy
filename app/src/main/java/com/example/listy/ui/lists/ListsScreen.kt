package com.example.listy.ui.lists

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.listy.R
import com.example.listy.data.ListType
import com.example.listy.data.Money
import com.example.listy.ui.theme.ListyTheme
import com.example.listy.ui.theme.TextSecondary
import com.example.listy.ui.theme.listColor

/** Ana ekranda gösterilecek liste özeti. */
data class ListSummary(
    val id: Long,
    val name: String,
    val colorIndex: Int,
    val type: ListType,
    val openTaskCount: Int,
    /** Ödeme listelerinde tamamlanmamış görevlerin toplamı (kuruş). */
    val remainingAmount: Long,
)

@Composable
fun ListsScreen(
    lists: List<ListSummary>,
    onOpenList: (Long) -> Unit,
    onCreateList: (String, ListType) -> Unit,
) {
    var showDialog by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        Text(
            text = "Listelerim",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(start = 20.dp, top = 24.dp, bottom = 12.dp),
        )
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant,
            modifier = Modifier.padding(horizontal = 16.dp),
        )

        Box(modifier = Modifier.weight(1f)) {
            if (lists.isEmpty()) {
                EmptyState()
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(lists, key = { it.id }) { list ->
                        ListRow(list = list, onClick = { onOpenList(list.id) })
                    }
                }
            }
        }

        NewListRow(onClick = { showDialog = true })
    }

    if (showDialog) {
        NewListDialog(
            onDismiss = { showDialog = false },
            onCreate = { name, type ->
                showDialog = false
                onCreateList(name, type)
            },
        )
    }
}

@Composable
private fun ListRow(list: ListSummary, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(
                if (list.type == ListType.PAYMENT) R.drawable.ic_payments else R.drawable.ic_list
            ),
            contentDescription = null,
            tint = listColor(list.colorIndex),
            modifier = Modifier.size(24.dp),
        )
        Spacer(Modifier.width(24.dp))
        Text(
            text = list.name,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
        )
        if (list.type == ListType.PAYMENT) {
            if (list.remainingAmount > 0) {
                Text(
                    text = Money.format(list.remainingAmount),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                )
            }
        } else if (list.openTaskCount > 0) {
            Text(
                text = list.openTaskCount.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
            )
        }
    }
}

@Composable
private fun NewListRow(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_add),
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(28.dp),
        )
        Spacer(Modifier.width(20.dp))
        Text(
            text = "Yeni liste",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary,
        )
    }
}

@Composable
private fun EmptyState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Henüz liste yok", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.padding(4.dp))
            Text(
                "Aşağıdaki \"Yeni liste\" ile bir liste oluştur",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
            )
        }
    }
}

@Composable
private fun NewListDialog(
    onDismiss: () -> Unit,
    onCreate: (String, ListType) -> Unit,
) {
    var name by rememberSaveable { mutableStateOf("") }
    var type by rememberSaveable { mutableStateOf(ListType.TODO) }
    val focusRequester = remember { FocusRequester() }
    val canCreate = name.isNotBlank()

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Yeni liste") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = { Text("Liste adı") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions(onDone = { if (canCreate) onCreate(name, type) }),
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                )
                Spacer(Modifier.padding(6.dp))
                Text("Liste türü", style = MaterialTheme.typography.labelLarge, color = TextSecondary)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = type == ListType.TODO,
                        onClick = { type = ListType.TODO },
                        label = { Text("Yapılacaklar") },
                        leadingIcon = { Icon(painterResource(R.drawable.ic_list), null, Modifier.size(18.dp)) },
                    )
                    FilterChip(
                        selected = type == ListType.PAYMENT,
                        onClick = { type = ListType.PAYMENT },
                        label = { Text("Ödeme") },
                        leadingIcon = { Icon(painterResource(R.drawable.ic_payments), null, Modifier.size(18.dp)) },
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onCreate(name, type) }, enabled = canCreate) {
                Text("Oluştur")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("İptal") }
        },
    )
}

@Preview
@Composable
private fun ListsScreenPreview() {
    ListyTheme {
        ListsScreen(
            lists = listOf(
                ListSummary(1, "Ödemeler", 0, ListType.PAYMENT, 2, 22_000_000),
                ListSummary(2, "TO DO", 1, ListType.TODO, 4, 0),
            ),
            onOpenList = {},
            onCreateList = { _, _ -> },
        )
    }
}
