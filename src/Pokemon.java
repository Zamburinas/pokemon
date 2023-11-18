public class Pokemon {
    private String name;
    private String primaryType;
    private String secondaryType;
    private Stats stats;
    private Move[] moves;
    private Status status;
    private boolean isDead = false;

    public Pokemon(String name, String primaryType, int level, int healthPoints, int attack, int defense, int special, int specialDefense, int speed) {
        this.name = name;
        this.primaryType = primaryType;
        this.secondaryType = null;
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

    public Pokemon() {
        this.isDead = true;
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

    public void addSecondaryType(String secondaryType) {
        this.secondaryType = secondaryType;
    }

    public int getLevel(){
        return this.stats.getLevel();
    }

    public boolean receiveDamage(double damage) {
        // return True if the pokemon dies
        return this.stats.decreaseHealtPoints(damage);
    }

    public boolean isType(String type) {
        return this.primaryType.equals(type) || (this.secondaryType != null && this.secondaryType.equals(type));
    }

    public int getAttack(String category) {
        if (category.toLowerCase().equals("physical")) {
            return this.stats.getAttack();
        } else if (category.toLowerCase().equals("special")) {
            return this.stats.getSpecialAttack();
        } else {
            return 0;
        }
    }

    public int getDefense(String category) {
        if (category.toLowerCase().equals("physical")) {
            return this.stats.getDefense();
        } else if (category.toLowerCase().equals("special")) {
            return this.stats.getSpecialDefense();
        } else {
            return 1;
        }
    }

    public int getSpeed() {
        return this.stats.getSpeed();
    }

    public void die(){
        isDead = true;
    }

    public boolean isDead(){
        return this.isDead;
    }

    public boolean isMoveAvailable(int move) {
        if (move < 0 || move > moves.length)
            return false;
        return moves[move].getRemaining() > 0;
    }

    public boolean isEqualTo(Pokemon pokemon) {
        return this.name.equals(pokemon.getName());
    }
}
