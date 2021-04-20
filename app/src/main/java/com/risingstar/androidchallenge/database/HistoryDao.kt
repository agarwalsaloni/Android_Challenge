package com.risingstar.androidchallenge.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(historyEntity: HistoryEntity)

    @Query("DELETE FROM history")
    fun clear()

    @Query("SELECT * FROM history")
    fun getHistory():List<HistoryEntity>
}