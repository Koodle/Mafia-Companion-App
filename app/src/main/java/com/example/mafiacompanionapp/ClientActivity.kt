package com.example.mafiacompanionapp

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import java.net.InetAddress

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