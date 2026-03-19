package game;

import java.awt.Point;

public class MapTrigger {
    private Point location;      // The tile coordinate of the door
    private String targetMap;    // The map to load
    private Point spawnPoint;    // The landing spot

    public MapTrigger(int x, int y, String target, Point spawn) {
        this.location = new Point(x, y);
        this.targetMap = target;
        this.spawnPoint = spawn;
    }

    // Getters
    public Point getLocation() { return location; }
    public String getTargetMap() { return targetMap; }
    public Point getSpawnPoint() { return spawnPoint; }
}