package com.example.androidmycoffee.data.datasource.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.androidmycoffee.data.datasource.converter.Converter
import com.example.androidmycoffee.data.datasource.local.dao.CoffeeDao
import com.example.androidmycoffee.data.datasource.local.entity.CoffeeEntity
import com.example.androidmycoffee.domain.model.TypeCoffee
import java.math.BigDecimal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [CoffeeEntity::class],
    version = 4,
    exportSchema = false,
)
@TypeConverters(Converter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun coffeeDao(): CoffeeDao

    companion object {
        @Volatile
        private var instances: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instances ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "coffee_db",
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(context))
                    .build()
                instances = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val context: Context,
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            CoroutineScope(Dispatchers.IO).launch {
                val dao = getInstance(context).coffeeDao()
                dao.insertAll(
                    listOf(
                        CoffeeEntity(1, "Latte", TypeCoffee.LATTE, BigDecimal("3.50")),
                        CoffeeEntity(2, "Cappuccino", TypeCoffee.CAPPUCCINO, BigDecimal("4.00")),
                        CoffeeEntity(3, "Espresso", TypeCoffee.ESPRESSO, BigDecimal("2.50")),
                    ),
                )
            }
        }
    }
}
