package com.asmaa.yummy.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.asmaa.yummy.model.FoodRecipe

@Entity(tableName = "recipestable")
class RecipesEntity(var foodRecipe: FoodRecipe) {

    @PrimaryKey(autoGenerate = false)
    var id = 0
}