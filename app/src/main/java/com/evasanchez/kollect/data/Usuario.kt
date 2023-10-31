package com.evasanchez.kollect.data

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

data class Wishlist(
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

