package pl.asi.asilotto.model

import com.google.android.gms.maps.model.LatLng

data class Bet(
    val price: Price,
    val latLng: LatLng
)