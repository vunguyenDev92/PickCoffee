package com.example.androidmycoffee.data.datasource.local

import com.example.androidmycoffee.data.datasource.local.dao.CoffeeDao
import com.example.androidmycoffee.data.datasource.local.entity.CoffeeEntity

class CoffeeLocalDataSourceImpl(private val coffeeDao: CoffeeDao) : CoffeeLocalDataSource {
    override suspend fun getAllCoffees(): List<CoffeeEntity> {
        return coffeeDao.getAll()
    }

    override suspend fun insertCoffees(list: List<CoffeeEntity>) {
        return coffeeDao.insertAll(list)
    }
    
    override suspend fun getCoffeeById(id: Int): CoffeeEntity? {
        return coffeeDao.getById(id)
    }
}
