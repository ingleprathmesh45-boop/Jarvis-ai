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

@Composable
fun ScreenVisionScreen(
    viewModel: JarvisViewModel,
    onBack: () -> Unit,
    onNavigateToChat: () -> Unit
) {
    val screenVisionText by viewModel.screenVisionText.collectAsState()
    val screenVisionAnalysis by viewModel.screenVisionAnalysis.collectAsState()
    val isProcessing by viewModel.isProcessing.collectAsState()

    var textInput by remember { mutableStateOf(screenVisionText) }

    val sampleText = """
        Subject: Q3 Project Review & Timeline
        Hi Team, Please find the attached report for Q3 deliverables. 
        1. Android App Release scheduled for Friday.
        2. Gemini API integration complete with Room local cache.
        3. Please review security compliance by EOD.
    """.trimIndent()

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
                    text = "SCREEN VISION & CONTEXT",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Real-time Text Extraction & AI Executive Summaries",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Screen Capture Input Area
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "EXTRACTED SCREEN CONTENT",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    TextButton(onClick = { textInput = sampleText }) {
                        Text("Load Sample Screen Text", fontSize = 11.sp)
                    }
                }

                OutlinedTextField(
                    value = textInput,
                    onValueChange = {
                        textInput = it
                        viewModel.setScreenVisionText(it)
                    },
                    placeholder = { Text("Paste screen text, article, or document content here...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    maxLines = 6
                )

                Button(
                    onClick = { viewModel.analyzeScreenVision(textInput) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = textInput.isNotBlank() && !isProcessing,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Analyze with Gemini Vision AI")
                }
            }
        }

        if (isProcessing) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Gemini Analysis Output
        if (screenVisionAnalysis.isNotBlank()) {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = "Analysis",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "JARVIS EXECUTIVE ANALYSIS",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Text(
                        text = screenVisionAnalysis,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                viewModel.sendChatMessage("Based on my screen vision: $screenVisionAnalysis, what actions should I take?")
                                onNavigateToChat()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                        ) {
                            Text("Discuss in Chat", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}
