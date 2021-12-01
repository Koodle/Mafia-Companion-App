package com.example.mafiacompanionapp

import android.net.nsd.NsdServiceInfo
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.os.Looper
import android.util.Log


class lobbyAdapter(private var mList: List<NsdServiceInfo>) : RecyclerView.Adapter<lobbyAdapter.ViewHolder>() {


    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_layout, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val lobby = mList[position]

        // sets the text to the textview from our itemHolder class
        holder.textView.text = lobby.serviceName

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    //update the adapter when changes are made to the original list
    fun update(){
        Handler(Looper.getMainLooper()).post(Runnable { this.notifyDataSetChanged() })
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        //val imageView: ImageView = itemView.findViewById(R.id.imageview)
        val textView: TextView = itemView.findViewById(R.id.lobbyName)
    }
}