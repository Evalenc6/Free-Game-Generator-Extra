package com.example.freegamegenerator

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.AdapterView
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import com.google.android.material.snackbar.Snackbar
import okhttp3.Headers
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    var pictureUrl = ""
    var gameName = ""
    var gameDescription = ""
    private lateinit var gameImageList: MutableList<String>
    private lateinit var gameNameList: MutableList<String>
    private lateinit var gameDescList: MutableList<String>
    private lateinit var gamesRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //find views
        val platformSpinner = findViewById<Spinner>(R.id.platformDropBox)
        val gameSpinner = findViewById<Spinner>(R.id.gameTypeDropBox)
        val generateButton = findViewById<Button>(R.id.generateGame)
         gamesRecyclerView = findViewById<RecyclerView>(R.id.gamesRecyclerView)
        //request url
        val url = "https://www.freetogame.com/api/games?"

        //Mutable Lists
        gameImageList = mutableListOf()
        gameNameList = mutableListOf()
        gameDescList = mutableListOf()

        //variables for platform
        var platformType = ""
        var gameType = ""



        //listeners
        platformSpinner.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                platformType = parent?.getItemAtPosition(position).toString().lowercase()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }

        gameSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                gameType = parent?.getItemAtPosition(position).toString().lowercase()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }

        generateButton.setOnClickListener{
            Log.d("buttonEvent", "Button Clicked!")
            getGameList(platformType, gameType, url)

        }
    }
    private fun getGameList(platform: String, type: String, url: String ){

        val client = AsyncHttpClient()
        val fullUrl = url+"platform="+platform+"&category="+type
        Log.d("URL", fullUrl)
        client[fullUrl, object : JsonHttpResponseHandler(){
            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                response: String?,
                throwable: Throwable?
            ) {
                Log.d("JSON", "Request Failed")
                val mySnackBar = Snackbar.make(findViewById(android.R.id.content),"No Games, Sorry!", Snackbar.LENGTH_LONG)
                mySnackBar.show()
            }

            override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {
                if(gameNameList.isNotEmpty()){
                    gameNameList.clear()
                    gameImageList.clear()
                    gameDescList.clear()
                }

                Log.d("JSON","Request Success $json")

                Log.d("JSON", "$pictureUrl \n $gameName")
                val gameJSON = json?.jsonArray

                if (gameJSON != null) {
                    for(i in 0 until  gameJSON.length()){
                        val tempJSON = gameJSON.getJSONObject(i)
                        gameNameList.add(tempJSON.getString("title"))
                        gameImageList.add(tempJSON.getString("thumbnail"))
                        gameDescList.add(tempJSON.getString("short_description"))

                    }
                }


                //setEverything(text, imageView, description)

                //TODO: set up adapter and RecyclerView
                val adapter = gameAdapter(gameNameList, gameImageList, gameDescList)
                gamesRecyclerView.adapter = adapter
                gamesRecyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
            }

        }]
    }

    private fun setStatusBarColor (){
        Glide.with(this)
            .asBitmap()
            .load(pictureUrl)
            .into(object: CustomTarget<Bitmap>(){
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    val pallete = Palette.from(resource).generate()
                    val dominantColor = pallete.getDominantColor(Color.MAGENTA)
                    window.statusBarColor = dominantColor
                    supportActionBar?.setBackgroundDrawable(ColorDrawable(dominantColor))
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.MAGENTA))
                }

            })
    }
}