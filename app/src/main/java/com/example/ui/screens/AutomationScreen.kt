package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AutomationRuleEntity
import com.example.ui.JarvisViewModel
import com.example.ui.components.GlassCard

@Composable
fun AutomationScreen(
    viewModel: JarvisViewModel,
    onBack: () -> Unit
) {
    val automations by viewModel.automations.collectAsState(initial = emptyList())

    var showAddDialog by remember { mutableStateOf(false) }
    var ruleName by remember { mutableStateOf("") }
    var triggerType by remember { mutableStateOf("BATTERY_LOW") }
    var triggerValue by remember { mutableStateOf("15%") }
    var actionType by remember { mutableStateOf("TTS") }
    var actionValue by remember { mutableStateOf("Battery critical, please connect power grid.") }

    Column(
        modifier = Modifier
            .fillMaxSize()
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "AUTOMATION ENGINE",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Local Trigger-Action Smart System Rules",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = { showAddDialog = true }) {
                Icon(
                    imageVector = Icons.Default.AddCircle,
                    contentDescription = "Add Rule",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        if (automations.isEmpty()) {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Autorenew,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No automation rules defined.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Create automated workflows like low battery spoken alerts, app auto-launchers, and scheduled routines.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(automations) { rule ->
                    AutomationRuleCard(
                        rule = rule,
                        onToggle = { isChecked -> viewModel.toggleAutomationRule(rule.id, isChecked) },
                        onDelete = { viewModel.deleteAutomationRule(rule.id) }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Automation Rule") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = ruleName,
                        onValueChange = { ruleName = it },
                        label = { Text("Rule Name") },
                        placeholder = { Text("e.g. Low Battery Alert") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = triggerType,
                        onValueChange = { triggerType = it },
                        label = { Text("Trigger Event") },
                        placeholder = { Text("BATTERY_LOW, TIME, NOTIFICATION") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = actionValue,
                        onValueChange = { actionValue = it },
                        label = { Text("Action Output / Message") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (ruleName.isNotBlank()) {
                            viewModel.addAutomationRule(
                                name = ruleName,
                                triggerType = triggerType,
                                triggerValue = triggerValue,
                                actionType = actionType,
                                actionValue = actionValue
                            )
                            showAddDialog = false
                            ruleName = ""
                        }
                    }
                ) {
                    Text("Save Rule")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun AutomationRuleCard(
    rule: AutomationRuleEntity,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = rule.ruleName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "TRIGGER: ${rule.triggerType} (${rule.triggerValue})",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 11.sp
                )
                Text(
                    text = "ACTION: ${rule.actionType} -> ${rule.actionValue}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(
                    checked = rule.isEnabled,
                    onCheckedChange = onToggle
                )
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Rule",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
