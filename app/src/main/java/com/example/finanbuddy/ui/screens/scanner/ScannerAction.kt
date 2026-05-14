package com.example.finanbuddy.ui.screens.scanner

import android.net.Uri

sealed interface ScannerAction {
    data class OnScanResult(val images: List<Uri>) : ScannerAction
    data object OnScanFailed : ScannerAction
    data object Retry : ScannerAction
    data object ScanAnother : ScannerAction
}