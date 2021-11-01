package com.example.liveplayerstats.playercomponents

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = arrayOf(Player::class), version = 1, exportSchema = false)
abstract class PlayerDatabase: RoomDatabase() {

    abstract fun playerDao(): PlayerDao

    private class PlayerDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.playerDao())
                }
            }
        }

        suspend fun populateDatabase(playerDao: PlayerDao) {
            playerDao.deleteAll()

            var player = Player("Jagoat","123")
            playerDao.insert(player)
            player = Player("JAGOD!","223")
            playerDao.insert(player)

        }
    }

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: PlayerDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): PlayerDatabase {

            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PlayerDatabase::class.java,
                    "player_database"
                ).addCallback(PlayerDatabaseCallback(scope)).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }

}

