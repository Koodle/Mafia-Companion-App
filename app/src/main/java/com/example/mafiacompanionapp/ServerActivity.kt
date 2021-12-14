package com.example.mafiacompanionapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter

class ServerActivity : AppCompatActivity() {

    val TAG = "LOG: ServerActivity"

    var nsdHelper: NsdHelper = NsdHelper(this)
    var serverName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_server)
        Log.d("debug - ServerActivity", "server activity")

        setServerName()
        startServer(serverName.toString())

        //todo put this in server helper class after testing it wrks
        var client = nsdHelper.mServerSocket.accept()
        while (true){

            //todo put this in threads

            //todo send greeting msg to client
            //get clients name
            var br: BufferedReader = BufferedReader(InputStreamReader(client.getInputStream()))
            var sb = StringBuilder()
            var line = br.readLine()

            while(line!=null){
                sb.append(line)
                line=br.readLine()
            }
            br.close()

            Log.d(TAG,"Message received from the client :: $sb")

            //send msg to client saying hello "name"
            val out = PrintWriter(client.getOutputStream(), true)
            out.println("Hello Client !!")
        }
    }

    fun setServerName(){
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
    }

    fun startServer(serverName: String){
        //set ServerName for NSD
        nsdHelper.setServiceName(serverName)
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