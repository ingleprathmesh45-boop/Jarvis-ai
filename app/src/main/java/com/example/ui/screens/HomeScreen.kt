package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.JarvisViewModel
import com.example.ui.components.GlassCard
import com.example.ui.components.JarvisOrb
import com.example.ui.components.StatusChip
import com.example.ui.components.WaveformVisualizer

@Composable
fun HomeScreen(
    viewModel: JarvisViewModel,
    onNavigateToChat: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToAutomations: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val assistantName by viewModel.assistantName.collectAsState()
    val greetingMessage by viewModel.greetingMessage.collectAsState()
    val batteryStatus by viewModel.batteryStatus.collectAsState()
    val isSpeaking by viewModel.isSpeaking.collectAsState()
    val isProcessing by viewModel.isProcessing.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState(initial = emptyList())
    val automations by viewModel.automations.collectAsState(initial = emptyList())

    var quickInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top HUD Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = assistantName.uppercase(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "STATUS: ONLINE",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            IconButton(onClick = onNavigateToSettings) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Status telemetry bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatusChip(
                label = "BATTERY",
                value = "${batteryStatus.level}%",
                icon = if (batteryStatus.isCharging) Icons.Default.BatteryChargingFull else Icons.Default.BatteryStd
            )
            StatusChip(
                label = "ENGINE",
                value = "GEMINI 2.5",
                icon = Icons.Default.AutoAwesome
            )
            StatusChip(
                label = "RULES",
                value = "${automations.filter { it.isEnabled }.size} ACTIVE",
                icon = Icons.Default.Autorenew
            )
        }

        // Animated Central Arc Reactor Orb
        Spacer(modifier = Modifier.height(8.dp))
        JarvisOrb(
            size = 200.dp,
            isSpeaking = isSpeaking,
            isProcessing = isProcessing
        )

        // Waveform
        WaveformVisualizer(
            isSpeaking = isSpeaking,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        // Greeting Card
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = greetingMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (chatMessages.isNotEmpty()) {
                    val lastMsg = chatMessages.last()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "LAST LOG: ${lastMsg.sender}: ${lastMsg.content}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp,
                        maxLines = 2
                    )
                }
            }
        }

        // Quick Input Bar
        OutlinedTextField(
            value = quickInput,
            onValueChange = { quickInput = it },
            placeholder = { Text("Ask $assistantName anything...") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            trailingIcon = {
                IconButton(onClick = {
                    if (quickInput.isNotBlank()) {
                        viewModel.sendChatMessage(quickInput)
                        quickInput = ""
                        onNavigateToChat()
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )

        // Quick Command Shortcuts
        Text(
            text = "QUICK HUD ACTIONS",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.Start)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { viewModel.sendChatMessage("Check my battery status and system health") },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Icon(imageVector = Icons.Default.BatterySaver, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Battery", fontSize = 11.sp)
            }

            Button(
                onClick = { viewModel.sendChatMessage("Open camera") },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Camera", fontSize = 11.sp)
            }

            Button(
                onClick = onNavigateToSettings,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("AI Config", fontSize = 11.sp)
            }
        }
    }
}
