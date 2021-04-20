package com.risingstar.androidchallenge.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [HistoryEntity::class],version = 1,exportSchema = false)
abstract class HistoryDatabase : RoomDatabase() {
    abstract fun hisDao() : HistoryDao
}