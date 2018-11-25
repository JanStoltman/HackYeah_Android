package pl.asi.asilotto.activity

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_payment.*
import org.jetbrains.anko.toast
import pl.asi.asilotto.R
import pl.asi.asilotto.adapter.BetsAdapter
import pl.asi.asilotto.api.ApiService
import pl.asi.asilotto.model.Bet
import pl.asi.asilotto.model.Price
import pl.asi.asilotto.util.AuthPrefManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PaymentActivity : BaseCompatActivity() {
    private val apiServie = ApiService()
    private val bets: ArrayList<Bet> = arrayListOf()
    private val betsAdapter = BetsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        setupToolbar()

        betsList.layoutManager = LinearLayoutManager(this)
        betsList.adapter = betsAdapter

        val price = intent.getDoubleExtra("price", 0.0)
        val size = intent.getDoubleExtra("size", 0.0)
        val lat = intent.getDoubleExtra("lat", 0.0)
        val lng = intent.getDoubleExtra("lng", 0.0)

        bets.add(Bet(Price(price, size), LatLng(lat, lng)))
        betsAdapter.setBets(bets)


        postBet(price, size, lat, lng)

        pay.setOnClickListener {
            val thread = object : Thread() {
                override fun run() {
                    try {
                        while (!this.isInterrupted) {
                            runOnUiThread {
                                loadProgress.visibility = View.VISIBLE
                                pay.visibility = View.INVISIBLE
                            }
                            Thread.sleep(1500)
                            runOnUiThread {
                                loadProgress.visibility = View.INVISIBLE
                                pay.visibility = View.VISIBLE
                                toast("Paid")
                            }
                            this.interrupt()
                        }
                    } catch (e: InterruptedException) {
                    }

                }
            }

            thread.start()
        }
    }

    private fun postBet(price: Double, size: Double, lat: Double, lng: Double) {
        apiServie.api.postBet(lat, lng, size, AuthPrefManager.getToken(this)).enqueue(object : Callback<Any> {
            override fun onFailure(call: Call<Any>, t: Throwable) {
                toast(t.message ?: "Api Error")
            }

            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                sum.text = String.format("%.2f zł", bets.asSequence().map { it.price.price }.sum())
            }
        })
    }
}
