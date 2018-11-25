package pl.asi.asilotto.model

import com.google.gson.annotations.SerializedName

data class Prize(
    @SerializedName("lottery_time") val lotteryTime: String,
    @SerializedName("prize") val prize: Double
)