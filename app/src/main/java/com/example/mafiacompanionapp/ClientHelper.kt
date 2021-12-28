package com.example.mafiacompanionapp

import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.InetAddress
import java.net.Socket

/* class that contains all the code to connect to a server */
class ClientHelper() {
    val TAG = "LOG: ClientHelper.kt"
//    private val connection: Socket = Socket(address, port)
//    private var connected: Boolean = true

    lateinit var serverIp: InetAddress
    var serverPort: Int = -1


    constructor(serverIp: InetAddress, serverPort: Int) : this(){
        this.serverIp = serverIp
        this.serverPort = serverPort
    }

    fun connect(){

        var client = Socket(serverIp, serverPort)
        //sent to server
        var output = PrintWriter(client.getOutputStream(), true)
        //recieved from server
        var input = BufferedReader(InputStreamReader(client.inputStream))

        output.println("JohnDoe")

        println("Client receiving [${input.readLine()}]")
        Log.d(TAG, "Client Receiving... ${input.readLine()}")
        client.close()


    }


}