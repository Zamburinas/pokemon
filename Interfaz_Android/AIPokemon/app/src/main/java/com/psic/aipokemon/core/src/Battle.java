package com.psic.aipokemon.core.src;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class Battle {
    private Player player1;
    private Player player2;
    private int turnCounter = 0;
    public static Map<String, Map<String,Double>> typeTable;
    private static final int pokemonChange = -1;

    private static ArrayList<String> messages = new ArrayList<>();
    private static ArrayList<String> chat = new ArrayList<>();

    public Battle(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
    }

    public String[] start() {

        int victoria = 0;

        player1.setCurrentPokemon(player1.getPokemonFromTeam(0));
        player2.setCurrentPokemon(player2.getPokemonFromTeam(0));
        logMessage(player1.getPlayerName() + " sends out " + player1.getCurrentPokemon().getName() + " to the field!");
        addArrays(player1.getPlayerName() + " sends out " + player1.getCurrentPokemon().getName() + " to the field!");
        logMessage(player2.getPlayerName() + " sends out " + player2.getCurrentPokemon().getName() + " to the field!");
        addArrays(player2.getPlayerName() + " sends out " + player2.getCurrentPokemon().getName() + " to the field!");
        int[] range = RandomSpeed(player2.getCurrentPokemon().getSpeed(), 30);
        logMessage("Speed:   "+range[0]+"-"+range[1]);        
        return new String[]{player1.getCurrentPokemon().getName(), player2.getCurrentPokemon().getName(),""+range[0],""+range[1]};


    }

    private void playerTurn() {
        turnCounter++;
        logMessage("Turn: " + turnCounter);
        if (player2.getCurrentPokemon().isDead()) {
            player2.setPokemonFromTeam(new MonteCarloTreeSearch().findBestMove(new PokemonBattleState(player1, player2)));
        }
        double healthPercentage2 = ((double) player2.getCurrentPokemon().getStats().getHealthPoints() / player2.getCurrentPokemon().getMaxHealthPoints()) * 100;
        logMessage("AI Pokémon: " + player2.getCurrentPokemon().getName() + " (" + String.format("%.2f", healthPercentage2) + "% HP)");
        int[] range = RandomSpeed(player2.getCurrentPokemon().getSpeed(), 30);
        logMessage("Speed:   "+range[0]+"-"+range[1]);
        int selectedMove1 = pokemonChange, selectedMove2 = pokemonChange;
        boolean pokemonSwitched = false;
        Pokemon player1Pokemon = player1.getCurrentPokemon();
        
        Scanner scanner;
        do {
            // Display details of the current Pok\u00E9mon and its available moves
            double healthPercentagePlayer1 = ((double) player1Pokemon.getStats().getHealthPoints() / player1Pokemon.getMaxHealthPoints()) * 100;
            logMessage("Current Pokémon: " + player1Pokemon.getName() + " (" + String.format("%.2f", healthPercentagePlayer1) + "% HP)");                
            logMessage("Choose an action:");
            logMessage("1. Attack with a move");            
            logMessage("2. Change Pok\u00E9mon");
    
            scanner = new Scanner(System.in);
            int choice = scanner.nextInt();
            switch(choice) {
                case 1:
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
                                break;
                            }
                            if (!(moveIndex > 0 && moveIndex <= player1Pokemon.getMoves().length) || !player1Pokemon.isMoveAvailable(moveIndex - 1)) {
                                logMessage("Invalid move index. Please choose a valid move.");
                            }
                        } while (!(moveIndex > 0 && moveIndex <= player1Pokemon.getMoves().length) || !player1Pokemon.isMoveAvailable(moveIndex - 1));
            
                        if (moveIndex != 0) {
                            selectedMove1 = moveIndex - 1;
                        }
                        break;
                case 2:
                        // Player wants to change Pok\u00E9mon
                        logMessage("Choose a Pok\u00E9mon to switch to (enter Pok\u00E9mon index or 0 to go back):");
                        pokemonSwitched = selectPokemonForPlayer(player1);
                        break;
                default:
                        logMessage("Invalid choice. Please choose again.");
            }
        } while (!(selectedMove1 != -1 || pokemonSwitched));
    
        
        MonteCarloTreeSearch mcts = new MonteCarloTreeSearch();
        selectedMove2 = mcts.findBestMove(new PokemonBattleState(player1, player2));
        resolveTurn(player1.getCurrentPokemon(), player2.getCurrentPokemon(), selectedMove1, selectedMove2, true);
        
        if (player1.getCurrentPokemon().isDead()) {
            do {selectPokemonForPlayer(player1);
            } while ((isBattleOver(player1, player2)==0) && player1.getCurrentPokemon().isDead());
        }
        // scanner.close();
    }
    

    public static void resolveTurn(Pokemon pokemonPlayer1, Pokemon pokemonPlayer2, int move1, int move2, boolean log) {
        pokemonPlayer1.getStats().setInitHP(pokemonPlayer1.getStats().getHealthPoints());
        pokemonPlayer2.getStats().setInitHP(pokemonPlayer2.getStats().getHealthPoints());
        // Both want To change
        if (move1 == pokemonChange && move2 == pokemonChange) {
            return;
        }
        // First we check who is faster
        // If they're equally fast, it will be random with 1/2 probability
        Pokemon fasterPokemon = pokemonPlayer1, slowerPokemon = pokemonPlayer2;
        int fastMove = move1, slowMove = move2;
        Move move;
        if (((pokemonPlayer2.getSpeed() > pokemonPlayer1.getSpeed() || (pokemonPlayer2.getSpeed() == pokemonPlayer1.getSpeed() && new Random().nextDouble() >= 0.5)) && move2 != pokemonChange)  || move1 == pokemonChange) {
            fasterPokemon = pokemonPlayer2;
            slowerPokemon = pokemonPlayer1;
            fastMove = move2;
            slowMove = move1;
        }

        if(!(fastMove==-1)) {
            move = fasterPokemon.useMove(fastMove);
            double damage = calculateDamage(fasterPokemon, slowerPokemon, move, log);

            applyPassiveEffect(fasterPokemon, slowerPokemon, move, log);
            if (slowerPokemon.receiveDamage(damage)) {
                slowerPokemon.die();
                if (log) {
                    logMessage(slowerPokemon.getName() + " fainted!");
                    addArrays(slowerPokemon.getName() + " fainted!");
                }
                
                return;
            }
            if (slowMove == pokemonChange) {
                return;
            }
        }
        if(!(slowMove==-1)) {
            move = slowerPokemon.useMove(slowMove);
            double damage = calculateDamage(slowerPokemon, fasterPokemon, move, log);

            applyPassiveEffect(slowerPokemon, fasterPokemon, move, log);
            if (fasterPokemon.receiveDamage(damage)) {
                fasterPokemon.die();
                if (log) {
                    logMessage(fasterPokemon.getName() + " fainted!");
                    addArrays(fasterPokemon.getName() + " fainted!");
                }
                
                return;
            }
        }
    }
    

    // Method to calculate damage
    private static double calculateDamage(Pokemon attacker, Pokemon defender, Move move, boolean log) {
        double probabilityCritical = log ? 0.0625 : 0.0;
        double stab = (1 + booleanToInt(0.5, attacker.isType(move.getType())));
        double critical = new Random().nextDouble() < probabilityCritical ? 2.0 : 1.0;
        double randomDamage = 0.85 + (1.0 - 0.85) * (new Random().nextDouble());
        double typeDamage = (typeTable.get(move.getType()).get(defender.getPrimaryType()));
        
        if (defender.getSecondaryType() != null)
            typeDamage *= typeTable.get(move.getType()).get(defender.getSecondaryType());
        
        double accuracy = move.getAccuracy();
        boolean attackHits = new Random().nextDouble() * 100 <= accuracy;
        
        if (!attackHits && log) {
            logMessage(attacker.getName() + " tried to use " + move.getName() + " but it missed!");
            addArrays(attacker.getName() + " tried to use " + move.getName() + " but it missed!");
            return 0; // Retorna 0 si el ataque falla
        }
        
        double modifier = stab * critical * randomDamage * typeDamage;
        double attack = (double) attacker.getAttack(move.getCategory());
        double defense = (double) defender.getDefense(move.getCategory());
        double normalDamage = (((2 * attacker.getLevel() + 10) / 250.0) * (attack / defense) * move.getPower());
        double damage=normalDamage * modifier;
        double damageDone;
        if (log) {
            damageDone = damage <= defender.remainingHealth() ? (damage * 100.0) / defender.getMaxHealthPoints() : (defender.remainingHealth() * 100.0) / defender.getMaxHealthPoints();
        } else {
            damageDone = (damage * 100.0) / defender.getMaxHealthPoints();
        }
        if (!move.getName().equalsIgnoreCase("Soft Boiled") && !move.getName().equalsIgnoreCase("Synthesis") && log) {
            logMessage(String.format(Locale.US, "%s used %s dealing %.2f %% damage to %s", attacker.getName(), move.getName(), damageDone, defender.getName()));
            addArrays(String.format(Locale.US, "%s used %s dealing %.2f %% damage to %s", attacker.getName(), move.getName(), damageDone, defender.getName()));
        }

        if(typeDamage>=2 && log){
            if (!move.getName().equalsIgnoreCase("Soft Boiled") && !move.getName().equalsIgnoreCase("Synthesis")) {
                logMessage("It's super effective!");
                addArrays("It's super effective!");
            }
        }

        if (critical == 2 && modifier != 0 && log) {
            if (!move.getName().equalsIgnoreCase("Soft Boiled") && !move.getName().equalsIgnoreCase("Synthesis")) {
                logMessage("Critical Damage!");
                addArrays("Critical Damage!");
            }
        }
        return damage;
    }       
    
    
    private static boolean applyPassiveEffect(Pokemon ownPokemon, Pokemon opponentPokemon, Move move, boolean log) {

        ownPokemon.getStats().resetAddedHP();

        // aplica pasivas y devuelve true si el pokemon activo muere
        if (move.getPassive() != null) {
            Move.Passive passive = move.getPassive();
            // Verifica si la pasiva tiene un efecto de "Health"
            if (passive.getEffect() != null && passive.getEffect().equalsIgnoreCase("Health")) {
                Move.Modifier modifier = passive.getModifier();
                // Verifica si el modificador es para "Health"
                if (modifier != null && modifier.getStat().equalsIgnoreCase("healthPoints")) {
                    String user = modifier.getUser();
                    if (log)
                        System.out.println("User: " + user);
                    // Si el modificador es propio
                    if (user.equalsIgnoreCase("Own")) {
                        // Si el valor del modificador es 0, asume un efecto total de agotamiento de la salud
                        if (modifier.getValue() == 0) {
                            // Establece la salud del Pokémon en 0 (agotamiento total)
                            ownPokemon.receiveDamage(ownPokemon.remainingHealth());
                            if (log) {
                                logMessage(ownPokemon.getName() + " used " + move.getName() + ", sacrificing itself!");
                                addArrays(ownPokemon.getName() + " used " + move.getName() + ", sacrificing itself!");
                            }
                            return true;
                        }
                        // Si el valor del modificador no es 0, asume un efecto diferente
                        else {
                            // Lógica para otros efectos de "Health" según sea necesario
                            // Por ejemplo, para movimientos que curan el 100% de la salud
                            double healAmount = ownPokemon.getMaxHealthPoints()/2.0;
                            double restoredHealthh = ownPokemon.addHealthPoints(healAmount); // Método para aumentar la salud del Pokémon propio
                            DecimalFormat formato = new DecimalFormat("#.##");
                            double percRestHealth = restoredHealthh/ownPokemon.getMaxHealthPoints()*100;
                            if (log) {
                                logMessage(ownPokemon.getName() + " used " + move.getName() + ", restoring " + formato.format(percRestHealth) + "% of its health!");
                                addArrays(ownPokemon.getName() + " used " + move.getName() + ", restoring " + formato.format(percRestHealth) + "% of its health!");
                            }
                            return false;
                        }
                    }
                    // Puedes agregar más verificaciones aquí para otros efectos de pasivas si es necesario
                }
            }
            
        }
        return false;
    }


    public static double booleanToInt(double ifTrueValue, boolean condition) {
        if (condition) {
            return ifTrueValue;
        }
        return 0;
    }


    public static int isBattleOver(Player player1Pokemon, Player player2Pokemon) {
        boolean allPlayer1PokemonsFainted = true;
        boolean allPlayer2PokemonsFainted = true;

        // Check if any of player1's Pok\u00E9mon have remaining health points
        for (Pokemon pokemon : player1Pokemon.getTeam()) {
            if (!pokemon.isDead()) {
                allPlayer1PokemonsFainted = false; // At least one Pok\u00E9mon of player1 has not fainted
                break;
            }
        }

        // Check if any of player2's Pok\u00E9mon have remaining health points
        for (Pokemon pokemon : player2Pokemon.getTeam()) {
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
    
    private static boolean selectPokemonForPlayer(Player player) {
        Scanner scanner = new Scanner(System.in);
        boolean validSelection = false;
        boolean switched = false;
    
        do {
            logMessage(player.getPlayerName() + ", choose your Pok\u00E9mon:");
            List<Pokemon> availablePokemon = new ArrayList<>();
    
            // Filter and display only Pok\u00E9mon with remaining health points
            for (Pokemon pokemon : player.getTeam()) {
                if (!pokemon.isDead() && !pokemon.isEqualTo(player.getCurrentPokemon())) {
                    availablePokemon.add(pokemon);
                }
            }
    
            if (availablePokemon.isEmpty()) {
                logMessage("No Pok\u00E9mon with remaining health points available!");
                return switched; // Exit method if no Pok\u00E9mon are available
            }
    
            for (int i = 0; i < availablePokemon.size(); i++) {
                logMessage((i + 1) + ". " + availablePokemon.get(i).getName());
            }
    
            logMessage("Enter the index of your Pok\u00E9mon (enter 0 to cancel): ");
            int chosenIndex = scanner.nextInt();
    
            if (chosenIndex == 0) {
                return switched; // Exit method if the player selects 0
            }
    
            if (chosenIndex > 0 && chosenIndex <= availablePokemon.size() && !availablePokemon.get(chosenIndex - 1).isEqualTo(player.getCurrentPokemon()) && !availablePokemon.get(chosenIndex - 1).isDead()) {
                Pokemon chosenPokemon = availablePokemon.get(chosenIndex - 1);
                logMessage(player.getPlayerName() + " has chosen " + chosenPokemon.getName() + "!");
                player.setCurrentPokemon(chosenPokemon);
                switched = true;
                validSelection = true; // Set to true to exit the loop
            } else {
                logMessage("Invalid index. Please choose a valid Pok\u00E9mon.");
                // Ask for Pok\u00E9mon selection again
            }
        } while (!validSelection); // Repeat until a valid Pok\u00E9mon index is chosen
        // scanner.close();
        return switched;
    }
    

    public static void logMessage(String message) { System.out.println(message); }

    public static void logMessage(String message, boolean newLine) {
        System.out.print(message);
    }

    public static void addArrays(String message) {
        messages.add(message);
        chat.add(message);
    }
    public static int[] RandomSpeed(int num, int range) {
        Random random = new Random();
        int halfRange = range / 2;
        int offset = random.nextInt(halfRange + 1) - halfRange;
        int lowLimit = num - halfRange + offset;
        int uppLimit = lowLimit + range - 1;

        if (lowLimit < 0) {
            lowLimit = 0;
            uppLimit = range - 1;
        }

        int[] rango = {lowLimit, uppLimit};
        return rango;
    }

    public ArrayList<String> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<String> messagesAux) {
        messages = messagesAux;
    }

    public ArrayList<String> getChat() {
        return chat;
    }

    public void setChat(ArrayList<String> chatAux) {
        chat = chatAux;
    }
}
