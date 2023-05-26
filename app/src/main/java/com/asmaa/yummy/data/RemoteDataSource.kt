package com.asmaa.yummy.data

import com.asmaa.yummy.data.network.FoodRecipesApi
import com.asmaa.yummy.model.FoodRecipe
import retrofit2.Response
import javax.inject.Inject

class RemoteDataSource @Inject constructor( private val foodRecipesApi: FoodRecipesApi ) {

    suspend fun getRecipes(queries: Map<String,String>): Response<FoodRecipe> {
        return foodRecipesApi.getFoodRecipes(queries)
    }
}