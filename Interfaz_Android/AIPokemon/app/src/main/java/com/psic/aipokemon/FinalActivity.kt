package com.psic.aipokemon

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity

class FinalActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_final)
        val intent= intent
        val text = intent.getStringExtra("string")
        val boxText= this.findViewById<TextView>(R.id.textView)
        boxText.text=text

        val buttonBack= this.findViewById<Button>(R.id.button)

        buttonBack.setOnClickListener {
            val intent = Intent(this, OptionsActivity::class.java)

            startActivity(intent)
        }
    }
}