package com.example.liveplayerstats.gameinfoactivity

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.liveplayerstats.R

private const val PLAYER_TEAM_ID = "teamId"

class BoxScoreFragment : Fragment() {

    private lateinit var teamId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments.let {
            teamId = it?.getString(PLAYER_TEAM_ID).toString()
        }

        
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_boxscore, container, false)
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