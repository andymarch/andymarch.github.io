package uk.co.andymarch.blogposter.data.draft

import android.content.Context
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Drafts are stored as a single JSON file rather than a database — there will
 * only ever be a handful of them for a personal blogging app, and it keeps the
 * dependency list (and the amount of code) small.
 *
 * The file is loaded synchronously at construction time (it's tiny — a few KB
 * at most) so [get] never races a background load; this class is built once,
 * on the main thread, during [android.app.Application.onCreate].
 */
class DraftStore(context: Context) {

    private val file = File(context.filesDir, "drafts.json")
    private val mutex = Mutex()
    private val json = Json { prettyPrint = true; ignoreUnknownKeys = true }

    private val _drafts = MutableStateFlow(readFromDisk())
    val drafts: StateFlow<List<Draft>> = _drafts

    fun get(id: String): Draft? = _drafts.value.firstOrNull { it.id == id }

    suspend fun upsert(draft: Draft) = withContext(Dispatchers.IO) {
        mutex.withLock {
            val updated = _drafts.value.filterNot { it.id == draft.id } + draft
            writeToDisk(updated)
            _drafts.value = updated
        }
    }

    suspend fun delete(id: String) = withContext(Dispatchers.IO) {
        mutex.withLock {
            val updated = _drafts.value.filterNot { it.id == id }
            writeToDisk(updated)
            _drafts.value = updated
        }
    }

    private fun readFromDisk(): List<Draft> {
        if (!file.exists()) return emptyList()
        return runCatching { json.decodeFromString<List<Draft>>(file.readText()) }.getOrDefault(emptyList())
    }

    private fun writeToDisk(drafts: List<Draft>) {
        val tmp = File(file.parentFile, "${file.name}.tmp")
        tmp.writeText(json.encodeToString(drafts))
        tmp.renameTo(file)
    }
}
