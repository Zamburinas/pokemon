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
                System.out.println("4. Random Team");
                System.out.println("5. Combat");
                System.out.println("6. Exit");
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
                        System.out.print("Enter the index of the Pokémon to remove(enter index, 0 to finish): ");
                        int indexToRemove;
                        do {
                            indexToRemove = scanner.nextInt();
                            if (indexToRemove > 0 && indexToRemove <= player.getTeam().size()) {
                                player.removePokemonFromTeam(indexToRemove - 1);
                                System.out.println("Pokémon removed from your team!");
                            } else if (indexToRemove != 0) {
                                System.out.println("Invalid index. Please choose again or enter 0 to finish.");
                            }
                        } while (indexToRemove != 0);
                        break;

                    case 4:
                        System.out.println("Clearing current Pokémon team...");
                        while (player.getTeam().size() > 0) {
                            player.removePokemonFromTeam(0);
                        }
                        System.out.println("Current team cleared!");
                    
                        System.out.println("Creating a random Pokémon team for the player...");
                        while (player.getTeam().size() < 3) {
                            Random random = new Random();
                            int randomIndex = random.nextInt(availablePokemons.size());
                            Pokemon randomPokemon = availablePokemons.get(randomIndex);
                            player.addPokemonToTeam(randomPokemon);
                        }
                        System.out.println("Random Pokémon team created!");
                        break;
                    
                    case 5:
                        //before combat, create IA Pokemon team
                        IA.getTeam().clear();
                        while (IA.getTeam().size() < 3) {
                            Random random = new Random();
                            int randomIndex = random.nextInt(availablePokemons.size());
                            Pokemon randomPokemon = availablePokemons.get(randomIndex);
                            IA.addPokemonToTeam(randomPokemon);
                        }
                        
                        System.out.println("Initiating combat...");
                        Battle battle = new Battle(player, IA, typeTable);
                        battle.start();
                        choice = 5;
                        break;
                    case 6:
                        System.out.println("Exiting the game. Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid choice. Please choose again.");
                        break;
                }
            } while (choice != 6);
        } else {
            System.out.println("Failed to read Pok\u00E9mon data.");
        }

        scanner.close();


    }
}
