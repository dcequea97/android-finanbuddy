package com.example.finanbuddy.ui.screens.scanner

import android.net.Uri
import com.example.finanbuddy.domain.data.expense.CategoryModel

enum class ScannerStep { IDLE, PROCESSING_IMAGE, SAVING, SUCCESS, ERROR }

data class ScannerState(
    val step: ScannerStep = ScannerStep.IDLE,
    val errorMessage: String? = null,
    val categories: List<CategoryModel> = emptyList(),
    /** Kept so Retry can re-process the same image without re-scanning. */
    val lastUri: Uri? = null,
    /** Incremented on ScanAnother to re-trigger the LaunchedEffect in the UI. */
    val scanTrigger: Int = 0,
)