package com.evasanchez.kollect.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class PhotocardRepository {
    private val _photocards = MutableLiveData<List<Photocard>>()
    val photocards: LiveData<List<Photocard>> = _photocards


    fun addPhotocardToMasterist(photocard: Photocard) {
        val currentList = _photocards.value ?: emptyList()
        _photocards.postValue(currentList + photocard)
    }


}