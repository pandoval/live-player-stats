package com.example.liveplayerstats.gameinfoactivity

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.core.view.marginEnd
import androidx.core.widget.TextViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.liveplayerstats.R
import com.example.liveplayerstats.boxscore.ActivePlayer
import com.example.liveplayerstats.boxscore.BoxScore
import com.example.liveplayerstats.databinding.FragmentBoxscoreBinding
import com.example.liveplayerstats.util.DataState
import com.example.liveplayerstats.util.TeamMapper
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint

private const val PLAYER_TEAM_ID = "teamId"

class BoxScoreFragment : Fragment() {
    //These two variables are to prevent the two preset rows from showing before the stats load
    private var firstLoad = true
    private var tabNeverSelected = true

    private val gameInfoSharedViewModel: GameInfoSharedViewModel by activityViewModels()

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

    private lateinit var teamMap: Map<String,String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //arguments.let {
        //    teamId = it?.getString(PLAYER_TEAM_ID).toString()
        //}

        teamMap = TeamMapper.tricodeNameMap(requireContext())

        gameInfoSharedViewModel.boxScore.observe(this, Observer { b ->
            if (b.basicGameData.statusNum != 1) {
                setupBoxScore(b)
            } else {
                binding.hScrollView.visibility = View.GONE
                binding.vScrollView.visibility = View.GONE
                binding.boxScoreTabLayout.visibility = View.GONE
                binding.notStartedTV.visibility = View.VISIBLE
            }
        })
    }

    private fun setupBoxScore(boxScore: BoxScore) {
        if (firstLoad) {
            firstLoad = false
            if (binding.hScrollView.visibility == View.GONE && tabNeverSelected) {
                binding.hScrollView.visibility = View.VISIBLE
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
            var rowLighter = false
            if (i % 2 == 0) {
                rowLighter = true
            }
            Log.d("hfirst", hFirstFive.toString())
            Log.d("vfirst", vFirstFive.toString())
            binding.hTableLayout.addView(addRow(hFirstFive[i-1], rowLighter), i)
            binding.vTableLayout.addView(addRow(vFirstFive[i-1], rowLighter), i)
            binding.hNamesTL.addView(addName(hFirstFive[i-1], rowLighter), i)
            binding.vNamesTL.addView(addName(vFirstFive[i-1], rowLighter), i)
        }
        for (i in 0 until hOthers.size) {
            var rowLighter = false
            if (i % 2 == 1) {
                rowLighter = true
            }
            binding.hTableLayout.addView(addRow(hOthers[i], rowLighter), i + 7)
            binding.hNamesTL.addView(addName(hOthers[i], rowLighter), i + 7)
        }
        for (i in 0 until vOthers.size) {
            var rowLighter = false
            if (i % 2 == 1) {
                rowLighter = true
            }
            binding.vTableLayout.addView(addRow(vOthers[i], rowLighter), i + 7)
            binding.vNamesTL.addView(addName(vOthers[i], rowLighter), i + 7)
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
            if (player.min.isNotEmpty()) {
                if (player.teamId == boxScore.basicGameData.hTeam.teamId) {
                    hTeamPlayers.add(player)
                } else {
                    vTeamPlayers.add(player)
                }
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

        hOthers.removeAll { it.min == "0:00" }
        vOthers.removeAll {it.min == "0:00"}
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

    private fun addRow(p: ActivePlayer, rowLighter: Boolean): TableRow {
        val row = TableRow(context)
        if (rowLighter) {
            row.setBackgroundResource(R.color.darker_gray)
        } else {
            row.setBackgroundResource(R.color.darkest_gray)
        }
        row.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
            TableRow.LayoutParams.WRAP_CONTENT)
        row.minimumHeight = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, ROW_HEIGHT,resources.displayMetrics).toInt()
        row.gravity = Gravity.START or Gravity.CENTER_VERTICAL
        row.addView(valueTV(p.pos))
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

    private fun addName(p: ActivePlayer, rowLighter: Boolean): TableRow {
        val row = TableRow(context)
        if (rowLighter) {
            row.setBackgroundResource(R.color.darker_gray)
        } else {
            row.setBackgroundResource(R.color.darkest_gray)
        }
        row.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
            TableRow.LayoutParams.WRAP_CONTENT)
        row.minimumHeight = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, ROW_HEIGHT,resources.displayMetrics).toInt()
        row.gravity = Gravity.START or Gravity.CENTER_VERTICAL
        val horizontalLinearLayout = LinearLayout(context)
        horizontalLinearLayout.orientation = LinearLayout.HORIZONTAL

        val name = "${p.firstName.substring(0,1)}.${p.lastName}"
        val tv = TextView(context)
        tv.layoutParams = LinearLayout.LayoutParams(TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 116f,resources.displayMetrics)
            .toInt(),
            LinearLayout.LayoutParams.MATCH_PARENT)
        tv.maxLines = 1
        //TextViewCompat.setAutoSizeTextTypeWithDefaults(tv, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM)
        tv.ellipsize = TextUtils.TruncateAt.END
        tv.gravity = Gravity.START or Gravity.CENTER_VERTICAL
        tv.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_gray))
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.box_score_text_size))
        tv.text = name
        tv.setPadding(TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 10f,resources.displayMetrics)
            .toInt(), 0, TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 8f,resources.displayMetrics)
            .toInt(), 0)
        horizontalLinearLayout.addView(tv)
        row.addView(horizontalLinearLayout)
        return row
    }

    private fun valueTV(text: String): TextView {
        val tv = TextView(context)
        tv.layoutParams = TableRow.LayoutParams(TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 44f, resources.displayMetrics).toInt(),
            TableRow.LayoutParams.MATCH_PARENT)
        tv.text = text
        tv.gravity = Gravity.CENTER
        tv.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_gray))
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.box_score_text_size))
        return tv
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
                        binding.hScrollView.visibility = View.VISIBLE
                        binding.vScrollView.visibility = View.GONE
                    }
                    1 -> {
                        if (tabNeverSelected) {
                            tabNeverSelected = false
                        }
                        binding.vScrollView.visibility = View.VISIBLE
                        binding.hScrollView.visibility = View.GONE
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

        const val ROW_HEIGHT = 30f
    }
}