package pl.asi.asilotto.activity

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import pl.asi.asilotto.R
import pl.asi.asilotto.api.ApiService
import pl.asi.asilotto.model.User
import pl.asi.asilotto.util.AuthPrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : BaseCompatActivity() {
    private val apiService = ApiService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setupToolbar()

        register.setOnClickListener {
            startActivity<RegisterActivity>()
        }

        login.setOnClickListener {
            apiService.api.getLogin(loginInput.text.toString(), passwordInput.text.toString())
                .enqueue(object : Callback<User> {
                    override fun onFailure(call: Call<User>, t: Throwable) {
                        toast("Niepoprawne dane")
                    }

                    override fun onResponse(call: Call<User>, response: Response<User>) {
                        if (response.isSuccessful && response.body() != null) {
                            AuthPrefManager.putToken(this@LoginActivity, response.body()?.id.toString()) //todo change
                            proceedToPayments()
                        } else {
                            toast("Niepoprawne dane")
                        }
                    }
                })
        }
    }

    override fun onStart() {
        super.onStart()
        if (AuthPrefManager.getToken(this@LoginActivity).isBlank().not()) {
            proceedToPayments()
        }
    }

    private fun proceedToPayments() {
        startActivity(intent)
        this.finish()
    }
}
