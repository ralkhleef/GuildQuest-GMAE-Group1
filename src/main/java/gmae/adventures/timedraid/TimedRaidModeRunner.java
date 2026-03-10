package gmae.adventures.timedraid;

public class TimedRaidModeRunner {

    public static void main(String[] args) {
        TimedRaidAdventure turnBased = new TimedRaidAdventure(5, 10, new int[]{2, 2, 2}, TimedRaidMode.TURN_BASED);
        TimedRaidAdventure realTime = new TimedRaidAdventure(5, 10, new int[]{2, 2, 2}, TimedRaidMode.REAL_TIME);

        turnBased.start();
        System.out.println("=== TURN-BASED ===");
        System.out.println(turnBased.getStatus());

        turnBased.handleInput(1, "r");
        turnBased.handleInput(2, "r");
        turnBased.handleInput(1, "d");
        turnBased.handleInput(2, "u");
        turnBased.handleInput(1, "w");
        turnBased.handleInput(2, "w");
        turnBased.handleInput(1, "w");
        turnBased.handleInput(2, "w");

        turnBased.handleInput(1, "p");
        turnBased.handleInput(2, "r");
        turnBased.handleInput(1, "p");
        turnBased.handleInput(2, "d");
        turnBased.handleInput(1, "p");
        turnBased.handleInput(2, "d");
        turnBased.handleInput(1, "p");
        turnBased.handleInput(2, "w");
        turnBased.handleInput(1, "p");
        turnBased.handleInput(2, "w");

        System.out.println(turnBased.getStatus());
        System.out.println(turnBased.getResult());
        System.out.println();

        realTime.start();
        System.out.println("=== REAL-TIME ===");
        System.out.println(realTime.getStatus());

        realTime.handleInput(1, "r");
        realTime.handleInput(1, "d");
        realTime.handleInput(1, "w");
        realTime.handleInput(1, "w");

        realTime.handleInput(2, "r");
        realTime.handleInput(2, "u");
        realTime.handleInput(2, "w");
        realTime.handleInput(2, "w");

        realTime.handleInput(2, "r");
        realTime.handleInput(2, "d");
        realTime.handleInput(2, "d");
        realTime.handleInput(2, "w");
        realTime.handleInput(2, "w");

        System.out.println(realTime.getStatus());
        System.out.println(realTime.getResult());
    }
}