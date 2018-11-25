package pl.asi.asilotto.activity

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_register.*
import org.jetbrains.anko.toast
import pl.asi.asilotto.R
import pl.asi.asilotto.api.ApiService
import pl.asi.asilotto.model.User
import pl.asi.asilotto.util.AuthPrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : BaseCompatActivity() {
    private val apiService = ApiService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        setupToolbar()

        register.setOnClickListener {
            registerUser(loginInput.text.toString(), emailInput.text.toString(), passwordInput.text.toString())
        }
    }

    private fun registerUser(name: String, email: String, password: String) {
        apiService.api.postRegister(User(name = name, email = email, password = password, id = -1))
            .enqueue(object : Callback<User> {
                override fun onFailure(call: Call<User>, t: Throwable) {
                    toast(t.message ?: "Api Error")
                }

                override fun onResponse(call: Call<User>, response: Response<User>) {
                    AuthPrefManager.putToken(this@RegisterActivity, response.body()?.id.toString()) //todo change
                    this@RegisterActivity.onBackPressed()
                }
            })
    }
}
