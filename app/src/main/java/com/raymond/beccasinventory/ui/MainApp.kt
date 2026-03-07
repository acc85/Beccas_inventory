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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.raymond.beccasinventory.ui.navigation.BottomRoute
import com.raymond.beccasinventory.ui.navigation.MainBottomNavigation
import com.raymond.beccasinventory.ui.screens.InventoryItemsScreen
import androidx.activity.compose.BackHandler
import kotlinx.coroutines.launch
import androidx.hilt.navigation.compose.hiltViewModel

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
                                androidx.compose.foundation.layout.Row {
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
                        Icon(androidx.compose.material.icons.Icons.Filled.Search, contentDescription = "Search")
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, start = 16.dp, end = 16.dp, bottom = 4.dp)
                )
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
                        viewModel = inventoryViewModel
                    )
                    1 -> com.raymond.beccasinventory.ui.screens.SettingsScreen()
                }
            }
        }
    }
}
