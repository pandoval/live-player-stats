package com.example.liveplayerstats

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.liveplayerstats.boxscore.Boxscore
import com.example.liveplayerstats.playercomponents.*
import com.example.liveplayerstats.util.DataState
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), PlayerListAdapter.OnItemClickListener,
    PlayerListAdapter.OnItemLongClickListener, OnActionItemClickListener {

    private val playerViewModel: PlayerViewModel by viewModels {
        PlayerViewModel.PlayerViewModelFactory((application as PlayerApplication).repository)
    }

    private val playerStatsViewModel: PlayerStatsViewModel by viewModels()

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PlayerListAdapter
    private lateinit var playerList: List<Player>
    lateinit var mainActionModeCallback: MainActionModeCallback

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var fab: FloatingActionButton

    val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val names = data?.getStringArrayListExtra(NewPlayerActivity.REPLY_NAMES)
            val ids = data?.getStringArrayListExtra(NewPlayerActivity.REPLY_IDS)
            val teamNames = data?.getStringArrayListExtra(NewPlayerActivity.REPLY_TEAMNAMES)
            val teamIds = data?.getStringArrayListExtra(NewPlayerActivity.REPLY_TEAMIDS)

            if (names != null && ids != null && teamNames != null && teamIds != null) {
                val listPlayers = ArrayList<Player>()
                for (i in names.indices) {
                    //add to end of database list (make position the last one)
                    listPlayers.add(Player(names[i], ids[i], teamNames[i], teamIds[i]))
                }
                playerViewModel.insertList(listPlayers)
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById<RecyclerView>(R.id.playerRecyclerView)
        adapter = PlayerListAdapter(this, this, this)
        adapter.setHasStableIds(true)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                val intent = Intent(this@MainActivity, NewPlayerActivity::class.java)
                resultLauncher.launch(intent)
            }

        })

        subscribeObservers()
        playerViewModel.allPlayers.observe(this) { players ->
            playerList = players
            updatePage()
        }

        swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.mainSwipeRefresh)
        swipeRefreshLayout.setOnRefreshListener {
            updatePage()
        }

    }

    private fun subscribeObservers() {
        playerStatsViewModel.dataState.observe(this, Observer { dataState ->
            when(dataState){
                is DataState.Success<List<Boxscore>> -> {
                    val pairList = ArrayList<Pair<Player, Boxscore>>()
                    for (i in playerList.indices) {
                        pairList.add(Pair(playerList[i], dataState.data[i]))
                    }
                    adapter.submitList(pairList)
                    swipeRefreshLayout.isRefreshing = false
                    fab.isEnabled = true
                }
                is DataState.Error -> {
                    Snackbar.make(this, findViewById(android.R.id.content),
                        "Network unavailable", Snackbar.LENGTH_SHORT).show()
                    swipeRefreshLayout.isRefreshing = false
                    fab.isEnabled = true
                }
                is DataState.Loading -> {
                    swipeRefreshLayout.isRefreshing = true
                    fab.isEnabled = false
                }
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
        } else if (item.itemId == R.id.refreshPlayers) {
            swipeRefreshLayout.isRefreshing = true
            updatePage()
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

    private fun updatePage() {
        val teamIds = ArrayList<String>();
        for (player in playerList) {
            teamIds.add(player.teamId)
        }
        playerStatsViewModel.setStateEvent(PlayerStatsViewModel.PlayerStatsStateEvent.GetPlayerStatsEvent,teamIds)
    }

    override fun onItemLongClick(id: String) {
        mainActionModeCallback = MainActionModeCallback(this)
        mainActionModeCallback.startActionMode(recyclerView, R.menu.delete_menu, "Delete players")
        adapter.newSelectionMode = true
        adapter.firstId = id
        adapter.selectionMode = true
        swipeRefreshLayout.isEnabled = false
        adapter.notifyDataSetChanged()
    }

    override fun onItemClick(id: String) {
        intent = Intent(this, GameInfoActivity::class.java)
        startActivity(intent)
    }

    override fun onActionItemClick(item: MenuItem) {
        playerViewModel.deleteList(adapter.selectedIds)
        var string = "players"
        if (adapter.selectedIds.size == 1) {
            string = "player"
        }
        Snackbar.make(this, findViewById(R.id.fab),
            "${adapter.selectedIds.size} $string deleted", Snackbar.LENGTH_SHORT).show()
        adapter.selectionMode = false
        adapter.notifyDataSetChanged()
        mainActionModeCallback.finishActionMode()
    }

    override fun onCanceled() {
        swipeRefreshLayout.isEnabled = true
        adapter.selectionMode = false
        adapter.notifyDataSetChanged()
    }
}