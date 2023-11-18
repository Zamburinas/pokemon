import java.util.List;
import java.util.Scanner;

public class PokemonGame {
    public static void main(String[] args) {
        Player player = new Player("Ash");
        Scanner scanner = new Scanner(System.in);

        List<Pokemon> availablePokemons = PokemonDataReader.createAvailablePokemons("../data/Pokemons.json");
        // int [][] typeTable = createTypesTable();
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
                        System.out.println("Your Pokémon Team:");
                        player.showTeam();
                        break;
                    case 2:
                        System.out.println("Choose Pokémon to add to your team (enter index, 0 to finish):");
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
                        System.out.print("Enter the index of the Pokémon to remove: ");
                        int indexToRemove = scanner.nextInt();
                        player.removePokemonFromTeam(indexToRemove - 1); // Adjust index for 0-based list
                        break;
                    case 4:
                        System.out.println("Initiating combat...");
                        
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
            System.out.println("Failed to read Pokémon data.");
        }

        scanner.close();


    }
}
