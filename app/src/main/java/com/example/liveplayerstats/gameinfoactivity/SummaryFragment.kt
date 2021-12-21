package com.example.liveplayerstats.gameinfoactivity

import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.size
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.liveplayerstats.R
import com.example.liveplayerstats.boxscore.BoxScore
import com.example.liveplayerstats.databinding.FragmentSummaryBinding

class SummaryFragment : Fragment() {

    private val gameInfoSharedViewModel: GameInfoSharedViewModel by activityViewModels()
    private val overtimeTVs = mutableListOf<TextView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        gameInfoSharedViewModel.boxScore.observe(this, Observer { b ->
            if (b.basicGameData.statusNum != 1) {
                binding.teamStatsTL.visibility = View.VISIBLE
                binding.summaryTableLayout.visibility = View.VISIBLE
                binding.notStartedTV.visibility = View.GONE
                setTable(b)
                setTeamStats(b)
            } else {
                binding.teamStatsTL.visibility = View.GONE
                binding.summaryTableLayout.visibility = View.GONE
                binding.notStartedTV.visibility = View.VISIBLE
            }

        })
    }

    private fun setTable(b: BoxScore) {
        val h = b.basicGameData.hTeam
        val v = b.basicGameData.vTeam

        binding.hName.text = h.triCode
        binding.vName.text = v.triCode
        binding.hTot.text = h.score
        binding.vTot.text = v.score

        when (b.basicGameData.period.current) {
            1 -> {
                binding.hq1.text = h.linescore[0].score
                binding.hq2.text = "-"
                binding.hq3.text = "-"
                binding.hq4.text = "-"
            }
            2 -> {
                binding.hq1.text = h.linescore[0].score
                binding.hq2.text = h.linescore[1].score
                binding.hq3.text = "-"
                binding.hq4.text = "-"
            }
            3 -> {
                binding.hq1.text = h.linescore[0].score
                binding.hq2.text = h.linescore[1].score
                binding.hq3.text = h.linescore[2].score
                binding.hq4.text = "-"
            }
            else -> {
                binding.hq1.text = h.linescore[0].score
                binding.hq2.text = h.linescore[1].score
                binding.hq3.text = h.linescore[2].score
                binding.hq4.text = h.linescore[3].score
            }
        }

        when (b.basicGameData.period.current) {
            1 -> {
                binding.vq1.text = v.linescore[0].score
                binding.vq2.text = "-"
                binding.vq3.text = "-"
                binding.vq4.text = "-"
            }
            2 -> {
                binding.vq1.text = v.linescore[0].score
                binding.vq2.text = v.linescore[1].score
                binding.vq3.text = "-"
                binding.vq4.text = "-"
            }
            3 -> {
                binding.vq1.text = v.linescore[0].score
                binding.vq2.text = v.linescore[1].score
                binding.vq3.text = v.linescore[2].score
                binding.vq4.text = "-"
            }
            else -> {
                binding.vq1.text = v.linescore[0].score
                binding.vq2.text = v.linescore[1].score
                binding.vq3.text = v.linescore[2].score
                binding.vq4.text = v.linescore[3].score
            }
        }

        if (b.basicGameData.period.current > 4) {
            val numOT = b.basicGameData.period.current - 4
            val currentOT = overtimeTVs.size / 3
            if (currentOT < numOT) {
                val difference = numOT - currentOT
                for (i in 1..difference) {
                    val tv1 = newTableTV("OT${currentOT + i}")
                    val tv2 = newTableTV(h.linescore[currentOT + i + 3].score)
                    val tv3 = newTableTV(v.linescore[currentOT + i + 3].score)

                    overtimeTVs.add(tv1)
                    overtimeTVs.add(tv2)
                    overtimeTVs.add(tv3)

                    binding.headerRow.addView(tv1, binding.headerRow.size - 1)
                    binding.hRow.addView(tv2, binding.hRow.size - 1)
                    binding.vRow.addView(tv3, binding.vRow.size - 1)
                }
            } else {
                for (i in 1..currentOT) {
                    val index = (i - 1) * 3
                    val headerText = "OT$i"
                    overtimeTVs[index].text = headerText
                    overtimeTVs[index + 1].text = h.linescore[i + 3].score
                    overtimeTVs[index + 2].text = v.linescore[i + 3].score
                }
            }
        }
    }

    private fun newTableTV(text: String): TextView {
        val tv = TextView(context)
        tv.layoutParams = TableRow.LayoutParams(
            TableRow.LayoutParams.MATCH_PARENT,
            TableRow.LayoutParams.MATCH_PARENT, 2f)
        tv.text = text
        tv.gravity = Gravity.CENTER
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.summary_table_text_size))
        tv.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_gray))
        return tv
    }

    private fun setTeamStats(b: BoxScore) {
        val h = b.stats.hTeam.totals
        val v = b.stats.vTeam.totals

        val hFGText = "${h.fgm}/${h.fga} (${h.fgp}%)"
        binding.hFG.text = hFGText
        val hTPText = "${h.tpm}/${h.tpa} (${h.tpp}%)"
        binding.hTP.text = hTPText
        val hFTText = "${h.ftm}/${h.fta} (${h.ftp}%)"
        binding.hFT.text = hFTText
        binding.hAst.text = h.assists
        binding.hReb.text = h.totReb
        binding.hOReb.text = h.offReb
        binding.hDReb.text = h.defReb
        binding.hStl.text = h.steals
        binding.hBlk.text = h.blocks
        binding.hTo.text = h.turnovers
        binding.hPOT.text = b.stats.hTeam.pointsOffTurnovers
        binding.hPIP.text = b.stats.hTeam.pointsInPaint
        binding.hSCP.text = b.stats.hTeam.secondChancePoints
        binding.hFBP.text = b.stats.hTeam.fastBreakPoints
        binding.hFouls.text = h.pFouls

        val vFGText = "(${v.fgp}%) ${v.fgm}/${v.fga}"
        binding.vFG.text = vFGText
        val vTPText = "(${v.tpp}%) ${v.tpm}/${v.tpa}"
        binding.vTP.text = vTPText
        val vFTText = "(${v.ftp}%) ${v.ftm}/${v.fta}"
        binding.vFT.text = vFTText
        binding.vAst.text = v.assists
        binding.vReb.text = v.totReb
        binding.vOReb.text = v.offReb
        binding.vDReb.text = v.defReb
        binding.vStl.text = v.steals
        binding.vBlk.text = v.blocks
        binding.vTo.text = v.turnovers
        binding.vPOT.text = b.stats.vTeam.pointsOffTurnovers
        binding.vPIP.text = b.stats.vTeam.pointsInPaint
        binding.vSCP.text = b.stats.vTeam.secondChancePoints
        binding.vFBP.text = b.stats.vTeam.fastBreakPoints
        binding.vFouls.text = v.pFouls
    }

    private var _binding: FragmentSummaryBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSummaryBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    companion object {

    }
}