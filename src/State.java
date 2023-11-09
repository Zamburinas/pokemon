public class State {
    private int remainingTurns; //-1 inifite or maybe math.infinite
    private String name;

    State(String name){
        // this.remainingTurns = random / perma
    }

    public void changeState(String newState){
        // this.remainingTurns = //whatever
        this.name = newState;
    }
}
