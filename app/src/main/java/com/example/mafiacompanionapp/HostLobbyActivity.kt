package com.example.mafiacompanionapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HostLobbyActivity : AppCompatActivity() {

    var serverName: String? = null
    var serverNameET: EditText? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_host_lobby)




        //get name of server
        //var serverName = serverNameET.text.toString()



        //To create the server
        var createGameBtn = findViewById<Button>(R.id.createServer).setOnClickListener {

            //init edit text
            serverNameET = findViewById<EditText>(R.id.serverName) as EditText

            //get server name
            serverName = serverNameET!!.text.toString()

            //todo check that serverName is valid text & is not empty
            val intent = Intent(this, ServerActivity::class.java)
            intent.putExtra("serverName", serverName)

            //navigate to the Server/Lobby view and pass the server name
            startActivity(intent)


        }

    }
}