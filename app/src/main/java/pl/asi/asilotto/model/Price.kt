package pl.asi.asilotto.model

import com.google.gson.annotations.SerializedName

data class Price(
    @SerializedName("price") val price: Double,
    @SerializedName("size") val size: Double
)