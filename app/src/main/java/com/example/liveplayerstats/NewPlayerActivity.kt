package com.example.liveplayerstats

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.SearchView
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.liveplayerstats.newplayercomponents.NewPlayerAdapter
import com.example.liveplayerstats.newplayercomponents.NewPlayerStateEvent
import com.example.liveplayerstats.newplayercomponents.NewPlayerViewModel
import com.example.liveplayerstats.playerlist.Standard
import com.example.liveplayerstats.util.DataState
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewPlayerActivity : AppCompatActivity(), NewPlayerAdapter.OnItemClickListener {

    private val viewModel: NewPlayerViewModel by viewModels()
    private lateinit var playerList: MutableList<Standard>
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NewPlayerAdapter
    private lateinit var searchView: androidx.appcompat.widget.SearchView
    private lateinit var confirmFab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_player)

        setupConfirmFab()
        subscribeObservers()
        viewModel.setStateEvent(NewPlayerStateEvent.GetPlayersEvents)
    }

    private fun subscribeObservers() {
        viewModel.dataState.observe(this, Observer { dataState ->
            when(dataState){
                is DataState.Success<List<Standard>> -> {
                    playerList = dataState.data.toMutableList()
                    val playerListCopy = playerList.toMutableList()
                    playerList.clear()
                    for (i in playerListCopy.indices) {
                        if (playerListCopy[i].teamId != "" && playerListCopy[i].personId != "") {
                            playerList.add(playerListCopy[i])
                        }
                    }
                    setupRecyclerView(playerList)
                    setupSearch()
                }
                is DataState.Error -> {
                    Snackbar.make(this, findViewById(android.R.id.content),
                        "Error", Snackbar.LENGTH_SHORT).show()
                }
                is DataState.Loading -> {
                    //None for now
                }
            }
        })

    }

    private fun setupRecyclerView(playerList: List<Standard>) {
        recyclerView = findViewById(R.id.newPlayerRecyclerView)
        adapter = NewPlayerAdapter(playerList.toMutableList(), applicationContext, this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun onItemClick(player: Standard) {
        if (adapter.selectedPlayers.size > 0 && confirmFab.visibility == View.GONE) {
            confirmFab.visibility = View.VISIBLE
        } else if (adapter.selectedPlayers.size == 0) {
            confirmFab.visibility = View.GONE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.search_menu, menu)
        val searchItem = menu?.findItem(R.id.action_search)
        searchView = searchItem?.actionView as androidx.appcompat.widget.SearchView
        searchView.imeOptions = EditorInfo.IME_ACTION_DONE
        return true
    }

    private fun setupSearch() {
        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                adapter.filter.filter(p0)
                return false
            }
        })
    }

    private fun setupConfirmFab() {
        confirmFab = findViewById(R.id.confirmFab)
        confirmFab.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                TODO("Not yet implemented")
            }

        })
    }

}