package com.evasanchez.kollect.ViewModels

import android.net.Uri
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.evasanchez.kollect.data.Photocard
import com.google.firebase.firestore.CollectionReference
import kotlinx.coroutines.tasks.await

class PhotocardDetailViewModel : ViewModel() {
    private val _albumName = MutableLiveData<String>()
    val albumName : LiveData<String> = _albumName

    private val _status = MutableLiveData<String>()
    val status : LiveData<String> = _status

    private val _groupName = MutableLiveData<String>()
    val groupName : LiveData<String> = _groupName

    private val _idolName = MutableLiveData<String>()
    val idolName : LiveData<String> = _idolName

    private val _value = MutableLiveData<String>()
    val value : LiveData<String> = _value

    private val _type = MutableLiveData<String>()
    val type : LiveData<String> = _type

    private val _photocardURL = MutableLiveData<String>()
    val photocardURL : LiveData<String> = _photocardURL

    private val _photocardVersion = MutableLiveData<String>()
    val photocardVersion : LiveData<String> = _photocardVersion

    fun setPhotocardDetails(photocard: Photocard){
        _albumName.value = photocard.albumName
        _status.value = photocard.status
        _groupName.value = photocard.groupName
        _idolName.value = photocard.idolName
        _value.value = photocard.value
        _type.value = photocard.type
        _photocardURL.value = photocard.photocardURL
        _photocardVersion.value = photocard.photocardVersion
    }

}