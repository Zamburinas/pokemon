public class Pokemon {
    private String name;
    private String primaryType;
    private String secondaryType;
    private Stats stats;
    private Move[] moves;
    private State state;

    public Pokemon(String name, String primaryType, int level, int healthPoints, int attack, int defense, int special, int specialDefense, int speed) {
        this.name = name;
        this.primaryType = primaryType;
        this.stats = new Stats(level, healthPoints, attack, defense, special, specialDefense, speed);
        this.moves = new Move[4];
        this.state = new State("normal");
    }

    public Pokemon(String name, String primaryType, String secondaryType, int level, int healthPoints, int attack, int defense, int special, int specialDefense, int speed) {
        this.name = name;
        this.primaryType = primaryType;
        this.secondaryType = secondaryType;
        this.stats = new Stats(level, healthPoints, attack, defense, special, specialDefense, speed);
        this.moves = new Move[4];
    }

    public Stats getStats() {
        return stats;
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
        this.moves[index].newRemaining();
        return this.moves[index];
    }

    public String getName() {
        return name;
    }

    public State getState() {
        return this.state;
    }

    public void newState(String state) {
        this.state.changeState(state);
    }

    public String getPrimaryType() {
        return primaryType;
    }

    public String getSecondaryType() {
        return secondaryType;
    }
}
