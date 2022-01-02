package com.example.mafiacompanionapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mafiacompanionapp.client.AdapterLobbyRecycView
import com.example.mafiacompanionapp.server.NsdHelper

class FindServerActivity : AppCompatActivity() {

    val TAG = "LOG: FindLobbyActivity"

    var nsdHelper: NsdHelper = NsdHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_server)

        //start finding servers
        nsdHelper.discoverServices()

        //setup Recyclerview
        val recyclerview = findViewById<RecyclerView>(R.id.recycAvailableLobbies)
        // this creates a vertical layout Manager
        recyclerview.layoutManager = LinearLayoutManager(this)
        if (nsdHelper.servicesList != null) {
            // This will pass the ArrayList to our Adapter
            val adapter = AdapterLobbyRecycView(nsdHelper.servicesList)
            // Setting the Adapter with the recyclerview
            recyclerview.adapter = adapter
            //pass the adapter to the nsdHelper
            //so, it can update the list using the adapters notifyDataSetChange() whenever a new server is found
            nsdHelper.setRecycAdapter(adapter)
        } else {
            Log.d(TAG, "servicelist is null")
        }

        //todo join a server
//        val serverCard = findViewById(R.id.serverName) as TextView
//        serverCard.setOnClickListener {
//            //find the servers port/ip
//            for (service in nsdHelper.getServices()){
//                //get the services's ip adress & port
//                if(service.serviceName.equals(serverCard.text.toString())){
//                    //todo make connection to the server
////                    var clientSocket = Socket(service.host, service.port)
//                    var connectedServer = ClientHelper(service.host, service.port)
//                    Log.d(TAG, "connected to server on ${service.host} & ${service.port}")
//
//
//
//                    //todo send the server a message
//                    //var br: BufferedReader = BufferedReader(InputStreamReader(clientSocket.getInputStream()))
//                    connectedServer.connect()
//
//                    //todo navigate to the lobby screen
//
//                }
//            }
//        }
            //call nsdHelper.getServices() to
            //search in that list for the serverName


    }



}