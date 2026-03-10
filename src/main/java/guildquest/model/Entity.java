package guildquest.model;

public class Entity implements Locatable {

    private final String id;
    private Location location;

    public Entity(String id, Location location) {
        
        this.id = id;
        this.location = location;
    }

    @Override
    public Location getLocation() {
        return location;
    }

    @Override
    public void setLocation(Location location) {
        this.location = location;
    }

    public String getId() {
        return id;
    }
}
