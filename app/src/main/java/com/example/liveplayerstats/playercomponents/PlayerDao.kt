package com.example.liveplayerstats.playercomponents

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {

    @Query("SELECT * FROM player_table ORDER BY name ASC")
    fun getAlphabetizedPlayers(): Flow<List<Player>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(player: Player)

    @Query("DELETE FROM player_table")
    suspend fun deleteAll()

    @Query("DELETE FROM player_table WHERE id = :id")
    suspend fun deleteById(id: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(playerList: List<Player>)
}