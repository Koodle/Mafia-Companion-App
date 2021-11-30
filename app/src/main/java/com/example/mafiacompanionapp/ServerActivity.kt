package com.example.mafiacompanionapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText

class ServerActivity : AppCompatActivity() {


    var nsdHelper: NsdHelper = NsdHelper(this)
    var serverName: String? = null

    //var serverName: String? = getIntent().getStringExtra("serverName")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_server)
        Log.d("debug - ServerActivity", "server activity")

        //get the serverName from HostLobbyIntent
        val extras = getIntent().getExtras()
        if (null != extras) {
            val value = extras.getString("serverName")
            Log.d("debug - ServerActivity", "found extra")

            //set serviceName to be server Name
            serverName = value.toString()

        }else{
            Log.d("debug - ServerActivity", "no extras")
        }

        //set ServerName for NSD
        nsdHelper.setServiceName(serverName.toString())
        //Allocate Socket & Register Service
        nsdHelper.registerService(nsdHelper.initializeServerSocket())




    }





    /*
    Unregister your service on application close
    It's important to enable and disable NSD functionality as appropriate during the application's lifecycle.
    Unregistering your application when it closes down helps prevent other applications from thinking it's still active and attempting to connect to it.
    Also, service discovery is an expensive operation, and should be stopped when the parent Activity is paused, and re-enabled when the Activity is resumed.
    Override the lifecycle methods of your main Activity and insert code to start and stop service broadcast and discovery as appropriate */
    override fun onPause() {
        //todo capture the port number
        nsdHelper.tearDown()
        super.onPause()
    }

    override fun onResume() {
        //todo reuse the same port number so that people that are already connected do not have to reconnect
        super.onResume()
        //todo call the register service method
        //nsdHelper.registerService(mlocalPort)
    }

    override fun onDestroy() {
        nsdHelper.tearDown()
        //connection.tearDown() todo idk how to implement... maybe means to disable server socket connection between the devices/close port
        super.onDestroy()
    }
}