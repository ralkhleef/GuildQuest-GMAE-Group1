package gmae.core;

public interface MiniAdventure {

    String id();

    String name();

    void start();

    void handleInput(int playerIndex, String input);

    String getStatus();

    boolean isOver();

    String getResult();

    void reset();

    default void setPlayers(PlayerProfile p1, PlayerProfile p2) { }
}