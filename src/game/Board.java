package game;

import java.awt.Graphics;
import java.awt.Point;
import java.util.HashMap;

public class Board {
    public static final int TILE_SIZE = 35;
    public static final int MAP_SIZE = 22;
    private int[][] map = new int[MAP_SIZE][MAP_SIZE]; // The grid
    private HashMap<Point, String> triggerMap = new HashMap<>();

    public Board() {
        // Start with a blank map or a default border
        generateDefaultBorder(); 
    }

    // Allow the MapLoader to inject the actual tile data
    public void setMapData(int[][] data) { 
        this.map = data; 
    }

    // REMEMBER TO KEEP TRACK OF THIS
    public String getTileName(int x, int y) {
        if (x < 0 || y < 0 || x >= map.length || y >= map[0].length) return "OUT OF BOUNDS";
        
        int id = map[y][x];

        // Names for the Debug Scanner (not used in the technical backend)
        return switch (id) {
            case 0 -> "Grass";
            case 1 -> "Wall";
            case 2 -> "Wood";
            case 3 -> "Log";
            case 4 -> "Wood Floor";
            case 8 -> "Sand";
            case 9 -> "Water";
            case 10 -> "Door Tile";
            case 11 -> "Sign Tile";
            case 20 -> "Tree";
            case 21 -> "Cobblestone";
            case 22 -> "Wood Roof";
            case 23 -> "Wood Roof Left";
            case 24 -> "Wood Roof Right";
            case 25 -> "Tree Base";
            case 26 -> "Tree Middle";
            case 27 -> "Tree Leaves";
            case 28 -> "Metal Fence";
            case 90 -> "Debug Floor";
            case 99 -> "ERROR/MISSING";
            default -> "Unknown (" + id + ")";
        };
    }

    // Purely to be used by AssetManager to be then called by Project Kumano
    public static String getTileNameByID(int id) {
        return switch (id) {
            case 0 -> "Grass";
            case 1 -> "Wall";
            case 2 -> "Wood";
            case 3 -> "Log";
            case 4 -> "Wood Floor";
            case 8 -> "Sand";
            case 9 -> "Water";
            case 10 -> "Door";
            case 11 -> "Sign";
            case 20 -> "Tree";
            case 21 -> "Cobble";
            case 22 -> "Roof";
            case 23 -> "Roof (L)";
            case 24 -> "Roof (R)";
            case 25 -> "Tree (B)";
            case 26 -> "Tree (M)";
            case 27 -> "Tree (T)";
            case 28 -> "Metal Fence";
            case 90 -> "Debug";
            case 99 -> "MISSING";
            default -> "ID: " + id;
        };
    }

    private void generateDefaultBorder() {
        for (int row = 0; row < 22; row++) {
            for (int col = 0; col < 22; col++) {
                if (row == 0 || row == 21 || col == 0 || col == 21) map[row][col] = 1;
                else map[row][col] = 0;
            }
        }
    }

    public boolean isWalkable(int x, int y, GamePanel gp) {
        // Map boundary check
        if (x < 0 || x >= 22 || y < 0 || y >= 22) {
            return false;
        }

        int id = map[y][x];

        // Tile-based collisions
        // If the tile is a wall (e.g., ID 1) or a tree (e.g., ID 5), return false
        if (id == 1 || 
            id == 2 || 
            id == 3 || 
            id == 9 || 
            id == 11 ||  
            id == 20 ||
            id == 25 ||
            id == 28
            ) { 
            return false; 
        }

        // Object-based collision (NPCs and Signs)
        // If there is an interactable object at these coordinates, it is NOT walkable.
        if (gp.getObjectManager().getObjectAt(x, y) != null) {
            return false;
        }

        return true;
    }

    // Basically checks if the tile is designated as a trigger
    public String getTriggerAt(int x, int y) {
        // Check against actual map dimensions instead of hardcoded 22
        if (x < 0 || x >= map[0].length || y < 0 || y >= map.length) return null;

        // Look up in a local dictionary of triggers for THIS map
        Point p = new Point(x, y);
        return triggerMap.getOrDefault(p, null);
    }

    // object that the player is supposed to walk underneath.
    private boolean isFringeTile(int id) {
        return id == 22 || 
                id == 23 || 
                id == 24 ||
                id == 26 ||
                id == 27;
    }

    // sits on the ground but doesn't fill the whole 35x35 square.
    private boolean isTransparentTile(int id) {
        return id == 11 || 
                id == 20 || 
                id == 22 || 
                id == 23 || 
                id == 24 || 
                id == 25 ||
                id == 28;
    }

    public void draw(Graphics g) {
        for (int r = 0; r < 22; r++) {
            for (int c = 0; c < 22; c++) {
                int id = map[r][c];
                // Fringe Drawing
                if (isFringeTile(id)) {
                    g.drawImage(TextureLoader.get(0), c * 35, r * 35, null); // Just ground
                } else if (isTransparentTile(id)) { // Transparent drawing
                    g.drawImage(TextureLoader.get(0), c * 35, r * 35, null); // Ground...
                    g.drawImage(TextureLoader.get(id), c * 35, r * 35, null); // ...then object
                } else {    // Normal drawing
                    g.drawImage(TextureLoader.get(id), c * 35, r * 35, null); // Standard
                }
            }
        }
    }

    public void drawFringe(Graphics g) {
        for (int r = 0; r < MAP_SIZE; r++) {
            for (int c = 0; c < MAP_SIZE; c++) {
                int id = map[r][c];
                
                if (isFringeTile(id)) {
                    g.drawImage(TextureLoader.get(id), c * 35, r * 35, null);
                }
            }
        }        
    }
}