package com.psic.aipokemon

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import com.psic.aipokemon.core.src.PokemonGame

class MainActivity : ComponentActivity() {
    private var mediaPlayer: MediaPlayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val botonStart = findViewById<Button>(R.id.botonStart)
        mediaPlayer = MediaPlayer.create(this, R.raw.intro);
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()

        botonStart.setOnClickListener {
            val intent = Intent(this, OptionsActivity::class.java)
            mediaPlayer?.stop()
            startActivity(intent)
        }


    }


}

