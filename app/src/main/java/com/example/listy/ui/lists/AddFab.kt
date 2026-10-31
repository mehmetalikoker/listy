package com.example.listy.ui.lists

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.listy.R

// Ana ekran ve liste içi artı butonu aynı boyutta olsun diye ortak
@Composable
fun AddFab(
    onClick: () -> Unit,
    containerColor: Color,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = containerColor,
        // Açık renkli zeminde (sarı, amber) beyaz artı seçilmez
        contentColor = if (containerColor.luminance() > 0.5f) Color.Black else Color.White,
        shape = CircleShape,
        modifier = modifier
            .padding(20.dp)
            .size(72.dp),
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_add),
            contentDescription = contentDescription,
            modifier = Modifier.size(36.dp),
        )
    }
}
