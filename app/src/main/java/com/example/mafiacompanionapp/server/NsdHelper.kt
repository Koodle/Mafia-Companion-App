package com.example.mafiacompanionapp.server

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.os.Build
import android.util.Log
import com.example.mafiacompanionapp.client.AdapterLobbyRecycView
import java.net.InetAddress
import java.net.ServerSocket

/*class that contains all the code to implement NSD*/

class NsdHelper(var mContext: Context) {

    //Register Service vars
    lateinit var mServerSocket: ServerSocket
    private var localPort = -1
    private var mServiceName = "MafiaApp - " + Build.MANUFACTURER + " - " + Build.MODEL //todo could get the user to imput their name here or do it in the sockets
    private var mServiceType = "_mafia._tcp"
    private var nsdManager: NsdManager? = null
    private var TAG = "LOG: NsdHelper"
    private var registrationListener: NsdManager.RegistrationListener? = null

    //Discover Service vars
    private var mService: NsdServiceInfo? = null
    private var port: Int? = null
    private var host: InetAddress? = null
    //public because it needs to be dynamic for the recycler view
    var servicesList: MutableList<NsdServiceInfo> = mutableListOf() //list of devices

    //Recycler View
    private var recycAdapterLobbyRecycView: AdapterLobbyRecycView? = null

    //Register Service meths

    fun initializeServerSocket(): Int {
        //initialize a server socket on the next available port
        mServerSocket = ServerSocket(0)
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
                Log.d("TAG", "service is registered" )
                Log.d("TAG", "service name $mServiceName" )
                Log.d("TAG", "service type $mServiceType")
                Log.d("TAG", "service port ${NsdServiceInfo.port}")
                Log.d("TAG", "service host ${NsdServiceInfo.host}")
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

    //gives the name inputted by the user. used in the lobby(server) name
    fun setServiceName(name: String){
        mServiceName = name
    }

    fun tearDown() {
        if(registrationListener != null){
            try {
                nsdManager?.unregisterService(registrationListener)
                Log.e(TAG, mServiceName + " service is unregisterd")
            }finally {
                registrationListener = null
            }
        }
    }

    //Discover Services meths

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
            Log.d(TAG, "Service discovery success $service")

            nsdManager?.resolveService(service, object : NsdManager.ResolveListener {

                override fun onResolveFailed(serviceInfo: NsdServiceInfo?, errorCode: Int) {
                    // Called when the resolve fails. Use the error code to debug.
                    Log.e(TAG, "Resolve failed: $errorCode")
                }

                override fun onServiceResolved(serviceInfo: NsdServiceInfo?) {
                    Log.e(TAG, "Resolve Succeeded. $serviceInfo")

                    if (serviceInfo != null) {

                        //make sure the device is not finding its self
                        if (serviceInfo.serviceName.equals(mServiceName)) {  //this should never be True since I have not coded the device to be able to host and find at the same time
                            Log.e(TAG, "found yourself")

                        }else if(serviceInfo.serviceType.contains("mafia._tcp")){

                            Log.d(TAG, "broadcastedservicename = $service.serviceName")

                            //use .add to append to the list without creating a new list in memory
                            servicesList.add(    //store the service in the list
                                service
                            )

                            if (recycAdapterLobbyRecycView != null ){
                                recycAdapterLobbyRecycView?.update()
                                Log.d(TAG, "recycAdapter is updated")
                            }else{
                                Log.e(TAG, "recycAdapter is null")
                            }
                        }
                    }




                }

            })

        }

        override fun onServiceLost(service: NsdServiceInfo) {
            // When the network service is no longer available.
            // Internal bookkeeping code goes here.
            Log.e(TAG, "service lost: $service")

            //todo could improve this to be faster
            //remove service from list
            for(i in servicesList.indices){
                if (servicesList[i].serviceName.equals(service.serviceName)){
                    //do not recreate the list or the adapter will not be able to find it using its reference in memory.
                    //we do operations on the existing list since if we were to recreate the list then its reference in memory will change.
                    //That will cause the adapter to get confused since the list that it was initialized with will no longer exist in memory
                    servicesList.removeAt(i)
                }
            }
            Log.d(TAG, "service removed from nsd.services list")

            //update the recycler view adapter
            if (recycAdapterLobbyRecycView != null ){
                recycAdapterLobbyRecycView?.update()
                Log.d(TAG, "recycAdaper is updated")

            }else{
                Log.e(TAG, "recycAdapter is null")
            }

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


    fun getServices(): MutableList<NsdServiceInfo>{
        return servicesList
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

    fun setRecycAdapter(adapterLobbyRecycView: AdapterLobbyRecycView){
        this.recycAdapterLobbyRecycView = adapterLobbyRecycView
    }

}