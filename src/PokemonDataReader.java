import org.json.*;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class PokemonDataReader {
    public List<Pokemon> readPokemonsFromJson(String filePath) {
        try (FileReader reader = new FileReader(filePath)) {
            Gson gson = new Gson();
            Type pokemonListType = new TypeToken<List<Pokemon>>(){}.getType();
            return gson.fromJson(reader, pokemonListType);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public List<Move> readMovementsFromJson(String filePath) {
        try (FileReader reader = new FileReader(filePath)) {
            Gson gson = new Gson();
            Type pokemonListType = new TypeToken<List<Pokemon>>(){}.getType();
            return gson.fromJson(reader, pokemonListType);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
