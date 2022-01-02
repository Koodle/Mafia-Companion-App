package com.example.mafiacompanionapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mafiacompanionapp.server.NsdHelper

class ClientActivity : AppCompatActivity() {

    var nsdHelper: NsdHelper = NsdHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client)

        nsdHelper.discoverServices()

    }


    override fun onDestroy() {
        nsdHelper.stopDiscovery()
        super.onDestroy()
    }

}