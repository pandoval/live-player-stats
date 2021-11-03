package com.example.liveplayerstats

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
}