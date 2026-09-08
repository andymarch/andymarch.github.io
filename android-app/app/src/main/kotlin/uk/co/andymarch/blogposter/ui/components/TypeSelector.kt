package uk.co.andymarch.blogposter.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import uk.co.andymarch.blogposter.core.PostType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TypeSelector(
    selected: PostType,
    customType: String,
    onSelect: (PostType) -> Unit,
    onCustomTypeChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(PostType.entries) { type ->
                FilterChip(
                    selected = selected == type,
                    onClick = { onSelect(type) },
                    label = { Text(type.label) },
                )
            }
        }
        if (selected == PostType.CUSTOM) {
            OutlinedTextField(
                value = customType,
                onValueChange = onCustomTypeChanged,
                label = { Text("Custom type") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
