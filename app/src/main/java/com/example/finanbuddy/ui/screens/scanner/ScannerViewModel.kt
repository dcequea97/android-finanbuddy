package com.example.finanbuddy.ui.screens.scanner

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import androidx.core.graphics.createBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.finanbuddy.R
import com.example.finanbuddy.domain.data.expense.CategoryModel
import com.example.finanbuddy.domain.data.onError
import com.example.finanbuddy.domain.data.onSuccess
import com.example.finanbuddy.domain.data.transaction.TransactionAiModel
import com.example.finanbuddy.domain.data.transaction.toDomain
import com.example.finanbuddy.domain.repository.CategoriesRepository
import com.example.finanbuddy.domain.repository.TransactionRepository
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class ScannerViewModel(
    private val application: Application,
    private val categoriesRepository: CategoriesRepository,
    private val transactionRepository: TransactionRepository,
) : AndroidViewModel(application) {

    private var hasLoadedInitialData = false

    private val _state = MutableStateFlow(ScannerState())
    val state = _state
        .onStart {
            if (!hasLoadedInitialData) {
                loadInitialData()
                hasLoadedInitialData = true
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = ScannerState()
        )

    fun onAction(action: ScannerAction) {
        when (action) {
            is ScannerAction.OnScanResult -> {
                val uri = action.images.firstOrNull() ?: return
                handleUriImage(uri)
            }
            is ScannerAction.OnScanFailed -> {
                _state.update {
                    it.copy(
                        step = ScannerStep.ERROR,
                        errorMessage = application.getString(R.string.scanner_error_scan_failed),
                    )
                }
            }
            is ScannerAction.Retry -> {
                val uri = _state.value.lastUri ?: return
                handleUriImage(uri)
            }
            is ScannerAction.ScanAnother -> {
                _state.update {
                    it.copy(
                        step = ScannerStep.IDLE,
                        errorMessage = null,
                        lastUri = null,
                        scanTrigger = it.scanTrigger + 1,
                    )
                }
            }
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            categoriesRepository.getExpensesCategoriesFlow()
                .collect { categories ->
                    _state.update { it.copy(categories = categories) }
                }
        }
    }

    private fun handleUriImage(uri: Uri) {
        _state.update {
            it.copy(
                step = ScannerStep.PROCESSING_IMAGE,
                errorMessage = null,
                lastUri = uri,
            )
        }
        viewModelScope.launch {
            val bitmap = loadBitmapFromUri(uri)
            if (bitmap == null) {
                _state.update {
                    it.copy(
                        step = ScannerStep.ERROR,
                        errorMessage = application.getString(R.string.scanner_error_load_image),
                    )
                }
                return@launch
            }
            sendPrompt(bitmap)
        }
    }

    private val generativeModel = Firebase.ai.generativeModel(
        modelName = "gemini-flash-latest",
    )

    private suspend fun sendPrompt(bitmap: Bitmap) {
        try {
            val response = generativeModel.generateContent(
                content {
                    image(bitmap)
                    text(getPrompt(_state.value.categories))
                }
            )
            val outputContent = response.text
            if (outputContent.isNullOrBlank()) {
                _state.update {
                    it.copy(
                        step = ScannerStep.ERROR,
                        errorMessage = application.getString(R.string.scanner_error_generic),
                    )
                }
                return
            }
            val transaction = Json.decodeFromString<TransactionAiModel>(outputContent.trim())
            saveTransaction(transaction)
        } catch (e: Exception) {
            _state.update {
                it.copy(
                    step = ScannerStep.ERROR,
                    errorMessage = e.localizedMessage
                        ?: application.getString(R.string.scanner_error_generic),
                )
            }
        }
    }

    private suspend fun saveTransaction(transaction: TransactionAiModel) {
        _state.update { it.copy(step = ScannerStep.SAVING) }
        transactionRepository.saveTransaction(transaction.toDomain())
            .onSuccess {
                _state.update { it.copy(step = ScannerStep.SUCCESS, errorMessage = null) }
            }
            .onError { message ->
                _state.update {
                    it.copy(
                        step = ScannerStep.ERROR,
                        errorMessage = message.ifBlank {
                            application.getString(R.string.scanner_error_generic)
                        },
                    )
                }
            }
    }

    private suspend fun loadBitmapFromUri(uri: Uri): Bitmap? = withContext(Dispatchers.IO) {
        // Try as a regular image first (JPEG, PNG, etc.)
        try {
            application.contentResolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream)
            }?.let { return@withContext it }
        } catch (_: Exception) { /* Fall through to PDF rendering */ }

        // Fallback: render first page of a PDF
        try {
            application.contentResolver.openFileDescriptor(uri, "r")?.use { descriptor ->
                PdfRenderer(descriptor).use { renderer ->
                    if (renderer.pageCount <= 0) return@withContext null
                    renderer.openPage(0).use { page ->
                        val bitmap = createBitmap(
                            page.width.coerceAtLeast(1),
                            page.height.coerceAtLeast(1),
                        )
                        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                        return@withContext bitmap
                    }
                }
            }
        } catch (_: Exception) {
            return@withContext null
        }

        null
    }

    private fun getPrompt(categories: List<CategoryModel>): String {
        val categoryNames = categories.joinToString(", ") { it.name }
        return """
            Extract the information from the attached receipt image and return it strictly in JSON format.
            Do not include any conversational text, markdown fences, or extra characters.
            Use the following structure:
            date: Format as yyyy-MM-dd'T'HH:mm:ss.SSS'Z'. Use today's date if not visible.
            amount: The total numerical value of the transaction (positive number).
            category: Act as an expert accountant. Analyze the notes, items, and amount to classify the expense.
                Select the EXACT value ONLY from this list: [${categoryNames}].
                Selection Logic:
                Supermarket/Groceries: For raw food ingredients, pantry staples, and cleaning supplies (e.g., rice, meat, soap), select the exact category from the list that represents groceries or supermarket.
                Snacks/Cravings: For light treats, convenience store items, chips, sodas, bakery goods, or small cravings, select the exact category from the list that represents snacks. Do NOT use the dining out category for these.
                Dining Out: For fully prepared meals, fast food combos, restaurant dining, or food delivery, select the exact category from the list that represents eating out. Do NOT output "Comida Afuera" or any other name unless it explicitly appears in the list provided.
                If the context is ambiguous, use the amount as a hint (e.g., very small amounts are likely Snacks, larger amounts are likely Dining Out or Groceries).
                Return ONLY the exact category name from the list [${categoryNames}], with no extra text or punctuation.
            business: The name of the business/store where the purchase was made. Use "Unknown" if not visible.
            notes: A list of objects, each with "product" (item name) and "price" (numeric price).
            Output Example:
            {"date":"2026-05-13T00:00:00.000Z","amount":35460,"category":"🛒Mercado","business":"DollarCity","notes":[{"product":"Example Item","price":1000}]}
        """.trimIndent()
    }
}