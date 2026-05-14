package com.example.finanbuddy.ui.screens.scanner

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.finanbuddy.R
import com.example.finanbuddy.ui.components.AppLoader
import com.example.finanbuddy.ui.theme.FinanBuddyTheme
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.SCANNER_MODE_FULL
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import org.koin.androidx.compose.koinViewModel

@Composable
fun ScannerRoot(
    onNavigateBack: () -> Unit,
    viewModel: ScannerViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ScannerScreen(
        state = state,
        onAction = viewModel::onAction,
        onNavigateBack = onNavigateBack,
    )
}

@Composable
fun ScannerScreen(
    state: ScannerState,
    onAction: (ScannerAction) -> Unit,
    onNavigateBack: () -> Unit,
) {
    val options = GmsDocumentScannerOptions.Builder()
        .setScannerMode(SCANNER_MODE_FULL)
        .setPageLimit(1)
        .setResultFormats(GmsDocumentScannerOptions.RESULT_FORMAT_JPEG)
        .build()

    val scanner = remember { GmsDocumentScanning.getClient(options) }
    val scannerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { result ->
            when (result.resultCode) {
                RESULT_OK -> {
                    val data = GmsDocumentScanningResult.fromActivityResultIntent(result.data)
                    val imagesUris = data?.pages.orEmpty().mapNotNull { it.imageUri }
                    onAction(ScannerAction.OnScanResult(imagesUris))
                }
                RESULT_CANCELED -> {
                    // User pressed back — leave the screen
                    onNavigateBack()
                }
                else -> {
                    // Scanner returned an unexpected error code
                    onAction(ScannerAction.OnScanFailed)
                }
            }
        }
    )
    val activity = LocalActivity.current as ComponentActivity

    // Re-launches the scanner whenever scanTrigger changes (initial launch + ScanAnother).
    LaunchedEffect(state.scanTrigger) {
        if (state.step != ScannerStep.IDLE) return@LaunchedEffect
        scanner.getStartScanIntent(activity)
            .addOnSuccessListener {
                scannerLauncher.launch(IntentSenderRequest.Builder(it).build())
            }
            .addOnFailureListener {
                Toast.makeText(
                    activity,
                    it.message ?: "Failed to start scanner",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        contentAlignment = Alignment.Center,
    ) {
        AnimatedContent(
            targetState = state.step,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "scanner_step",
        ) { step ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                when (step) {
                    ScannerStep.IDLE -> {
                        AppLoader()
                        Text(
                            text = stringResource(R.string.scanner_step_processing),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                        )
                    }

                    ScannerStep.PROCESSING_IMAGE -> {
                        AppLoader()
                        Text(
                            text = stringResource(R.string.scanner_step_processing),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                        )
                    }

                    ScannerStep.SAVING -> {
                        AppLoader()
                        Text(
                            text = stringResource(R.string.scanner_step_saving),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                        )
                    }

                    ScannerStep.SUCCESS -> {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                        Text(
                            text = stringResource(R.string.scanner_success_title),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                        )
                        Text(
                            text = stringResource(R.string.scanner_success_subtitle),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { onAction(ScannerAction.ScanAnother) },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(text = stringResource(R.string.btn_scan_another))
                        }
                    }

                    ScannerStep.ERROR -> {
                        Icon(
                            imageVector = Icons.Outlined.ErrorOutline,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.error,
                        )
                        Text(
                            text = state.errorMessage
                                ?: stringResource(R.string.scanner_error_generic),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { onAction(ScannerAction.Retry) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                            ),
                        ) {
                            Text(text = stringResource(R.string.btn_retry))
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewProcessing() {
    FinanBuddyTheme {
        ScannerScreen(
            state = ScannerState(step = ScannerStep.PROCESSING_IMAGE),
            onAction = {},
            onNavigateBack = {},
        )
    }
}

@Preview
@Composable
private fun PreviewSuccess() {
    FinanBuddyTheme {
        ScannerScreen(
            state = ScannerState(step = ScannerStep.SUCCESS),
            onAction = {},
            onNavigateBack = {},
        )
    }
}

@Preview
@Composable
private fun PreviewError() {
    FinanBuddyTheme {
        ScannerScreen(
            state = ScannerState(
                step = ScannerStep.ERROR,
                errorMessage = "Could not load the image. Please try again."
            ),
            onAction = {},
            onNavigateBack = {},
        )
    }
}