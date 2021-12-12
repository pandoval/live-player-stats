package com.example.liveplayerstats.gameinfoactivity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.liveplayerstats.R
import com.example.liveplayerstats.databinding.FragmentPBPBinding

class PBPFragment : Fragment() {

    private val gameInfoSharedViewModel: GameInfoSharedViewModel by activityViewModels()

    lateinit var adapter: PlaysAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        gameInfoSharedViewModel.plays.observe(this, Observer { plays ->
            if (gameInfoSharedViewModel.boxScore.value?.basicGameData?.statusNum != 1) {
                binding.playsRecyclerView.visibility = View.VISIBLE
                binding.notStartedTV.visibility = View.GONE
                adapter.submitList(plays)
            } else {
                binding.playsRecyclerView.visibility = View.GONE
                binding.notStartedTV.visibility = View.VISIBLE
            }

        })
    }

    private var _binding: FragmentPBPBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPBPBinding.inflate(inflater, container, false)
        val view = binding.root

        adapter = PlaysAdapter()
        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                (binding.playsRecyclerView.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(positionStart, 0)
            }
        })
        binding.playsRecyclerView.adapter = adapter
        binding.playsRecyclerView.layoutManager = LinearLayoutManager(context)

        return view
    }

    companion object {

    }
}