package com.asmaa.yummy.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.asmaa.yummy.databinding.ItemRecipesBinding
import com.asmaa.yummy.model.FoodRecipe
import com.asmaa.yummy.model.ResultFood
import com.asmaa.yummy.util.RecipesDiffUtil

class RecipesAdapter: RecyclerView.Adapter<RecipesAdapter.MyViewHolder>(){

    private var recipes = emptyList<ResultFood>()

    class MyViewHolder(private val binding: ItemRecipesBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(resultFood: ResultFood){
            binding.result = resultFood
            binding.executePendingBindings()
        }

        companion object{
            fun from(parent: ViewGroup):MyViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemRecipesBinding.inflate(layoutInflater,parent,false)
                return MyViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun getItemCount(): Int {
        return recipes.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentResult = recipes[position]
        holder.bind(currentResult)
    }

    fun setData(newData: FoodRecipe){
        val recipesDiffUtil = RecipesDiffUtil(recipes , newData.results)
        val diffUtilResult = DiffUtil.calculateDiff(recipesDiffUtil)
        recipes = newData.results
        diffUtilResult.dispatchUpdatesTo(this)
    }
}