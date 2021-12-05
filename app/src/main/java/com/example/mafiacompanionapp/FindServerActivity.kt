package com.example.mafiacompanionapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FindServerActivity : AppCompatActivity() {

    val TAG = "LOG: FindLobbyActivity"

    var nsdHelper: NsdHelper = NsdHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_server)

        //start finding servers
        nsdHelper.discoverServices()
        //nsdHelper.getServices()

        //setup Recyclerview
        val recyclerview = findViewById<RecyclerView>(R.id.recycAvailableLobbies)
        // this creates a vertical layout Manager
        recyclerview.layoutManager = LinearLayoutManager(this)
        if (nsdHelper.servicesList != null) {

            // This will pass the ArrayList to our Adapter
            val adapter = serverAdapter(nsdHelper.servicesList)

            // Setting the Adapter with the recyclerview
            recyclerview.adapter = adapter

            //pass the adapter to the nsdHelper
            //so, it can update the list using the adapters notifyDataSetChange() whenever a new server is found
            nsdHelper.setRecycAdapter(adapter)

        } else {
            Log.d(TAG, "servicelist is null")
        }

        //todo join a server
        val serverCard = findViewById(R.id.serverName) as EditText
        serverCard.setOnClickListener {
            //get the serverName
            //call nsdHelper.getServices() to
            //search in that list for the serverName
            //get the services's ip adress & port
            //make connection to the server
            //navigate to the lobby screen
        }
    }
}