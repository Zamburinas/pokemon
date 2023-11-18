import java.util.ArrayList;
import java.util.List;

public class Player {
    private String playerName;
    private List<Pokemon> team; // 3 pokemon max

    public Player(String playerName) {
        this.playerName = playerName;
        this.team = new ArrayList<>();
    }

    public void addPokemonToTeam(Pokemon pokemon) {
        if (team.size() < 3) {
            team.add(pokemon);
            System.out.println(pokemon.getName() + " was added to " + playerName + "'s team");
        } else {
            System.out.println("You team is full");
        }
    }

    public Pokemon getPokemonFromTeam(int index) {
        if (index >= 0 && index < team.size()) {
            return team.get(index);
        } else {
            System.out.println("Invalid index. Could not get Pokémon from the team.");
            return null;
        }
    }

    public void removePokemonFromTeam(int index) {
        if (index >= 0 && index < team.size()) {
            Pokemon removedPokemon = team.remove(index);
            System.out.println(removedPokemon.getName() + " has been removed from " + playerName + "'s team");
        } else {
            System.out.println("Invalid index. Could not remove Pokémon from the team.");
        }
    }

    public void showTeam() {
        System.out.println("Team of " + playerName + ":");
        for (int i = 0; i < team.size(); i++) {
            System.out.println((i + 1) + ". " + team.get(i).getName());
        }
    }

    public String getPlayerName() {
        return playerName;
    }

    public List<Pokemon> getTeam() {
        return team;
    }
}
