package com.example.mafiacompanionapp

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import java.net.InetAddress
import java.net.ServerSocket

/*class that contains all the code to implement NSD*/

class NsdHelper {

    var mContext: Context? = null

    //Register Service
    var localPort = -1
    var mServiceName = "MafiaApp - " + Build.MANUFACTURER + " - " + Build.MODEL //todo could get the user to imput their name here or do it in the sockets
    var mServiceType = "_mafia._tcp"
    var nsdManager: NsdManager? = null
    var TAG = "LOG: "
    var registrationListener: NsdManager.RegistrationListener? = null

    //Discover Service
    var mService: NsdServiceInfo? = null
    var port: Int? = null
    var host: InetAddress? = null


    constructor(context: Context){
        this.mContext = context
    }

    fun initializeServerSocket(): Int {
        //initialize a server socket on the next available port
        var mServerSocket = ServerSocket(0)
        //store the chosen port
        localPort = mServerSocket.localPort

        return localPort
    }

    //Note that this method is asynchronous,
    // so any code that needs to run after the service has been registered
    // must go in the onServiceRegistered() method.
    fun registerService(port: Int) {
        // Create the NsdServiceInfo object, and populate it.
        val serviceInfo = NsdServiceInfo().apply {
            // The name is subject to change based on conflicts
            // with other services advertised on the same network.
            serviceName = mServiceName
            serviceType = mServiceType
            setPort(port)
        }

        registrationListener = object : NsdManager.RegistrationListener {

            override fun onServiceRegistered(NsdServiceInfo: NsdServiceInfo) {
                // Save the service name. Android may have changed it in order to
                // resolve a conflict, so update the name you initially requested
                // with the name Android actually used
                mServiceName = NsdServiceInfo.serviceName
                //mServiceType = NsdServiceInfo.serviceType
                Log.d("TAG", "service name $mServiceName" )
                Log.d("TAG", "service type $mServiceType")
                Log.d("TAG", "port ${NsdServiceInfo.port}")
                Log.d("TAG", "host ${NsdServiceInfo.host}")
            }

            override fun onRegistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                // Registration failed! Put debugging code here to determine why.
                Log.e("TAG", "Service Unregisterd")
            }

            override fun onServiceUnregistered(arg0: NsdServiceInfo) {
                // Service has been unregistered. This only happens when you call
                // NsdManager.unregisterService() and pass in this listener.
                Log.i("TAG", "Service Unregisterd")
            }

            override fun onUnregistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                // Unregistration failed. Put debugging code here to determine why.
                Log.e("TAG", "Service Unregisterd")
            }
        }

        nsdManager =  (mContext?.getSystemService(Context.NSD_SERVICE) as NsdManager).apply {
            registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, registrationListener)
        }

    }

    fun tearDown() {
        if(registrationListener != null){
            try {
                nsdManager?.unregisterService(registrationListener)
            }finally {
                registrationListener = null
            }
        }
    }

    //Discover Services

    fun discoverServices(){

        nsdManager = (mContext?.getSystemService(Context.NSD_SERVICE) as NsdManager)

        nsdManager?.discoverServices(mServiceType, NsdManager.PROTOCOL_DNS_SD, discoveryListener)
    }

    //Discover services on the network
    private var discoveryListener = object : NsdManager.DiscoveryListener {

        // Called as soon as service discovery begins.
        override fun onDiscoveryStarted(regType: String) {
            Log.d(TAG, "Service discovery started")
        }

        override fun onServiceFound(service: NsdServiceInfo) {
            // A service was found! Do something with it.
            Log.d(TAG, "Service discovery success$service")
            nsdManager?.resolveService(service, object : NsdManager.ResolveListener {
                override fun onResolveFailed(serviceInfo: NsdServiceInfo?, errorCode: Int) {
                    // Called when the resolve fails. Use the error code to debug.
                    Log.e(TAG, "Resolve failed: $errorCode")
                }

                override fun onServiceResolved(serviceInfo: NsdServiceInfo?) {
                    Log.e(TAG, "Resolve Succeeded. $serviceInfo")

                    if (serviceInfo != null) {
                        if (serviceInfo.serviceName == mServiceName) {
                            Log.d(TAG, "Same IP.")
                            return
                        }
                    }
                    mService = serviceInfo
                    if (serviceInfo != null) {
                        port = serviceInfo.port
                    }
                    if (serviceInfo != null) {
                        host = serviceInfo.host
                    }
                }

            })
            when {
                service.serviceType != mServiceType -> // Service type is the string containing the protocol and
                    // transport layer for this service.
                    Log.d(TAG, "Unknown Service Type: ${service.serviceType}")
                service.serviceName == mServiceName -> // The name of the service tells the user what they'd be
                    // connecting to. It could be "Bob's Chat App".
                    Log.d(TAG, "Same machine: $mServiceName")
                service.serviceType.contains("mafia") ->  //todo gonna change from service name to type
                    Log.d(TAG, "broadcastedservicename = $service.serviceName")

            }
        }

        override fun onServiceLost(service: NsdServiceInfo) {
            // When the network service is no longer available.
            // Internal bookkeeping code goes here.
            Log.e(TAG, "service lost: $service")
        }

        override fun onDiscoveryStopped(serviceType: String) {
            Log.i(TAG, "Discovery stopped: $serviceType")
        }

        override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
            Log.e(TAG, "Discovery failed: Error code:$errorCode")
            nsdManager?.stopServiceDiscovery(this)
        }

        override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
            Log.e(TAG, "Discovery failed: Error code:$errorCode")
            nsdManager?.stopServiceDiscovery(this)
        }
    }

    fun stopDiscovery() {
        if (discoveryListener != null) {
            try {
                Log.i(TAG, "stopServiceDiscoveryCalled")
                nsdManager?.stopServiceDiscovery(discoveryListener)
            } finally {
            }
            //discoveryListener = null
        }
    }

}