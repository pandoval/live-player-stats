package com.example.liveplayerstats.gameinfoactivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import com.example.liveplayerstats.playercomponents.PlayerListAdapter
import com.example.liveplayerstats.util.DataState
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GameInfoActivity : AppCompatActivity() {

    private val viewModel: GameInfoViewModel by viewModels()
    private lateinit var teamId: String
    private var initialBoxScore: BoxScore? = null
    private lateinit var binding: ActivityGameInfoBinding

    private lateinit var teamArray: Array<String>
    private lateinit var teamImgResources: Array<TeamImgResources>
    private lateinit var resourcesMap: Map<String, TeamImgResources>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameInfoBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        teamId = intent.getStringExtra(MainActivity.TEAM_ID).toString()
        val gson = Gson()
        initialBoxScore = gson.fromJson(intent.getStringExtra(MainActivity.BOX_SCORE_JSON), BoxScore::class.java)

        teamArray = resources.getStringArray(R.array.nba_team_id)
        teamImgResources = TeamImgResources.values()
        resourcesMap = teamArray.zip(teamImgResources).toMap()

        val tabLayout = findViewById<TabLayout>(R.id.gameInfoTabLayout)
        val viewPager2 = findViewById<ViewPager2>(R.id.gameInfoViewPager)
        val adapter = ViewPagerAdapter(supportFragmentManager, lifecycle, teamId)
        viewPager2.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Summary"
                }
                1 -> {
                    tab.text = "Box Score"
                }
            }
        }.attach()

        initialBoxScore?.let { updateScoreboard(it) }
        subscribeObservers()
        fetchScoreboardStats()
    }

    private fun subscribeObservers() {
        viewModel.dataState.observe(this, Observer { dataState ->
            when (dataState) {
                is DataState.Success<List<BoxScore>> -> {
                    updateScoreboard(dataState.data[0])
                }
                is DataState.Error -> {
                    Snackbar.make(
                        this, binding.root,
                        "Network unavailable", Snackbar.LENGTH_SHORT
                    ).show()
                }
                is DataState.Loading -> {
                    //TODO("ADD THING IN ACTION BAR MAYBE")
                }
            }
        })

    }

    private fun fetchScoreboardStats() {
        viewModel.setStateEvent(
            GameInfoViewModel.GameInfoStateEvent.GetGameInfoEvent,
            listOf(teamId)
        )
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
}