package com.asmaa.yummy.util

import androidx.recyclerview.widget.DiffUtil
import com.asmaa.yummy.model.ResultFood

class RecipesDiffUtil( val oldList: List<ResultFood> , val newList:List<ResultFood>):DiffUtil.Callback(){
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] === newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]

    }
}