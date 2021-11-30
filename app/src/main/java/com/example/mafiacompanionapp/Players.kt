package com.example.mafiacompanionapp

/*could turn this into a players class to keep hold of the
*each player should have:
* -Device Name
* -IP Address
* -Role
* -
*
* */
data class Players(
    val name: String,
    val id: Int,
    val port: String
)

