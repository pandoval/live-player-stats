package com.example.liveplayerstats.gameinfoactivity

import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.liveplayerstats.R
import com.example.liveplayerstats.boxscore.ActivePlayer
import com.example.liveplayerstats.boxscore.BoxScore
import com.example.liveplayerstats.databinding.FragmentBoxscoreBinding
import com.example.liveplayerstats.util.DataState
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

private const val PLAYER_TEAM_ID = "teamId"

@AndroidEntryPoint
class BoxScoreFragment : Fragment() {

    private val boxScoreViewModel: BoxScoreViewModel by viewModels()
    private lateinit var teamId: String

    private lateinit var hFirstFive: ArrayList<ActivePlayer>
    private lateinit var vFirstFive: ArrayList<ActivePlayer>
    private lateinit var hOthers: ArrayList<ActivePlayer>
    private lateinit var vOthers: ArrayList<ActivePlayer>

    private lateinit var tricodeArray: Array<String>
    private lateinit var teamNameArray: Array<String>
    private lateinit var teamMap: Map<String,String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments.let {
            teamId = it?.getString(PLAYER_TEAM_ID).toString()
        }

        tricodeArray = requireContext().resources.getStringArray(R.array.nba_tricode)
        teamNameArray = requireContext().resources.getStringArray(R.array.nba_team_names)
        teamMap = tricodeArray.zip(teamNameArray).toMap()

        subscribeObservers()
        fetchStats()
    }

    private fun setupBoxScore(boxScore: BoxScore) {
        binding.notStartedTV.visibility = View.GONE

        binding.boxScoreTabLayout.visibility = View.VISIBLE
        binding.boxScoreTabLayout.getTabAt(0)?.text = teamMap[boxScore.basicGameData.hTeam.triCode]
        binding.boxScoreTabLayout.getTabAt(1)?.text = teamMap[boxScore.basicGameData.vTeam.triCode]

        binding.hLinearLayout.visibility = View.VISIBLE
        binding.vLinearLayout.visibility = View.VISIBLE
        orderPlayers(boxScore, boxScore.basicGameData.statusNum == 2)
        Log.d("hi",hFirstFive.toString())
        Log.d("hi",hOthers.toString())
        Log.d("hi",vFirstFive.toString())
        Log.d("hi",vOthers.toString())


        for (i in 1..5) {
            binding.hTableLayout.addView(addRow(hFirstFive[i-1]), i)
            binding.vTableLayout.addView(addRow(vFirstFive[i-1]), i)
        }
        for (i in 0 until hOthers.size) {
            binding.hTableLayout.addView(addRow(hOthers[i]), i + 7)
        }
        for (i in 0 until vOthers.size) {
            binding.vTableLayout.addView(addRow(vOthers[i]), i + 7)
        }
    }

    private fun orderPlayers(boxScore: BoxScore, gameActive: Boolean) {
        val hTeamPlayers = arrayListOf<ActivePlayer>()
        val vTeamPlayers = arrayListOf<ActivePlayer>()

        for(player in boxScore.stats.activePlayers) {
            if (player.teamId == boxScore.basicGameData.hTeam.teamId) {
                hTeamPlayers.add(player)
            } else {
                vTeamPlayers.add(player)
            }
        }

        hTeamPlayers.sortByDescending { it.min.replace(":", ".").toDouble() }
        vTeamPlayers.sortByDescending { it.min.replace(":", ".").toDouble() }

        hFirstFive = arrayListOf()
        vFirstFive = arrayListOf()
        hOthers = arrayListOf()
        vOthers = arrayListOf()

        if (gameActive) {
            for (player in hTeamPlayers) {
                if (player.isOnCourt) {
                    hFirstFive.add(player)
                } else {
                    hOthers.add(player)
                }
            }
            for (player in vTeamPlayers) {
                if (player.isOnCourt) {
                    vFirstFive.add(player)
                } else {
                    vOthers.add(player)
                }
            }
        } else {
            for (player in hTeamPlayers) {
                if (player.pos != "") {
                    hFirstFive.add(player)
                } else {
                    hOthers.add(player)
                }
            }
            for (player in vTeamPlayers) {
                if (player.pos != "") {
                    vFirstFive.add(player)
                } else {
                    vOthers.add(player)
                }
            }
        }
    }

    private fun addRow(p: ActivePlayer): TableRow {
        val row = TableRow(context)
        row.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
            TableRow.LayoutParams.WRAP_CONTENT)
        row.addView(valueTV(p.min))
        row.addView(valueTV(p.points))
        row.addView(valueTV(p.totReb))
        row.addView(valueTV(p.assists))
        row.addView(valueTV(p.steals))
        row.addView(valueTV(p.blocks))
        val fgText = "${p.fgm}-${p.fga}"
        row.addView(valueTV(fgText))
        val tpText = "${p.tpm}-${p.tpa}"
        row.addView(valueTV(tpText))
        val ftText = "${p.ftm}-${p.fta}"
        row.addView(valueTV(ftText))
        row.addView(valueTV(p.offReb))
        row.addView(valueTV(p.defReb))
        row.addView(valueTV(p.turnovers))
        row.addView(valueTV(p.pFouls))
        row.addView(valueTV(p.plusMinus))
        return row
    }

    private fun valueTV(text: String): TextView {
        val tv = TextView(context)
        tv.layoutParams = TableRow.LayoutParams(TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 44f, resources.displayMetrics).toInt(),
            TableRow.LayoutParams.MATCH_PARENT)
        tv.text = text
        tv.gravity = Gravity.CENTER
        tv.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.stat_text_size))
        return tv
    }

    private fun subscribeObservers() {
        boxScoreViewModel.dataState.observe(this, Observer { dataState ->
            when(dataState){
                is DataState.Success<List<BoxScore>> -> {
                    if (dataState.data[0].basicGameData.statusNum != 1) {
                        setupBoxScore(dataState.data[0])
                    } else {
                        binding.hLinearLayout.visibility = View.GONE
                        binding.vLinearLayout.visibility = View.GONE
                        binding.boxScoreTabLayout.visibility = View.GONE
                        binding.notStartedTV.visibility = View.VISIBLE
                    }


                }
                is DataState.Error -> {
                    Snackbar.make(requireContext(), binding.root,
                        "Network unavailable", Snackbar.LENGTH_SHORT).show()
                }
                is DataState.Loading -> {
                    //TODO("ADD THING IN ACTION BAR MAYBE")
                }
            }
        })

    }

    private fun fetchStats() {
        boxScoreViewModel.setStateEvent(BoxScoreViewModel.BoxScoreStateEvent.GetBoxScoreEvent,
            listOf(teamId))
    }

    private var _binding: FragmentBoxscoreBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBoxscoreBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(teamId: String) =
            BoxScoreFragment().apply {
                arguments = Bundle().apply {
                    putString(PLAYER_TEAM_ID, teamId)
                }
            }
    }
}