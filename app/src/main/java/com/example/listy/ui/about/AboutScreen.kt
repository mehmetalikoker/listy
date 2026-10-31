package com.example.listy.ui.about

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.example.listy.R
import com.example.listy.ui.theme.Accent
import com.example.listy.ui.theme.CardSurface
import com.example.listy.ui.theme.ListyTheme
import com.example.listy.ui.theme.TextPrimary
import com.example.listy.ui.theme.TextSecondary

private const val DEVELOPER_NAME = "Mehmet Ali Köker"
private const val DEVELOPER_EMAIL = "mehmetalikoker@outlook.com"

@Composable
fun AboutScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val version = runCatching {
        context.packageManager.getPackageInfo(context.packageName, 0).versionName
    }.getOrNull() ?: "-"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
        ) {
            IconButton(onClick = onBack) {
                Icon(painterResource(R.drawable.ic_arrow_back), contentDescription = "Geri")
            }
            Text("Hakkında", style = MaterialTheme.typography.titleLarge)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            ) {
                Box(
                    modifier = Modifier.size(80.dp).clip(CircleShape).background(Accent),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_list),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(40.dp),
                    )
                }
                Spacer(Modifier.height(12.dp))
                Text("Listy", fontSize = 28.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Text("Sürüm $version", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            }

            Section("Uygulama") {
                Text(
                    "Listy; yapılacakları, ödemeleri ve hatırlatıcıları tek yerde toplayan sade bir liste uygulamasıdır.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary,
                )
                Feature(R.drawable.ic_list, "Yapılacaklar", "Görev ekle, tamamlananları işaretle")
                Feature(R.drawable.ic_payments, "Ödeme", "Tutarları takip et, günlük ödemeler ay sonuna kadar her gün düşer")
                Feature(R.drawable.ic_reminder, "Hatırlatıcı", "Görevleri tarihleriyle tut, en yakını en üstte")
                Feature(R.drawable.ic_palette, "Temalar", "Ana ekran ve her liste için ayrı renk")
                Text(
                    "Tüm veriler yalnızca bu cihazda saklanır; internete gönderilmez.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                )
            }

            Section("Geliştirici") {
                Text(DEVELOPER_NAME, style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
                Text(
                    text = DEVELOPER_EMAIL,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Accent,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .clickable {
                            // E-posta uygulaması yoksa sessizce geç
                            runCatching {
                                context.startActivity(Intent(Intent.ACTION_SENDTO, "mailto:$DEVELOPER_EMAIL".toUri()))
                            }
                        },
                )
            }
        }
    }
}

@Composable
private fun Section(title: String, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardSurface)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(title, style = MaterialTheme.typography.labelLarge, color = TextSecondary)
        content()
    }
}

@Composable
private fun Feature(icon: Int, title: String, description: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(painterResource(icon), contentDescription = null, tint = Accent, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(14.dp))
        Column {
            Text(title, style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
            Text(description, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
    }
}

@Preview
@Composable
private fun AboutPreview() {
    ListyTheme { AboutScreen(onBack = {}) }
}
