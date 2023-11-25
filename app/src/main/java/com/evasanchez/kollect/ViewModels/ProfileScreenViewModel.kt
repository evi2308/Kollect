package com.evasanchez.kollect.ViewModels

import android.app.Activity
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.random.Random

class ProfileScreenViewModel : ViewModel() {
    private val _allGroups = MutableLiveData<List<String>>()
    val allGroups: LiveData<List<String>> = _allGroups
    private val auth: FirebaseAuth = Firebase.auth
    val userID = auth.currentUser?.uid
    val db = Firebase.firestore

    private val _kGroup = MutableLiveData<String>()
    val kGroup: LiveData<String> = _kGroup

    private val _idol = MutableLiveData<String>()
    val idol: LiveData<String> = _idol

    private val _addIdolEnabled = MutableLiveData<Boolean>()
    val addIdolEnabled: LiveData<Boolean> = _addIdolEnabled

    private val _successMessage = MutableLiveData<String>()
    val successMessage: LiveData<String> = _successMessage

    private val _profilePicture = MutableLiveData<String>()
    val profilePicture: LiveData<String> = _profilePicture

    private val _username = MutableLiveData<String>()
    val username: LiveData<String> = _username


    fun showSuccessToast(message: String) {
        _successMessage.postValue(message)
    }

    init {
        val db = FirebaseFirestore.getInstance()
        db.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
        viewModelScope.launch {
            getKGroupListRepository()
        }
    }

    private var subColReference: CollectionReference? = null // Subcollection reference


    fun onKGroupChanged(kGroup: String) {
        _kGroup.value = kGroup
    }

    fun onIdolChanged(idol: String) {
        _idol.value = idol
    }


    fun addKgroupToUser(kGroup: String) {
        viewModelScope.launch(Dispatchers.IO) {
            subColReference = userID?.let { getSubcollectionReference(it) }
            if (subColReference != null) {
                val kGroupData = mapOf("group_name" to kGroup)
                subColReference!!.add(kGroupData)
                    .addOnSuccessListener {
                        showSuccessToast("Grupo añadido")
                        Log.d("Grupo añadido", "Se ha creado el grupo")
                        // Call getKGroupListRepository to refresh the list of groups
                        viewModelScope.launch(Dispatchers.IO) {
                            getKGroupListRepository()

                        }
                    }
                    .addOnFailureListener {
                        Log.d("Error", "Algo ha salido mal")
                    }
            }
        }


    }

    fun addIdolToUser(kGroup: String, idol: String) {
        viewModelScope.launch(Dispatchers.IO) {
            subColReference = userID?.let { getIdolSubColReference(it) }
            if (subColReference != null) {
                val idolData = mapOf(
                    "group_name" to kGroup,
                    "idol_name" to idol
                )
                subColReference!!.add(idolData)
                    .addOnSuccessListener {
                        Log.d("Idol añadido", "Se ha añadido el idol, ${idolData}")
                        showSuccessToast("Idol añadido")
                        // Call getKGroupListRepository to refresh the list of groups
                        viewModelScope.launch(Dispatchers.IO) {
                            //getKGroupListRepository()
                        }
                    }
                    .addOnFailureListener {
                        Log.d("Error", "Algo ha salido mal")
                    }
            }
        }


    }

    suspend fun getIdolSubColReference(userId: String): CollectionReference {
        val usersCollection = db.collection("usuario")
        val query = usersCollection.whereEqualTo("user_id", userId)
        val querySnapshot = query.get().await()

        if (!querySnapshot.isEmpty) {
            val documentSnapshot = querySnapshot.documents.first()
            val documentPath = documentSnapshot.reference.path
            return db.document(documentPath).collection("Idols")
        } else {
            throw NoSuchElementException("Error")
        }
    }

    // Sacar SubCollecion para los grupos
    suspend fun getSubcollectionReference(userId: String): CollectionReference {
        val usersCollection = db.collection("usuario")
        val query = usersCollection.whereEqualTo("user_id", userId)
        val querySnapshot = query.get().await()

        if (!querySnapshot.isEmpty) {
            val documentSnapshot = querySnapshot.documents.first()
            val documentPath = documentSnapshot.reference.path
            return db.document(documentPath).collection("Kgroups")
        } else {
            throw NoSuchElementException("Error")
        }
    }

    // Lista de los grupos
    suspend fun getKGroupListRepository() {
        viewModelScope.launch(Dispatchers.IO) {
            val subColReference = userID?.let { getSubcollectionReference(it) }

            if (subColReference != null) {
                try {
                    val querySnapshot = subColReference.get().await()
                    val groupNames = mutableListOf<String>()

                    for (document in querySnapshot.documents) {
                        val name = document.getString("group_name")
                        if (name != null) {
                            groupNames.add(name)
                        }
                    }

                    _allGroups.postValue(groupNames)
                } catch (e: Exception) {
                    _allGroups.postValue(emptyList())
                }
            } else {
                _allGroups.postValue(emptyList())
            }
        }
    }

    suspend fun getProfilePicture() {
        val usersCollection = db.collection("usuario")
        viewModelScope.launch(Dispatchers.IO) {
            if (usersCollection != null) {
                try {
                    val usernameQuery =
                        usersCollection.whereEqualTo("user_id", userID).get().await()
                    for (document in usernameQuery.documents) {
                        val pfpURL = document.getString("pfpURL")
                        if (pfpURL != null) {
                            Log.d("pfp", pfpURL)
                            _profilePicture.postValue(pfpURL)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("Firestore Query Error", e.message ?: "Unknown error")
                }
            }
        }


    }

    suspend fun getUsername() {
        val usersCollection = db.collection("usuario")
        viewModelScope.launch(Dispatchers.IO) {
            if (usersCollection != null) {
                try {
                    val usernameQuery =
                        usersCollection.whereEqualTo("user_id", userID).get().await()
                    for (document in usernameQuery.documents) {
                        val username = document.getString("username")
                        if (username != null) {
                            Log.d("User", username)
                            _username.postValue(username)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("Firestore Query Error", e.message ?: "Unknown error")
                    _username.postValue("Usuario")
                }
            }
        }
    }

    fun logout(context: Activity) {
        auth.signOut()
        context.finishActivity(0)
    }

    suspend fun getColeccionSubcollectionReference(userId: String): CollectionReference {
        val usersCollection = db.collection("usuario")
        val query = usersCollection.whereEqualTo("user_id", userId)
        val querySnapshot = query.get().await()

        if (!querySnapshot.isEmpty) {
            val documentSnapshot = querySnapshot.documents.first()
            val documentPath = documentSnapshot.reference.path
            return db.document(documentPath).collection("Coleccion")
        } else {
            throw NoSuchElementException("User not found")
        }
    }

    private val _showDialog = MutableLiveData<Boolean>()
    val showDialog: LiveData<Boolean> = _showDialog
    private val _dialogText = MutableLiveData<String>()
    val dialogText: LiveData<String> = _dialogText
    fun getTotalValuePcs() {
        viewModelScope.launch {
            val userID = FirebaseAuth.getInstance().currentUser?.uid
            val subColReference = userID?.let { getColeccionSubcollectionReference(it) }


            subColReference?.let {
                val querySnapshot = it.get().await()
                var total_value = querySnapshot.documents.sumOf { document ->
                    val stringValue = document.getString("value") ?: "0.0"
                    stringValue.toDoubleOrNull() ?: 0.0
                }
                _showDialog.postValue(true)
                _dialogText.postValue("Valor total de las photocards en tu coleccion: ${total_value} €")
            }

        }

    }

    fun getTotalPcsInCollection() {
        viewModelScope.launch {
            val userID = FirebaseAuth.getInstance().currentUser?.uid
            val subColReference = userID?.let { getColeccionSubcollectionReference(it) }


            subColReference?.let {
                val querySnapshot = it.get().await()
                var count_pcs = querySnapshot.size()
                _showDialog.postValue(true)
                _dialogText.postValue("Número de Photocards en tu coleccion: ${count_pcs}")
            }

        }

    }

    fun onDismissDialog() {
        _showDialog.value = false
    }

    fun changeProfilePicture(uri: Uri?) {
        val usersCollection = db.collection("usuario")
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        var randomNum: String = Random.nextInt().toString()
        var pfpNameStorage = "pfp- ${randomNum} "
        val pfpNameStorageRef = storageRef.child("profilePics/${pfpNameStorage}")
        viewModelScope.launch {
            val userID = auth.currentUser?.uid
            val usernameQuery = usersCollection.whereEqualTo("user_id", userID).get().await()
            if (uri != null) {
                val uploadTask = uri?.let { pfpNameStorageRef.putFile(it) }
                if (uploadTask != null) {
                    uploadTask.addOnFailureListener {
                        Log.d("Error al subir la imagen", "Ha habido algun error")
                    }.addOnSuccessListener { taskSnapshot ->
                        pfpNameStorageRef.downloadUrl.addOnSuccessListener { uri ->
                            // Sacar la URI del archivo desde el storage
                            val document = usernameQuery.documents.first()
                            if (document != null) {
                                document.reference.update("pfpURL", uri.toString()).addOnSuccessListener {
                                    Log.d("Foto de perfil", "Foto actualizada")
                                    _profilePicture.postValue(uri.toString())

                                }
                            }
                        }
                    }
                }

            }
        }

    }
}








