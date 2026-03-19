package game;

import java.awt.Graphics;
import java.util.HashMap;
import java.util.Map;

public class ObjectManager {
    private Map<String, Interactable> objects = new HashMap<>();

    public void addObject(int x, int y, Interactable obj) {
        obj.setX(x);
        obj.setY(y);
        objects.put(x + "," + y, obj);
    }

    public Interactable getObjectAt(int x, int y) {
        return objects.get(x + "," + y);
    }

    public int getObjectCount() {
        return objects.size();
    }

    public void clear() {
        objects.clear();
    }

    public void draw(Graphics g, GamePanel gp) {
        for (Interactable obj : objects.values()) {
            obj.draw(g, gp); // Pass the GamePanel reference here
        }
    }

    public void resetAllNPCDirections() {
        for (Interactable obj : objects.values()) { 
            if (obj instanceof NPC) {
                ((NPC) obj).resetDirection();
            }
        }
    }
}