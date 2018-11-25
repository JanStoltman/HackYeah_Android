package pl.asi.asilotto.activity

import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import pl.asi.asilotto.R

abstract class BaseCompatActivity : AppCompatActivity() {
    fun setupToolbar(toolbarId: Int = R.id.toolbar) {
        setSupportActionBar(findViewById(toolbarId))
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                this@BaseCompatActivity.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}