package pl.asi.asilotto.activity

import android.graphics.Color
import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.view.MenuItem
import android.view.View
import android.widget.RadioButton
import com.crashlytics.android.Crashlytics
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import pl.asi.asilotto.R
import pl.asi.asilotto.api.ApiService
import pl.asi.asilotto.model.Price
import pl.asi.asilotto.model.Prize
import pl.asi.asilotto.util.AuthPrefManager
import pl.asi.asilotto.util.POLAND_CENTER
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs


class MainActivity : BaseCompatActivity(), OnMapReadyCallback {

    private var mMap: GoogleMap? = null
    private var circleRadiousMtrs = 0.0
    private var circleOptions: CircleOptions = CircleOptions().radius(circleRadiousMtrs)
        .strokeColor(Color.RED)
        .fillColor(0x30ff0000)
        .strokeWidth(2f)
    private var circle: Circle? = null
    private val apiService = ApiService()
    private var prices: List<Price>? = null
    private lateinit var mDrawerLayout: DrawerLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mDrawerLayout = findViewById(R.id.drawer_layout)
        setupToolbar()

        supportActionBar?.apply {
            setHomeAsUpIndicator(R.drawable.ic_menu)
        }

        obtainMap()

        Fabric.with(this, Crashlytics())
        setupListeners()

        getPrices()
        getPrize()
    }

    private fun getPrize() {
        apiService.api.getPrize().enqueue(object : Callback<List<Prize>> {
            override fun onFailure(call: Call<List<Prize>>, t: Throwable) {
                toast(t.message ?: "Api Error")
            }

            override fun onResponse(call: Call<List<Prize>>, response: Response<List<Prize>>) {
                if(response.body()?.size ?: 0 > 0) {
                    parseTime(response.body()?.get(0)?.lotteryTime.toString())
                    moneyPool.text = String.format("%.2f zł", response.body()?.get(0)?.prize ?: 0.0)
                }
            }

            private fun parseTime(lotteryTime: String) {
                setDate(lotteryTime)
                val thread = object : Thread() {
                    override fun run() {
                        try {
                            while (!this.isInterrupted) {
                                Thread.sleep(60000)
                                runOnUiThread {
                                    setDate(lotteryTime)
                                }
                            }
                        } catch (e: InterruptedException) {
                        }

                    }
                }

                thread.start()
            }
        })
    }

    private fun setDate(lotteryTime: String) {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.GERMANY)

        try {
            val d1 = format.parse(lotteryTime)
            val d2 = Calendar.getInstance().time

            val diff = abs(d2.time - d1.time)

            val diffMinutes = diff / (60 * 1000) % 60
            val diffHours = diff / (60 * 60 * 1000) % 24
            val diffDays = diff / (24 * 60 * 60 * 1000)

            timeLeft.text = String.format("%d:%tM h", diffHours + diffDays * 24, diffMinutes)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getPrices() {
        apiService.api.getPrices().enqueue(object : Callback<List<Price>> {
            override fun onFailure(call: Call<List<Price>>, t: Throwable) {
                toast(t.message ?: "Api Error")
            }

            override fun onResponse(call: Call<List<Price>>, response: Response<List<Price>>) {
                prices = response.body()

                for (p in response.body() ?: listOf()) {
                    val radio = RadioButton(this@MainActivity)
                    radio.text = p.price.toString()
                    sizesGroup.addView(radio)
                }

                if(sizesGroup.childCount > 0) {
                    sizesGroup.check(sizesGroup.getChildAt(0).id)
                }
                confirmButton.visibility = View.VISIBLE
                loadProgress.visibility = View.GONE
            }
        })
    }

    private fun obtainMap() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun setupListeners() {
        confirmButton.setOnClickListener {
            if (AuthPrefManager.getToken(this@MainActivity).isBlank()) {
                openLogin()
            } else {
                openPayment()
            }
        }

        sizesGroup.setOnCheckedChangeListener { radioGroup, buttonID ->
            val radio = radioGroup.findViewById<RadioButton>(buttonID)
            val price = prices?.first { it.price.toString() == radio.text }
            circleRadiousMtrs = price?.size?.times(1000.0) ?: 0.0

            mMap?.cameraPosition?.let {
                moveCircle(it)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                mDrawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun openPayment() {
        val price = prices?.first { it.size == circleRadiousMtrs / 1000.0 }

        startActivity<PaymentActivity>(
            "price" to price?.price, "size" to price?.size,
            "lat" to mMap?.cameraPosition?.target?.latitude, "lng" to mMap?.cameraPosition?.target?.longitude
        )
    }

    private fun openLogin() {
        startActivity<LoginActivity>()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap?.moveCamera(CameraUpdateFactory.zoomTo(9f))
        mMap?.moveCamera(CameraUpdateFactory.newLatLng(POLAND_CENTER))

        circleOptions.center(LatLng(mMap?.cameraPosition?.target!!.latitude, mMap?.cameraPosition?.target!!.longitude))
        circle = mMap?.addCircle(circleOptions)

        mMap?.setOnCameraMoveListener {
            mMap?.cameraPosition?.let {
                moveCircle(it)
            }
        }
    }

    private fun moveCircle(c: CameraPosition) {
        if (circleRadiousMtrs > 0) {
            circle?.apply {
                if (circleRadiousMtrs != radius) {
                    radius = circleRadiousMtrs
                }

                center = LatLng(c.target.latitude, c.target.longitude)
            }
        }
    }
}
