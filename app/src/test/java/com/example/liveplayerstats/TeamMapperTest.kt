package com.example.liveplayerstats

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.liveplayerstats.util.TeamMapper
import org.junit.Test
import org.junit.Assert.assertTrue
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TeamMapperTest {

    @Test
    fun triCodeNameMapTest() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val map = TeamMapper.triCodeNameMap(context)
        assertTrue(map["ATL"] == "Hawks")
        assertTrue(map["POR"] == "Trail Blazers" )
        assertTrue(map["UGA"] == null)
        assertTrue(map.size == 30)
    }
}