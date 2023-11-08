package com.evasanchez.kollect.ViewModels

import android.util.Log
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

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
        Log.d("A ver", "Entra en el init")
        val db = FirebaseFirestore.getInstance()
        db.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
        viewModelScope.launch {
            getKGroupListRepository()}
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
        }

        if (subColReference != null) {
            val kGroupData = mapOf("group_name" to kGroup)
            subColReference!!.add(kGroupData)
                .addOnSuccessListener {
                    showSuccessToast("Grupo añadido")
                    Log.d("Hola", "Se ha creado el grupo")
                    // Call getKGroupListRepository to refresh the list of groups
                    viewModelScope.launch(Dispatchers.IO) {
                        getKGroupListRepository()

                    }
                }
                .addOnFailureListener {
                    Log.d("Hola", "Algo ha malido sal")
                }
        }
    }
    fun addIdolToUser(kGroup: String, idol:String) {
        viewModelScope.launch(Dispatchers.IO) {
            subColReference = userID?.let { getIdolSubColReference(it) }
            if (subColReference != null) {
                val idolData = mapOf(
                    "group_name" to kGroup,
                    "idol_name" to idol)
                subColReference!!.add(idolData)
                    .addOnSuccessListener {
                        Log.d("Hola", "Se ha añadido el idol, ${idolData}")
                        showSuccessToast("Idol añadido")
                        // Call getKGroupListRepository to refresh the list of groups
                        viewModelScope.launch(Dispatchers.IO) {
                            //getKGroupListRepository()
                        }
                    }
                    .addOnFailureListener {
                        Log.d("Hola", "Algo ha malido sal")
                    }
            }
        }


    }

    suspend fun getIdolSubColReference(userId: String): CollectionReference{
        val usersCollection = db.collection("usuario")
        val query = usersCollection.whereEqualTo("user_id", userId)
        val querySnapshot = query.get().await()

        if (!querySnapshot.isEmpty) {
            val documentSnapshot = querySnapshot.documents.first()
            val documentPath = documentSnapshot.reference.path
            return db.document(documentPath).collection("Idols")
        } else {
            throw NoSuchElementException("User not found")
        }
    }

    // Function to get the subcollection reference
    suspend fun getSubcollectionReference(userId: String): CollectionReference {
        val usersCollection = db.collection("usuario")
        val query = usersCollection.whereEqualTo("user_id", userId)
        val querySnapshot = query.get().await()

        if (!querySnapshot.isEmpty) {
            val documentSnapshot = querySnapshot.documents.first()
            val documentPath = documentSnapshot.reference.path
            return db.document(documentPath).collection("Kgroups")
        } else {
            throw NoSuchElementException("User not found")
        }
    }

    // Function to get a list of documents in the subcollection
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
                            Log.d("Hostia", name)
                            groupNames.add(name)
                        }
                    }

                    _allGroups.postValue(groupNames)
                } catch (e: Exception) {
                    Log.e("Firestore Query Error", e.message ?: "Unknown error")
                    _allGroups.postValue(emptyList())
                }
            } else {
                Log.d("Else", "subColReference is null")
                _allGroups.postValue(emptyList())
            }
        }
    }

    suspend fun getProfilePicture(){
        Log.d("GETPROFILEPIC", "Entra en getProfilePicture")
        val usersCollection = db.collection("usuario")
        viewModelScope.launch(Dispatchers.IO) {
            if (usersCollection != null) {
                try {
                    val usernameQuery = usersCollection.whereEqualTo("user_id", userID).get().await()
                    for (document in usernameQuery.documents) {
                        val pfpURL = document.getString("pfpURL")
                        if (pfpURL != null) {
                            Log.d("pfp", pfpURL)
                            _username.postValue(pfpURL)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("Firestore Query Error", e.message ?: "Unknown error")
                }
            }
        }


}
    suspend fun getUsername(){
        Log.d("GETUSERNAME", "Entra en getUsername")
        val usersCollection = db.collection("usuario")
        viewModelScope.launch(Dispatchers.IO) {
            if (usersCollection != null) {
                try {
                    val usernameQuery = usersCollection.whereEqualTo("user_id", userID).get().await()
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

}








