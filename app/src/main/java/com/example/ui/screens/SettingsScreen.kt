package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.JarvisViewModel
import com.example.ui.components.GlassCard

@Composable
fun SettingsScreen(
    viewModel: JarvisViewModel,
    onBack: () -> Unit,
    onNavigateToPermissions: () -> Unit,
    onNavigateToMemory: () -> Unit
) {
    val assistantName by viewModel.assistantName.collectAsState()
    val assistantAvatar by viewModel.assistantAvatar.collectAsState()
    val voiceLanguage by viewModel.voiceLanguage.collectAsState()
    val speakingSpeed by viewModel.speakingSpeed.collectAsState()
    val personalityStyle by viewModel.personalityStyle.collectAsState()
    val customPrompt by viewModel.customPersonalityPrompt.collectAsState()
    val greetingMessage by viewModel.greetingMessage.collectAsState()
    val themeStyle by viewModel.themeStyle.collectAsState()
    val customApiKey by viewModel.customApiKey.collectAsState()

    var nameInput by remember { mutableStateOf(assistantName) }
    var promptInput by remember { mutableStateOf(customPrompt) }
    var greetingInput by remember { mutableStateOf(greetingMessage) }
    var apiKeyInput by remember { mutableStateOf(customApiKey) }
    var showApiKey by remember { mutableStateOf(false) }

    var aiSelfUpdatePrompt by remember { mutableStateOf("") }
    val selfUpdateResult by viewModel.selfUpdateResult.collectAsState()
    val isProcessing by viewModel.isProcessing.collectAsState()

    LaunchedEffect(assistantName) { nameInput = assistantName }
    LaunchedEffect(customPrompt) { promptInput = customPrompt }
    LaunchedEffect(greetingMessage) { greetingInput = greetingMessage }
    LaunchedEffect(customApiKey) { apiKeyInput = customApiKey }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(4.dp))
            Column {
                Text(
                    text = "ASSISTANT SETTINGS",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Customize Identity, Voice & Intelligence",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Section 0: Prompt-Driven AI Self-Configurator
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI Prompt Configurator",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "PROMPT-DRIVEN AI SELF-CONFIGURATOR",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    text = "Enter any instruction to dynamically reconfigure JARVIS settings, update personality/tone, or automatically generate new memories and automation rules.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )

                Text("QUICK PROMPT TEMPLATES:", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp)
                val presetPrompts = listOf(
                    "British AI + Fast Speech" to "Change assistant name to Friday, British accent, 1.3x speed.",
                    "Cyberpunk + Hinglish" to "Speak Hinglish casually, set theme to Cyberpunk Gold.",
                    "Low Battery Alert + Memory" to "Add rule to alert on 15% battery and remember I am a Kotlin developer.",
                    "Executive Minimalist" to "Set greeting to 'Systems active, boss.' and keep responses under 2 sentences."
                )

                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(presetPrompts) { (label, prompt) ->
                        FilterChip(
                            selected = false,
                            onClick = { aiSelfUpdatePrompt = prompt },
                            label = { Text(label, fontSize = 10.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = aiSelfUpdatePrompt,
                    onValueChange = { aiSelfUpdatePrompt = it },
                    placeholder = { Text("e.g. 'Set assistant name to Jarvis Ultra, speak in Hinglish, and save my coffee order as Cappuccino'") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                Button(
                    onClick = {
                        if (aiSelfUpdatePrompt.isNotBlank()) {
                            viewModel.executePromptSelfUpdate(aiSelfUpdatePrompt)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = aiSelfUpdatePrompt.isNotBlank() && !isProcessing,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Reconfiguring System...")
                    } else {
                        Icon(imageVector = Icons.Default.Bolt, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("EXECUTE PROMPT RECONFIGURATION")
                    }
                }

                if (selfUpdateResult.isNotBlank()) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = selfUpdateResult,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // Section 1: Assistant Identity
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "ASSISTANT IDENTITY",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { newValue ->
                        nameInput = newValue
                        viewModel.updateAssistantName(newValue)
                    },
                    label = { Text("Assistant Name") },
                    placeholder = { Text("e.g. JARVIS, FRIDAY, NOVA") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Text("AVATAR STYLE:", style = MaterialTheme.typography.labelSmall)
                val avatars = listOf("Cyan Arc", "Orange Core", "Emerald Pulse", "Gold Ring")
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(avatars) { avatar ->
                        FilterChip(
                            selected = assistantAvatar == avatar,
                            onClick = { viewModel.updateAssistantAvatar(avatar) },
                            label = { Text(avatar, fontSize = 11.sp) }
                        )
                    }
                }
            }
        }

        // Section 2: Voice & Language Configuration
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "VOICE & LANGUAGE",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Text("LANGUAGE PREFERENCE:", style = MaterialTheme.typography.labelSmall)
                val languages = listOf("English", "Hindi", "Hinglish")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    languages.forEach { lang ->
                        FilterChip(
                            selected = voiceLanguage == lang,
                            onClick = { viewModel.updateVoiceLanguage(lang) },
                            label = { Text(lang) }
                        )
                    }
                }

                Text(
                    text = "SPEAKING SPEED: ${String.format("%.1fx", speakingSpeed)}",
                    style = MaterialTheme.typography.labelSmall
                )
                Slider(
                    value = speakingSpeed,
                    onValueChange = { viewModel.updateSpeakingSpeed(it) },
                    valueRange = 0.5f..2.0f,
                    steps = 5
                )
            }
        }

        // Section 3: Personality Style & Custom Editor
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "PERSONALITY & BEHAVIOR",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                val personalities = listOf("Professional", "Friendly", "Funny", "Calm", "Minimal", "Technical", "Custom")
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(personalities) { style ->
                        FilterChip(
                            selected = personalityStyle == style,
                            onClick = { viewModel.updatePersonalityStyle(style) },
                            label = { Text(style, fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = promptInput,
                    onValueChange = { newValue ->
                        promptInput = newValue
                        viewModel.updateCustomPersonalityPrompt(newValue)
                    },
                    label = { Text("Custom Communication Instructions") },
                    placeholder = { Text("e.g. 'Speak casually in Hinglish. Keep answers concise.'") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                OutlinedTextField(
                    value = greetingInput,
                    onValueChange = { newValue ->
                        greetingInput = newValue
                        viewModel.updateGreetingMessage(newValue)
                    },
                    label = { Text("Default Greeting Message") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        }

        // Section 4: Visual Theme
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "VISUAL THEME ENGINE",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                val themes = listOf("Futuristic", "Cyberpunk Gold", "Emerald", "Light Clean")
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(themes) { th ->
                        FilterChip(
                            selected = themeStyle == th,
                            onClick = { viewModel.updateThemeStyle(th) },
                            label = { Text(th, fontSize = 11.sp) }
                        )
                    }
                }
            }
        }

        // Section 5: AI API Configuration
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "AI PROVIDER CREDENTIALS",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Gemini API Key is automatically managed via AI Studio Secrets. You can also specify an optional custom key below.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = apiKeyInput,
                    onValueChange = { newValue ->
                        apiKeyInput = newValue
                        viewModel.updateCustomApiKey(newValue)
                    },
                    label = { Text("Custom Gemini API Key (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = if (showApiKey) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showApiKey = !showApiKey }) {
                            Icon(
                                imageVector = if (showApiKey) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Toggle key"
                            )
                        }
                    }
                )
            }
        }

        // Section 6: Permissions Dashboard
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateToPermissions() }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Permissions",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "PERMISSION & PRIVACY CENTER",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Audit system access, clear local data",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Open",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
