package com.evasanchez.kollect.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PhotocardFormViewModel: ViewModel() {

   private val _photocardId = MutableLiveData<String>()
    val photocardId : LiveData<String> = _photocardId

    private val _albumName = MutableLiveData<String>()
    val albumName : LiveData<String> = _albumName

    private val _status = MutableLiveData<String>()
    val status : LiveData<String> = _status

    private val _groupName =MutableLiveData<String>()
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




}