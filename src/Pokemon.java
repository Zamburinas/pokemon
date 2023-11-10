public class Pokemon {
    private String name;
    private String primaryType;
    private String secondaryType;
    private Stats stats;
    private Move[] moves;
    private Status status;

    public Pokemon(String name, String primaryType, int level, int healthPoints, int attack, int defense, int special, int specialDefense, int speed) {
        this.name = name;
        this.primaryType = primaryType;
        this.stats = new Stats(level, healthPoints, attack, defense, special, specialDefense, speed);
        this.moves = new Move[4];
        this.status = new Status("normal", -1, 100, null);
    }

    public Pokemon(String name, String primaryType, String secondaryType, int level, int healthPoints, int attack, int defense, int special, int specialDefense, int speed) {
        this.name = name;
        this.primaryType = primaryType;
        this.secondaryType = secondaryType;
        this.stats = new Stats(level, healthPoints, attack, defense, special, specialDefense, speed);
        this.moves = new Move[4];
    }

    public Stats getStats() {
        return this.stats;
    }

    public void setStats(Stats stats) {
        this.stats = stats;
    }

    public Move[] getMoves(){
        return this.moves;
    }

    public void learnMove(Move move, int index) {
        this.moves[index] = move;
    }

    public Move useMove(int index){
        this.moves[index].updateRemaining();
        return this.moves[index];
    }

    public String getName() {
        return this.name;
    }

    public Status getStatus() {
        return this.status;
    }

    public void changeStatus(String status, int remainingTurns, float probability, String affectedStat) {
        this.status = new Status(status, remainingTurns, probability, affectedStat);
    }

    public String getPrimaryType() {
        return this.primaryType;
    }

    public String getSecondaryType() {
        return this.secondaryType;
    }
}
