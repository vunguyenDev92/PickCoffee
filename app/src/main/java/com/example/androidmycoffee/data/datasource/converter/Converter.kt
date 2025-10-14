package com.example.androidmycoffee.data.datasource.converter

import androidx.room.TypeConverter
import com.example.androidmycoffee.domain.model.TypeCoffee
import java.math.BigDecimal

class Converter {

    @TypeConverter
    fun fromBigDecimal(value: BigDecimal?): String? {
        return value?.toPlainString()
    }

    @TypeConverter
    fun toBigDecimal(value: String?): BigDecimal? {
        return value?.let { BigDecimal(it) }
    }

    @TypeConverter
    fun fromTypeCoffee(type: TypeCoffee?): String? {
        return type?.name
    }

    @TypeConverter
    fun toTypeCoffee(value: String?): TypeCoffee? {
        return value?.let { TypeCoffee.valueOf(it) }
    }
}
