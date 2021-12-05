package com.example.mafiacompanionapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        var btnHost = findViewById(R.id.btnHost) as Button
        var btnFind = findViewById(R.id.btnFind) as Button
        btnHost.setOnClickListener {

            var intent = Intent(this@MainActivity, HostServerActivity::class.java)
            startActivity(intent)

        }

        btnFind.setOnClickListener {

            var intent = Intent(this@MainActivity, FindServerActivity::class.java)
            startActivity(intent)

        }


    }


}