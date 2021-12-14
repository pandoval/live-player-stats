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
import com.example.liveplayerstats.databinding.FragmentPBPBinding
import com.example.liveplayerstats.databinding.FragmentSummaryBinding

class SummaryFragment : Fragment() {

    private val gameInfoSharedViewModel: GameInfoSharedViewModel by activityViewModels()
    private val overtimeTVs = mutableListOf<TextView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        gameInfoSharedViewModel.boxScore.observe(this, Observer { b ->
            if (b.basicGameData.statusNum != 1) {
                binding.summaryTableLayout.visibility = View.VISIBLE
                binding.notStartedTV.visibility = View.GONE
                setTable(b)
            } else {
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
                    val tv1 = newTV("OT${currentOT + i}")
                    val tv2 = newTV(h.linescore[currentOT + i + 3].score)
                    val tv3 = newTV(v.linescore[currentOT + i + 3].score)

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

    private fun newTV(text: String): TextView {
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