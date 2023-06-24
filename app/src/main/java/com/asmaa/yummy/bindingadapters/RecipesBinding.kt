package com.asmaa.yummy.bindingadapters

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.asmaa.yummy.data.database.RecipesEntity
import com.asmaa.yummy.model.FoodRecipe
import com.asmaa.yummy.util.NetworkResult

class RecipesBinding {
    companion object {

        @BindingAdapter("readApiResponse", "readDataBase", requireAll = true)
        @JvmStatic
        fun errorImageViewVisibilty(
            imageView: ImageView,
            apiResponse: NetworkResult<FoodRecipe>?,
            database: List<RecipesEntity>?
        ) {
            if (apiResponse is NetworkResult.Error && database.isNullOrEmpty()) {
                imageView.visibility = View.VISIBLE
            } else if(apiResponse is NetworkResult.Loading) {
                imageView.visibility = View.INVISIBLE
            } else if(apiResponse is NetworkResult.Sucsses) {
                imageView.visibility = View.INVISIBLE
            }
        }

        @BindingAdapter("readApiResponse2", "readDataBase2", requireAll = true)
        @JvmStatic
        fun errorTextViewVisibilty(
            textView: TextView,
            apiResponse: NetworkResult<FoodRecipe>?,
            database: List<RecipesEntity>?
        ) {
            if (apiResponse is NetworkResult.Error && database.isNullOrEmpty()) {
                textView.visibility = View.VISIBLE
                textView.text = apiResponse.message.toString()
            } else if(apiResponse is NetworkResult.Loading) {
                textView.visibility = View.INVISIBLE
            } else if(apiResponse is NetworkResult.Sucsses) {
                textView.visibility = View.INVISIBLE
            }
        }
    }
}