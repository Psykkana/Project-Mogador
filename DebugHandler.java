import java.awt.*;

public class DebugHandler {
    public void draw(Graphics g, GamePanel gp, MapData currentMapData) {
        g.setColor(Color.YELLOW);
        g.setFont(new Font("Monospaced", Font.PLAIN, 12));

        Point f = gp.getPlayer().getFacingTile();
        
        // Gather Data
        MapTrigger trigger = currentMapData.getTriggerAt(f.x, f.y);        Interactable obj = gp.getObjectManager().getObjectAt(f.x, f.y);
        boolean walkable = gp.getBoard().isWalkable(f.x, f.y, gp);
        String tileName = gp.getBoard().getTileName(f.x, f.y);
        long usedMemory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024;
        int totalEntities = gp.getObjectManager().getObjectCount() + 1;

        // Build Detail String
        java.util.StringJoiner details = new java.util.StringJoiner(" | ");
        details.add("Tile:" + tileName);
        if (trigger != null) details.add("Door to:" + trigger);
        if (obj != null) details.add(obj.getClass().getSimpleName());

        // Drawing
        renderHighlight(g, f, trigger, obj, walkable);
        
        // Drawing Layout
        int y = 20; // Starting Y position
        int spacing = 15; // Consistent line spacing

        // Section 1: Player & Position
        g.drawString("COORD: " + gp.getPlayer().getX() + "," + gp.getPlayer().getY(), 10, y);
        y += spacing;
        g.drawString("FACING: " + f.x + "," + f.y, 10, y);
        y += spacing;
        
        // Section 2: Scanning & World
        g.drawString("SCAN: " + (walkable ? "WALKABLE" : "SOLID"), 10, y);
        y += spacing;
        String objType = (obj != null) ? obj.getClass().getSimpleName() : "None";
        g.drawString("OBJ: " + objType + " | " + details.toString(), 10, y);
        y += spacing;
        
        // Section 3: Map Metadata
        g.drawString("MAP: " + currentMapData.getMapName(), 10, y);
        y += spacing;
        g.drawString("ENTITY COUNT: " + totalEntities, 10, y);
        y += spacing;
        
        // Section 4: Engine Performance and Audio
        g.setColor(Color.CYAN);
        int screenWidth = gp.getWidth();
        int rightX = screenWidth - 250; 
        int audioY = 20;
        int lineSpacing = 15;

        // Pull BGM info from the music instance
        String bgmInfo = gp.getMusic().getDebugStatus();
        String[] bgmLines = bgmInfo.split("\n");

        int currentLine = 0;
        for (String line : bgmLines) {
            // We only want to print the BGM part of the string from this instance
            if (!line.contains("ACTIVE SE") && !line.equals("None") && !line.contains("SE ID")) {
                g.drawString(line, rightX, audioY + (currentLine * lineSpacing));
                currentLine++;
            }
        }

        // Pull SE info from the se instance
        String seInfo = gp.getSE().getDebugStatus();
        String[] seLines = seInfo.split("\n");

        for (String line : seLines) {
            // We only want to print the SE part of the string from this instance
            if (line.contains("ACTIVE SE") || line.contains("SE ID") || line.equals("None")) {
                // Skip the "None" that belongs to the BGM part
                if (line.equals("None") && currentLine < 3) continue; 
                
                g.drawString(line, rightX, audioY + (currentLine * lineSpacing));
                currentLine++;
            }
        }
    }

    private void renderHighlight(Graphics g, Point f, MapTrigger trigger, Interactable obj, boolean walkable) {
        if (trigger != null) g.setColor(new Color(0, 200, 255, 100)); // Blue for doors
        else if (obj != null) g.setColor(new Color(0, 255, 0, 100));   // Green for objects
        else if (!walkable) g.setColor(new Color(255, 0, 0, 100));    // Red for walls
        else g.setColor(new Color(255, 255, 255, 50));                // White for floor

        g.fillRect(f.x * 35, f.y * 35, 35, 35);
        g.setColor(Color.YELLOW);
        g.drawRect(f.x * 35, f.y * 35, 35, 35);
    }
}
