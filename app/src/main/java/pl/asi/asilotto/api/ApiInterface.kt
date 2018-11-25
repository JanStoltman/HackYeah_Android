package pl.asi.asilotto.api

import pl.asi.asilotto.model.Price
import pl.asi.asilotto.model.Prize
import pl.asi.asilotto.model.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiInterface {
    @GET("/prices")
    fun getPrices(): Call<List<Price>>

    @POST("/register/user")
    fun postRegister(@Body user: User): Call<User>

    @GET("/auth/{login}/{password}")
    fun getLogin(@Path("login") login: String, @Path("password") password: String): Call<User>

    @GET("/prizes")
    fun getPrize(): Call<List<Prize>>

    @GET("/bet/{lat}/{lng}/{size}/{userId}")
    fun postBet(@Path("lat") lat: Double, @Path("lng") lng: Double, @Path("size") size: Double, @Path("userId") userId: String): Call<Any>
}
