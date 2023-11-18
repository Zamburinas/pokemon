import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class PokemonGame {
    public static void main(String[] args) {
        Player player = new Player("Ash");
        Player IA = new Player("IA");
        Scanner scanner = new Scanner(System.in);

        List<Pokemon> availablePokemons = PokemonDataReader.createAvailablePokemons("../data/Pokemons.json");
        Map<String, Map<String,Double>> typeTable = PokemonDataReader.createTypeTable("../data/TypesTable.json");
        if (availablePokemons != null) {
            int choice;
            do {
                System.out.println("Welcome to the Pokemon Game!");
                System.out.println("1. Show Team");
                System.out.println("2. Add Pokemon");
                System.out.println("3. Remove Pokemon");
                System.out.println("4. Combat");
                System.out.println("5. Exit");
                System.out.print("Choose an option: ");

                choice = scanner.nextInt();
                switch (choice) {
                    case 1:
                        System.out.println("Your Pok\u00E9mon Team:");
                        player.showTeam();
                        break;
                    case 2:
                        System.out.println("Choose Pok\u00E9mon to add to your team (enter index, 0 to finish):");
                        for (int i = 0; i < availablePokemons.size(); i++) {
                            System.out.println((i + 1) + ". " + availablePokemons.get(i).getName());
                        }
                        int index;
                        do {
                            index = scanner.nextInt();
                            if (index > 0 && index <= availablePokemons.size()) {
                                boolean added = player.addPokemonToTeam(availablePokemons.get(index - 1));
                                if (added) {
                                    System.out.println(availablePokemons.get(index - 1).getName() + " added to your team!");
                                }
                            } else if (index != 0) {
                                System.out.println("Invalid index. Please choose again or enter 0 to finish.");
                            }
                        } while (index != 0);
                        break;
                    case 3:
                        System.out.print("Enter the index of the Pok\u00E9mon to remove: ");
                        int indexToRemove = scanner.nextInt();
                        player.removePokemonFromTeam(indexToRemove - 1); // Adjust index for 0-based list
                        break;
                    case 4:
                        //before combat, create IA Pokemon team
                        Random random = new Random();
                        for (int i = 0; i < 3; i++) {
                            int randomIndex = random.nextInt(availablePokemons.size());
                            Pokemon randomPokemon = availablePokemons.get(randomIndex);
                            IA.addPokemonToTeam(randomPokemon);
                        }
                        System.out.println("Initiating combat...");
                        Battle battle = new Battle(player, IA, typeTable);
                        battle.start();
                        break;
                    case 5:
                        System.out.println("Exiting the game. Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid choice. Please choose again.");
                        break;
                }
            } while (choice != 5);
        } else {
            System.out.println("Failed to read Pok\u00E9mon data.");
        }

        scanner.close();


    }
}
