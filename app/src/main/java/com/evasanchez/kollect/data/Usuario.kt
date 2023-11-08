package com.evasanchez.kollect.data

import android.os.Parcelable
import com.google.errorprone.annotations.Keep
import com.google.firebase.firestore.PropertyName

data class Usuario(
    val userId: String = "",
    val username: String = "",
    val email: String = "",
    val pfpURL: String = "",
    val contPcs: String = "",
    val valuePcs: String = "",
   /* val wishlist: List<Wishlist> = emptyList(),
    val coleccion: List<Coleccion> = emptyList(),
    val kgroups: List<Kgroups> = emptyList(),
    val idols: List<Idols> = emptyList() */
) {
    fun userToMap(): MutableMap<String, Any>{
        return mutableMapOf(
            "user_id" to this.userId,
            "username" to this.username,
            "email" to this.email,
            "pfpURL" to this.pfpURL,
            "cont_pcs" to this.contPcs,
            "valuePcs" to this.valuePcs,
            /*"wishlist" to this.wishlist,
            "coleccion" to this.coleccion,
            "kgroups" to this.kgroups,
            "idols" to this.idols*/
        )
    }
}

data class KgroupsItem(
    val groupName: String
){

}

data class Idols(
    val idolId: String,
    val idolName: String,
    val groupName2: String
){

}
@Keep
data class Photocard(
    @get:PropertyName("photocard_id")
    @PropertyName("photocard_id")
    val photocardId: String,
    @get:PropertyName("album_name")
    @PropertyName("album_name")
    val albumName: String,
    @get:PropertyName("status")
    @PropertyName("status")
    val status: String = "Wishlist", //Para evitar que quede en null?
    @get:PropertyName("group_name")
    @PropertyName("group_name")
    val groupName: String,
    @get:PropertyName("idol_name")
    @PropertyName("idol_name")
    val idolName: String,
    @get:PropertyName("value")
    @PropertyName("value")
    val value: String,
    @get:PropertyName("type")
    @PropertyName("type")
    val type: String,
    @get:PropertyName("photocard_url")
    @PropertyName("photocard_url")
    val photocardURL: String,
    @get:PropertyName("photocard_version")
    @PropertyName("photocard_version")
    val photocardVersion: String

){
    constructor() : this("", "", "", "", "", "", "","","")
    fun photocardToMap(): MutableMap<String, Any>{
        return mutableMapOf(
            "photocard_id" to this.photocardId,
            "album_name" to this.albumName,
            "status" to this.status,
            "group_name" to this.groupName,
            "idol_name" to this.idolName,
            "value" to this.value,
            "type" to this.type,
            "photocard_url" to this.photocardURL,
            "photocard_version" to this.photocardVersion
        )
    }
}

data class Coleccion(
    val photocardId: String,
    val albumName: String,
    val status: String,
    val groupName: String,
    val idolName: String,
    val value: String,
    val type: String,
    val photocardURL: String,
    val photocardVersion: String
){

}

