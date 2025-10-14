package com.example.androidmycoffee.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.androidmycoffee.data.datasource.local.entity.CoffeeEntity

@Dao
interface CoffeeDao {
    @Query("SELECT * FROM coffee")
    suspend fun getAll(): List<CoffeeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<CoffeeEntity>)
}
