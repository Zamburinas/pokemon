package com.psic.aipokemon.core.src;

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
        this.status = new Status("normal", -1, 100, null);
    }

    public Pokemon(Pokemon pokemon){
        this.name = pokemon.getName();
        this.primaryType = pokemon.getPrimaryType();
        this.secondaryType = pokemon.getSecondaryType();
        this.stats = new Stats(pokemon.getStats());
        this.status = new Status(pokemon.getStatus());
        this.moves = new Move[4];
        Move [] copyMoves = pokemon.getMoves();
        for (int i = 0; i < 4; i++) {
            this.moves[i] = new Move(copyMoves[i]);
        }
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
        if (isDead == false && this.stats.getHealthPoints() <= 0) {
            this.die();
        }
        return this.isDead;
    }

    public boolean isMoveAvailable(int move) {
        if (move < 0 || move > moves.length)
            return false;
        return moves[move].getRemaining() > 0;
    }

    public boolean isEqualTo(Pokemon pokemon) {
        return pokemon != null && this.name.equals(pokemon.getName());
    }

    public int remainingHealth () {
        return this.stats.getHealthPoints();
    }

    public int getMaxHealthPoints() {
        return this.stats.getMaxHealthPoints();
    }

    public double addHealthPoints(double healthPoints) {
        double healthPointsAdded = 0;
        if(this.stats.getHealthPoints()+healthPoints>this.stats.getMaxHealthPoints()){
            healthPointsAdded = this.stats.getMaxHealthPoints()-this.stats.getHealthPoints();
            this.stats.setHealthPoints(this.stats.getMaxHealthPoints());
            
        }else if(this.stats.getHealthPoints()+healthPoints<0){
            healthPointsAdded = this.stats.getHealthPoints();
            this.stats.setHealthPoints(0);
            
        }else{
            healthPointsAdded = healthPoints;
            this.stats.setHealthPoints(this.stats.getHealthPoints()+healthPoints);
            
        }
        return healthPointsAdded;
    }
}
