package com.example.mafiacompanionapp

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import java.net.ServerSocket

class MainActivity : AppCompatActivity() {

    var mlocalPort = -1
    var mServiceName = "noNameSet"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeServerSocket()

        Log.d("TAG", mlocalPort.toString())


        registerService(mlocalPort)



    }

    fun initializeServerSocket(){
        //initialize a server socket on the next available port
        var mServerSocket = ServerSocket(0)

        //store the chosen port
        mlocalPort = mServerSocket.localPort
    }

    //Note that this method is asynchronous,
    // so any code that needs to run after the service has been registered
    // must go in the onServiceRegistered() method.
    fun registerService(port: Int) {
        // Create the NsdServiceInfo object, and populate it.
        val serviceInfo = NsdServiceInfo().apply {
            // The name is subject to change based on conflicts
            // with other services advertised on the same network.
            serviceName = "getbluetoothname"
            serviceType = "_mafia._tcp"
            setPort(port)
        }

        val nsdManager = (getSystemService(Context.NSD_SERVICE) as NsdManager).apply {
            registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, registrationListener)
        }
    }

    private val registrationListener = object : NsdManager.RegistrationListener {

        override fun onServiceRegistered(NsdServiceInfo: NsdServiceInfo) {
            // Save the service name. Android may have changed it in order to
            // resolve a conflict, so update the name you initially requested
            // with the name Android actually used.
            mServiceName = NsdServiceInfo.serviceName
            Log.d("TAG", mServiceName)
        }

        override fun onRegistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
            // Registration failed! Put debugging code here to determine why.
        }

        override fun onServiceUnregistered(arg0: NsdServiceInfo) {
            // Service has been unregistered. This only happens when you call
            // NsdManager.unregisterService() and pass in this listener.
        }

        override fun onUnregistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
            // Unregistration failed. Put debugging code here to determine why.
        }
    }
}