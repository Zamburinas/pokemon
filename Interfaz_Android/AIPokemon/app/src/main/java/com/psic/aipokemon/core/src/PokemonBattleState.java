package com.psic.aipokemon.core.src;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PokemonBattleState implements Cloneable {
    private Player player1;
    private Player player2;
    private boolean isPlayer1Turn;
    private boolean isTerminal;

    public PokemonBattleState(Player player1, Player player2) {
        this.player1 = new Player(player1);
        this.player2 = new Player(player2);
        this.player1 = player1;
        this.player2 = player2;
        this.isPlayer1Turn = true;
        this.isTerminal = false;
    }

    public PokemonBattleState(PokemonBattleState state) {
        this.player1 = new Player(state.getPlayer1());
        this.player2 = new Player(state.getPlayer2());
        this.isPlayer1Turn = state.isPlayer1Turn();
        this.isTerminal = state.isTerminal;
    }

    public Player getPlayer1() {
        return player1;
    }

    public boolean isPlayer1Turn(){
        return isPlayer1Turn;
    }

    public Player getPlayer2() {
        return player2;
    }

    public boolean isTerminal() {
        for (int i = 0; i < player1.getTeam().size(); i++){
            if (player1.getTeam().get(i).getShown() && !player1.getTeam().get(i).isDead()) {
                return false || Battle.isBattleOver(player1, player2) != 0 || isTerminal;
            }
        }
        return true;
    }

    public void performAction(int action) {
        // PokemonBattleState newState = new PokemonBattleState(this);
        int pokemon2move = getAction(player2, player1);
        if (action >= 0 && action < 4 && !player2.getCurrentPokemon().isDead()) {
            Battle.resolveTurn(player1.getCurrentPokemon(), player2.getCurrentPokemon(), pokemon2move, action, false);
            if (player2.getCurrentPokemon().isDead()) {
                this.isTerminal = true;
            }
        } else{
            Pokemon newPokemon = getPlayer2().getPokemonFromTeam(action);
            switchActivePokemon(player2, newPokemon);
            Battle.resolveTurn(player2.getCurrentPokemon(), player2.getCurrentPokemon(), pokemon2move, -1, false);
        }
    }

    public List<Integer> getLegalActions() {
        return getLegalActions(player2);
    }

    public List<Integer> getLegalActions(Player playerTest) {
        List<Integer> legalActions = new ArrayList<>();
        Move[] moves = playerTest.getCurrentPokemon().getMoves();
        if (!playerTest.getCurrentPokemon().isDead()) {
            for (int i = 0; i < moves.length; i++) {
                if (playerTest.getCurrentPokemon().isMoveAvailable(i) && !playerTest.getCurrentPokemon().isDead()) {
                    legalActions.add(i);
                }
            }
        } else {
            for (int i = 0; i < playerTest.getRemainingChange().size(); i++) {
                legalActions.add(playerTest.getRemainingChange().get(i));
            }
        }


        return legalActions;
    }

    public double getScore(PokemonBattleState state) {
        double diffHealth = player2.getTeamHealth() - player1.getTeamHealth();

        double mediumPoints = 50 * ((double) ((state.getPlayer1().getRemainingPokemons() - player1.getRemainingPokemons()) - (state.getPlayer2().getRemainingPokemons() - player2.getRemainingPokemons())));

        double battleOver = 0;
        if (Battle.isBattleOver(player1, player2) != 0)
            battleOver = Battle.isBattleOver(player1, player2) == 1 ? -200 : 200;
        double finalPoints = mediumPoints + battleOver + diffHealth;

        return finalPoints;
    }

    private Pokemon chooseRandomPokemon(Player player, boolean check) {
        List<Pokemon> availablePokemon = new ArrayList<>();
        for (Pokemon pokemon : player.getTeam()) {
            if (!pokemon.isDead() && !pokemon.isEqualTo(player.getCurrentPokemon()) && (!check || pokemon.getShown())) {
                availablePokemon.add(pokemon); // Create a new Pokemon instance
            }
        }

        if (!availablePokemon.isEmpty()) {
            Random random = new Random();
            return availablePokemon.get(random.nextInt(availablePokemon.size()));
        } else {
            // No available Pokemon, return null or handle accordingly
            return null;
        }
    }

    private void switchActivePokemon(Player player, Pokemon newPokemon) {
        player.setCurrentPokemon(newPokemon);
    }

    public int getAction(){
        return getAction(player1, player2);
    }

    public int getAction(Player player1Playing, Player player2Playing){
        List<Integer> legalActions = getLegalActions(player2Playing);
        List<Integer> legalActionsBest = new ArrayList<>();
        Move [] moves = player2Playing.getCurrentPokemon().getMoves();
        double bestTypeDamage = -1000;
        for(int i = 0; i < legalActions.size(); i++) {
            if (getEffectiveness(moves[legalActions.get(i)], player1Playing.getCurrentPokemon()) > bestTypeDamage) {
                bestTypeDamage = getEffectiveness(moves[legalActions.get(i)], player1Playing.getCurrentPokemon());
            }
        }

        for(int i = 0; i < legalActions.size(); i++) {
            if (getEffectiveness(moves[legalActions.get(i)], player1Playing.getCurrentPokemon()) == bestTypeDamage) {
                legalActionsBest.add(legalActions.get(i));
            }
        }
        if (legalActionsBest.size() > 0) {
            return legalActionsBest.get(new Random().nextInt(legalActionsBest.size()));
        }
        return legalActions.get(new Random().nextInt(legalActions.size()));
    }

    public double getEffectiveness(Move move, Pokemon pokemon) {
        double typeDamage = (Battle.typeTable.get(move.getType()).get(pokemon.getPrimaryType()));

        if (pokemon.getSecondaryType() != null)
            typeDamage *= Battle.typeTable.get(move.getType()).get(pokemon.getSecondaryType());

        return typeDamage;
    }
}
