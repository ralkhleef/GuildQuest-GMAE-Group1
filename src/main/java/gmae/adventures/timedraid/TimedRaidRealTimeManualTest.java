package gmae.adventures.timedraid;

import java.util.Scanner;

public class TimedRaidRealTimeManualTest {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        TimedRaidAdventure adv = new TimedRaidAdventure(5, 10, new int[]{2, 2, 2}, TimedRaidMode.REAL_TIME);

        adv.start();

        Thread timerWatcher = new Thread(() -> {
            while (true) {
                if (adv.isOver()) {
                    System.out.println();
                    System.out.println(adv.getResult());
                    System.exit(0);
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    return;
                }
            }
        });
        timerWatcher.setDaemon(true);
        timerWatcher.start();

        int player = 1;

        while (!adv.isOver()) {
            System.out.println();
            System.out.println(adv.getStatus());
            System.out.print("Player " + player + " input: ");
            String input = sc.nextLine();
            adv.handleInput(player, input);
            player = (player == 1) ? 2 : 1;
        }

        System.out.println();
        System.out.println(adv.getResult());
    }
}