import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PokemonGame {
    public static void main(String[] args) {
        Player player = new Player("Ash");
        Scanner scanner = new Scanner(System.in);

        //PokemonDataReader dataReader = new PokemonDataReader();
        //List<Pokemon> availablePokemons = dataReader.readPokemonsFromJson("../data/Pokemons.json");


        List<Pokemon> availablePokemons = createAvailablePokemons();

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
    private static List<Pokemon> createAvailablePokemons() {
        List<Pokemon> pokemons = new ArrayList<>();

        // Charizard
        Pokemon charizard = new Pokemon("Charizard", "Fire", "Flying", 50, 153, 114, 107, 141, 115, 132);
        charizard.learnMove(new Move("Fire Blast", "Fire", "Special", 110, 5, 85), 0);
        charizard.learnMove(new Move("Flamethrower", "Fire", "Special", 90, 15, 100), 1);
        charizard.learnMove(new Move("Hurricane", "Flying", "Special", 110, 10, 70), 2);
        charizard.learnMove(new Move("Earthquake", "Ground", "Physical", 100, 10, 100), 3);
        pokemons.add(charizard);

        // Venusaur
        Pokemon venusaur = new Pokemon("Venusaur", "Grass", "Poison", 50, 155, 112, 113, 132, 132, 110);
        venusaur.learnMove(new Move("Sludge Bomb", "Poison", "Special", 90, 10, 100), 0);
        venusaur.learnMove(new Move("Power Whip", "Grass", "Physical", 120, 10, 85), 1);
        venusaur.learnMove(new Move("Synthesis", "Grass", "Status", 0, 5, 100), 2);
        venusaur.learnMove(new Move("Earthquake", "Ground", "Physical", 100, 10, 100), 3);
        pokemons.add(venusaur);

        // Blastoise
        Pokemon blastoise = new Pokemon("Blastoise", "Water", null, 50, 154, 113, 132, 115, 137, 107);
        blastoise.learnMove(new Move("Hydro Pump", "Water", "Special", 110, 5, 80), 0);
        blastoise.learnMove(new Move("Surf", "Water", "Special", 90, 15, 100), 1);
        blastoise.learnMove(new Move("Ice Punch", "Ice", "Physical", 75, 15, 100), 2);
        blastoise.learnMove(new Move("Headbutt", "Normal", "Physical", 70, 15, 100), 3);
        pokemons.add(blastoise);

        // Snorlax
        Pokemon snorlax = new Pokemon("Snorlax", "Normal", null, 50, 235, 143, 93, 93, 143, 55);
        snorlax.learnMove(new Move("Hyper Beam", "Normal", "Special", 150, 5, 90), 0);
        snorlax.learnMove(new Move("Headbutt", "Normal", "Physical", 70, 15, 100), 1);
        snorlax.learnMove(new Move("Soft Boiled", "Normal", "Status", 0, 10, 100), 2);
        snorlax.learnMove(new Move("Earthquake", "Ground", "Physical", 100, 10, 100), 3);
        pokemons.add(snorlax);

        return pokemons;
    }
    

}
