package com.example.liveplayerstats

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.liveplayerstats.playercomponents.Player
import com.example.liveplayerstats.playercomponents.PlayerApplication
import com.example.liveplayerstats.playercomponents.PlayerListAdapter
import com.example.liveplayerstats.playercomponents.PlayerViewModel
import com.example.liveplayerstats.playerlist.Standard
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private val playerViewModel: PlayerViewModel by viewModels {
        PlayerViewModel.PlayerViewModelFactory((application as PlayerApplication).repository)
    }

    val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val names = data?.getStringArrayListExtra(NewPlayerActivity.REPLY_NAMES)
            val ids = data?.getStringArrayListExtra(NewPlayerActivity.REPLY_IDS)

            if (names != null && ids != null) {
                names.zip(ids).forEach { pair ->
                    playerViewModel.insert(Player(pair.component1(),pair.component2()))
                }
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.playerRecyclerView)
        val adapter = PlayerListAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        playerViewModel.allPlayers.observe(this) { players ->
            players.let { adapter.submitList(it) }
        }

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                val intent = Intent(this@MainActivity, NewPlayerActivity::class.java)
                resultLauncher.launch(intent)
            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.resetPlayers) {
            playerViewModel.deleteAll()
            Snackbar.make(this, findViewById(R.id.fab),
                "All Players Deleted", Snackbar.LENGTH_SHORT).show()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        fun isNetworkAvailable(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE)
                    as ConnectivityManager
            val nw = connectivityManager.activeNetwork ?: return false
            val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                //for other device how are able to connect with Ethernet
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                //for check internet over Bluetooth
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
                else -> false
            }
        }
    }
}