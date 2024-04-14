package com.ayush.wallpapers.data

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

internal var Category: String = ""

class WallpaperViewModel: ViewModel() {
    var state :Flow<List<String>> = flowOf()
    init {
        state = getData()
    }
    private fun getData() = flow {
            emit(getWallpaperCategoryFromFireStore())
    }
}

class WallpaperCategoricalViewModel: ViewModel() {
    var categoryState = mutableStateMapOf<Int, List<String>>()
    init {
        getData(Category)
    }
    private fun getData(category: String) {
        viewModelScope.launch {
            categoryState = getCategoricalWallpaperFromFireStore(category)
        }
    }
}

@Suppress("UNCHECKED_CAST")
fun getCategoricalWallpaperFromFireStore(category: String): SnapshotStateMap<Int, List<String>> {
    val db = FirebaseFirestore.getInstance()
    val resultStateFlow = mutableStateMapOf<Int, List<String>>()
    try{
        db.collection("users").document(category).get().addOnSuccessListener {
            it.data!!.map {data ->
                resultStateFlow[data.key.toInt()] = data.value as List<String>
            }
        }
    } catch (e: FirebaseFirestoreException) {
        println(e.localizedMessage)
    }
    return resultStateFlow
}
suspend fun getWallpaperCategoryFromFireStore(): List<String> {
    val db = FirebaseFirestore.getInstance()
    val result  = mutableListOf<String>()
    try{
        for (category in db.collection("users").get().await().documents) {
            result.add(category.id)
        }
    } catch (e: FirebaseFirestoreException) {
        println(e.localizedMessage)
    }
    return result
}