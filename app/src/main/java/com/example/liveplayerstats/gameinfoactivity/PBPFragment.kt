package com.example.liveplayerstats.gameinfoactivity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.liveplayerstats.R
import com.example.liveplayerstats.databinding.FragmentPBPBinding

class PBPFragment : Fragment() {

    private val gameInfoSharedViewModel: GameInfoSharedViewModel by activityViewModels()

    lateinit var adapter: PlaysAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        gameInfoSharedViewModel.plays.observe(this, Observer { plays ->
            adapter.submitList(plays)
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
        binding.playsRecyclerView.adapter = adapter
        binding.playsRecyclerView.layoutManager = LinearLayoutManager(context)

        return view
    }

    companion object {

    }
}