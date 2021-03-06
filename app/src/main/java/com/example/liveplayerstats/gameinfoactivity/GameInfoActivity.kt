package com.example.liveplayerstats.gameinfoactivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.liveplayerstats.MainActivity
import com.example.liveplayerstats.R
import com.example.liveplayerstats.boxscore.BoxScore
import com.example.liveplayerstats.databinding.ActivityGameInfoBinding
import com.example.liveplayerstats.enums.TeamImgResources
import com.example.liveplayerstats.pbp.Play
import com.example.liveplayerstats.playercomponents.PlayerListAdapter
import com.example.liveplayerstats.util.DataState
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GameInfoActivity : AppCompatActivity() {

    private val gameInfoPBPViewModel: GameInfoPBPViewModel by viewModels()
    private val gameInfoSharedViewModel: GameInfoSharedViewModel by viewModels()
    private val viewModel: GameInfoViewModel by viewModels()
    private lateinit var teamId: String
    private var currentBoxScore: BoxScore? = null
    private lateinit var binding: ActivityGameInfoBinding

    private lateinit var teamArray: Array<String>
    private lateinit var teamImgResources: Array<TeamImgResources>
    private lateinit var resourcesMap: Map<String, TeamImgResources>

    var handler = Handler(Looper.getMainLooper())
    var runnable: Runnable? = null
    val delay = 15000

    private var firstLoad = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameInfoBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        teamId = intent.getStringExtra(MainActivity.TEAM_ID).toString()
        val gson = Gson()
        currentBoxScore = gson.fromJson(intent.getStringExtra(MainActivity.BOX_SCORE_JSON), BoxScore::class.java)

        teamArray = resources.getStringArray(R.array.nba_team_id)
        teamImgResources = TeamImgResources.values()
        resourcesMap = teamArray.zip(teamImgResources).toMap()

        val tabLayout = findViewById<TabLayout>(R.id.gameInfoTabLayout)
        val viewPager2 = findViewById<ViewPager2>(R.id.gameInfoViewPager)
        val adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
        viewPager2.adapter = adapter
        viewPager2.offscreenPageLimit = 2
        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Summary"
                }
                1 -> {
                    tab.text = "Play-by-Play"
                }
                2 -> {
                    tab.text = "Box Score"
                }
            }
        }.attach()

        currentBoxScore?.let { updateScoreboard(it) }
        currentBoxScore?.let { gameInfoSharedViewModel.setBoxScore(it) }
        subscribeObservers()
    }

    private fun subscribeObservers() {
        viewModel.dataState.observe(this, Observer { dataState ->
            when (dataState) {
                is DataState.Success<List<BoxScore>> -> {
                    currentBoxScore = dataState.data[0]
                    gameInfoSharedViewModel.setBoxScore(dataState.data[0])
                    updateScoreboard(dataState.data[0])
                }
                is DataState.Error -> {
                    Snackbar.make(
                        this, binding.root,
                        "Network unavailable", Snackbar.LENGTH_SHORT
                    ).show()
                }
                is DataState.Loading -> {

                }
            }
        })

        gameInfoPBPViewModel.dataState.observe(this, Observer { dataState ->
            when (dataState) {
                is DataState.Success<List<Play>> -> {
                    gameInfoSharedViewModel.setPlays(dataState.data)
                }
                is DataState.Error -> {

                }
                is DataState.Loading -> {

                }
            }
        })
    }

    private fun fetchStats() {
        Log.d("gameinfoactivity", "fetching stats")
        viewModel.setStateEvent(
            GameInfoViewModel.GameInfoStateEvent.GetGameInfoEvent,
            listOf(teamId)
        )
        currentBoxScore?.basicGameData?.gameId?.let {
            gameInfoPBPViewModel.setStateEvent(GameInfoPBPViewModel.GameInfoPBPStateEvent.GetGameInfoPBPEvent,
                it
            )
        }
    }




    private fun updateScoreboard(b: BoxScore) {
        val h = b.basicGameData.hTeam
        val v = b.basicGameData.vTeam

        binding.pHTeam.text = h.triCode
        binding.pVTeam.text = v.triCode
        val hRecord = "${h.win}-${h.loss}"
        val vRecord = "${v.win}-${v.loss}"
        binding.pHTeamRecord.text = hRecord
        binding.pVTeamRecord.text = vRecord
        Glide.with(this)
            .load(resourcesMap[h.teamId]?.id).into(binding.pHTeamLogo)
        Glide.with(this)
            .load(resourcesMap[v.teamId]?.id).into(binding.pVTeamLogo)
        binding.pHTeamPoints.text = h.score
        binding.pVTeamPoints.text = v.score

        val quarterText = "Q${b.basicGameData.period.current}"
        binding.pQuarter.text = ""
        binding.pClock.text = ""
        binding.pFinal.visibility = View.INVISIBLE

        when (b.basicGameData.statusNum) {
            1 -> {
                binding.pQuarter.text = PlayerListAdapter.getDateString(b)
                binding.pClock.text = b.basicGameData.startTimeEastern
                binding.pHTeamPoints.text = ""
                binding.pVTeamPoints.text = ""
            }
            2 -> {
                if (b.basicGameData.period.isHalftime) {
                    binding.pFinal.visibility = View.VISIBLE
                    binding.pFinal.text = "Halftime"
                } else if (b.basicGameData.period.isEndOfPeriod) {
                    binding.pFinal.visibility = View.VISIBLE
                    val endOfQ = "End of $quarterText"
                    binding.pFinal.text = endOfQ
                } else {
                    binding.pQuarter.text = quarterText
                    binding.pClock.text = b.basicGameData.clock
                }
            }
            3 -> {
                binding.pFinal.visibility = View.VISIBLE
                binding.pFinal.text = "Final"
            }
        }
    }

    override fun onResume() {
        if (currentBoxScore?.basicGameData?.statusNum == 2 || firstLoad) {
            fetchStats()
            firstLoad = false
        }
        if (currentBoxScore?.basicGameData?.statusNum == 2) {
            handler.postDelayed(Runnable {
                handler.postDelayed(runnable!!, delay.toLong())
                fetchStats()
            }.also { runnable = it }, delay.toLong())
        }
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        if (runnable != null) {
            handler.removeCallbacks(runnable!!)
        }
    }
}