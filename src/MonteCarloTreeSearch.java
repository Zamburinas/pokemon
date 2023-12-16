import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class MonteCarloTreeSearch {
    private static final int SIMULATION_COUNT = 1000;
    private static final double EXPLORATION_PARAMETER = 1.4;

    /**
     * Find the best move using Monte Carlo Tree Search.
     * @param initialState The initial state of the game.
     * @return The best move.
     */
    public static int findBestMove(PokemonBattleState initialState) {
        Node root = new Node();
        root.state = initialState;

        // Run simulations to build the tree
        for (int i = 0; i < SIMULATION_COUNT; i++) {
            Node selectedNode = select(root);
            Node expandedNode = expand(selectedNode);
            double simulationResult = simulate(expandedNode);
            backpropagate(expandedNode, simulationResult);
        }

        // Choose the best move based on the tree
        Node bestChild = getBestChild(root);
        return bestChild.action;
    }

    /**
     * Select a node for exploration in the tree.
     * @param node The current node.
     * @return The selected node.
     */
    private static Node select(Node node) {
        // Traverse the tree until a node is found that is not fully expanded or is terminal
        while (!node.state.isTerminal() && node.isFullyExpanded()) {
            node = node.bestChild(EXPLORATION_PARAMETER);
        }
        return node;
    }

    /**
     * Expand the tree by adding a new child node.
     * @param node The current node.
     * @return The expanded node.
     */
    private static Node expand(Node node) {
        List<Integer> untriedActions = node.getUntriedActions();
        if (!untriedActions.isEmpty()) {
            int randomAction = untriedActions.remove(new Random().nextInt(untriedActions.size()));
            PokemonBattleState nextState = node.state.performAction(randomAction);
            Node child = new Node();
            child.state = nextState;
            child.parent = node;
            child.action = randomAction;
            node.children.add(child);
            return child;
        }
        return node;
    }

    /**
     * Simulate a random playthrough from a node.
     * @param node The node to start the simulation from.
     * @return The result of the simulation.
     */
    private static double simulate(Node node) {
        PokemonBattleState state = node.state.clone();
        while (!state.isTerminal()) {
            List<Integer> legalActions = state.getLegalActions();
            int randomAction = legalActions.get(new Random().nextInt(legalActions.size()));
            state = state.performAction(randomAction);
        }
        return state.getScore();
    }

    /**
     * Backpropagate the result of a simulation up the tree.
     * @param node The node to start the backpropagation from.
     * @param result The result of the simulation.
     */
    private static void backpropagate(Node node, double result) {
        while (node != null) {
            node.visits++;
            node.totalScore += result;
            node = node.parent;
        }
    }

    /**
     * Get the best child node based on exploration and exploitation.
     * @param node The current node.
     * @return The best child node.
     */
    private static Node getBestChild(Node node) {
        return node.bestChild(0); // Exploration parameter set to 0 for pure exploitation
    }

    /**
     * Internal class representing a node in the MCTS tree.
     */
    private static class Node {
        PokemonBattleState state;
        Node parent;
        int action;
        int visits;
        double totalScore;
        List<Node> children = new ArrayList<>();

        /**
         * Check if the node is fully expanded.
         * @return True if fully expanded, false otherwise.
         */
        boolean isFullyExpanded() {
            return getUntriedActions().isEmpty();
        }

        /**
         * Get untried actions from the current state.
         * @return List of untried actions.
         */
        List<Integer> getUntriedActions() {
            List<Integer> allActions = state.getLegalActions();
            List<Integer> triedActions = new ArrayList<>();
            for (Node child : children) {
                triedActions.add(child.action);
            }
            allActions.removeAll(triedActions);
            return allActions;
        }

        /**
         * Get the best child node based on exploration and exploitation.
         * @param explorationParameter The exploration parameter.
         * @return The best child node.
         */
        Node bestChild(double explorationParameter) {
            double bestValue = Double.NEGATIVE_INFINITY;
            Node bestChild = null;
            for (Node child : children) {
                double exploitation = (child.totalScore / child.visits);
                double exploration = Math.sqrt(Math.log(visits) / child.visits);
                double value = exploitation + explorationParameter * exploration;
                if (value > bestValue) {
                    bestValue = value;
                    bestChild = child;
                }
            }
            return bestChild;
        }
    }
}
