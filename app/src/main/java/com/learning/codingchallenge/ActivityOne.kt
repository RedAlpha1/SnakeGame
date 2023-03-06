package com.learning.codingchallenge

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.learning.codingchallenge.databinding.ActivityBaseBinding
import com.learning.codingchallenge.utils.Extension.redirectionToGame
import com.learning.codingchallenge.utils.NetworkConnectivityCallback
import com.learning.codingchallenge.utils.NetworkConnectivityHelper

class ActivityOne : AppCompatActivity(),NetworkConnectivityCallback {

    private var networkConnectivityHelper : NetworkConnectivityHelper? =null
    private lateinit var binding : ActivityBaseBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_base)
        networkConnectivityHelper = NetworkConnectivityHelper.getInstance(this)
        networkConnectivityHelper?.registerCallback(this)
        clickListener()
    }

    private fun clickListener() {
        binding.tvScreenOne.setOnClickListener {
            startActivity(Intent(this, ActivityTwo::class.java))

        }
    }

    override fun onNetworkAvailable() {}

    override fun onNetworkLost() {
        this.redirectionToGame(this)
    }
}