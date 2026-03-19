package game;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class NPC implements Interactable {
    private String name;
    private String[] dialogue;
    private int x, y;
    private int currentSpriteID;
    private int defaultSpriteID;

    // This map allows each NPC to have its own unique set of direction IDs
    private Map<String, Integer> spriteMap = new HashMap<>();

    // MAIN CONSTRUCTOR
    public NPC(String name, String[] dialogue, int downID, int upID, int leftID, int rightID) {
        this.name = name;
        this.dialogue = dialogue;
        
        spriteMap.put("UP", upID);
        spriteMap.put("DOWN", downID);
        spriteMap.put("LEFT", leftID);
        spriteMap.put("RIGHT", rightID);

        this.defaultSpriteID = downID;
        this.currentSpriteID = downID;
    }

    // STATIC CONSTRUCTOR (For signs/statues)
    public NPC(String name, String[] dialogue, int spriteID) {
        this(name, dialogue, spriteID, spriteID, spriteID, spriteID);
    }

    @Override
    public void interact(GamePanel gp) {
        facePlayer(gp);
        gp.getDialogueManager().startDialogue(this.dialogue, this.name);
        gp.getUIHandler().resetTypewriter();
        gp.getGSM().setState(GameStateManager.GameState.DIALOGUE);
    }

    private void facePlayer(GamePanel gp) {
        int px = gp.getPlayer().getX();
        int py = gp.getPlayer().getY();
        String dir = "DOWN";

        if (py < this.y)      dir = "UP";
        else if (py > this.y) dir = "DOWN";
        else if (px < this.x) dir = "LEFT";
        else if (px > this.x) dir = "RIGHT";

        this.currentSpriteID = spriteMap.getOrDefault(dir, defaultSpriteID);
    }

    public void resetDirection() {
        this.currentSpriteID = defaultSpriteID;
    }

    @Override
    public void draw(Graphics g, GamePanel gp) {
        g.drawImage(TextureLoader.get(currentSpriteID), x * 35, y * 35, null);

        if (gp.isDebugEnabled()) {
            g.setColor(Color.YELLOW); 
            g.setFont(new Font("Monospaced", Font.BOLD, 12));
            int stringWidth = g.getFontMetrics().stringWidth(name);
            g.drawString(name, (x * 35) + (35 / 2) - (stringWidth / 2), (y * 35) - 5); 
            g.setColor(new Color(255, 255, 0, 100)); 
            g.drawRect(x * 35, y * 35, 35, 35);
        }
    }

    @Override public void setX(int x) { this.x = x; }
    @Override public void setY(int y) { this.y = y; }
}