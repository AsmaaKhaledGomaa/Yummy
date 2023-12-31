package com.asmaa.yummy.viewmodels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.asmaa.yummy.data.Repositry
import com.asmaa.yummy.data.database.RecipesEntity
import com.asmaa.yummy.model.FoodRecipe
import com.asmaa.yummy.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repositry: Repositry,application: Application):AndroidViewModel(application) {

    /** Room DataBase*/
    val readRecipes: LiveData<List<RecipesEntity>> = repositry.local.readDataBase().asLiveData()

    private fun insertRecipes(recipesEntity: RecipesEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repositry.local.insertRecipes(recipesEntity)

        }


    /** Retrofit */
    var recipesResponse: MutableLiveData<NetworkResult<FoodRecipe>> = MutableLiveData()
    var searchRecipesResponse: MutableLiveData<NetworkResult<FoodRecipe>> = MutableLiveData()

    fun getRecipes(queries: Map<String , String>) = viewModelScope.launch{
        getRecipesSafeCall(queries)
    }
    fun searchRecipes(searchQuery: Map<String , String>) = viewModelScope.launch {
        searchRecipesSafaCall(searchQuery)
    }

    private suspend fun getRecipesSafeCall(queries: Map<String, String>) {
        recipesResponse.value = NetworkResult.Loading()
        if(hasInternetConnection() == true){
            try {
                val response = repositry.remote.getRecipes(queries)
                    recipesResponse.value = handleFoodRecipesResponce(response)
                val foodRecipe = recipesResponse.value!!.data
                if (foodRecipe!= null){
                    offlineCacheRecipes(foodRecipe)
                }
            }catch (e:Exception){
                    recipesResponse.value = NetworkResult.Error("Recipes Not Found.")
            }
        }else{
            recipesResponse.value = NetworkResult.Error("No Internet Connection")
        }
    }
    private suspend fun searchRecipesSafaCall(searchQuery: Map<String, String>) {
        searchRecipesResponse.value = NetworkResult.Loading()
        if(hasInternetConnection() == true){
            try {
                val response = repositry.remote.getRecipes(searchQuery)
                searchRecipesResponse.value = handleFoodRecipesResponce(response)
            }catch (e:Exception){
                searchRecipesResponse.value = NetworkResult.Error("Recipes Not Found.")
            }
        }else{
            searchRecipesResponse.value = NetworkResult.Error("No Internet Connection")
        }
    }

    private fun offlineCacheRecipes(foodRecipe: FoodRecipe) {
        val recipesEntity = RecipesEntity(foodRecipe)
        insertRecipes(recipesEntity)
    }

    private fun handleFoodRecipesResponce(response: Response<FoodRecipe>): NetworkResult<FoodRecipe>? {
        when{
            response.message().toString().contains("timeout") -> {
                return NetworkResult.Error("Timeout")
            }
            response.code() == 402 -> {
                return NetworkResult.Error("Api Key Limted.")
            }
            response.body()!!.results.isNullOrEmpty() -> {
                return NetworkResult.Error("Recipes Not Found.")
            }
            response.isSuccessful -> {
                val foodRecipe = response.body()
                return NetworkResult.Sucsses(foodRecipe!!)
            }
            else -> {
               return NetworkResult.Error(response.message())
            }
        }
    }

    private fun hasInternetConnection():Boolean{
            val connectivityManeger = getApplication<Application>()
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            val activeNetwork = connectivityManeger.activeNetwork?: return false
            val capabilities = connectivityManeger.getNetworkCapabilities(activeNetwork)?: return false

            return when{
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) -> true
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) -> true
                else -> false
          }
    }
}