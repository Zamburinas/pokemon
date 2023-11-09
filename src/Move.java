public class Move {
    private String name;
    private String type; //Ej: fire, water, etc
    private String category; // Physical, Special and Status
    private int power;
    private int remaining;
    private float precision;

    public Move(String name, String type, String category, int power, int remaining, float precision) {
        this.name = name;
        this.type = type;
        this.power = power;
        this.remaining = remaining;
        this.precision = precision;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getCategory(){
        return this.category;
    }

    public int getPower() {
        return power;
    }

    public float getRemaining(){
        return this.remaining;
    }

    public float getPrecision() {
        return this.precision;
    }

    public void newRemaining() {
        this.remaining--;
    }
}
