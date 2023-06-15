package com.asmaa.yummy.ui.fragment.recipes

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.asmaa.yummy.MainViewModel
import com.asmaa.yummy.R
import com.asmaa.yummy.adapters.RecipesAdapter
import com.asmaa.yummy.databinding.FragmentRecipesBinding
import com.asmaa.yummy.util.Constants.Companion.API_KEY
import com.asmaa.yummy.util.NetworkResult
import com.asmaa.yummy.viewmodels.RecipesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecipesFragment : Fragment() {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var viewDataBinding: FragmentRecipesBinding
    private lateinit var recipesViewModel: RecipesViewModel
    private val recipesAdapter by lazy { RecipesAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        recipesViewModel = ViewModelProvider(requireActivity()).get(RecipesViewModel::class.java)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewDataBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_recipes,container,false)

        setUpRecycleView()
        readDataBase()

        return viewDataBinding.root
    }

    private fun setUpRecycleView(){
        viewDataBinding.recipeRecycleView.adapter = recipesAdapter
    }
    private fun readDataBase() {
        lifecycleScope.launch {
            mainViewModel.readRecipes.observe(viewLifecycleOwner) { dataBase ->
                if (dataBase.isNotEmpty()) {
                    Log.d("RecipesFragment","ReadData Called!!")
                    recipesAdapter.setData(dataBase[0].foodRecipe)
                } else {
                    requestApiData()
                }
            }
        }
    }

    private fun requestApiData(){
       Log.d("RecipesFragment","RequestApiData Called!!")

       mainViewModel.getRecipes(recipesViewModel.applyQueries())
       mainViewModel.recipesResponse.observe(viewLifecycleOwner) { response ->

            when (response) {
                is NetworkResult.Sucsses -> {
                    response.data?.let { recipesAdapter.setData(it) }
                }

                is NetworkResult.Error -> {
                    loadDataFromCache()
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

    private fun loadDataFromCache() {

        lifecycleScope.launch {
            mainViewModel.readRecipes.observe(viewLifecycleOwner) { dataBase ->
                if (dataBase.isNotEmpty()) {
                    Log.d("RecipesFragment","Load Data From Cache !!")
                    recipesAdapter.setData(dataBase[0].foodRecipe)
                } else {
                    requestApiData()
                }
            }
        }
    }
}