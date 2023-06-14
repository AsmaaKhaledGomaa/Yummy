package com.asmaa.yummy.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database( entities = [RecipesEntity::class], version = 1, exportSchema = false )

@TypeConverters(RecipesTypeConverter::class)
abstract class RecipesDataBase: RoomDatabase() {

    abstract fun recipesDao(): RecipesDao
}