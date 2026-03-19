package game;

import java.awt.Point;
import java.util.*; // Required for the HashMap keys

public class MapData {
    private int[][] tileMap;
    private String mapName;
    private List<SignData> signs;
    private List<MapTrigger> triggers;
    private List<NPCData> npcs = new ArrayList<>();
    
    // The "Shortcut" map for fast lookups
    private Map<Point, MapTrigger> triggerMap = new HashMap<>();

    // v0.9 - integrating music into WorldData
    private int musicIndex = 0; // Default to 0

    public MapData(int[][] tileMap, List<MapTrigger> triggers, List<SignData> signs, List<NPCData> npcs) {
        this.tileMap = tileMap;
        this.triggers = triggers;
        this.signs = signs;
        this.npcs = npcs;

        finalizeMap(); // Use the logic below to fill the triggerMap
    }

    public MapData(String name) {
        this.mapName = name;
        this.signs = new ArrayList<>();
        this.triggers = new ArrayList<>();
        this.npcs = new ArrayList<>();
    }

    public void setMapName(String name) { 
        this.mapName = name; 
    }

    public String getMapName() {
        return mapName;
    }

    public int[][] getTileMap() { 
        return tileMap; 
    }

    public List<SignData> getSigns() { 
        return signs; 
    }

    public void setMusicIndex(int i) { 
        this.musicIndex = i; 
    }

    public int getMusicIndex() { 
        return musicIndex; 
    }
    
    public void finalizeMap() {
        triggerMap.clear();
        for (MapTrigger t : triggers) {
            // Use .getLocation() from your external MapTrigger class
            triggerMap.put(t.getLocation(), t);
        }
    }

    public MapTrigger getTriggerAt(int x, int y) {
        return triggerMap.get(new Point(x, y));
    }

    public static class SignData {
        public int x, y;
        public String[] text;

        public SignData(int x, int y, String[] text) {
            this.x = x; this.y = y; this.text = text;
        }
    }
    
    public List<NPCData> getNPCs() {
        return npcs;
    }

    // ADD THIS: A way for the MapLoader to add NPCs to the list
    public void addNPC(NPCData npc) {
        npcs.add(npc);
    }
}