package com.psic.aipokemon.core.src;

public class Stats {
    private int level;
    private int healthPoints;
    private int attack;
    private int defense;
    private int specialAttack;
    private int specialDefense;
    private int speed;
    private int maxHealthPoints;

    private int addedHP;

    private int initHP;

    public Stats(int level, int healthPoints, int attack, int defense, int specialAttack, int specialDefense, int speed) {
        this.level = level;
        this.healthPoints = healthPoints;
        this.maxHealthPoints = healthPoints;
        this.attack = attack;
        this.specialDefense = specialDefense;
        this.specialAttack = specialAttack;
        this.defense = defense;
        this.speed = speed;
        this.addedHP = 0;
        this.initHP = healthPoints;
    }

    public Stats (Stats stats) {
        this.level = stats.getLevel();
        this.healthPoints = stats.getHealthPoints();
        this.attack = stats.getAttack();
        this.specialDefense = stats.getSpecialDefense();
        this.specialAttack = stats.getSpecialAttack();
        this.defense = stats.getDefense();
        this.speed = stats.getSpeed();
        this.maxHealthPoints = stats.getMaxHealthPoints();
        this.addedHP = 0;
        this.initHP = stats.getMaxHealthPoints();
    }

    public int getLevel() {
        return this.level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getHealthPoints() {
        return this.healthPoints;
    }

    public void setHealthPoints(double healthPoints) {
        this.healthPoints = (int) healthPoints;
    }

    public int getAttack() {
        return this.attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public int getDefense() {
        return this.defense;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    public int getSpecialAttack() {
        return this.specialAttack;
    }

    public void setSpecialAttack(int specialAttack) {
        this.specialAttack = specialAttack;
    }

    public int getSpecialDefense() {
        return this.specialDefense;
    }

    public void setSpecialDefense(int specialDefense) {
        this.specialDefense = specialDefense;
    }

    public int getSpeed() {
        return this.speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public boolean decreaseHealtPoints(double damage) {
        this.healthPoints = (int) (this.healthPoints - damage);
        if (healthPoints <= 0) {
            return true;
        }
        return false;
    }

    public void increaseHealthPoints(double health) {
        this.healthPoints = (int) (this.healthPoints + health);
    }

    public void setAdddedHP(double health) {
        this.addedHP = (int) (health);
    }

    public int getMaxHealthPoints() {
        return this.maxHealthPoints;
    }

    public int getAddedHP() {
        return this.addedHP;
    }

    public void resetAddedHP() { this.addedHP = 0; }

    public void setInitHP(double health) {
        this.initHP = (int) (health);
    }
    public int getInitHP() {
        return this.initHP;
    }
}
