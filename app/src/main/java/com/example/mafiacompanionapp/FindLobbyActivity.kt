package com.example.mafiacompanionapp

import android.net.nsd.NsdServiceInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FindLobbyActivity : AppCompatActivity() {

    val TAG = "LOG: FindLobbyActivity"

    var nsdHelper: NsdHelper = NsdHelper(this)

    var servicesList: MutableList<NsdServiceInfo> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_lobby)

        nsdHelper.discoverServices()
        nsdHelper.getServices()

        // getting the recyclerview by its id
        val recyclerview = findViewById<RecyclerView>(R.id.recycAvailableLobbies)

        // this creates a vertical layout Manager
        recyclerview.layoutManager = LinearLayoutManager(this)

        // init servicesList
        servicesList = nsdHelper.getServices()

        if (servicesList != null) {

            Log.d(TAG, "servicelist is not null")

            Log.d(TAG, "servicelist: " + servicesList)

            // This will pass the ArrayList to our Adapter
            val adapter = lobbyAdapter(nsdHelper.servicesList)  //todo make this list dynamic

            // Setting the Adapter with the recyclerview
            recyclerview.adapter = adapter

        } else {

            Log.d(TAG, "servicelist is null")

        }



        // This loop will create 20 Views containing
        // the image with the count of view
//        for (i in 1..20) {
//            data.add(ItemsViewModel(R.drawable.mafia_man, "Item " + i))
//        }






    }
}