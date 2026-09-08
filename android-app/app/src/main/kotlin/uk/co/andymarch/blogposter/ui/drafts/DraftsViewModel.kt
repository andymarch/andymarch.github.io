package uk.co.andymarch.blogposter.ui.drafts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uk.co.andymarch.blogposter.data.draft.Draft
import uk.co.andymarch.blogposter.data.draft.DraftStore

class DraftsViewModel(private val draftStore: DraftStore) : ViewModel() {

    val drafts: StateFlow<List<Draft>> = draftStore.drafts

    fun delete(id: String) {
        viewModelScope.launch { draftStore.delete(id) }
    }
}
