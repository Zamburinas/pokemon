package com.psic.aipokemon.core.src;

public class Status {
    private int remainingTurns; //-1 inifite or maybe math.infinite (only useful for rest)
    private String name;
    private float probability; //Freeze and sleep
    private String affectedStat; 

    public Status(String name, int remainingTurns, float probability, String affectedStat) {
        this.remainingTurns = remainingTurns;
        this.name = name;
        this.probability = probability;
        this.affectedStat = affectedStat;
    }

    public Status (Status status) {
        this.remainingTurns = status.getRemainingTurns();
        this.name = status.getName();
        this.probability = status.getProbability();
        this.affectedStat = status.getAffectedStat();
    }
    
    public int getRemainingTurns() {
        return this.remainingTurns;
    }

    public void setRemainingTurns(int remainingTurns) {
        this.remainingTurns = remainingTurns;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getProbability() {
        return this.probability;
    }

    public void setProbability(float probability) {
        this.probability = probability;
    }

    public String getAffectedStat() {
        return this.affectedStat;
    }

    public void setAffectedStat(String affectedStat) {
        this.affectedStat = affectedStat;
    }
}
