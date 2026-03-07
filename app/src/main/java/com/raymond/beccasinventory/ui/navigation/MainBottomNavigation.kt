package com.raymond.beccasinventory.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

enum class BottomRoute(
    val title: String
) {
    InventoryItems("InventoryItems"),
    Settings("Settings")
}

@Composable
fun MainBottomNavigation(
    currentRoute: BottomRoute,
    onNavigate: (BottomRoute) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
    ) {
        NavigationBarItem(
            selected = currentRoute == BottomRoute.InventoryItems,
            onClick = { onNavigate(BottomRoute.InventoryItems) },
            icon = {
                Icon(
                    imageVector = if (currentRoute == BottomRoute.InventoryItems) Icons.Filled.List else Icons.Outlined.List,
                    contentDescription = "InventoryItems"
                )
            },
            label = { Text(BottomRoute.InventoryItems.title) }
        )
        NavigationBarItem(
            selected = currentRoute == BottomRoute.Settings,
            onClick = { onNavigate(BottomRoute.Settings) },
            icon = {
                Icon(
                    imageVector = if (currentRoute == BottomRoute.Settings) Icons.Filled.Settings else Icons.Outlined.Settings,
                    contentDescription = "Settings"
                )
            },
            label = { Text(BottomRoute.Settings.title) }
        )
    }
}


