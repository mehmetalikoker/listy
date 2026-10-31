package com.example.listy.ui.lists

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.listy.R
import com.example.listy.data.ListType
import com.example.listy.data.Money
import com.example.listy.ui.theme.Accent
import com.example.listy.ui.theme.DeleteRed
import com.example.listy.ui.theme.EditYellow
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
    onRenameList: (id: Long, name: String) -> Unit,
    onDeleteList: (Long) -> Unit,
) {
    var showDialog by rememberSaveable { mutableStateOf(false) }
    var editingId by rememberSaveable { mutableStateOf<Long?>(null) }
    var deletingId by rememberSaveable { mutableStateOf<Long?>(null) }

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
                // Alt boşluk: son satırın ikonları artı butonunun altında kalmasın
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 112.dp),
                ) {
                    items(lists, key = { it.id }) { list ->
                        ListRow(
                            list = list,
                            onClick = { onOpenList(list.id) },
                            onEdit = { editingId = list.id },
                            onDelete = { deletingId = list.id },
                        )
                    }
                }
            }

            AddFab(
                onClick = { showDialog = true },
                containerColor = Accent,
                contentDescription = "Yeni liste",
                modifier = Modifier.align(Alignment.BottomEnd),
            )
        }
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

    lists.find { it.id == editingId }?.let { list ->
        RenameListDialog(
            currentName = list.name,
            onDismiss = { editingId = null },
            onSave = { name ->
                onRenameList(list.id, name)
                editingId = null
            },
        )
    }

    lists.find { it.id == deletingId }?.let { list ->
        AlertDialog(
            onDismissRequest = { deletingId = null },
            title = { Text("Liste silinsin mi?") },
            text = { Text("\"${list.name}\" listesi içindeki tüm görevlerle birlikte kalıcı olarak silinecek.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteList(list.id)
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
private fun ListRow(
    list: ListSummary,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(start = 20.dp, end = 8.dp, top = 10.dp, bottom = 10.dp),
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
        Spacer(Modifier.width(8.dp))
        IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
            Icon(
                painter = painterResource(R.drawable.ic_edit),
                contentDescription = "Listeyi düzenle",
                tint = EditYellow,
                modifier = Modifier.size(20.dp),
            )
        }
        IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
            Icon(
                painter = painterResource(R.drawable.ic_delete),
                contentDescription = "Listeyi sil",
                tint = DeleteRed,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
private fun RenameListDialog(
    currentName: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit,
) {
    var name by rememberSaveable { mutableStateOf(currentName) }
    val canSave = name.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Listeyi düzenle") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Liste adı") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(onDone = { if (canSave) onSave(name) }),
                modifier = Modifier.fillMaxWidth(),
            )
        },
        confirmButton = {
            TextButton(onClick = { onSave(name) }, enabled = canSave) { Text("Kaydet") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("İptal") }
        },
    )
}

@Composable
private fun EmptyState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Henüz liste yok", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.padding(4.dp))
            Text(
                "Sağ alttaki + ile bir liste oluştur",
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
            onRenameList = { _, _ -> },
            onDeleteList = {},
        )
    }
}
