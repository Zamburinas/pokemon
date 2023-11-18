import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PokemonDataReader {
    public static List<Pokemon> createAvailablePokemons(String filePath) {
        List<Pokemon> pokemonList = new ArrayList<>();
        try {
            String jsonData = new String(Files.readAllBytes(Paths.get(filePath)));
            Map<String, Move> availableMovements = createMovements("../data/Movements.json");
            JSONObject pokemons = new JSONObject(jsonData);
            for (String pokemonName : pokemons.keySet()) {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pokemonList;
    }
    public static Map<String, Move> createMovements(String filePath) {
        Map<String, Move> moves = new HashMap<>();
        try {
            String jsonData = new String(Files.readAllBytes(Paths.get(filePath)));
            JSONObject movements = new JSONObject(jsonData);
            for (String moveName : movements.keySet()) {
                JSONObject moveData = movements.getJSONObject(moveName);

                String category = moveData.getString("Category");
                String type = moveData.getString("Type");
                int power = moveData.getInt("Power");
                int accuracy = moveData.getInt("Accuracy");
                int maxMoves = moveData.getInt("PP");
                Move move = new Move(moveName, type, category, power, maxMoves, accuracy);
                moves.put(moveName, move);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return moves;
    }

    public static Map<String, Map<String,Double>> createTypeTable(String filePath) {
        Map<String, Map<String,Double>> moves = new HashMap<>();
        try {
            String jsonData = new String(Files.readAllBytes(Paths.get(filePath)));
            JSONObject types = new JSONObject(jsonData);
            for (String typeName : types.keySet()) {
                JSONObject typeData = types.getJSONObject(typeName);
                Map<String,Double> movesDamage = new HashMap<>();
                for (String typeDamageName : typeData.keySet()) {
                    movesDamage.put(typeDamageName, typeData.getDouble(typeDamageName));
                }
                moves.put(typeName, movesDamage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return moves;
    }
}
