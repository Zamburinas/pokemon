public class Move {
    private String name;
    private String type; //Ej: fire, water, etc
    private String category; // Physical, Special and Status
    private int power;
    private int remaining;
    private float accuracy;
    private int totalRemaining;

    public Move(String name, String type, String category, int power, int remaining, float accuracy) {
        this.name = name;
        this.type = type;
        this.power = power;
        this.remaining = remaining;
        this.accuracy = accuracy;
        this.category = category;
        this.totalRemaining = this.remaining;
    }

    public Move (Move move) {
        this.name = move.getName();
        this.type = move.getType();
        this.power = move.getPower();
        this.remaining = move.getRemaining();
        this.accuracy = move.getAccuracy();
        this.category = move.getCategory();
        this.totalRemaining = move.getTotalRemaining();
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

    public void addPassive() {

    }
}
