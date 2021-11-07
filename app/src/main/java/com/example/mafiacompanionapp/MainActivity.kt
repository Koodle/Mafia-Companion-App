package com.example.mafiacompanionapp

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import java.net.InetAddress
import java.net.ServerSocket

class MainActivity : AppCompatActivity() {

    var mlocalPort = -1
    var mServiceName = "" //get from bluetooth and append to end
    var mServiceType = "_mafia._tcp"
    var nsdManager: NsdManager? = null
    var TAG = "tag"

    var mService: NsdServiceInfo? = null
    var port: Int? = null
    var host: InetAddress? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeServerSocket()
        registerService(mlocalPort)

        nsdManager?.discoverServices(mServiceType, NsdManager.PROTOCOL_DNS_SD, discoveryListener)

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
            serviceName = Build.MANUFACTURER + " - " + Build.MODEL //todo get the name of the device from bluetooth
            serviceType = "_mafia._tcp"
            setPort(port)
        }

        nsdManager = (getSystemService(Context.NSD_SERVICE) as NsdManager).apply {
            registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, registrationListener)
        }

    }

    private val registrationListener = object : NsdManager.RegistrationListener {

        override fun onServiceRegistered(NsdServiceInfo: NsdServiceInfo) {
            // Save the service name. Android may have changed it in order to
            // resolve a conflict, so update the name you initially requested
            // with the name Android actually used
            mServiceName = NsdServiceInfo.serviceName
            //mServiceType = NsdServiceInfo.serviceType
            Log.d("TAG", "service name $mServiceName" )
            Log.d("TAG", "service type $mServiceType")
            Log.d("TAG", "port ${NsdServiceInfo.port}")
            Log.d("TAG", "port ${NsdServiceInfo.host}")
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

    // Instantiate a new DiscoveryListener
    private val discoveryListener = object : NsdManager.DiscoveryListener {

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

    private val resolveListener = object : NsdManager.ResolveListener {

        override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
            // Called when the resolve fails. Use the error code to debug.
            Log.e(TAG, "Resolve failed: $errorCode")
        }

        override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
            Log.e(TAG, "Resolve Succeeded. $serviceInfo")

            if (serviceInfo.serviceName == mServiceName) {
                Log.d(TAG, "Same IP.")
                return
            }
            mService = serviceInfo
            port = serviceInfo.port
            host = serviceInfo.host
        }
    }


    //In your application's Activity

    override fun onPause() {
        tearDown()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        registerService(mlocalPort)
        nsdManager?.discoverServices(mServiceType, NsdManager.PROTOCOL_DNS_SD, discoveryListener)

    }

    override fun onDestroy() {
        tearDown()
        //connection.tearDown() todo idk how to implement... maybe means to disable socket/close port
        super.onDestroy()
    }

    // NsdHelper's tearDown method
    fun tearDown() {
        nsdManager?.apply {
            unregisterService(registrationListener)
            stopServiceDiscovery(discoveryListener)
        }
    }



}