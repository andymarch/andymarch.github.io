package uk.co.andymarch.blogposter.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.outlined.FormatBold
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

/** Wraps the current selection (or inserts an empty pair at the cursor) with [prefix]/[suffix]. */
private fun wrap(value: TextFieldValue, prefix: String, suffix: String = prefix): TextFieldValue {
    val start = value.selection.min
    val end = value.selection.max
    val selected = value.text.substring(start, end)
    val newText = value.text.substring(0, start) + prefix + selected + suffix + value.text.substring(end)
    val cursor = if (selected.isEmpty()) start + prefix.length else start + prefix.length + selected.length + suffix.length
    return TextFieldValue(newText, selection = TextRange(cursor))
}

private fun insertLinePrefix(value: TextFieldValue, linePrefix: String): TextFieldValue {
    val lineStart = value.text.lastIndexOf('\n', (value.selection.min - 1).coerceAtLeast(0)).let { if (it == -1) 0 else it + 1 }
    val newText = value.text.substring(0, lineStart) + linePrefix + value.text.substring(lineStart)
    return TextFieldValue(newText, selection = TextRange(value.selection.min + linePrefix.length))
}

@Composable
fun MarkdownToolbar(
    bodyValue: TextFieldValue,
    onBodyValueChanged: (TextFieldValue) -> Unit,
    onPickFromGallery: () -> Unit,
    onCapturePhoto: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier) {
        IconButton(onClick = { onBodyValueChanged(wrap(bodyValue, "**")) }) {
            Icon(Icons.Outlined.FormatBold, contentDescription = "Bold")
        }
        IconButton(onClick = { onBodyValueChanged(wrap(bodyValue, "*")) }) {
            Icon(Icons.Filled.FormatItalic, contentDescription = "Italic")
        }
        IconButton(onClick = { onBodyValueChanged(insertLinePrefix(bodyValue, "> ")) }) {
            Icon(Icons.Filled.FormatQuote, contentDescription = "Quote")
        }
        IconButton(onClick = { onBodyValueChanged(wrap(bodyValue, "[", "](url)")) }) {
            Icon(Icons.Filled.Link, contentDescription = "Link")
        }
        IconButton(onClick = onPickFromGallery) {
            Icon(Icons.Filled.AddPhotoAlternate, contentDescription = "Add photo from gallery")
        }
        IconButton(onClick = onCapturePhoto) {
            Icon(Icons.Filled.PhotoCamera, contentDescription = "Take a photo")
        }
    }
}
