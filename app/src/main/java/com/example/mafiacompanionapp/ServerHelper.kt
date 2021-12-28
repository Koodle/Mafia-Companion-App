package com.example.mafiacompanionapp

import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.nio.Buffer

/* class that contains all the code to host your server */
class ServerHelper() : Runnable { //pass the serverSocket
    val TAG = "LOG: serverHelper"

    lateinit var client: Socket
    //input output streams
    lateinit var input: BufferedReader
    lateinit var output: PrintWriter

    constructor(clientSocket: Socket) : this() {
        this.client = clientSocket
        this.input = BufferedReader(InputStreamReader(client.getInputStream()))
        this.output = PrintWriter(client.getOutputStream(), true)
    }

   override fun run(){


       try {
           while (true){
               //todo send greeting msg to client
               //get clients name
               var sb = StringBuilder()
               var line = input.readLine()

               while(line!=null){
                   sb.append(line)
                   line=input.readLine()
               }
               input.close()

               Log.d(TAG,"Message received from the client :: $sb")

               //send msg to client saying hello "name"
               output.println("Hello Client !!")
               //output.println("Hello Client " + sb)
           }
       }catch (e: IOException){
           Log.e(TAG, "IOException")
           Log.e(TAG, e.stackTrace.toString())
       }finally {
           output.close()
           input.close()
       }
   }
}