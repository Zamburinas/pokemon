import org.json.JSONArray;
import org.json.JSONObject;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.swing.text.html.parser.Element;

public class PokemonDataReader {
    public List<Pokemon> readPokemonsFromJson(String filePath) {

        try {
            String jsonData = new String(Files.readAllBytes(Paths.get(filePath)));

            JSONArray pokemons = new JSONArray(jsonData);
            System.err.println(jsonData);
            List<Pokemon> pokemonList = new ArrayList<>();

            // Now you can use the 'pokemonList' for further processing
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<Pokemon> meow = new ArrayList<Pokemon>();
        return meow;
    }
    public List<Move> readMovementsFromJson(String filePath) {
        return new ArrayList<Move>();
    }
}
