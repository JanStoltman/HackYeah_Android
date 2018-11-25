package pl.asi.asilotto.util

import android.content.Context
import android.content.Context.MODE_PRIVATE

class AuthPrefManager {
    companion object {
        const val SHARED_PREF_AUTH = "priv_auth"
        const val TOKEN = "token"
        fun getToken(context: Context): String =
            context.applicationContext.getSharedPreferences(SHARED_PREF_AUTH, MODE_PRIVATE).getString(
                TOKEN, ""
            )

        fun putToken(context: Context, token: String) =
            context.applicationContext.getSharedPreferences(SHARED_PREF_AUTH, MODE_PRIVATE)
                .edit().putString(TOKEN, token).commit()
    }
}