package ru.dikoresearch.warehouse.presentation.utils

import android.os.Bundle

sealed class NavigationEvent{
    data class Navigate(val destination: String, val bundle: Bundle? = null): NavigationEvent()
    data class ShowToast(val message: String): NavigationEvent()
}
