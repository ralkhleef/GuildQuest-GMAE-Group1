package guildquest.model;

import java.util.Objects;

public final class Location {

    private int x;
    private int y;

    public Location(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int x() { return x; }
    public int y() { return y; }

    public Location withX(int x) { return new Location(x, this.y); }
    public Location withY(int y) { return new Location(this.x, y); }

    public int distance(Location other) {
        return Math.abs(x - other.x) + Math.abs(y - other.y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}
