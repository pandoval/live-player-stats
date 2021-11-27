package com.example.liveplayerstats.gameinfoactivity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.example.liveplayerstats.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class GameInfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_info)

        val tabLayout = findViewById<TabLayout>(R.id.gameInfoTabLayout)
        val viewPager2 = findViewById<ViewPager2>(R.id.gameInfoViewPager)

        val adapter = ViewPagerAdapter(supportFragmentManager, lifecycle)
        viewPager2.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Summary"
                }
                1 -> {
                    tab.text = "Box Score"
                }
            }
        }.attach()
    }
}