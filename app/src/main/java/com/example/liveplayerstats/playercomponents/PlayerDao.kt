package com.example.liveplayerstats.playercomponents

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface  PlayerDao {

    @Query("SELECT * FROM player_table ORDER BY name ASC")
    fun getAlphabetizedPlayers(): Flow<List<Player>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(player: Player)

    @Query("DELETE FROM player_table")
    suspend fun deleteAll()
}