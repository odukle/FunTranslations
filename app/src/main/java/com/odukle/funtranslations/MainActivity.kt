package com.odukle.funtranslations

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView

var ma: MainActivity? = null
const val INTERSTITIAL_UNIT_ID = "ca-app-pub-9193191601772541/2982266308"

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    lateinit var mInterstitialAd: InterstitialAd
    var count = 0
    lateinit var adRequest: AdRequest
    val loadQ = LoadRandomQuotes()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ma = this
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        if (isOnline(this)) {
            loadQ.loadQuotesPage()
        } else {
            Toast.makeText(this, "No Internet!", Toast.LENGTH_SHORT).show()
        }

        MobileAds.initialize(this)
        adRequest = AdRequest.Builder().build()
        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = INTERSTITIAL_UNIT_ID
        mInterstitialAd.loadAd(adRequest)
        mInterstitialAd.adListener = object : AdListener() {
            override fun onAdClosed() {
                mInterstitialAd.loadAd(AdRequest.Builder().build())
            }
        }

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)

        val navView: NavigationView = findViewById(R.id.nav_view)
        navController = findNavController(R.id.nav_host_fragment)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_shakespeare,
                R.id.nav_yoda,
                R.id.nav_dothraki,
                R.id.nav_valyrian,
                R.id.nav_hodor,
                R.id.nav_wakandan,
                R.id.nav_minion,
                R.id.nav_avatar,
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_about -> {
                val dialog = MaterialAlertDialogBuilder(this)
                when (navController.currentDestination?.id) {
                    R.id.nav_shakespeare -> {
                        dialog.setMessage(getString(R.string.shake_str))
                            .show()
                    }
                    R.id.nav_yoda -> {
                        val dia = MaterialAlertDialogBuilder(this)
                        dia.setMessage(getString(R.string.yoda_str))
                            .show()
                    }
                    R.id.nav_dothraki -> {
                        dialog.setMessage(getString(R.string.dothraki_str))
                            .show()
                    }
                    R.id.nav_valyrian -> {
                        dialog.setMessage(getString(R.string.valyrian_str))
                            .show()
                    }
                    R.id.nav_hodor -> {
                        dialog.setMessage(getString(R.string.hodor_str))
                            .show()
                    }
                    R.id.nav_wakandan -> {
                        dialog.setMessage(getString(R.string.wakanda_str))
                            .show()
                    }
                    R.id.nav_minion -> {
                        dialog.setMessage(getString(R.string.minion_str))
                            .show()
                    }
                    R.id.nav_avatar -> {
                        dialog.setMessage(getString(R.string.navi_str))
                            .show()
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
//        val navController = findNavController(R.id.nav_host_fragment)
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun openInsta(view: View) {
        //Get url from tag
        val url = view.tag as String
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.addCategory(Intent.CATEGORY_BROWSABLE)

        //pass the url to intent data
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

    fun openEmail(view: View) {
        val dialog = MaterialAlertDialogBuilder(this)
        dialog.setTitle("Open Gmail")
            .setPositiveButton(
                "Yes"
            ) { dialog, _ ->
                val id = view.tag as String
                val intent = Intent()
                intent.action = Intent.ACTION_VIEW
                intent.data = Uri.parse("mailto:$id")
                startActivity(intent)
            }
            .setNegativeButton(
                "Cancel"
            ) { dialog, _ -> dialog.dismiss() }
            .show()

    }

    override fun onBackPressed() {
        finish()
    }
}

@RequiresApi(Build.VERSION_CODES.M)
fun isOnline(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val capabilities =
        connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
    if (capabilities != null) {
        when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                return true
            }

            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                return true
            }

            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                return true
            }
        }
    }
    return false
}