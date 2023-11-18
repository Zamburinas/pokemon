import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class Battle {
    private Player player1;
    private Player player2;
    private int turnCounter = 0;
    private Map<String, Map<String,Integer>> typeTable;

    public Battle(Player player1, Player player2, Map<String, Map<String,Integer>> typeTable) {
        this.player1 = player1;
        this.player2 = player2;
        this.typeTable = typeTable;
    }

    public void start() {
        selectPokemonForPlayer(player1);
        player2.setCurrentPokemon(player2.getPokemonFromTeam(0));
        logMessage(player1.getPlayerName() + " sends out " + player1.getCurrentPokemon().getName() + " to the field!");
        logMessage(player2.getPlayerName() + " sends out " + player2.getCurrentPokemon().getName() + " to the field!");
        
        while (true) {
            playerTurn();
            if (isBattleOver()) break; // Verificar si la batalla ha terminado antes del segundo turno
        }

        // Mostrar resultados de la batalla...
    }

    private void playerTurn() {
        turnCounter++;
        logMessage("Turn: " + turnCounter);
        while(player2.getCurrentPokemon().isDead()) player2.setPokemonFromTeam(new Random().nextInt(3));
        logMessage("AI Pok\u00E9mon: " + player2.getCurrentPokemon().getName());
        int selectedMove1 = -1, selectedMove2 = -1;
        Pokemon player1Pokemon = player1.getCurrentPokemon();
        // Display details of the current Pok\u00E9mon and its available moves
        logMessage("Current Pok\u00E9mon: " + player1Pokemon.getName());
        logMessage("HP: " + player1Pokemon.getStats().getHealthPoints());        

        int choice;
        Scanner scanner;
        boolean condition;
        do {
            scanner = new Scanner(System.in);
            logMessage("Choose an action:");
            if (!player1Pokemon.isDead()) {
                logMessage("1. Attack with a move");
            } else {
                choice = 2;
            }
            logMessage("2. Change Pok\u00E9mon");
            choice = scanner.nextInt();
            condition = choice < 0 || choice > 2;
            if (condition)
                logMessage("Invalid choice. Please choose again.");
        } while (condition);
       
        
        if (choice == 1) {
            // Player wants to attack with a move
            logMessage("Choose a move (enter move index):");
            logMessage("Available moves:");
            logMessage("0. Go back");
            Move [] moves = player1.getCurrentPokemon().getMoves();
            for (int i = 0; i < moves.length; i++) {
                if (player1Pokemon.isMoveAvailable(i))
                    logMessage((i + 1) + ". " + player1Pokemon.getMoves()[i].getInfo(), false);
                    logMessage(player1Pokemon.getMoves()[i].getMoveStats());
            }
            int moveIndex;
            
            do{
                moveIndex = scanner.nextInt();
                condition = !(moveIndex > 0 && moveIndex <= player1Pokemon.getMoves().length) || !player1Pokemon.isMoveAvailable(moveIndex-1);
                if (condition) 
                    logMessage("Invalid move index. Please choose a valid move.");
            } while(condition);
            selectedMove1 = moveIndex - 1;

        } else if (choice == 2) {
            // Player wants to change Pok\u00E9mon
            logMessage("Choose a Pok\u00E9mon to switch to (enter Pok\u00E9mon index):");
            logMessage("0. Go back");
            // Display available Pok\u00E9mon and stats
            for (int i = 0; i < player1.getTeam().size(); i++) {
                Pokemon pokemon = player1.getTeam().get(i);
                if (pokemon.equals(player1Pokemon))
                    continue;
                logMessage((i + 1) + ". " + pokemon.getName() + " (HP: " + pokemon.getStats().getHealthPoints() + ")");
            }
            
            int switchIndex;
             do{
                switchIndex = scanner.nextInt();
                condition = !(switchIndex > 0 && switchIndex <= player1.getTeam().size());
                if (condition) 
                    logMessage("Invalid Pok\u00E9mon index. Please choose a valid Pok\u00E9mon.");
            } while(condition);
                Pokemon selectedPokemon = player1.getPokemonFromTeam(switchIndex - 1);
                player1.setCurrentPokemon(selectedPokemon);
                logMessage(player1.getPlayerName() + " switched to " + selectedPokemon.getName() + "!");
        }

        
        selectedMove2 = new Random().nextInt(player2.getCurrentPokemon().getMoves().length);

        resolveTurn(player1.getCurrentPokemon(), player2.getCurrentPokemon(), selectedMove1, selectedMove2);
    }

    private void resolveTurn(Pokemon pokemonPlayer1, Pokemon pokemonPlayer2, int move1, int move2) {
        // First we check who is faster
        // If they're equally fast, it will be random with 1/2 probability
        Pokemon fasterPokemon = pokemonPlayer1, slowerPokemon = pokemonPlayer2;
        int fastMove = move1, slowMove = move2;
        Move move;
        if (pokemonPlayer2.getSpeed() > pokemonPlayer1.getSpeed() || (pokemonPlayer2.getSpeed() == pokemonPlayer1.getSpeed() && new Random().nextDouble() >= 0.5)) {
            fasterPokemon = pokemonPlayer2;
            slowerPokemon = pokemonPlayer1;
            fastMove = move1;
            slowMove = move2;
        }
        move = fasterPokemon.useMove(fastMove);
        double damage = calculateDamage(fasterPokemon, slowerPokemon, move);
        
        logMessage(fasterPokemon.getName() + " used " + move.getName() + " dealing " + damage + " damage to " + slowerPokemon.getName());
        if (slowerPokemon.receiveDamage(damage)) {
            slowerPokemon.die();
            logMessage(slowerPokemon.getName() + " fainted!");
            return;
        }
        move = slowerPokemon.useMove(slowMove);
        damage = calculateDamage(slowerPokemon, fasterPokemon, slowerPokemon.useMove(slowMove));
        logMessage(slowerPokemon.getName() + " used " + move.getName() + " dealing " + damage + " damage to " + fasterPokemon.getName());
        if (fasterPokemon.receiveDamage(damage)) {
            fasterPokemon.die();
            logMessage(fasterPokemon.getName() + " fainted!");
            return;
        }
    }
    
    // Method to calculate damage
    private double calculateDamage(Pokemon attacker, Pokemon defender, Move move) {
        double probabilityCritical = 0.0625;
        double stab = (1 + booleanToInt(0.5, attacker.isType(move.getType())));
        double critical = new Random().nextDouble() < probabilityCritical ? 2 : 1;
        double randomDamage = 0.85 + (1.0 - 0.85) * (new Random().nextDouble());
        double typeDamage = (typeTable.get(move.getType()).get(defender.getPrimaryType()));
        if (defender.getSecondaryType() != null)
            typeDamage *= typeTable.get(move.getType()).get(defender.getSecondaryType());
        double modifier = critical * stab * critical * randomDamage * typeDamage;
        double attack = (double) attacker.getAttack(move.getCategory());
        double defense = (double) defender.getDefense(move.getCategory());
        double normalDamage = (((2*attacker.getLevel() + 10) / 250.0) * (attack/defense)*move.getPower() + 2);
        if (critical == 2) {
            logMessage("Critical Damage!");
        }
        return normalDamage * modifier;
    }
    
    public double booleanToInt(double ifTrueValue, boolean condition) {
        if (condition) {
            return ifTrueValue;
        }
        return 0;
    }

    private boolean isBattleOver() {
        boolean allPlayer1PokemonsFainted = true;
        boolean allPlayer2PokemonsFainted = true;

        // Check if any of player1's Pok\u00E9mon have remaining health points
        for (Pokemon pokemon : player1.getTeam()) {
            if (!pokemon.isDead()) {
                allPlayer1PokemonsFainted = false; // At least one Pok\u00E9mon of player1 has not fainted
                break;
            }
        }

        // Check if any of player2's Pok\u00E9mon have remaining health points
        for (Pokemon pokemon : player2.getTeam()) {
            if (!pokemon.isDead()) {
                allPlayer2PokemonsFainted = false; // At least one Pok\u00E9mon of player2 has not fainted
                break;
            }
        }

        // If all Pok\u00E9mon of a player have fainted, the battle is over
        return allPlayer1PokemonsFainted || allPlayer2PokemonsFainted;
    }
    
    private void selectPokemonForPlayer(Player player) {
        Scanner scanner = new Scanner(System.in);
        boolean validSelection = false;

        do {
            logMessage(player.getPlayerName() + ", choose your Pok\u00E9mon:");
            List<Pokemon> availablePokemon = new ArrayList<>();

            // Filter and display only Pok\u00E9mon with remaining health points
            for (Pokemon pokemon : player.getTeam()) {
                if (!pokemon.isDead()) {
                    availablePokemon.add(pokemon);
                }
            }

            if (availablePokemon.isEmpty()) {
                logMessage("No Pok\u00E9mon with remaining health points available!");
                return; // Exit method if no Pok\u00E9mon are available
            }

            for (int i = 0; i < availablePokemon.size(); i++) {
                logMessage((i + 1) + ". " + availablePokemon.get(i).getName());
            }

            System.out.print("Enter the index of your Pok\u00E9mon: ");
            int chosenIndex = scanner.nextInt();

            if (chosenIndex > 0 && chosenIndex <= availablePokemon.size()) {
                Pokemon chosenPokemon = availablePokemon.get(chosenIndex - 1);
                logMessage(player.getPlayerName() + " has chosen " + chosenPokemon.getName() + "!");
                player.setCurrentPokemon(chosenPokemon);
                validSelection = true; // Set to true to exit the loop
            } else {
                logMessage("Invalid index. Please choose a valid Pok\u00E9mon.");
                // Ask for Pok\u00E9mon selection again
            }
        } while (!validSelection); // Repeat until a valid Pok\u00E9mon index is chosen
    }

    public void logMessage(String message) {
        System.out.println(message);
    }

    public void logMessage(String message, boolean newLine) {
        System.out.print(message);
    }
}
