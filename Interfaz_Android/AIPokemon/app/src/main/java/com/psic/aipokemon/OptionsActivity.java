package com.psic.aipokemon;

import androidx.activity.ComponentActivity;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;


import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;

public class OptionsActivity extends ComponentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);



            // Obtén referencias a los botones en tu diseño de interfaz de usuario (activity_main.xml)
            Button showPokemons = findViewById(R.id.botonSet);
            Button addPokemonButton = findViewById(R.id.botonChoose);
            Button Backbutton = findViewById(R.id.botonBack);
            Button BattleButton = findViewById(R.id.botonBattle);

            showPokemons.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(OptionsActivity.this, AllPokemonActivity.class);
                    startActivity(intent);
                }
            });

            addPokemonButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(OptionsActivity.this, ChooseTeamActivity.class);
                    startActivity(intent);
                }
            });

            Backbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(OptionsActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            });

            BattleButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(OptionsActivity.this, BattleActivity.class);
                    startActivity(intent);
                }
            });

        }
}