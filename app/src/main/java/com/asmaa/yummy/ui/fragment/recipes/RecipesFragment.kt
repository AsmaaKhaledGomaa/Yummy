package com.asmaa.yummy.ui.fragment.recipes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.asmaa.yummy.MainViewModel
import com.asmaa.yummy.R
import com.asmaa.yummy.adapters.RecipesAdapter
import com.asmaa.yummy.databinding.FragmentRecipesBinding
import com.asmaa.yummy.util.Constants.Companion.API_KEY
import com.asmaa.yummy.util.NetworkResult
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecipesFragment : Fragment() {

    private lateinit var recipesViewModel:MainViewModel
    private lateinit var viewDataBinding:FragmentRecipesBinding
    private val recipesAdapter by lazy { RecipesAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewDataBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_recipes,container,false)

        recipesViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        setUpRecycleView()
        requestApiData()

        return viewDataBinding.root
    }

    private fun requestApiData(){
       recipesViewModel.getRecipes(applyQueries())
        recipesViewModel.recipesResponse.observe(viewLifecycleOwner) { response ->

            when (response) {
                is NetworkResult.Sucsses -> {
                    response.data?.let { recipesAdapter.setData(it) }
                }

                is NetworkResult.Error -> {
                    Toast.makeText(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_LONG).show()
                }
                is NetworkResult.Loading -> {
                    Toast.makeText(
                        requireContext(),
                        response.message.toString(),
                        Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun applyQueries(): HashMap<String , String>{
        val queries: HashMap<String , String> = HashMap()
        queries["number"] = "50"
        queries["apiKey"] = API_KEY
        queries["type"] = "snack"
        queries["diet"] = "vegan"
        queries["addRecipeInformation"] = "true"
        queries["fillIngredients"] = "true"
        return queries
    }

    private fun setUpRecycleView(){
      viewDataBinding.recipeRecycleView.adapter = recipesAdapter
    }

}