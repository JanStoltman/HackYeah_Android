package pl.asi.asilotto.api

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class ApiService {
    companion object {
        private const val BASE_URL = "http://10.250.193.60:5000"
    }

    var api: ApiInterface

    init {
        val gson = GsonBuilder()
            .setLenient()
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        api = retrofit.create<ApiInterface>(ApiInterface::class.java)
    }


}