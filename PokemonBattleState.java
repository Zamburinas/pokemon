import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PokemonBattleState {
    private Player player1;
    private Player player2;
    private Player currentPlayer;
    private Player otherPlayer;
    private boolean isPlayer1Turn;
    private boolean isTerminal;

    public PokemonBattleState(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.currentPlayer = player1;
        this.otherPlayer = player2;
        this.isPlayer1Turn = true;
        this.isTerminal = false;
    }

    public boolean isTerminal() {
        return isTerminal;
    }

    public PokemonBattleState performAction(int action) {
        PokemonBattleState newState = new PokemonBattleState(player1, player2);
        newState.switchTurn();

        if (action >= 0 && action < 4) {
            // Implement logic to perform the attack
            // Example: newState.useMove(currentPlayer, otherPlayer, action);
        } else if (action == 4) {
            Pokemon newPokemon = newState.chooseRandomPokemon(currentPlayer);
            newState.switchActivePokemon(currentPlayer, newPokemon);
        }

        // Implement any other logic based on your game's rules

        return newState;
    }

    public List<Integer> getLegalActions() {
        List<Integer> legalActions = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            legalActions.add(i);
        }

        legalActions.add(4);

        return legalActions;
    }

    public double getScore() {
        return 0.0; // Placeholder, replace with actual implementation
    }

    private void switchTurn() {
        isPlayer1Turn = !isPlayer1Turn;
        if (isPlayer1Turn) {
            currentPlayer = player1;
            otherPlayer = player2;
        } else {
            currentPlayer = player2;
            otherPlayer = player1;
        }
    }

    private Pokemon chooseRandomPokemon(Player player) {
        List<Pokemon> availablePokemon = new ArrayList<>();
        for (Pokemon pokemon : player.getTeam()) {
            if (!pokemon.isDead() && !pokemon.isEqualTo(player.getCurrentPokemon())) {
                availablePokemon.add(new Pokemon(pokemon)); // Create a new Pokemon instance
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
