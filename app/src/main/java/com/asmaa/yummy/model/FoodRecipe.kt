package com.asmaa.yummy.model


import com.google.gson.annotations.SerializedName

data class FoodRecipe(

    @SerializedName("results")
    val results: List<ResultFood>
)