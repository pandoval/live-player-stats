package com.example.liveplayerstats.gameinfoactivity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.liveplayerstats.R
import com.example.liveplayerstats.databinding.FragmentPBPBinding
import com.example.liveplayerstats.databinding.FragmentSummaryBinding

class SummaryFragment : Fragment() {

    private val gameInfoSharedViewModel: GameInfoSharedViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        gameInfoSharedViewModel.boxScore.observe(this, Observer { b ->
            val h = b.basicGameData.hTeam
            val v = b.basicGameData.vTeam

            binding.hName.text = h.triCode
            binding.hq1.text = h.linescore[0].score
            binding.hq2.text = h.linescore[1].score
            binding.hq3.text = h.linescore[2].score
            binding.hq4.text = h.linescore[3].score
            binding.hTot.text = h.score

            binding.vName.text = v.triCode
            binding.vq1.text = v.linescore[0].score
            binding.vq2.text = v.linescore[1].score
            binding.vq3.text = v.linescore[2].score
            binding.vq4.text = v.linescore[3].score
            binding.vTot.text = v.score
        })
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