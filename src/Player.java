import java.util.ArrayList;
import java.util.List;

public class Player {
    private String playerName;
    private List<Pokemon> team; // 3 pokemon max
    private Pokemon currentPokemon;

    public Player(String playerName) {
        this.playerName = playerName;
        this.team = new ArrayList<>();
    }

    public Player(Player player) {
        List<Pokemon> teamCopy = player.getTeam();
        this.team = new ArrayList<>();
        for (int i = 0; i < teamCopy.size(); i++) {
            this.team.add(new Pokemon(teamCopy.get(i)));
            if (teamCopy.get(i).equals(player.getCurrentPokemon())) {
                this.currentPokemon = this.team.get(i);
            } 
        }
        this.playerName = player.getPlayerName();
    }

    public boolean addPokemonToTeam(Pokemon pokemon) {
        if (team.size() < 3) {
            boolean isAlreadyInTeam = team.stream().anyMatch(p -> p.getName().equals(pokemon.getName()));
            if (!isAlreadyInTeam) {
                team.add(new Pokemon(pokemon));
                System.out.println(pokemon.getName() + " was added to " + playerName + "'s team");
                return true;
            } else {
                System.out.println(pokemon.getName() + " is already in your team!");
                return false;
            }
        } else {
            System.out.println("Your team is full");
            return false;
        }
    }
    
    

    public void setPokemonFromTeam(int index) {
        if (index >= 0 && index < team.size()) {
            this.currentPokemon = team.get(index);
        }
    }

    public Pokemon getPokemonFromTeam(int index) {
        if (index >= 0 && index < team.size()) {
            return team.get(index);
        } else {
            System.out.println("Invalid index. Could not get Pokémon from the team.");
            return new Pokemon();
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

    public Pokemon getCurrentPokemon() {
        return currentPokemon;
    }

    public void setCurrentPokemon(Pokemon currentPokemon) {
        currentPokemon.shown = true;
        this.currentPokemon = currentPokemon;
    }

    public int getRemainingPokemons() {
        int remaining = 0;
        for (int i = 0; i < team.size(); i++) {
            if (!team.get(i).isDead()) {
                remaining++;
            }
        }
        return remaining;
    }

    public List<Integer> getRemainingChange() {
        List<Integer> remaining = new ArrayList<>();
        for (int i = 0; i < team.size(); i++) {
            if (!team.get(i).isDead() && team.get(i) != currentPokemon) {
                remaining.add(i);
            }
        }
        return remaining;
    }

    public double getTeamHealth() {
        double health = 0;
        for (int i = 0; i < team.size(); i++) {
            System.out.println(playerName + " " + ((double) team.get(i).getStats().getHealthPoints() / (double) team.get(i).getMaxHealthPoints()) * 100.0);
            health += team.get(i).isDead() ? 0 : ((double) team.get(i).getStats().getHealthPoints() / (double) team.get(i).getMaxHealthPoints()) * 100.0;
        }
        return health;
    }
}
