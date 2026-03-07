package com.raymond.beccasinventory.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import com.raymond.beccasinventory.ui.components.rememberImagePickerLauncher
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.raymond.beccasinventory.models.InventoryItem
import com.raymond.beccasinventory.models.Tag

/**
 * A bottom sheet pre-populated with an existing [InventoryItem] for editing.
 * Slides in, and fades out when Save is tapped.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditInventoryItemSheet(
    inventoryItem: InventoryItem?,
    allInventoryItems: List<InventoryItem> = emptyList(),
    onSave: (name: String, quantity: Int, imageUri: String?, tags: List<Tag>) -> Unit,
    onDismiss: () -> Unit
) {
    val visible = inventoryItem != null
    var isSaving by remember { mutableStateOf(false) }
    LaunchedEffect(visible) { if (!visible) isSaving = false }

    // Scrim
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(initialOffsetY = { it }, animationSpec = tween(400)),
        exit = if (isSaving) fadeOut(tween(300))
               else slideOutVertically(targetOffsetY = { it }, animationSpec = tween(350))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.45f))
                .clickable(onClick = onDismiss)
        )
    }

    // Sheet
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(initialOffsetY = { it }, animationSpec = tween(400)),
        exit = if (isSaving) fadeOut(tween(300))
               else slideOutVertically(targetOffsetY = { it }, animationSpec = tween(350))
    ) {
        if (inventoryItem != null) {
            EditInventoryItemContent(
                initial = inventoryItem,
                allInventoryItems = allInventoryItems,
                onSave = { name, desc, uri, tags ->
                    isSaving = true
                    onSave(name, desc, uri, tags)
                },
                onDismiss = onDismiss
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditInventoryItemContent(
    initial: InventoryItem,
    allInventoryItems: List<InventoryItem>,
    onSave: (String, Int, String?, List<Tag>) -> Unit,
    onDismiss: () -> Unit
) {
    // Pre-fill fields from existing entry
    var name by remember(initial.id) { mutableStateOf(initial.name) }
    var quantity by remember(initial.id) { mutableStateOf(initial.quantity.toString()) }
    var imageUri by remember { mutableStateOf(if (initial.imageUri != null) Uri.parse(initial.imageUri) else null) }
    var currentTag by remember(initial.id) { mutableStateOf("") }
    val tags = remember { mutableStateListOf<Tag>() }.also { it.addAll(initial.tags) }
    
    val launchImagePicker = rememberImagePickerLauncher(onImageSelected = { uri -> imageUri = uri })

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            tonalElevation = 8.dp,
            shadowElevation = 8.dp
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {

                // ── Toolbar ──────────────────────────────────────────
                TopAppBar(
                    title = { Text("Edit InventoryItem") },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Filled.Close, contentDescription = "Close")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )

                // ── Body ─────────────────────────────────────────────
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        // Image picker / preview
                        Box(
                            modifier = Modifier
                                .size(width = 110.dp, height = 110.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .border(
                                    width = 2.dp,
                                    color = MaterialTheme.colorScheme.outlineVariant,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { launchImagePicker() },
                            contentAlignment = Alignment.Center
                        ) {
                            if (imageUri != null) {
                                AsyncImage(
                                    model = imageUri,
                                    contentDescription = "Selected image",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Add,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(36.dp)
                                    )
                                    Spacer(modifier = Modifier.height(32.dp))
                                    Text(
                                        text = "Tap to add",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        // Fields
                        Column(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it },
                                label = { Text("InventoryItem Name") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = quantity,
                                onValueChange = { newValue -> if (newValue.all { it.isDigit() }) quantity = newValue },
                                label = { Text("Quantity") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Tags
                    Text("Tags", style = MaterialTheme.typography.titleSmall)
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    val existingTags: List<String> = remember(allInventoryItems, tags.size) {
                        val allExistingTags = mutableListOf<String>()
                        for (c in allInventoryItems) {
                            for (t in c.tags) {
                                allExistingTags.add(t.name)
                            }
                        }
                        allExistingTags
                            .distinct()
                            .filter { existingName -> tags.none { it.name.equals(existingName, ignoreCase = true) } }
                            .sorted()
                    }
                    
                    val suggestions = existingTags.filter { it.contains(currentTag, ignoreCase = true) }
                    var expanded by remember { mutableStateOf(false) }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ExposedDropdownMenuBox(
                            expanded = expanded && suggestions.isNotEmpty(),
                            onExpandedChange = { expanded = it },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = currentTag,
                                onValueChange = { 
                                    currentTag = it
                                    expanded = true 
                                },
                                label = { Text("New Tag") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                singleLine = true
                            )
                            
                            ExposedDropdownMenu(
                                expanded = expanded && suggestions.isNotEmpty(),
                                onDismissRequest = { expanded = false }
                            ) {
                                suggestions.forEach { suggestion ->
                                    DropdownMenuItem(
                                        text = { Text(suggestion) },
                                        onClick = {
                                            tags.add(Tag(name = suggestion))
                                            currentTag = ""
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(onClick = {
                            if (currentTag.isNotBlank()) {
                                val newTagName = currentTag.trim()
                                if (tags.none { it.name.equals(newTagName, ignoreCase = true) }) {
                                    tags.add(Tag(name = newTagName))
                                }
                                currentTag = ""
                                expanded = false
                            }
                        }) {
                            Icon(Icons.Filled.Add, contentDescription = "Add Tag")
                        }
                    }

                    if (tags.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(tags) { tag ->
                                AssistChip(
                                    onClick = { tags.remove(tag) },
                                    label = { Text(tag.name) },
                                    trailingIcon = {
                                        Icon(
                                            Icons.Filled.Close,
                                            contentDescription = "Remove",
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Save button
                    Button(
                        onClick = { onSave(name, quantity.toIntOrNull() ?: 0, imageUri?.toString(), tags.toList()) },
                        enabled = name.isNotBlank(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Save Changes", style = MaterialTheme.typography.titleMedium)
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}


