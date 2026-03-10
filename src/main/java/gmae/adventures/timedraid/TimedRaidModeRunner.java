package gmae.adventures.timedraid;

public class TimedRaidModeRunner {

    public static void main(String[] args) {
        TimedRaidAdventure turnBased = new TimedRaidAdventure(5, 10, new int[]{2, 2, 2}, TimedRaidMode.TURN_BASED);
        TimedRaidAdventure realTime = new TimedRaidAdventure(5, 10, new int[]{2, 2, 2}, TimedRaidMode.REAL_TIME);

        turnBased.start();
        System.out.println("=== TURN-BASED MODE DEMO ===");
        System.out.println(turnBased.getStatus());

        turnBased.handleInput(1, "right");
        turnBased.handleInput(2, "right");
        turnBased.handleInput(1, "down");
        turnBased.handleInput(2, "up");
        turnBased.handleInput(1, "complete");
        turnBased.handleInput(2, "complete");
        turnBased.handleInput(1, "complete");
        turnBased.handleInput(2, "complete");

        turnBased.handleInput(1, "pass");
        turnBased.handleInput(2, "right");
        turnBased.handleInput(1, "pass");
        turnBased.handleInput(2, "down");
        turnBased.handleInput(1, "pass");
        turnBased.handleInput(2, "down");
        turnBased.handleInput(1, "pass");
        turnBased.handleInput(2, "complete");
        turnBased.handleInput(1, "pass");
        turnBased.handleInput(2, "complete");

        System.out.println(turnBased.getStatus());
        System.out.println(turnBased.getResult());
        System.out.println();

        realTime.start();
        System.out.println("=== REAL-TIME MODE DEMO ===");
        System.out.println(realTime.getStatus());

        realTime.handleInput(1, "right");
        realTime.handleInput(1, "down");
        realTime.handleInput(1, "complete");
        realTime.handleInput(1, "complete");

        realTime.handleInput(2, "right");
        realTime.handleInput(2, "up");
        realTime.handleInput(2, "complete");
        realTime.handleInput(2, "complete");

        realTime.handleInput(2, "right");
        realTime.handleInput(2, "down");
        realTime.handleInput(2, "down");
        realTime.handleInput(2, "complete");
        realTime.handleInput(2, "complete");

        System.out.println(realTime.getStatus());
        System.out.println(realTime.getResult());
    }
}