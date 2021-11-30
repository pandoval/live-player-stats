package com.example.liveplayerstats.gameinfoactivity

import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.liveplayerstats.R
import com.example.liveplayerstats.boxscore.ActivePlayer
import com.example.liveplayerstats.boxscore.BoxScore
import com.example.liveplayerstats.databinding.FragmentBoxscoreBinding
import com.example.liveplayerstats.util.DataState
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint

private const val PLAYER_TEAM_ID = "teamId"

@AndroidEntryPoint
class BoxScoreFragment : Fragment() {
    private var firstLoad = true
    private var tabNeverSelected = true

    private val boxScoreViewModel: BoxScoreViewModel by viewModels()
    private lateinit var teamId: String

    private lateinit var hFirstFive: ArrayList<ActivePlayer>
    private lateinit var vFirstFive: ArrayList<ActivePlayer>
    private lateinit var hOthers: ArrayList<ActivePlayer>
    private lateinit var vOthers: ArrayList<ActivePlayer>

    private lateinit var hStatHeaders1: TableRow
    private lateinit var hStatHeaders2: TableRow
    private lateinit var hNameHeaders1: TableRow
    private lateinit var hNameHeaders2: TableRow
    private lateinit var vStatHeaders1: TableRow
    private lateinit var vStatHeaders2: TableRow
    private lateinit var vNameHeaders1: TableRow
    private lateinit var vNameHeaders2: TableRow

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
        if (firstLoad) {
            firstLoad = false
            if (binding.hLinearLayout.visibility == View.GONE && tabNeverSelected) {
                binding.hLinearLayout.visibility = View.VISIBLE
            }
        }

        resetTableLayouts()
        setNameHeaders(boxScore.basicGameData.statusNum == 2)

        binding.notStartedTV.visibility = View.GONE

        binding.boxScoreTabLayout.visibility = View.VISIBLE
        binding.boxScoreTabLayout.getTabAt(0)?.text = teamMap[boxScore.basicGameData.hTeam.triCode]
        binding.boxScoreTabLayout.getTabAt(1)?.text = teamMap[boxScore.basicGameData.vTeam.triCode]

        orderPlayers(boxScore, boxScore.basicGameData.statusNum == 2)

        for (i in 1..5) {
            binding.hTableLayout.addView(addRow(hFirstFive[i-1]), i)
            binding.vTableLayout.addView(addRow(vFirstFive[i-1]), i)
            binding.hNamesTL.addView(addName(hFirstFive[i-1]), i)
            binding.vNamesTL.addView(addName(vFirstFive[i-1]), i)
        }
        for (i in 0 until hOthers.size) {
            binding.hTableLayout.addView(addRow(hOthers[i]), i + 7)
            binding.hNamesTL.addView(addName(hOthers[i]), i + 7)
        }
        for (i in 0 until vOthers.size) {
            binding.vTableLayout.addView(addRow(vOthers[i]), i + 7)
            binding.vNamesTL.addView(addName(vOthers[i]), i + 7)
        }
    }

    private fun resetTableLayouts() {
        binding.hTableLayout.removeAllViews()
        binding.vTableLayout.removeAllViews()
        binding.hNamesTL.removeAllViews()
        binding.vNamesTL.removeAllViews()

        binding.hTableLayout.addView(hStatHeaders1)
        binding.hTableLayout.addView(hStatHeaders2)
        binding.vTableLayout.addView(vStatHeaders1)
        binding.vTableLayout.addView(vStatHeaders2)

        binding.hNamesTL.addView(hNameHeaders1)
        binding.hNamesTL.addView(hNameHeaders2)
        binding.vNamesTL.addView(vNameHeaders1)
        binding.vNamesTL.addView(vNameHeaders2)
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

    private fun setNameHeaders(gameActive: Boolean) {
        val h1 = hNameHeaders1[0] as TextView
        val h2 = hNameHeaders2[0] as TextView
        val v1 = vNameHeaders1[0] as TextView
        val v2 = vNameHeaders2[0] as TextView
        h2.text = getString(R.string.bench_header)
        v2.text = getString(R.string.bench_header)
        if (gameActive) {
            h1.text = getString(R.string.oncourt_header)
            v1.text = getString(R.string.oncourt_header)
        } else {
            h1.text = getString(R.string.starters_header)
            v1.text = getString(R.string.starters_header)
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

    private fun addName(p: ActivePlayer): TableRow {
        val row = TableRow(context)
        row.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
            TableRow.LayoutParams.WRAP_CONTENT)
        val name = "${p.firstName.substring(0,1)}.${p.lastName}"
        val tv = valueTV(name)
        tv.gravity = Gravity.START
        tv.setPadding(TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 10f,resources.displayMetrics)
            .toInt(), 0, 0, 0)
        row.addView(tv)
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
        hStatHeaders1 = binding.hTableLayout[0] as TableRow
        hStatHeaders2 = binding.hTableLayout[1] as TableRow
        hNameHeaders1 = binding.hNamesTL[0] as TableRow
        hNameHeaders2 = binding.hNamesTL[1] as TableRow
        vStatHeaders1 = binding.vTableLayout[0] as TableRow
        vStatHeaders2 = binding.vTableLayout[1] as TableRow
        vNameHeaders1 = binding.vNamesTL[0] as TableRow
        vNameHeaders2 = binding.vNamesTL[1] as TableRow

        binding.boxScoreTabLayout.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        binding.hLinearLayout.visibility = View.VISIBLE
                        binding.vLinearLayout.visibility = View.GONE
                    }
                    1 -> {
                        if (tabNeverSelected) {
                            tabNeverSelected = false
                        }
                        binding.vLinearLayout.visibility = View.VISIBLE
                        binding.hLinearLayout.visibility = View.GONE
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

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