package com.raymond.beccasinventory.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raymond.beccasinventory.contracts.repository.PreferencesRepository
import com.raymond.beccasinventory.contracts.usecase.InventoryItemUseCase
import com.raymond.beccasinventory.models.InventoryItem
import com.raymond.beccasinventory.models.Tag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.collections.immutable.toImmutableList
import javax.inject.Inject

@HiltViewModel
class InventoryItemsViewModel @Inject constructor(
    private val inventoryItemUseCase: InventoryItemUseCase,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    val isLocked: StateFlow<Boolean> = preferencesRepository.isLocked
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    val inventoryItems: StateFlow<List<InventoryItem>> = inventoryItemUseCase.observeInventoryItems()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addInventoryItem(name: String, quantity: Int, imageUri: String?, tags: List<Tag>) {
        viewModelScope.launch {
            inventoryItemUseCase.saveInventoryItem(
                InventoryItem(
                    name = name,
                    quantity = quantity,
                    imageUri = imageUri,
                    tags = tags.toImmutableList()
                )
            )
        }
    }

    fun deleteInventoryItem(inventoryItem: InventoryItem) {
        viewModelScope.launch {
            inventoryItemUseCase.deleteInventoryItem(inventoryItem)
        }
    }

    fun deleteMultipleInventoryItems(inventoryItems: List<InventoryItem>) {
        viewModelScope.launch {
            inventoryItemUseCase.deleteMultipleInventoryItems(inventoryItems)
        }
    }

    fun updateInventoryItem(original: InventoryItem, name: String, quantity: Int, imageUri: String?, tags: List<Tag>) {
        viewModelScope.launch {
            inventoryItemUseCase.saveInventoryItem(
                original.copy(
                    name = name,
                    quantity = quantity,
                    imageUri = imageUri,
                    tags = tags.toImmutableList()
                )
            )
        }
    }

    fun updateInventoryItemQuantity(original: InventoryItem, name: String, quantity: Int, imageUri: String?, tags: List<Tag>) {
        viewModelScope.launch {
            inventoryItemUseCase.updateInventoryItemQuantity(
                original.copy(
                    name = name,
                    quantity = quantity,
                    imageUri = imageUri,
                    tags = tags.toImmutableList()
                )
            )
        }
    }

    /** Immediate DB write — UI is handled by local state in the composable,
     *  and @Immutable on InventoryItem lets Compose skip unchanged items */
    fun updateQuantity(item: InventoryItem, newQuantity: Int) {
        viewModelScope.launch {
            inventoryItemUseCase.updateInventoryItemQuantity(item.copy(quantity = newQuantity))
        }
    }

    fun toggleLocked() {
        viewModelScope.launch {
            preferencesRepository.setLocked(!isLocked.value)
        }
    }
}
