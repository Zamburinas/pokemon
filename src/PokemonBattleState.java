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
        return Battle.isBattleOver(player1, player2) != 0;
    }

    public PokemonBattleState performAction(int action) {
        PokemonBattleState newState = new PokemonBattleState(this);
        int pokemon2move = new Random().nextInt(4);

        if (action >= 0 && action < 4) {
            Battle.resolveTurn(newState.player1.getCurrentPokemon(), newState.player2.getCurrentPokemon(), action, pokemon2move, false);
            Pokemon newPokemon = newState.chooseRandomPokemon(newState.player1);
            if (newState.player1.getCurrentPokemon().isDead() && newPokemon != null) {
                newState.player1.setCurrentPokemon(newPokemon);
            }
            newPokemon = newState.chooseRandomPokemon(newState.player2);
            if (newState.player2.getCurrentPokemon().isDead() && newPokemon != null) {
                newState.player2.setCurrentPokemon(newPokemon);
            }
        } else if (action == -1) {
            Pokemon newPokemon = newState.chooseRandomPokemon(newState.player2);
            newState.switchActivePokemon(newState.player2, newPokemon);
            Battle.resolveTurn(newState.player2.getCurrentPokemon(), newState.player2.getCurrentPokemon(), action, pokemon2move, false);
        }
        return newState;
    }

    public List<Integer> getLegalActions() {
        List<Integer> legalActions = new ArrayList<>();
        Move[] moves = player1.getCurrentPokemon().getMoves();

        for (int i = 0; i < moves.length; i++) {
            if (player1.getCurrentPokemon().isMoveAvailable(i) && !player1.getCurrentPokemon().isDead()) {
                legalActions.add(i);
            }
        }

        return legalActions;
    }

    public double getScore() {
        // // * 1 IF SPEED OF AI > SPEED USER, * 0.8 IF SPEED USER > SPEED AI
        // double multiplier = player1.getCurrentPokemon().getSpeed() > player2.getCurrentPokemon().getSpeed() ? 0.8 : 1;
        // Different in health of both pokemons
        double diffHealth =  player1.getCurrentPokemon().getStats().getHealthPoints() - player2.getCurrentPokemon().getStats().getHealthPoints();
        double finalPoints = diffHealth + 33.33 * (player2.getRemainingPokemons() - player1.getRemainingPokemons());
        return finalPoints;
    }

    private Pokemon chooseRandomPokemon(Player player) {
        List<Pokemon> availablePokemon = new ArrayList<>();
        for (Pokemon pokemon : player.getTeam()) {
            if (!pokemon.isDead() && !pokemon.isEqualTo(player.getCurrentPokemon())) {
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
}
