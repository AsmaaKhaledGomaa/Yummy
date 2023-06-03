package com.asmaa.yummy

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
abstract class RecipesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertRecipes(recipesEntity: RecipesEntity)

    @Query("SELECT * FROM recipestable ORDER BY id ASC")
    abstract fun readRecipes(): Flow<List<RecipesEntity>>

}