package uk.co.andymarch.blogposter.ui.drafts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import uk.co.andymarch.blogposter.data.draft.Draft
import uk.co.andymarch.blogposter.data.draft.DraftStatus

@Composable
fun DraftsScreen(viewModel: DraftsViewModel, onOpenDraft: (String) -> Unit) {
    val drafts by viewModel.drafts.collectAsStateWithLifecycle()
    val sorted = drafts.sortedByDescending { it.updatedAtEpochMillis }

    if (sorted.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No drafts yet. Start a new post from the New Post tab.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(sorted, key = { it.id }) { draft ->
            DraftCard(draft = draft, onClick = { onOpenDraft(draft.id) }, onDelete = { viewModel.delete(draft.id) })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DraftCard(draft: Draft, onClick: () -> Unit, onDelete: () -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.fillMaxWidth(0.85f)) {
                Text(
                    draft.title.ifBlank { "Untitled post" },
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(draft.dateIso, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    AssistChip(onClick = onClick, label = { Text(draft.status.label()) })
                }
                if (draft.status == DraftStatus.FAILED && draft.lastError != null) {
                    Text(draft.lastError, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete draft")
            }
        }
    }
}

private fun DraftStatus.label(): String = when (this) {
    DraftStatus.DRAFT -> "Draft"
    DraftStatus.PUBLISHING -> "Publishing…"
    DraftStatus.PUBLISHED -> "Published"
    DraftStatus.FAILED -> "Failed"
}
