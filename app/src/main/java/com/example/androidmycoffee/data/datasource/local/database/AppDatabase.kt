package com.example.androidmycoffee.data.datasource.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.androidmycoffee.data.datasource.converter.Converter
import com.example.androidmycoffee.data.datasource.local.dao.CoffeeDao
import com.example.androidmycoffee.data.datasource.local.entity.CoffeeEntity

@Database(
    entities = [CoffeeEntity::class],
    version = 1,
    exportSchema = false,
)
@TypeConverters(Converter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun coffeeDao(): CoffeeDao
}
