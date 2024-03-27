package com.example.myretrofitapp.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.myretrofitapp.R
import com.example.myretrofitapp.data.response.CustomerReviewsItem
import com.example.myretrofitapp.data.response.PostReviewResponse
import com.example.myretrofitapp.data.response.Restaurant
import com.example.myretrofitapp.data.response.RestaurantResponse
import com.example.myretrofitapp.data.retrofit.ApiConfig
import com.example.myretrofitapp.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Activity Ktx
    private val mainViewModel by viewModels<MainViewModel>()

    companion object{
        private const val TAG = "MainActivity"
        private const val RESTAURANT_ID = "uewq1zg2zlskfw1e867"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        // Panggil MainViewModel nya (Sekarang Pake Activity KTX)
        //val mainViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(MainViewModel::class.java)
        //mainViewModel.restaurant.observe(this){restaurant ->
        //    setRestaurantData(restaurant)
        //}

        val layoutManager = LinearLayoutManager(this)
        binding.rvReview.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvReview.addItemDecoration(itemDecoration)


        // Panggil Method Yang Aada Pada MainViewModel
        mainViewModel.listReview.observe(this) {customerReview ->
            setReviewData(customerReview)
        }

        mainViewModel.isLoading.observe(this){
            showLoading(it)
        }


        // SnackBar
        //mainViewModel.snackBar.observe(this){
        //    Snackbar.make(window.decorView.rootView, it, Snackbar.LENGTH_SHORT).show()
        //}

        // SnackBar Dengan Event Wrapper
        mainViewModel.snackBar.observe(this,{
            it.getContentIfNotHandled().let { snackBarText ->
                if (snackBarText != null) {
                    Snackbar.make(
                        window.decorView.rootView,
                        snackBarText,
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        })


        binding.btnSend.setOnClickListener { view ->
            mainViewModel.postReview(binding.edReview.text.toString())
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }


    private fun setRestaurantData( restaurant: Restaurant){
        binding.tvTitle.text  = restaurant.name
        binding.tvDescription.text = restaurant.description
        Glide.with(this@MainActivity)
            .load("https://restaurant-api.dicoding.dev/images/large/${restaurant.pictureId}")
            .into(binding.ivPicture)
    }

    private fun setReviewData(customerReviews: List<CustomerReviewsItem>){
        val adapter = ReviewAdapter()
        adapter.submitList(customerReviews)
        binding.rvReview.adapter = adapter
        binding.edReview.setText("")
    }

    private fun showLoading(isLoading: Boolean){
        if (isLoading){
            binding.progressBar.visibility = View.VISIBLE
        } else{
            binding.progressBar.visibility = View.GONE
        }
    }


}


