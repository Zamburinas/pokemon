import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Battle {
    private Player player1;
    private Player player2;
    private int turnCounter = 0;

    public Battle(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
    }

    public void start() {
        selectPokemonForPlayer(player1);
        player2.setCurrentPokemon(player2.getPokemonFromTeam(0));
        System.out.println(player1.getPlayerName() + " sends out " + player1.getCurrentPokemon().getName() + " to the field!");
        System.out.println(player2.getPlayerName() + " sends out " + player2.getCurrentPokemon().getName() + " to the field!");
        
        while (true) {
            playerTurn();
            if (isBattleOver()) break; // Verificar si la batalla ha terminado antes del segundo turno
        }

        // Mostrar resultados de la batalla...
    }

    private void playerTurn() {
        turnCounter++;
        System.out.println("Turn: " + turnCounter);

        Move selectedMove1 = null;
        Move selectedMove2 = null;

        // Display details of the current Pokémon and its available moves
        System.out.println("Current Pokemon:\" " + player1.getCurrentPokemon().getName());
        System.out.println("HP: " + player1.getCurrentPokemon().getStats().getHealthPoints());
        System.out.println("Available moves:");
        for (int i = 0; i < player1.getCurrentPokemon().getMoves().length; i++) {
            System.out.println((i + 1) + ". " + player1.getCurrentPokemon().getMoves()[i].getName());
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("Choose an action:");
        System.out.println("1. Attack with a move");
        System.out.println("2. Change Pokémon");
        int choice = scanner.nextInt();

        if (choice == 1) {
            // Player wants to attack with a move
            System.out.println("Choose a move (enter move index):");
            int moveIndex = scanner.nextInt();

            if (moveIndex > 0 && moveIndex <= player1.getCurrentPokemon().getMoves().length) {
                selectedMove1 = player1.getCurrentPokemon().getMoves()[moveIndex - 1];
            } else {
                System.out.println("Invalid move index. Please choose a valid move.");
            }
        } else if (choice == 2) {
            // Player wants to change Pokémon
            System.out.println("Choose a Pokémon to switch to (enter Pokémon index):");
            // Display available Pokémon and stats
            for (int i = 0; i < player1.getTeam().size(); i++) {
                Pokemon pokemon = player1.getTeam().get(i);
                System.out.println((i + 1) + ". " + pokemon.getName() + " (HP: " + pokemon.getStats().getHealthPoints() + ")");
            }
            
            int switchIndex = scanner.nextInt();

            if (switchIndex > 0 && switchIndex <= player1.getTeam().size()) {
                Pokemon selectedPokemon = player1.getPokemonFromTeam(switchIndex - 1);
                player1.setCurrentPokemon(selectedPokemon);
                System.out.println(player1.getPlayerName() + " switched to " + selectedPokemon.getName() + "!");
            } else {
                System.out.println("Invalid Pokémon index. Please choose a valid Pokémon.");
            }
        } else {
            System.out.println("Invalid choice. Please choose again.");
        }

        Random random = new Random();
        int randomMoveIndex = random.nextInt(player2.getCurrentPokemon().getMoves().length);
        selectedMove2 = player2.getCurrentPokemon().getMoves()[randomMoveIndex];

        resolveTurn(player1.getCurrentPokemon(), player2.getCurrentPokemon(), selectedMove1, selectedMove2);
    }

    private void resolveTurn(Pokemon pokemonPlayer1, Pokemon pokemonPlayer2, Move move1, Move move2) {
        // Attack from player 1 to player 2
        if (move1 != null) {
            int damageToPlayer2 = calculateDamage(pokemonPlayer1, pokemonPlayer2, move1);
            pokemonPlayer2.getStats().setHealthPoints(pokemonPlayer2.getStats().getHealthPoints() - damageToPlayer2);
            move1.updateRemaining(); // Reduce remaining uses (PP) of the move by one
            System.out.println(pokemonPlayer1.getName() + " used " + move1.getName() + " dealing " + damageToPlayer2 + " damage to " + pokemonPlayer2.getName());
        }
    
        // Attack from player 2 to player 1
        if (move2 != null) {
            int damageToPlayer1 = calculateDamage(pokemonPlayer2, pokemonPlayer1, move2);
            pokemonPlayer1.getStats().setHealthPoints(pokemonPlayer1.getStats().getHealthPoints() - damageToPlayer1);
            move2.updateRemaining(); // Reduce remaining uses (PP) of the move by one
            System.out.println(pokemonPlayer2.getName() + " used " + move2.getName() + " dealing " + damageToPlayer1 + " damage to " + pokemonPlayer1.getName());
        }
    }
    
    // Method to calculate damage
    private int calculateDamage(Pokemon attacker, Pokemon defender, Move move) {
        // Logic to calculate damage based on stats, move type, type advantages, etc.
        // This implementation is simplified and may vary depending on Pokémon rules.
        // Return a simulated damage value for now
        return 20; // Simulated damage value
    }
    

    private boolean isBattleOver() {
        boolean allPlayer1PokemonsFainted = true;
        boolean allPlayer2PokemonsFainted = true;

        // Check if any of player1's Pokémon have remaining health points
        for (Pokemon pokemon : player1.getTeam()) {
            if (pokemon.getStats().getHealthPoints() > 0) {
                allPlayer1PokemonsFainted = false; // At least one Pokémon of player1 has not fainted
                break;
            }
        }

        // Check if any of player2's Pokémon have remaining health points
        for (Pokemon pokemon : player2.getTeam()) {
            if (pokemon.getStats().getHealthPoints() > 0) {
                allPlayer2PokemonsFainted = false; // At least one Pokémon of player2 has not fainted
                break;
            }
        }

        // If all Pokémon of a player have fainted, the battle is over
        return allPlayer1PokemonsFainted || allPlayer2PokemonsFainted;
    }
    
    private void selectPokemonForPlayer(Player player) {
    Scanner scanner = new Scanner(System.in);
    boolean validSelection = false;

    do {
        System.out.println(player.getPlayerName() + ", choose your Pokémon:");
        List<Pokemon> availablePokemon = new ArrayList<>();

        // Filter and display only Pokémon with remaining health points
        for (Pokemon pokemon : player.getTeam()) {
            if (pokemon.getStats().getHealthPoints() > 0) {
                availablePokemon.add(pokemon);
            }
        }

        if (availablePokemon.isEmpty()) {
            System.out.println("No Pokémon with remaining health points available!");
            return; // Exit method if no Pokémon are available
        }

        for (int i = 0; i < availablePokemon.size(); i++) {
            System.out.println((i + 1) + ". " + availablePokemon.get(i).getName());
        }

        System.out.print("Enter the index of your Pokémon: ");
        int chosenIndex = scanner.nextInt();

        if (chosenIndex > 0 && chosenIndex <= availablePokemon.size()) {
            Pokemon chosenPokemon = availablePokemon.get(chosenIndex - 1);
            System.out.println(player.getPlayerName() + " has chosen " + chosenPokemon.getName() + "!");
            player.setCurrentPokemon(chosenPokemon);
            validSelection = true; // Set to true to exit the loop
        } else {
            System.out.println("Invalid index. Please choose a valid Pokémon.");
            // Ask for Pokémon selection again
        }
    } while (!validSelection); // Repeat until a valid Pokémon index is chosen
}

    
}
