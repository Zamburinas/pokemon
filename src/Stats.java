public class Stats {
    private int level;
    private int healthPoints;
    private int attack;
    private int defense;
    private int specialAttack;
    private int specialDefense;
    private int speed;
   

    public Stats(int level, int healthPoints, int attack, int defense, int specialAttack, int specialDefense, int speed) {
        this.level = level;
        this.healthPoints = healthPoints;
        this.attack = attack;
        this.specialDefense = specialDefense;
        this.specialAttack = specialAttack;
        this.defense = defense;
        this.speed = speed;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getHealthPoints() {
        return healthPoints;
    }

    public void setHealthPoints(int healthPoints) {
        this.healthPoints = healthPoints;
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public int getDefense() {
        return defense;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    public int getSpecialAttack() {
        return specialAttack;
    }

    public void setSpecialAttack(int specialAttack) {
        this.specialAttack = specialAttack;
    }

    public int getSpecialDefense() {
        return specialDefense;
    }

    public void setSpecialDefense(int specialDefense) {
        this.specialDefense = specialDefense;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}
