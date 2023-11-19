import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class Battle {
    private Player player1;
    private Player player2;
    private int turnCounter = 0;
    private Map<String, Map<String,Double>> typeTable;

    public Battle(Player player1, Player player2, Map<String, Map<String, Double>> typeTable2) {
        this.player1 = player1;
        this.player2 = player2;
        this.typeTable = typeTable2;
    }

    public void start() {

        int victoria = 0;

        if(!selectPokemonForPlayer(player1)){
            return;
        }
        player2.setCurrentPokemon(player2.getPokemonFromTeam(0));
        logMessage(player1.getPlayerName() + " sends out " + player1.getCurrentPokemon().getName() + " to the field!");
        logMessage(player2.getPlayerName() + " sends out " + player2.getCurrentPokemon().getName() + " to the field!");
        
        while (true) {
            playerTurn();
            victoria=isBattleOver();
            if (victoria!=0) break; // Verificar si la batalla ha terminado antes del segundo turno
        }

        if (victoria==1) {
            logMessage(player1.getPlayerName() + " wins!");
        } else if (victoria==2) {
            logMessage(player2.getPlayerName() + " wins!");
        } else {
            logMessage("Draw!");
        }


    }

    private void playerTurn() {
        turnCounter++;
        logMessage("Turn: " + turnCounter);
        while(player2.getCurrentPokemon().isDead()) player2.setPokemonFromTeam(new Random().nextInt(3));
        logMessage("AI Pokémon: " + player2.getCurrentPokemon().getName() + " HP: " + player2.getCurrentPokemon().getStats().getHealthPoints());
        int selectedMove1 = -1, selectedMove2 = -1;
        boolean pokemonSwitched = false;
        Pokemon player1Pokemon = player1.getCurrentPokemon();
        
        Scanner scanner;
        do {
            // Display details of the current Pokémon and its available moves
            logMessage("Current Pokémon: " + player1Pokemon.getName());
            logMessage("HP: " + player1Pokemon.getStats().getHealthPoints());        
    
            logMessage("Choose an action:");
            logMessage("1. Attack with a move");            
            logMessage("2. Change Pokémon");
    
            scanner = new Scanner(System.in);
            int choice = scanner.nextInt();
            
            if (choice == 1) {
                // Player wants to attack with a move
                logMessage("Choose a move (enter move index):");
                logMessage("Available moves:");
                logMessage("0. Go back");
                Move[] moves = player1.getCurrentPokemon().getMoves();
                for (int i = 0; i < moves.length; i++) {
                    if (player1Pokemon.isMoveAvailable(i)) {
                        logMessage((i + 1) + ". " + player1Pokemon.getMoves()[i].getInfo(), false);
                        logMessage(player1Pokemon.getMoves()[i].getMoveStats());
                    }
                }
                
                int moveIndex;
                do {
                    moveIndex = scanner.nextInt();
                    if (moveIndex == 0) {
                        break; // Go back to the action menu
                    }
                    if (!(moveIndex > 0 && moveIndex <= player1Pokemon.getMoves().length) || !player1Pokemon.isMoveAvailable(moveIndex - 1)) {
                        logMessage("Invalid move index. Please choose a valid move.");
                    }
                } while (!(moveIndex > 0 && moveIndex <= player1Pokemon.getMoves().length) || !player1Pokemon.isMoveAvailable(moveIndex - 1));
    
                if (moveIndex != 0) {
                    selectedMove1 = moveIndex - 1;
                }
            } else if (choice == 2) {
                // Player wants to change Pokémon
                logMessage("Choose a Pokémon to switch to (enter Pokémon index or 0 to go back):");
                pokemonSwitched = selectPokemonForPlayer(player1);

            } else {
                logMessage("Invalid choice. Please choose again.");
            }
            
            if (selectedMove1 != -1 || pokemonSwitched) {
                break; // Exit loop if a move is selected or pokemon is changed
            }
        } while (true);
    
        

        selectedMove2 = new Random().nextInt(player2.getCurrentPokemon().getMoves().length);
        resolveTurn(player1.getCurrentPokemon(), player2.getCurrentPokemon(), selectedMove1, selectedMove2);

        if (player1.getCurrentPokemon().isDead()) {
            do {selectPokemonForPlayer(player1);
            } while (isBattleOver()==0);
        }
    }
    

    private void resolveTurn(Pokemon pokemonPlayer1, Pokemon pokemonPlayer2, int move1, int move2) {
        // Borth want To change
        if (move1 == -1 && move2 == -1) {
            return;
        }
        // First we check who is faster
        // If they're equally fast, it will be random with 1/2 probability
        Pokemon fasterPokemon = pokemonPlayer1, slowerPokemon = pokemonPlayer2;
        int fastMove = move1, slowMove = move2;
        Move move;
        if (pokemonPlayer2.getSpeed() > pokemonPlayer1.getSpeed() || (pokemonPlayer2.getSpeed() == pokemonPlayer1.getSpeed() && new Random().nextDouble() >= 0.5) || move1 == -1) {
            fasterPokemon = pokemonPlayer2;
            slowerPokemon = pokemonPlayer1;
            fastMove = move2;
            slowMove = move1;
        }
        move = fasterPokemon.useMove(fastMove);
        double damage = calculateDamage(fasterPokemon, slowerPokemon, move);
        
        logMessage(fasterPokemon.getName() + " used " + move.getName() + " dealing " + damage + " damage to " + slowerPokemon.getName());
        if (slowerPokemon.receiveDamage(damage)) {
            slowerPokemon.die();
            logMessage(slowerPokemon.getName() + " fainted!");
            return;
        }
        if (slowMove == -1) {
            return;
        }
        move = slowerPokemon.useMove(slowMove);
        damage = calculateDamage(slowerPokemon, fasterPokemon, move);
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

    private int isBattleOver() {
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

        if (allPlayer2PokemonsFainted) {
            return 1;
        } else if (allPlayer1PokemonsFainted) {
            return 2;
        }             

        return 0;
    }
    
    private boolean selectPokemonForPlayer(Player player) {
        Scanner scanner = new Scanner(System.in);
        boolean validSelection = false;
        boolean switched = false;
    
        do {
            logMessage(player.getPlayerName() + ", choose your Pokémon:");
            List<Pokemon> availablePokemon = new ArrayList<>();
    
            // Filter and display only Pokémon with remaining health points
            for (Pokemon pokemon : player.getTeam()) {
                if (!pokemon.isDead()) {
                    availablePokemon.add(pokemon);
                }
            }
    
            if (availablePokemon.isEmpty()) {
                logMessage("No Pokémon with remaining health points available!");
                return switched; // Exit method if no Pokémon are available
            }
    
            for (int i = 0; i < availablePokemon.size(); i++) {
                logMessage((i + 1) + ". " + availablePokemon.get(i).getName());
            }
    
            System.out.print("Enter the index of your Pokémon (enter 0 to cancel): ");
            int chosenIndex = scanner.nextInt();
    
            if (chosenIndex == 0) {
                return switched; // Exit method if the player selects 0
            }
    
            if (chosenIndex > 0 && chosenIndex <= availablePokemon.size()) {
                Pokemon chosenPokemon = availablePokemon.get(chosenIndex - 1);
                logMessage(player.getPlayerName() + " has chosen " + chosenPokemon.getName() + "!");
                player.setCurrentPokemon(chosenPokemon);
                switched = true;
                validSelection = true; // Set to true to exit the loop
            } else {
                logMessage("Invalid index. Please choose a valid Pokémon.");
                // Ask for Pokémon selection again
            }
        } while (!validSelection); // Repeat until a valid Pokémon index is chosen
    
        return switched;
    }
    

    public void logMessage(String message) {
        System.out.println(message);
    }

    public void logMessage(String message, boolean newLine) {
        System.out.print(message);
    }
}
