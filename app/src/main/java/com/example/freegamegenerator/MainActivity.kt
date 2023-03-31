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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //find views
        val platformSpinner = findViewById<Spinner>(R.id.platformDropBox)
        val gameSpinner = findViewById<Spinner>(R.id.gameTypeDropBox)
        val generateButton = findViewById<Button>(R.id.generateGame)
        val gameImage = findViewById<ImageView>(R.id.gameImage)
        val gameText = findViewById<TextView>(R.id.gameName)
        val descriptionText = findViewById<TextView>(R.id.gameDescription)

        //request url
        val url = "https://www.freetogame.com/api/games?"


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
            getGameList(platformType, gameType, url, gameText, gameImage, descriptionText)

        }
    }
    private fun getGameList(platform: String, type: String, url: String, text: TextView,imageView: ImageView, description: TextView ){

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

            override fun onSuccess(statusCode: Int, headers: Headers?, json: JSON?) {
                Log.d("JSON","Request Success $json")
                // Get a random index within the bounds of the jsonArray
                val randomIndex = Random.nextInt(json?.jsonArray?.length() ?: 0)

                // Get the random object at the randomIndex
                val randomObject = json?.jsonArray?.getJSONObject(randomIndex)

                // Get the values for pictureUrl and gameName
                //gameUrl = randomObject?.getString("freetogame_profile_url").toString()
                gameName = randomObject?.getString("title").toString()
                pictureUrl = randomObject?.getString("thumbnail").toString()
                gameDescription = randomObject?.getString("short_description").toString()
                Log.d("JSON", "$pictureUrl \n $gameName")
                setEverything(text, imageView, description)

            }

        }]
    }
    private fun setEverything(text: TextView, imageView: ImageView, description: TextView){
        description.text = gameDescription
        text.text = gameName
        Glide.with(this)
            .load(pictureUrl)
            .fitCenter()
            .into(imageView)

        setStatusBarColor()
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