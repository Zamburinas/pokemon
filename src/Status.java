public class Status {
    private int remainingTurns; //-1 inifite or maybe math.infinite (only useful for rest)
    private String name;
    private float probCure; //Freeze and sleep
    private String affectedStat; 

    public Status(int remainingTurns, String name, float probCure, String affectedStat) {
        this.remainingTurns = -1;
        this.name = name;
        this.probCure = probCure;
        this.affectedStat = affectedStat;
    }

    public int getRemainingTurns() {
        return this.remainingTurns;
    }

    public void setRemainingTurns(int remainingTurns) {
        this.remainingTurns = remainingTurns;
    }

    public int getName() {
        return this.name;
    }

    public void setName(int name) {
        this.name = name;
    }

    public int getProbCure() {
        return this.probCure;
    }

    public void setProbCure(int probCure) {
        this.probCure = probCure;
    }

    public int getAffectedStat() {
        return this.affectedStat;
    }

    public void setAffectedStat(int affectedStat) {
        this.affectedStat = affectedStat;
    }

    // public void changeStatus(String newState,int remainingTurns,float probCure,String affectedStat){
    //     // this.remainingTurns = //whatever
    //     this.name = newState;
    // }
}
