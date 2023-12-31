package com.psic.aipokemon.core.src;

import android.content.res.Resources;
import android.os.Build;

import com.psic.aipokemon.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class PokemonDataReader {

    public static List<Pokemon> createAvailablePokemons(String jsonData, String jsonStringMove) {
        List<Pokemon> pokemonList = new ArrayList<>();
        try {
            Map<String, Move> availableMovements = createMovements(jsonStringMove);
            JSONObject pokemons = new JSONObject(jsonData);
            for (Iterator<String> it = pokemons.keys(); it.hasNext(); ) {
                String pokemonName = it.next();
                JSONObject pokemonData = pokemons.getJSONObject(pokemonName);

                // Retrieve values
                String primaryType = pokemonData.getString("primaryType");
                int level = pokemonData.getInt("level");
                int attack = pokemonData.getInt("attack");
                int defense = pokemonData.getInt("defense");
                int healthPoints = pokemonData.getInt("healtPoints");
                int specialAttack = pokemonData.getInt("specialAttack");
                int specialDefense = pokemonData.getInt("specialdefense");
                int speed = pokemonData.getInt("speed");
                Pokemon pokemon = new Pokemon(pokemonName, primaryType, level, healthPoints, attack, defense, specialAttack, specialDefense, speed);

                if (pokemonData.has("secondaryType")) {
                    pokemon.addSecondaryType(pokemonData.getString("secondaryType"));
                }
                // Retrieve movements array
                JSONArray movements = pokemonData.getJSONArray("movements");
                for (int i = 0; i < movements.length(); i++) {
                    String movement = movements.getString(i);
                    pokemon.learnMove(availableMovements.get(movement), i);
                }
                pokemonList.add(pokemon);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return pokemonList;
    }
    public static Map<String, Move> createMovements(String jsonData) {
        Map<String, Move> moves = new HashMap<>();
        try {
            JSONObject movements = new JSONObject(jsonData);
            for (Iterator<String> it = movements.keys(); it.hasNext(); ) {
                String moveName = it.next();
                JSONObject moveData = movements.getJSONObject(moveName);

                String category = moveData.getString("Category");
                String type = moveData.getString("Type");
                int power = moveData.getInt("Power");
                int accuracy = moveData.getInt("Accuracy");
                int maxMoves = moveData.getInt("PP");

                Move move;
                if (moveData.has("Passive")) {
                    JSONObject passiveData = moveData.getJSONObject("Passive");

                    int probability = passiveData.getInt("Probability");

                    Move.Modifier modifier = null;
                    if (passiveData.has("Modifier")) {
                        JSONObject modifierData = passiveData.getJSONObject("Modifier");
                        String user = modifierData.getString("User");
                        String stat = modifierData.optString("Stat", null);
                        float value = (float) modifierData.optDouble("Value", 0.0f);

                        if (stat != null) {
                            modifier = new Move.Modifier(user, stat, value);
                        } else {
                            modifier = new Move.Modifier(user);
                        }
                    }

                    if (passiveData.has("Effect")) {
                        String effect = passiveData.getString("Effect");
                        Move.Passive passive = new Move.Passive(probability, effect, modifier);
                        move = new Move(moveName, type, category, power, maxMoves, accuracy, passive);
                    } else {
                        Move.Passive passive = new Move.Passive(probability, modifier);
                        move = new Move(moveName, type, category, power, maxMoves, accuracy, passive);
                    }
                } else {
                    move = new Move(moveName, type, category, power, maxMoves, accuracy);
                }

                moves.put(moveName, move);
                //move.printMoveDetails(); 
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return moves;
    }
    

    public static Map<String, Map<String,Double>> createTypeTable(String jsonData) {
        Map<String, Map<String,Double>> moves = new HashMap<>();
        try {
            JSONObject types = new JSONObject(jsonData);
            for (Iterator<String> it = types.keys(); it.hasNext(); ) {
                String typeName = it.next();
                JSONObject typeData = types.getJSONObject(typeName);
                Map<String,Double> movesDamage = new HashMap<>();
                for (Iterator<String> iter = typeData.keys(); iter.hasNext(); ) {
                    String typeDamageName = iter.next();
                    movesDamage.put(typeDamageName, typeData.getDouble(typeDamageName));
                }
                moves.put(typeName, movesDamage);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return moves;
    }
}
