package com.risingstar.androidchallenge.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) var id : Int,
    @ColumnInfo(name = "searchedItem")var searchedItem : String?
)