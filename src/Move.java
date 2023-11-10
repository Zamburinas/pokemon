public class Move {
    private String name;
    private String type; //Ej: fire, water, etc
    private String category; // Physical, Special and Status
    private int power;
    private int remaining;
    private float accuracy;

    public Move(String name, String type, String category, int power, int remaining, float accuracy) {
        this.name = name;
        this.type = type;
        this.power = power;
        this.remaining = remaining;
        this.accuracy = accuracy;
        this.category = category;
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

    public float getRemaining(){
        return this.remaining;
    }

    public float getAccuracy() {
        return this.accuracy;
    }

    public void updateRemaining() {
        this.remaining--;
    }
}
