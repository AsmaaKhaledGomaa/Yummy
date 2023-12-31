package com.asmaa.yummy.ui.fragment.recipes

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.asmaa.yummy.viewmodels.MainViewModel
import com.asmaa.yummy.R
import com.asmaa.yummy.adapters.RecipesAdapter
import com.asmaa.yummy.databinding.FragmentRecipesBinding
import com.asmaa.yummy.util.NetworkListener
import com.asmaa.yummy.util.NetworkResult
import com.asmaa.yummy.util.observeOnce
import com.asmaa.yummy.viewmodels.RecipesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class RecipesFragment : Fragment() , SearchView.OnQueryTextListener {

    private val args by navArgs<RecipesFragmentArgs>()

    private var _binding: FragmentRecipesBinding?= null
    private val binding get() = _binding!!

    private lateinit var mainViewModel: MainViewModel
    private lateinit var recipesViewModel: RecipesViewModel
    private val recipesAdapter by lazy { RecipesAdapter() }
    private lateinit var networkListener: NetworkListener

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
        _binding = FragmentRecipesBinding.inflate(inflater,container,false)
        binding.lifecycleOwner = this
        binding.mainviewmodel = mainViewModel

        setHasOptionsMenu(true)
        setUpRecycleView()

        recipesViewModel.readBackOnline.observe(viewLifecycleOwner) {
            recipesViewModel.backOnline = it
        }

        lifecycleScope.launch {
            networkListener = NetworkListener()
            networkListener.checkNetworkAvailability(requireContext()).collect{ status->
                Log.d("Network Listener",status.toString())

                recipesViewModel.networkStatus = status
                recipesViewModel.showNetworkStatus()
                readDataBase()
            }
        }

        binding.recipesFab.setOnClickListener {
            if (recipesViewModel.networkStatus){
                findNavController().navigate(R.id.action_recipesFragment_to_recipesBottomSheetFragment)
            } else{
                recipesViewModel.showNetworkStatus()
            }
        }
        return binding.root
    }

    private fun setUpRecycleView(){
        binding.recipeRecycleView.adapter = recipesAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.recipes_menu,menu)
        val search = menu.findItem(R.id.menu_search)
        val searchView = search.actionView as? SearchView
        searchView?.isSubmitButtonEnabled = true
        searchView?.setOnQueryTextListener(this)
    }
    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null){
            searchApiData(query)
        }
        return true
    }
    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }

    private fun readDataBase() {
        lifecycleScope.launch {
            mainViewModel.readRecipes.observeOnce(viewLifecycleOwner) { dataBase ->
                if (dataBase.isNotEmpty() && !args.backFromBottomSheet) {
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

    private fun searchApiData(searchQuery: String){
        mainViewModel.searchRecipes(recipesViewModel.applySearchQuery(searchQuery))
        mainViewModel.searchRecipesResponse.observe(viewLifecycleOwner){ response ->
            when(response){
                is NetworkResult.Sucsses ->{
                    val foodRecipe = response.data
                    foodRecipe?.let { recipesAdapter.setData(it) }
                }
                is NetworkResult.Error ->{
                    loadDataFromCache()
                    Toast.makeText(requireContext(),response.message.toString(),Toast.LENGTH_LONG).show()
                }
                is NetworkResult.Loading ->{
                    Toast.makeText(requireContext(),response.message.toString(),Toast.LENGTH_LONG).show()
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}