package uk.co.andymarch.blogposter.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SettingsScreen(viewModel: SettingsViewModel, onSignedOut: () -> Unit) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val verifyResult by viewModel.verifyResult.collectAsStateWithLifecycle()

    var owner by remember(settings.owner) { mutableStateOf(settings.owner) }
    var repo by remember(settings.repo) { mutableStateOf(settings.repo) }
    var branch by remember(settings.branch) { mutableStateOf(settings.branch) }
    var authorName by remember(settings.authorName) { mutableStateOf(settings.authorName) }
    var authorEmail by remember(settings.authorEmail) { mutableStateOf(settings.authorEmail) }
    var maxDimension by remember(settings.imageMaxDimensionPx) { mutableStateOf(settings.imageMaxDimensionPx.toFloat()) }
    var jpegQuality by remember(settings.imageJpegQuality) { mutableStateOf(settings.imageJpegQuality.toFloat()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Repository", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = owner,
            onValueChange = { owner = it },
            label = { Text("Owner") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = repo,
            onValueChange = { repo = it },
            label = { Text("Repository") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = branch,
            onValueChange = { branch = it },
            label = { Text("Branch") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        verifyResult?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
        Button(onClick = { viewModel.verifyAccess(owner, repo) }) { Text("Verify access") }
        Button(onClick = { viewModel.updateRepo(owner, repo, branch) }) { Text("Save repository settings") }

        HorizontalDivider()

        Text("Commit author (optional)", style = MaterialTheme.typography.titleMedium)
        Text(
            "Leave blank to let GitHub attribute commits to your token's account.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        OutlinedTextField(
            value = authorName,
            onValueChange = { authorName = it },
            label = { Text("Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = authorEmail,
            onValueChange = { authorEmail = it },
            label = { Text("Email") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
        )
        Button(onClick = { viewModel.updateAuthor(authorName, authorEmail) }) { Text("Save author") }

        HorizontalDivider()

        Text("Image optimisation", style = MaterialTheme.typography.titleMedium)
        Text("Max dimension: ${maxDimension.toInt()}px", style = MaterialTheme.typography.bodyMedium)
        Slider(value = maxDimension, onValueChange = { maxDimension = it }, valueRange = 512f..4096f, steps = 6)
        Text("JPEG quality: ${jpegQuality.toInt()}", style = MaterialTheme.typography.bodyMedium)
        Slider(value = jpegQuality, onValueChange = { jpegQuality = it }, valueRange = 40f..100f)
        Button(onClick = { viewModel.updateImageOptions(maxDimension.toInt(), jpegQuality.toInt()) }) {
            Text("Save image settings")
        }

        HorizontalDivider()

        OutlinedButton(onClick = {
            viewModel.signOut()
            onSignedOut()
        }) {
            Text("Sign out")
        }
    }
}
