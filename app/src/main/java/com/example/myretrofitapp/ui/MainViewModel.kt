package com.example.myretrofitapp.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.myretrofitapp.data.response.CustomerReviewsItem
import com.example.myretrofitapp.data.response.PostReviewResponse
import com.example.myretrofitapp.data.response.Restaurant
import com.example.myretrofitapp.data.response.RestaurantResponse
import com.example.myretrofitapp.data.retrofit.ApiConfig
import com.example.myretrofitapp.util.Event
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel: ViewModel() {

    private val _restaurant = MutableLiveData<Restaurant>()
    val restaurant: LiveData<Restaurant> = _restaurant

    private val _listReview = MutableLiveData<List<CustomerReviewsItem>>()
    val listReview: LiveData<List<CustomerReviewsItem>> = _listReview

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // SnackBar
    //private val _snackBar = MutableLiveData<String>()
    //val snackBar: LiveData<String> = _snackBar

    // SnackBar Dibungkus Pake Event Wrapper
    private val _snackBar = MutableLiveData<Event<String>>()
    val snackBar: LiveData<Event<String>> = _snackBar

    companion object{
        private const val   TAG = "MainViewModel"
        private const val RESTAURANT_ID = "uewq1zg2zlskfw1e867"
    }

    init {
        findRestaurant()
    }


    private fun findRestaurant() {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getRestaurant(RESTAURANT_ID)
        client.enqueue(object : Callback<RestaurantResponse> {
            override fun onResponse(
                call: Call<RestaurantResponse>,
                response: Response<RestaurantResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful){
                    val responseBody = response.body()
                    if (responseBody != null){
                        _restaurant.value = response.body()?.restaurant
                        _listReview.value = response.body()?.restaurant?.customerReviews
                    }else{
                        Log.d(TAG, "Error di: ${response.message()}")
                    }
                }
            }

            override fun onFailure(call: Call<RestaurantResponse>, t: Throwable) {
                _isLoading.value= false
                Log.e(TAG, "Error: ${t.message}")
            }

        })
    }


    // Post Review
     fun postReview(review: String){
        _isLoading.value = true
        val client = ApiConfig.getApiService().postReview(RESTAURANT_ID, "Si Boy", review)
        client.enqueue(object  : Callback<PostReviewResponse>{
            override fun onResponse(
                call: Call<PostReviewResponse>,
                response: Response<PostReviewResponse>
            ) {
                _isLoading.value = false
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null){
                    _listReview.value = response.body()?.customerReviews
                    _snackBar.value = Event(response.body()?.message.toString())
                } else{
                    Log.e(TAG, "Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<PostReviewResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "Error: ${t.message}")
            }

        })
    }
}