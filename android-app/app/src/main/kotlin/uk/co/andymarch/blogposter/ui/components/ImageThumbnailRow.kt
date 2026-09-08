package uk.co.andymarch.blogposter.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import uk.co.andymarch.blogposter.data.draft.DraftImage
import uk.co.andymarch.blogposter.data.draft.ImageUploadStatus

@Composable
fun ImageThumbnailRow(
    images: List<DraftImage>,
    onRemove: (String) -> Unit,
    onRetry: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (images.isEmpty()) return
    LazyRow(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(images, key = { it.id }) { image ->
            ImageThumbnail(image = image, onRemove = { onRemove(image.id) }, onRetry = { onRetry(image.id) })
        }
    }
}

@Composable
private fun ImageThumbnail(image: DraftImage, onRemove: () -> Unit, onRetry: () -> Unit) {
    Box(modifier = Modifier.size(96.dp)) {
        AsyncImage(
            model = image.localUri,
            contentDescription = image.altText.ifBlank { "Attached photo" },
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(96.dp)
                .clip(RoundedCornerShape(12.dp)),
        )

        when (image.status) {
            ImageUploadStatus.UPLOADING, ImageUploadStatus.PENDING -> Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.35f)),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(modifier = Modifier.size(28.dp), color = Color.White)
            }

            ImageUploadStatus.FAILED -> Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.45f))
                    .clickable(onClick = onRetry),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.ErrorOutline, contentDescription = "Upload failed, tap to retry", tint = Color.White)
            }

            ImageUploadStatus.UPLOADED -> Unit
        }

        if (image.status == ImageUploadStatus.FAILED) {
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .size(20.dp)
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable(onClick = onRetry),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.Refresh, contentDescription = "Retry", modifier = Modifier.size(14.dp))
            }
        }

        Box(
            modifier = Modifier
                .padding(4.dp)
                .size(20.dp)
                .align(Alignment.TopEnd)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.55f))
                .clickable(onClick = onRemove),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Filled.Close, contentDescription = "Remove image", tint = Color.White, modifier = Modifier.size(14.dp))
        }
    }
}
