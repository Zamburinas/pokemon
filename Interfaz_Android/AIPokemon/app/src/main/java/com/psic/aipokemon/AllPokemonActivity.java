package com.psic.aipokemon;

import androidx.activity.ComponentActivity;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.Scanner;

import android.widget.LinearLayout;
import android.widget.ScrollView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AllPokemonActivity extends ComponentActivity {
    private AlertDialog alertDialog;
    private ImageView currentImageView;
    private static final String[] nombresImagenes = {
            "charizard", "blastoise","machamp","venusaur","pikachu","gengar","mewtwo","pinsir","snorlax","golem"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_pokemon);

        Button Backbutton = findViewById(R.id.botonBack);

        Backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AllPokemonActivity.this, OptionsActivity.class);
                startActivity(intent);
            }
        });


        findViewById(android.R.id.content).post(new Runnable() {
            @Override
            public void run() {
                setupImageLayout();
            }
        });



    }

    private void setupImageLayout() {
        ScrollView scrollView = findViewById(R.id.scrollView);
        LinearLayout linearContainer = findViewById(R.id.linearContainer);
        int widthContainer = linearContainer.getWidth();
        int imageSize = (int) getResources().getDisplayMetrics().density * 100; // Tamaño fijo de la imagen en dp
        int totalImageWidth = 3 * imageSize; // Ancho total ocupado por tres imágenes
        int margin = (widthContainer - totalImageWidth) / 2; // Margen para centrar las imágenes

        LinearLayout rowLayout = null;
        for (int i = 0; i < nombresImagenes.length; i++) {
            if (i % 3 == 0) {
                // Comienza una nueva fila
                rowLayout = new LinearLayout(this);
                rowLayout.setOrientation(LinearLayout.HORIZONTAL);
                linearContainer.addView(rowLayout);
            }

            ImageView imageView = new ImageView(this);
            int resourceId = getResources().getIdentifier(nombresImagenes[i], "drawable", getPackageName());
            imageView.setImageResource(resourceId);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(imageSize, imageSize));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            final String nombrePokemon = nombresImagenes[i];
            imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mostrarPantallita(nombrePokemon); // Mostrar la pantallita al mantener pulsado
                    return true; // Indica que el evento fue manejado
                }
            });

            // Agregar OnTouchListener para manejar el evento de liberación
            imageView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        // Ocultar la pantallita al liberar
                        ocultarPantallita();
                    }
                    return false;
                }
            });

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) imageView.getLayoutParams();
            layoutParams.bottomMargin = (int) getResources().getDisplayMetrics().density * 8; // Margen inferior de 8dp

            // La primera imagen de cada fila no tiene margen izquierdo
            if (i % 3 != 0) {
                layoutParams.leftMargin = margin;
            }

            if (rowLayout != null) {
                rowLayout.addView(imageView);
            }
        }
    }

    private void mostrarPantallita(String pokemonNombre) {
        ocultarPantallita(); // Oculta la pantallita actual antes de mostrar una nueva

        // Obtiene los datos del JSON basado en el nombre del Pokémon
        JSONObject pokemonData = obtenerDatosPokemon(pokemonNombre);


        // Verifica si se encontraron datos para el Pokémon
        if (pokemonData != null) {

            // Accede a los campos específicos del JSON
            int level = pokemonData.optInt("level", 0);
            int healthPoints = pokemonData.optInt("healtPoints", 0);
            int attack = pokemonData.optInt("attack", 0);
            int defense = pokemonData.optInt("defense", 0);
            int speed = pokemonData.optInt("speed", 0);
            int specialAttack = pokemonData.optInt("specialAttack", 0);
            int specialDefense = pokemonData.optInt("specialdefense", 0);
            String primaryType = pokemonData.optString("primaryType", "");
            String secondaryType = pokemonData.optString("secondaryType", "");
            JSONArray movementsArray = pokemonData.optJSONArray("movements");

            // Ahora puedes usar estos datos como desees, por ejemplo, mostrarlos en un cuadro de diálogo
            mostrarDatosPokemonEnDialogo(level, healthPoints, attack, defense, speed, specialAttack, specialDefense, primaryType, secondaryType, movementsArray);
        }
    }

    private JSONObject obtenerDatosPokemon(String pokemonNombre) {
        try {
            // Obtiene el JSON del recurso
            String jsonString = obtenerJsonString(); // Reemplaza esto con tu método para obtener el JSON
            JSONObject jsonObject = new JSONObject(jsonString);

            // Obtiene los datos específicos del Pokémon
            return jsonObject.optJSONObject(capitalizarPrimeraLetra(pokemonNombre));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static String capitalizarPrimeraLetra(String input) {
        if (input == null || input.isEmpty()) {
            return input; // Devuelve la cadena original si es nula o vacía
        }

        // Capitaliza la primera letra y concatena el resto de la cadena
        return Character.toUpperCase(input.charAt(0)) + input.substring(1);
    }
    private String obtenerJsonString() {
        Resources res = getResources();
        InputStream inputStream = res.openRawResource(R.raw.pokemons);
        Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name()).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }

    private void mostrarDatosPokemonEnDialogo(int level, int healthPoints, int attack, int defense, int speed, int specialAttack, int specialDefense, String primaryType, String secondaryType, JSONArray movementsArray) {
        String type = secondaryType.isEmpty() ? primaryType : primaryType + "/" + secondaryType;
        String movements = formatearMovimientos(movementsArray);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Datos de Pokémon")
                .setMessage("Level: " + level +
                        "\nHP: " + healthPoints +
                        "\nAttack: " + attack +
                        "\nDefense: " + defense +
                        "\nSpeed: " + speed +
                        "\nSpecial Attack: " + specialAttack +
                        "\nSpecial Defense: " + specialDefense +
                        "\nType: " + type +
                        "\nMovements: " + "\n"+movements)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
    private String formatearMovimientos(JSONArray movementsArray) {
        StringBuilder formattedMovements = new StringBuilder();


        for (int i = 0; i < movementsArray.length(); i++) {

            formattedMovements.append("\t\t\t\t"+movementsArray.optString(i));
            formattedMovements.append("\n"); // Añade una tabulación después del salto de línea
        }

        return formattedMovements.toString(); // Elimina cualquier espacio en blanco al final
    }







    private void ocultarPantallita() {
        // Cerrar el AlertDialog si está abierto
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }
}