public class Move {
    private String name;
    private String type; //Ej: fire, water, etc
    private String category; // Physical, Special and Status
    private int power;
    private int remaining;
    private float accuracy;
    private int totalRemaining;
    private Passive passive;

    public Move(String name, String type, String category, int power, int remaining, float accuracy) {
        this.name = name;
        this.type = type;
        this.power = power;
        this.remaining = remaining;
        this.accuracy = accuracy;
        this.category = category;
        this.totalRemaining = this.remaining;
    }

    public Move(String name, String type, String category, int power, int remaining, float accuracy, Passive passive) {
        this(name, type, category, power, remaining, accuracy);
        this.passive = passive;
    }

    
    public Move (Move move) {
        this.name = move.getName();
        this.type = move.getType();
        this.power = move.getPower();
        this.remaining = move.getRemaining();
        this.accuracy = move.getAccuracy();
        this.category = move.getCategory();
        this.totalRemaining = move.getTotalRemaining();
        this.passive = move.getPassive();
    }

    public void printMoveDetails() {
        System.out.println("Move Name: " + this.name);
        System.out.println("Type: " + this.type);
        System.out.println("Category: " + this.category);
        System.out.println("Power: " + this.power);
        System.out.println("Accuracy: " + this.accuracy);
        System.out.println("Remaining: " + this.remaining + "/" + this.totalRemaining);

        if (this.passive != null) {
            System.out.println("Passive - Probability: " + this.passive.getProbability());
            if (this.passive.getEffect() != null) {
                System.out.println("Passive - Effect: " + this.passive.getEffect());
            }
            Move.Modifier modifier = this.passive.getModifier();
            if (modifier != null) {
                System.out.println("Passive - Modifier - User: " + modifier.getUser());
                if (modifier.getStat() != null) {
                    System.out.println("Passive - Modifier - Stat: " + modifier.getStat());
                }
                System.out.println("Passive - Modifier - Value: " + modifier.getValue());
            }
        }
        System.out.println("--------");
    }
    
    public String getName() {
        return this.name;
    }

    public String getType() {
        return this.type;
    }

    public String getCategory(){
        return this.category;
    }

    public int getPower() {
        return this.power;
    }

    public int getRemaining(){
        return this.remaining;
    }

    public float getAccuracy() {
        return this.accuracy;
    }

    public void updateRemaining() {
        this.remaining--;
    }

    public int getTotalRemaining() {
        return this.totalRemaining;
    }

    public String getInfo() {
        return this.name + "("+ this.remaining + "/" + this.totalRemaining + ") ";
    }

    public String getMoveStats() {
        return "Type: " + this.type + " Category: " + this.category + " Power: " + this.power + " Accuracy: " + this.accuracy;
    }


    public Passive getPassive() {
        return this.passive;
    }

    

    public static class Passive {
        private int probability;
        private String effect;
        private Modifier modifier;

        public Passive(int probability, String effect, Modifier modifier) {
            this.probability = probability;
            this.effect = effect;
            this.modifier = modifier;
        }

        // Constructor que no toma el efecto como parámetro
    public Passive(int probability, Modifier modifier) {
        this.probability = probability;
        this.modifier = modifier;
    }

        public int getProbability() {
            return probability;
        }

        public String getEffect() {
            return effect;
        }

        public Modifier getModifier() {
            return modifier;
        }
    }


    public static class Modifier {
        private String user;
        private String stat;
        private float value;

        public Modifier(String user, String stat, float value) {
            this.user = user;
            this.stat = stat;
            this.value = value;
        }

        public Modifier(String user) {
            this.user = user;
        }

        public String getUser() {
            return user;
        }

        public String getStat() {
            return stat;
        }

        public float getValue() {
            return value;
        }
    }

}


