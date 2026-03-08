package com.raymond.beccasinventory.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Column
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.clip
import com.raymond.beccasinventory.ui.navigation.BottomRoute
import com.raymond.beccasinventory.ui.navigation.MainBottomNavigation
import com.raymond.beccasinventory.ui.screens.InventoryItemsScreen
import androidx.activity.compose.BackHandler
import kotlinx.coroutines.launch
import androidx.hilt.navigation.compose.hiltViewModel
import com.raymond.beccasinventory.models.InventorySortType
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.automirrored.filled.List
import com.raymond.beccasinventory.models.SortDirection
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.ArrowDropDown

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainApp(
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val coroutineScope = rememberCoroutineScope()
    val inventoryViewModel: com.raymond.beccasinventory.ui.screens.InventoryItemsViewModel = hiltViewModel()

    val currentRoute = when (pagerState.currentPage) {
        0 -> BottomRoute.InventoryItems
        1 -> BottomRoute.Settings
        else -> BottomRoute.InventoryItems
    }

    // Controls whether the Add InventoryItem sheet is open
    var showAddInventoryItem by remember { mutableStateOf(false) }

    var searchQuery by remember { mutableStateOf("") }
    var sortType by remember { mutableStateOf(InventorySortType.NAME) }
    var sortDirection by remember { mutableStateOf(SortDirection.ASCENDING) }
    
    // Multi-Select States hoisted to parent
    var selectedInventoryItemIds by remember { mutableStateOf(setOf<Long>()) }
    
    // Show confirmation dialog state
    var showDeleteConfirm by remember { mutableStateOf(false) }

    // Lock/Unlock state observed from DataStore
    val isLocked by inventoryViewModel.isLocked.collectAsState()
    val isUnlocked = !isLocked

    val inMultiSelectMode = selectedInventoryItemIds.isNotEmpty()
    BackHandler(enabled = inMultiSelectMode) {
        selectedInventoryItemIds = emptySet()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { 
                    AnimatedContent(
                        targetState = selectedInventoryItemIds.size,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "TopBarTitleAnimation"
                    ) { selectionCount ->
                        Text(
                            text = if (selectionCount > 0) "$selectionCount Selected" else currentRoute.title,
                            modifier = Modifier.padding(start = 8.dp),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                },
                navigationIcon = {
                    val selectionCount = selectedInventoryItemIds.size
                    if (selectionCount > 0) {
                        IconButton(onClick = { 
                            selectedInventoryItemIds = emptySet()
                        }) {
                            Icon(Icons.Filled.Close, contentDescription = "Cancel Selection", tint = Color.White)
                        }
                    }
                },
                actions = {
                    AnimatedContent(
                        targetState = selectedInventoryItemIds.size,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "TopBarActionAnimation"
                    ) { selectionCount ->
                        if (selectionCount > 0) {
                            IconButton(onClick = { showDeleteConfirm = true }) {
                                Icon(Icons.Filled.Delete, contentDescription = "Delete Selected", tint = Color.White)
                            }
                        } else {
                            if (currentRoute == BottomRoute.InventoryItems) {
                                Row {
                                    IconButton(onClick = { inventoryViewModel.toggleLocked() }) {
                                        Icon(
                                            imageVector = if (isUnlocked) Icons.Filled.LockOpen else Icons.Filled.Lock,
                                            contentDescription = if (isUnlocked) "Lock Quantities" else "Unlock Quantities",
                                            tint = Color.White
                                        )
                                    }
                                    IconButton(onClick = { showAddInventoryItem = true }) {
                                        Icon(Icons.Filled.Add, contentDescription = "Add InventoryItem", tint = Color.White)
                                    }
                                }
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        bottomBar = {
            MainBottomNavigation(
                currentRoute = currentRoute,
                onNavigate = { route ->
                    coroutineScope.launch {
                        when (route) {
                            BottomRoute.InventoryItems -> pagerState.animateScrollToPage(0)
                            BottomRoute.Settings -> pagerState.animateScrollToPage(1)
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Static Search Bar placed above the ViewPager
            if (currentRoute != BottomRoute.Settings) {
                val searchHint = "Search by name or category…"
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text(searchHint) },
                    leadingIcon = {
                        Icon(Icons.Filled.Search, contentDescription = "Search")
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, start = 16.dp, end = 16.dp, bottom = 4.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Sort by:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    
                    var showSortMenu by remember { mutableStateOf(false) }
                    Box {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { showSortMenu = true }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (sortType == InventorySortType.NAME) "Name" else "Qty",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Icon(Icons.Filled.ArrowDropDown, contentDescription = null, modifier = Modifier.size(20.dp))
                        }
                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Name") },
                                onClick = {
                                    sortType = InventorySortType.NAME
                                    showSortMenu = false
                                },
                                leadingIcon = { Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = null) }
                            )
                            DropdownMenuItem(
                                text = { Text("Quantity") },
                                onClick = {
                                    sortType = InventorySortType.QUANTITY
                                    showSortMenu = false
                                },
                                leadingIcon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            sortDirection = if (sortDirection == SortDirection.ASCENDING) {
                                SortDirection.DESCENDING
                            } else {
                                SortDirection.ASCENDING
                            }
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (sortDirection == SortDirection.ASCENDING) Icons.Filled.ArrowUpward else Icons.Filled.ArrowDownward,
                            contentDescription = "Toggle Sort Direction",
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            HorizontalPager(
                state = pagerState,
                userScrollEnabled = false,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> InventoryItemsScreen(
                        searchQuery = searchQuery,
                        selectedIds = selectedInventoryItemIds,
                        onToggleSelect = { id ->
                            selectedInventoryItemIds = if (selectedInventoryItemIds.contains(id)) {
                                selectedInventoryItemIds - id
                            } else {
                                selectedInventoryItemIds + id
                            }
                        },
                        showAddSheet = showAddInventoryItem,
                        onAddSheetDismiss = { showAddInventoryItem = false },
                        showDeleteConfirm = currentRoute == BottomRoute.InventoryItems && showDeleteConfirm,
                        onDeleteConfirmDismiss = { showDeleteConfirm = false },
                        onDeleteConfirmComplete = {
                            selectedInventoryItemIds = emptySet()
                            showDeleteConfirm = false
                        },
                        isUnlocked = isUnlocked,
                        sortType = sortType,
                        sortDirection = sortDirection,
                        viewModel = inventoryViewModel
                    )
                    1 -> com.raymond.beccasinventory.ui.screens.SettingsScreen()
                }
            }
        }
    }
}
