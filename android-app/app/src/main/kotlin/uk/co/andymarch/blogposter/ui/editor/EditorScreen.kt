package uk.co.andymarch.blogposter.ui.editor

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.io.File
import java.time.LocalDate
import uk.co.andymarch.blogposter.data.draft.DraftStatus
import uk.co.andymarch.blogposter.ui.components.DatePickerField
import uk.co.andymarch.blogposter.ui.components.ImageThumbnailRow
import uk.co.andymarch.blogposter.ui.components.MarkdownToolbar
import uk.co.andymarch.blogposter.ui.components.TagInput
import uk.co.andymarch.blogposter.ui.components.TypeSelector

@Composable
fun EditorScreen(viewModel: EditorViewModel, onPublished: () -> Unit) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    DisposableEffect(Unit) {
        onDispose { viewModel.discardIfBlank() }
    }

    LaunchedEffect(state.status) {
        if (state.status == DraftStatus.PUBLISHED) onPublished()
    }

    var bodyFieldValue by remember(state.draftId) { mutableStateOf(TextFieldValue(state.body)) }
    LaunchedEffect(state.body) {
        if (state.body != bodyFieldValue.text) {
            bodyFieldValue = TextFieldValue(state.body, selection = TextRange(state.body.length))
        }
    }

    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { uris ->
        if (uris.isNotEmpty()) viewModel.pickImages(uris)
    }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        val uri = pendingCameraUri
        if (success && uri != null) viewModel.pickImages(listOf(uri))
        pendingCameraUri = null
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = viewModel::publish,
                text = {
                    if (state.status == DraftStatus.PUBLISHING) {
                        CircularProgressIndicator(modifier = Modifier.height(20.dp), color = MaterialTheme.colorScheme.onPrimaryContainer)
                    } else {
                        Text("Publish")
                    }
                },
                icon = { if (state.status != DraftStatus.PUBLISHING) Icon(Icons.Filled.Publish, contentDescription = null) },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (!state.isRepoConfigured) {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                    Text(
                        "Set up your repository in Settings before publishing.",
                        modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                    )
                }
            }
            if (state.status == DraftStatus.FAILED && state.lastError != null) {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                    Text(
                        "Publish failed: ${state.lastError}",
                        modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                    )
                }
            }

            OutlinedTextField(
                value = state.title,
                onValueChange = viewModel::updateTitle,
                label = { Text("Title") },
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                modifier = Modifier.fillMaxWidth(),
            )

            DatePickerField(date = LocalDate.parse(state.dateIso), onDateChanged = viewModel::updateDate)

            TypeSelector(
                selected = state.postType,
                customType = state.customType,
                onSelect = viewModel::selectPostType,
                onCustomTypeChanged = viewModel::updateCustomType,
            )

            TagInput(tags = state.tags, onTagsChanged = viewModel::updateTags)

            MarkdownToolbar(
                bodyValue = bodyFieldValue,
                onBodyValueChanged = { value ->
                    bodyFieldValue = value
                    viewModel.updateBody(value.text)
                },
                onPickFromGallery = {
                    galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                },
                onCapturePhoto = {
                    val uri = createCameraCaptureUri(context)
                    pendingCameraUri = uri
                    cameraLauncher.launch(uri)
                },
            )

            ImageThumbnailRow(
                images = state.images,
                onRemove = viewModel::removeImage,
                onRetry = viewModel::retryUpload,
            )

            OutlinedTextField(
                value = bodyFieldValue,
                onValueChange = { value ->
                    bodyFieldValue = value
                    viewModel.updateBody(value.text)
                },
                label = { Text("Post") },
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp),
            )
        }
    }
}

private fun createCameraCaptureUri(context: Context): Uri {
    val dir = File(context.cacheDir, "camera").apply { mkdirs() }
    val file = File(dir, "capture-${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
}
