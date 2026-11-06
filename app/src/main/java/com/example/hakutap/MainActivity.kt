package com.example.hakutap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.DisposableEffect
import android.widget.Toast
import android.provider.Settings
import android.content.Intent
import android.content.Context
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.paint
import androidx.compose.foundation.layout.size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.Offset
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DropdownMenu
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hakutap.ui.theme.HakuTapTheme

private val Midnight = Color(0xFF081426)
private val PanelBlue = Color(0xFF14233B)
private val Gold = Color(0xFFFFD477)
private val SoftGold = Color(0xFFFFE8B0)
private val PaleText = Color(0xFFF5F2EC)
private val MutedText = Color(0xFFB8C2D4)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            HakuTapTheme {
                HakuTapHome()
            }
        }
    }
}

@Composable
fun HakuTapHome() {
    val context = LocalContext.current
    val settings = remember(context) {
        context.getSharedPreferences(TapAccessibilityService.PREFS_NAME, Context.MODE_PRIVATE)
    }

    var interval by remember(settings) {
        mutableStateOf(settings.getString(TapAccessibilityService.KEY_INTERVAL, "100 ms") ?: "100 ms")
    }
    var repeatCount by remember(settings) {
        mutableStateOf(settings.getString(TapAccessibilityService.KEY_REPEAT, "Unlimited") ?: "Unlimited")
    }
    var delay by remember(settings) {
        mutableStateOf(settings.getString(TapAccessibilityService.KEY_DELAY, "3 sec") ?: "3 sec")
    }
    var isRunning by remember { mutableStateOf(false) }
    var targetSelected by remember {
        mutableStateOf(settings.getBoolean(TapAccessibilityService.KEY_HAS_TARGET, false))
    }

    fun saveSettings() {
        settings.edit()
            .putString(TapAccessibilityService.KEY_INTERVAL, interval)
            .putString(TapAccessibilityService.KEY_REPEAT, repeatCount)
            .putString(TapAccessibilityService.KEY_DELAY, delay)
            .apply()
    }

    fun openAccessibilitySettings() {
        Toast.makeText(context, "Enable Haku Tap in Accessibility settings, then return here.", Toast.LENGTH_LONG).show()
        context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
    }

    DisposableEffect(Unit) {
        TapAccessibilityService.runningListener = { isRunning = it }
        TapAccessibilityService.targetListener = { targetSelected = it }

        TapAccessibilityService.instance?.let { service ->
            isRunning = service.isRunning
            targetSelected = service.hasTarget()
        }

        onDispose {
            TapAccessibilityService.runningListener = null
            TapAccessibilityService.targetListener = null
        }
    }

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val compactPhone = configuration.screenHeightDp < 760

    Scaffold(containerColor = Midnight) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF101E35), Midnight, Color(0xFF060D18))
                    )
                )
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(top = 6.dp, bottom = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height((screenWidth * 0.54f).coerceIn(150.dp, 240.dp))
            ) {
                Image(
                    painter = painterResource(R.drawable.haku_header),
                    contentDescription = "Haku beneath the moon with his spirit dragon",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 4.dp, end = 4.dp, top = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Spacer(Modifier.width(1.dp))
                    Text(
                        text = "\u2699",
                        color = Gold,
                        fontSize = 27.sp
                    )
                }
            }

            Spacer(Modifier.height(if (compactPhone) 10.dp else 16.dp))

            Card(
                modifier = Modifier
                    .padding(horizontal = if (screenWidth < 360.dp) 12.dp else 18.dp)
                    .fillMaxWidth()
                    .border(1.dp, Color(0x99FFD477), RoundedCornerShape(22.dp)),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xE6152238))
            ) {
                Column(Modifier.padding(start = 10.dp, end = 10.dp, top = 0.dp, bottom = 8.dp)) {
                    Image(
                        painter = painterResource(R.drawable.tap_settings_header),
                        contentDescription = "Tap settings",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(if (compactPhone) 38.dp else 44.dp)
                            .graphicsLayer(scaleX = 1.09f)
                    )

                    Spacer(Modifier.height(8.dp))


                    DropdownSetting(
                        icon = "\u23F1",
                        title = "Tap Interval",
                        subtitle = "Time between taps",
                        value = interval,
                        options = listOf("50 ms", "100 ms", "200 ms", "500 ms", "1000 ms"),
                        onSelect = { interval = it; settings.edit().putString(TapAccessibilityService.KEY_INTERVAL, it).apply() }
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 36.dp, end = 2.dp)
                            .height(1.dp)
                            .background(Color(0x334E607A))
                    )

                    DropdownSetting(
                        icon = "\u21BB",
                        title = "Repeat Count",
                        subtitle = "Number of taps",
                        value = repeatCount,
                        options = listOf("Unlimited", "50 taps", "100 taps", "500 taps"),
                        onSelect = { repeatCount = it; settings.edit().putString(TapAccessibilityService.KEY_REPEAT, it).apply() }
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 36.dp, end = 2.dp)
                            .height(1.dp)
                            .background(Color(0x334E607A))
                    )

                    DropdownSetting(
                        icon = "\u25F7",
                        title = "Start Delay",
                        subtitle = "Delay before starting",
                        value = delay,
                        options = listOf("0 sec", "3 sec", "5 sec", "10 sec"),
                        onSelect = { delay = it; settings.edit().putString(TapAccessibilityService.KEY_DELAY, it).apply() }
                    )
                }
            }

            Spacer(Modifier.height(if (compactPhone) 6.dp else 10.dp))



            Image(
                painter = painterResource(R.drawable.tap_target_ambience),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height((screenWidth * 0.62f).coerceIn(175.dp, if (compactPhone) 270.dp else 330.dp))
                    .alpha(0.9f)
            )

            Text(
                text = when {
                    isRunning -> "Haku is tapping"
                    targetSelected -> "Target selected Â· tap Start"
                    else -> "Tap here to choose a point"
                },
                color = MutedText,
                fontSize = 12.sp,
                modifier = Modifier.clickable {
                    saveSettings()
                    val service = TapAccessibilityService.instance
                    if (service == null) {
                        openAccessibilitySettings()
                    } else {
                        service.showTargetOverlay()
                        Toast.makeText(context, "Switch to your target app, drag the reticle, then tap Set.", Toast.LENGTH_LONG).show()
                    }
                }
            )
            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    saveSettings()
                    val service = TapAccessibilityService.instance
                    when {
                        service == null -> openAccessibilitySettings()
                        service.isRunning -> service.stopClicking()
                        !service.hasTarget() -> {
                            service.showTargetOverlay()
                            Toast.makeText(context, "Drag the reticle to your target, then tap Set.", Toast.LENGTH_LONG).show()
                        }
                        else -> service.showTargetOverlay()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = if (screenWidth < 360.dp) 12.dp else 18.dp)
                    .height(if (compactPhone) 54.dp else 60.dp)
                    .paint(
                        painter = painterResource(R.drawable.start_button_art),
                        contentScale = ContentScale.Crop
                    ),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Midnight
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 2.dp
                )
            ) {
                Text(
                    text = if (isRunning) "\u25A0  Stop" else "\u25B6  Start",
                    fontSize = if (screenWidth < 360.dp) 18.sp else 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun SettingLineIcon(title: String) {
    Canvas(modifier = Modifier.size(22.dp)) {
        val gold = Gold
        val stroke = 1.7.dp.toPx()
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = 6.8.dp.toPx()
        val lineStyle = Stroke(width = stroke)

        when (title) {
            "Tap Interval" -> {
                drawCircle(gold, radius, center, style = lineStyle)
                drawLine(gold, Offset(center.x, center.y - radius - 3.dp.toPx()), Offset(center.x, center.y - radius), stroke)
                drawLine(gold, Offset(center.x - 2.5.dp.toPx(), center.y - radius - 3.dp.toPx()), Offset(center.x + 2.5.dp.toPx(), center.y - radius - 3.dp.toPx()), stroke)
                drawLine(gold, center, Offset(center.x, center.y - radius * 0.52f), stroke)
                drawLine(gold, center, Offset(center.x + radius * 0.48f, center.y), stroke)
            }

            "Repeat Count" -> {
                val arcTopLeft = Offset(center.x - radius, center.y - radius)
                val arcSize = Size(radius * 2f, radius * 2f)

                drawArc(gold, 45f, 135f, false, arcTopLeft, arcSize, style = lineStyle)
                drawArc(gold, 225f, 135f, false, arcTopLeft, arcSize, style = lineStyle)

                drawLine(gold, Offset(center.x - radius, center.y), Offset(center.x - radius + 3.dp.toPx(), center.y - 2.5.dp.toPx()), stroke)
                drawLine(gold, Offset(center.x - radius, center.y), Offset(center.x - radius + 3.dp.toPx(), center.y + 2.5.dp.toPx()), stroke)
                drawLine(gold, Offset(center.x + radius, center.y), Offset(center.x + radius - 3.dp.toPx(), center.y - 2.5.dp.toPx()), stroke)
                drawLine(gold, Offset(center.x + radius, center.y), Offset(center.x + radius - 3.dp.toPx(), center.y + 2.5.dp.toPx()), stroke)
            }

            else -> {
                drawCircle(gold, radius, center, style = lineStyle)
                drawLine(gold, center, Offset(center.x, center.y - radius * 0.58f), stroke)
                drawLine(gold, center, Offset(center.x + radius * 0.48f, center.y + radius * 0.2f), stroke)
            }
        }
    }
}
@Composable
private fun DropdownSetting(
    icon: String,
    title: String,
    subtitle: String,
    value: String,
    options: List<String>,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val availableWidth = LocalConfiguration.current.screenWidthDp.dp
    val valueWidth = (availableWidth - 240.dp).coerceIn(78.dp, 100.dp)
    val rowHeight = if (LocalConfiguration.current.screenHeightDp < 760) 58.dp else 62.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(rowHeight),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(36.dp),
            contentAlignment = Alignment.Center
        ) {
            SettingLineIcon(title = title)
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = PaleText,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = subtitle,
                color = MutedText,
                fontSize = 11.sp
            )
        }

        Box(modifier = Modifier.offset(x = (-10).dp)) {
            Surface(
                modifier = Modifier
                    .width(valueWidth)
                    .height(36.dp)
                    .border(1.dp, Color(0x88FFD477), RoundedCornerShape(11.dp))
                    .clickable { expanded = true },
                color = Color(0xFF24344F),
                shape = RoundedCornerShape(11.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = value,
                        color = SoftGold,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 12.dp)
                    )
                    Text(
                        text = "\u2304",
                        color = SoftGold,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 9.dp)
                    )
                }
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onSelect(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
@Composable
private fun SettingHeader(title: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = PaleText,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        )
        Surface(
            color = Color(0xFF24344F),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(
                text = value,
                color = SoftGold,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
            )
        }
    }
}

@Composable
private fun SmallChoice(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.height(38.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) Gold else Color(0xFF24344F),
            contentColor = if (selected) Midnight else PaleText
        )
    ) {
        Text(text = label, fontSize = 12.sp)
    }
}

@Composable
private fun ActionButton(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(58.dp).border(1.dp, Color(0x66FFD477), RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = PanelBlue,
            contentColor = SoftGold
        )
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            lineHeight = 15.sp
        )
    }
}
