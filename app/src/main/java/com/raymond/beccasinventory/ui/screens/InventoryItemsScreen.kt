package com.raymond.beccasinventory.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.combinedClickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Checkbox
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.raymond.beccasinventory.models.InventoryItem
import com.raymond.beccasinventory.models.Tag
import com.raymond.beccasinventory.models.InventorySortType
import com.raymond.beccasinventory.models.SortDirection
import kotlinx.collections.immutable.ImmutableList
import kotlin.math.roundToInt
import kotlinx.coroutines.launch
import androidx.hilt.navigation.compose.hiltViewModel
import com.raymond.beccasinventory.ui.components.DeleteConfirmationDialog

// Total width of the two revealed action buttons
private val ACTION_BUTTON_WIDTH = 140.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryItemsScreen(
    modifier: Modifier = Modifier,
    searchQuery: String = "",
    selectedIds: Set<Long> = emptySet(),
    onToggleSelect: (Long) -> Unit = {},
    showAddSheet: Boolean = false,
    onAddSheetDismiss: () -> Unit = {},
    showDeleteConfirm: Boolean = false,
    onDeleteConfirmDismiss: () -> Unit = {},
    onDeleteConfirmComplete: () -> Unit = {},
    isUnlocked: Boolean = false,
    sortType: InventorySortType = InventorySortType.NAME,
    sortDirection: SortDirection = SortDirection.ASCENDING,
    viewModel: InventoryItemsViewModel = hiltViewModel()
) {
    val inventoryItems by viewModel.inventoryItems.collectAsState()
    var detailInventoryItem by remember { mutableStateOf<InventoryItem?>(null) }
    var editInventoryItem by remember { mutableStateOf<InventoryItem?>(null) }
    var inventoryItemToDelete by remember { mutableStateOf<InventoryItem?>(null) }

    // Stable callbacks to prevent unnecessary recomposition
    val stableOnToggleSelect = remember { { id: Long -> onToggleSelect(id) } }
    val stableOnDelete = remember { { item: InventoryItem -> inventoryItemToDelete = item } }
    val stableOnEdit = remember { { item: InventoryItem -> editInventoryItem = item } }
    val stableOnQuantityChange = remember { { item: InventoryItem, newQty: Int -> viewModel.updateQuantity(item, newQty) } }
    
    // Tap handler depends on selection mode
    val stableOnTap = remember(selectedIds.isEmpty()) { 
        { item: InventoryItem ->
            if (selectedIds.isNotEmpty()) {
                onToggleSelect(item.id)
            } else {
                detailInventoryItem = item
            }
        }
    }
    
    // Filter and sort items
    val filteredInventoryItems = remember(inventoryItems, searchQuery, sortType, sortDirection) {
        val filtered = if (searchQuery.isBlank()) {
            inventoryItems
        } else {
            val query = searchQuery.lowercase()
            inventoryItems.filter { item ->
                item.name.lowercase().contains(query) || 
                item.tags.any { it.name.lowercase().contains(query) }
            }
        }
        
        when (sortType) {
            InventorySortType.NAME -> {
                if (sortDirection == SortDirection.ASCENDING) filtered.sortedBy { it.name.lowercase() }
                else filtered.sortedByDescending { it.name.lowercase() }
            }
            InventorySortType.QUANTITY -> {
                if (sortDirection == SortDirection.ASCENDING) filtered.sortedBy { it.quantity }
                else filtered.sortedByDescending { it.quantity }
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── List ──────────────────────────────────────────────────
            if (filteredInventoryItems.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        if (searchQuery.isBlank()) "No inventoryItems added yet."
                        else "No results for \"$searchQuery\""
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(top = 16.dp)
                ) {
                    items(filteredInventoryItems, key = { it.id }) { inventoryItem ->
                        val isSelected = selectedIds.contains(inventoryItem.id)
                        val inSelectionMode = selectedIds.isNotEmpty()

                        SwipeableInventoryItemItem(
                            inventoryItem = inventoryItem,
                            isSelected = isSelected,
                            inSelectionMode = inSelectionMode,
                            isUnlocked = isUnlocked,
                            onToggleSelect = stableOnToggleSelect,
                            onTap = stableOnTap,
                            onDelete = stableOnDelete,
                            onEdit = stableOnEdit,
                            onQuantityChange = stableOnQuantityChange
                        )
                    }
                }
            }
        }

        // Add sheet
        AddInventoryItemSheet(
            visible = showAddSheet,
            allInventoryItems = inventoryItems,
            onSave = { name, desc, uri, tags ->
                viewModel.addInventoryItem(name, desc, uri, tags)
                onAddSheetDismiss()
            },
            onDismiss = onAddSheetDismiss
        )

        // Detail sheet
        InventoryItemDetailSheet(
            inventoryItem = detailInventoryItem,
            onDismiss = { detailInventoryItem = null }
        )

        // Edit sheet
        EditInventoryItemSheet(
            inventoryItem = editInventoryItem,
            allInventoryItems = inventoryItems,
            onSave = { name, desc, uri, tags ->
                editInventoryItem?.let { viewModel.updateInventoryItem(it, name, desc, uri, tags) }
                editInventoryItem = null
            },
            onDismiss = { editInventoryItem = null }
        )

        // Delete Confirmation Dialog for batch deletions
        if (showDeleteConfirm) {
            DeleteConfirmationDialog(
                itemCount = selectedIds.size,
                onConfirm = {
                    val inventoryItemsToDelete = inventoryItems.filter { selectedIds.contains(it.id) }
                    viewModel.deleteMultipleInventoryItems(inventoryItemsToDelete)
                    onDeleteConfirmComplete()
                },
                onDismiss = onDeleteConfirmDismiss
            )
        }

        // Delete Confirmation Dialog for single swipe deletion
        if (inventoryItemToDelete != null) {
            DeleteConfirmationDialog(
                itemCount = 1,
                onConfirm = {
                    inventoryItemToDelete?.let { viewModel.deleteInventoryItem(it) }
                    inventoryItemToDelete = null
                },
                onDismiss = { inventoryItemToDelete = null }
            )
        }
    }
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun SwipeableInventoryItemItem(
    inventoryItem: InventoryItem,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    inSelectionMode: Boolean = false,
    isUnlocked: Boolean = false,
    onToggleSelect: (Long) -> Unit = {},
    onTap: (InventoryItem) -> Unit,
    onDelete: (InventoryItem) -> Unit,
    onEdit: (InventoryItem) -> Unit,
    onQuantityChange: (InventoryItem, Int) -> Unit = { _, _ -> }
) {
    val scope = rememberCoroutineScope()
    // Positive offset = card moves RIGHT = buttons revealed on LEFT
    val offsetX = remember { Animatable(0f) }
    // Measure the width of the action buttons so we know how far to drag
    var revealWidthPx by remember { mutableStateOf(0f) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
    ) {
        // ── Action buttons on the LEFT (revealed by swiping right) ───
        Row(
            modifier = Modifier
                .matchParentSize()
                .background(MaterialTheme.colorScheme.surfaceVariant),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Edit — leftmost
            Box(
                modifier = Modifier
                    .width(70.dp)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = {
                    scope.launch { offsetX.animateTo(0f, tween(250)) }
                    onEdit(inventoryItem)
                }) {
                    Icon(
                        Icons.Filled.Edit,
                        contentDescription = "Edit",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            // Delete — to the right of Edit
            Box(
                modifier = Modifier
                    .width(70.dp)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.error),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = {
                    scope.launch { offsetX.animateTo(0f, tween(250)) }
                    onDelete(inventoryItem)
                }) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = "Delete",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .clip(RoundedCornerShape(16.dp))
                .combinedClickable(
                    onClick = {
                        if (offsetX.value == 0f) {
                            onTap(inventoryItem)
                        } else {
                            scope.launch { offsetX.animateTo(0f, tween(250)) }
                        }
                    },
                    onLongClick = {
                        if (offsetX.value == 0f && !inSelectionMode) {
                            onToggleSelect(inventoryItem.id)
                        }
                    }
                )
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            scope.launch {
                                if (offsetX.value > revealWidthPx / 2f)
                                    offsetX.animateTo(revealWidthPx, tween(250))
                                else
                                    offsetX.animateTo(0f, tween(250))
                            }
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            scope.launch {
                                offsetX.snapTo(
                                    (offsetX.value + dragAmount).coerceIn(0f, revealWidthPx)
                                )
                            }
                        }
                    )
                },
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            // Capture revealWidthPx once on layout
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .pointerInput(Unit) { revealWidthPx = ACTION_BUTTON_WIDTH.toPx() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Checkbox for Multi-Select Mode
                AnimatedVisibility(
                    visible = inSelectionMode,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut()
                ) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = { onToggleSelect(inventoryItem.id) },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }

                // Isolated static content (image, name, tags) — will be skipped if quantity-only changes
                ItemStaticContent(
                    name = inventoryItem.name,
                    imageUri = inventoryItem.imageUri,
                    tags = inventoryItem.tags,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Isolated quantity row — only this part recomposes on tap
                QuantityRow(
                    id = inventoryItem.id,
                    initialQuantity = inventoryItem.quantity,
                    isUnlocked = isUnlocked,
                    onQuantityChange = { newQty -> onQuantityChange(inventoryItem, newQty) }
                )

                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}

@Composable
fun ItemStaticContent(
    name: String,
    imageUri: String?,
    tags: ImmutableList<Tag>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Image thumbnail
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            if (imageUri != null) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Name + Tags
        Column(modifier = Modifier.weight(1f)) {
            Text(text = name, style = MaterialTheme.typography.titleMedium)
            if (tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                @OptIn(ExperimentalLayoutApi::class)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    tags.forEach { tag: Tag ->
                        ReadOnlyTag(tag.name)
                    }
                }
            }
        }
    }
}

@Composable
fun QuantityRow(
    id: Long,
    initialQuantity: Int,
    isUnlocked: Boolean,
    onQuantityChange: (Int) -> Unit
) {
    var localQuantity by remember(id) { mutableStateOf(initialQuantity) }
    
    // Sycn from external (e.g. DB emission)
    androidx.compose.runtime.LaunchedEffect(initialQuantity) {
        localQuantity = initialQuantity
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
            onClick = {
                if (localQuantity > 0) {
                    localQuantity--
                    onQuantityChange(localQuantity)
                }
            },
            enabled = isUnlocked,
            modifier = Modifier
                .size(32.dp)
                .background(
                    if (isUnlocked) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    RoundedCornerShape(8.dp)
                )
        ) {
            Text("-", style = MaterialTheme.typography.titleMedium, color = if (isUnlocked) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
        }
        Text(
            text = "$localQuantity",
            style = MaterialTheme.typography.bodyLarge,
            color = if (isUnlocked) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.width(40.dp)
        )
        IconButton(
            onClick = {
                localQuantity++
                onQuantityChange(localQuantity)
            },
            enabled = isUnlocked,
            modifier = Modifier
                .size(32.dp)
                .background(
                    if (isUnlocked) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    RoundedCornerShape(8.dp)
                )
        ) {
            Icon(
                Icons.Filled.Add, 
                contentDescription = "Increase", 
                modifier = Modifier.size(16.dp),
                tint = if (isUnlocked) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun ReadOnlyTag(name: String) {
    Box(
        modifier = Modifier
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}


