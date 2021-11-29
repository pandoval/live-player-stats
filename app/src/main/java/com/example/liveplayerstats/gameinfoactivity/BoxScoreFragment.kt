package com.example.liveplayerstats.gameinfoactivity

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.liveplayerstats.R
import com.example.liveplayerstats.boxscore.BoxScore
import com.example.liveplayerstats.databinding.FragmentBoxscoreBinding

private const val PLAYER_TEAM_ID = "teamId"

class BoxScoreFragment : Fragment() {

    private lateinit var teamId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments.let {
            teamId = it?.getString(PLAYER_TEAM_ID).toString()
        }
    }

    private fun addRows(boxscore: BoxScore, homeTeam: Boolean) {
        val tableLayout: TableLayout = if (homeTeam) binding.hTableLayout else binding.vTableLayout
        for (i in 0..15) {
            val row = TableRow(context)
            //row.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                //TableRow.LayoutParams.WRAP_CONTENT)

        }
    }

    private fun valueTV(text: String): TextView {
        val tv = TextView(context)
        tv.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            resources.getDimension(R.dimen.boxscore_tv_width).toInt())
        tv.text = text
        tv.gravity = Gravity.CENTER
        tv.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        tv.textSize = resources.getDimension(R.dimen.stat_text_size)
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