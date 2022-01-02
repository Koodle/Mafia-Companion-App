package com.example.mafiacompanionapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.mafiacompanionapp.server.NsdHelper
import com.example.mafiacompanionapp.server.ServerHelper
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.concurrent.thread

class ServerActivity : AppCompatActivity() {

    val TAG = "LOG: ServerActivity"

    var nsdHelper: NsdHelper = NsdHelper(this)
    var nsdName: String? = null //name of lobby

    //store all client connections
    var clients: MutableList<ServerHelper> = arrayListOf()
    //execute the client threads
    var pool: ExecutorService = Executors.newFixedThreadPool(10) //cannot have more than 10 clients connected

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_server)
        Log.d("debug - ServerActivity", "server activity")

        //start NSD
        //todo make this run before ServerSocket.Accept
        setNsdName()
        startNsd(nsdName.toString())

        thread {
            //todo fix this
            while (true){
                Log.d(TAG, "[SERVER] Waiting for client connection...")
                var client = nsdHelper.mServerSocket.accept()
                Log.d(TAG, "[SERVER] connected to client")

                //create client thread
                var clientThread = ServerHelper(client)
                //add it to list
                clients.add(clientThread)
                //run it
                pool.execute(clientThread)
            }
        }


    }

    fun setNsdName(){
        //get the serverName from HostLobbyIntent
        val extras = getIntent().getExtras()
        if (null != extras) {
            val value = extras.getString("serverName")
            Log.d("debug - ServerActivity", "found extra")

            //set serviceName to be server Name
            nsdName = value.toString()

        }else{
            Log.d("debug - ServerActivity", "no extras")
        }
    }

    fun startNsd(nsdName: String){
        //set ServerName for NSD
        nsdHelper.setServiceName(nsdName)
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