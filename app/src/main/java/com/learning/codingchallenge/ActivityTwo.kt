package com.learning.codingchallenge

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.learning.codingchallenge.databinding.ActivityTwoBinding
import com.learning.codingchallenge.utils.Extension.redirectionToGame
import com.learning.codingchallenge.utils.NetworkConnectivityCallback
import com.learning.codingchallenge.utils.NetworkConnectivityHelper

class ActivityTwo : AppCompatActivity(),NetworkConnectivityCallback {
    private var networkConnectivityHelper : NetworkConnectivityHelper? =null
    private lateinit var binding : ActivityTwoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_two)
        networkConnectivityHelper = NetworkConnectivityHelper.getInstance(this);
        networkConnectivityHelper?.registerCallback(this)
        binding.tvScreenTwo.setOnClickListener {
            startActivity(Intent(this, ActivityOne::class.java))
        }


    }

    override fun onNetworkAvailable() {}

    override fun onNetworkLost() {
        this.redirectionToGame(this)
    }
}