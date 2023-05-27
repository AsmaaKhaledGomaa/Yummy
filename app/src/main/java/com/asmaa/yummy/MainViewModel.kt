package com.asmaa.yummy

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.asmaa.yummy.data.Repositry
import com.asmaa.yummy.model.FoodRecipe
import com.asmaa.yummy.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repositry: Repositry,application: Application):AndroidViewModel(application) {

    var recipesResponse: MutableLiveData<NetworkResult<FoodRecipe>> = MutableLiveData()

    fun getRecipes(queries: Map<String , String>) = viewModelScope.launch{
        getRecipesSafeCall(queries)
    }

    private suspend fun getRecipesSafeCall(queries: Map<String, String>) {

        recipesResponse.value = NetworkResult.Loading()

        if(hasInternetConnection() == true){
            try {
                val response = repositry.remote.getRecipes(queries)
                    recipesResponse.value = handleFoodRecipesResponce(response)

            }catch (e:Exception){
                    recipesResponse.value = NetworkResult.Error("Recipes Not Found.")
            }
        }else{
            recipesResponse.value = NetworkResult.Error("No Internet Connection")
        }
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