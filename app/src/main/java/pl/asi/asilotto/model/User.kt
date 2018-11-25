package pl.asi.asilotto.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("email") val email: String,
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("password") val password: String
)
