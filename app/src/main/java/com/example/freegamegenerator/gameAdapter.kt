package com.example.freegamegenerator

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class gameAdapter(private val gameNameList: List<String>, private val gameImageList: List<String>, private val gameDescList: List<String>): RecyclerView.Adapter<gameAdapter.ViewHolder>(){


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
    val gameImage: ImageView
    val gameTitle : TextView
    val gameDescr : TextView

    init {
        // Find our RecyclerView item's ImageView for future use
        gameImage = view.findViewById(R.id.gameImage)
        gameTitle = view.findViewById(R.id.gameName)
        gameDescr = view.findViewById(R.id.gameDescription)
    }

}
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.game_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        Glide.with(holder.itemView)
            .load(gameImageList[position])
            .fitCenter()
            .into(holder.gameImage)

        holder.gameTitle.text = gameNameList[position]
        holder.gameDescr.text = gameDescList[position]
    }

    override fun getItemCount(): Int {
        return gameNameList.size
    }

}