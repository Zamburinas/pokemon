package com.psic.aipokemon;

import androidx.activity.ComponentActivity;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ChooseTeamActivity extends ComponentActivity {
    private AlertDialog alertDialog;
    private ImageView currentImageView;
    private static final String[] nombresImagenes = {
            "charizard", "blastoise","machamp","venusaur","pikachu","gengar","mewtwo","pinsir","snorlax","golem"
    };
    private int checkBoxesSeleccionados = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_team);

        Button Backbutton = findViewById(R.id.botonBack);
        Button GoButton = findViewById(R.id.botonGo);


        Backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseTeamActivity.this, OptionsActivity.class);
                startActivity(intent);
            }
        });

        GoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Verificar si hay exactamente 3 elementos seleccionados
                if (checkBoxesSeleccionados == 3) {
                        Intent intent = new Intent(ChooseTeamActivity.this, BattleActivity.class);
                        startActivity(intent);

                } else {

                    Toast toast = Toast.makeText(getApplicationContext(), "Select 3 Pokemons", Toast.LENGTH_SHORT);

                    // Verificar si la vista es null antes de intentar modificarla
                    View view = toast.getView();
                    if (view != null) {
                        view.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
                    }

                    toast.show();
                }
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

            RelativeLayout relativeLayout = new RelativeLayout(this);

            ImageView imageView = new ImageView(this);
            int resourceId = getResources().getIdentifier(nombresImagenes[i], "drawable", getPackageName());
            imageView.setImageResource(resourceId);
            imageView.setLayoutParams(new RelativeLayout.LayoutParams(imageSize, imageSize));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            final String nombrePokemon = nombresImagenes[i];

            CheckBox checkBox = new CheckBox(this);
            checkBox.setVisibility(View.INVISIBLE); // Hace que el CheckBox no sea visible

            // LayoutParams para posicionar el CheckBox sobre la imagen
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);

            relativeLayout.addView(imageView);
            relativeLayout.addView(checkBox, layoutParams);

            // Cambiar el fondo de la imagen al seleccionar/deseleccionar
            relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Encontrar el CheckBox asociado a la imagen
                    CheckBox associatedCheckBox = (CheckBox) ((RelativeLayout) v).getChildAt(1);

                    if (associatedCheckBox.isChecked()) {
                        // Deseleccionar
                        associatedCheckBox.setChecked(false);
                        imageView.setBackgroundColor(Color.TRANSPARENT);
                        checkBoxesSeleccionados--;
                    } else {
                        // Verificar si se ha alcanzado el límite de selección
                        if (checkBoxesSeleccionados < 3) {
                            // Seleccionar
                            associatedCheckBox.setChecked(true);
                            imageView.setBackgroundColor(Color.BLUE); // Cambia el color de fondo a azul
                            checkBoxesSeleccionados++;
                        }
                    }
                }
            });

            // LayoutParams para el RelativeLayout
            LinearLayout.LayoutParams relativeLayoutParams = new LinearLayout.LayoutParams(imageSize, imageSize);
            relativeLayoutParams.bottomMargin = (int) getResources().getDisplayMetrics().density * 8; // Margen inferior de 8dp

            // La primera imagen de cada fila no tiene margen izquierdo
            if (i % 3 != 0) {
                relativeLayoutParams.leftMargin = margin;
            }

            rowLayout.addView(relativeLayout, relativeLayoutParams);
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