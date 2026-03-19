package game;

import java.awt.Graphics;

public class Signboard implements Interactable {
    private String[] text; // Change from String to String[]
    private int x, y;

    public Signboard(String[] text) {
        this.text = text;
    }

    // This fulfills the first half of the requirement
    @Override
    public void setX(int x) { 
        this.x = x; 
    }

    // This fulfills the second half (the missing piece!)
    @Override
    public void setY(int y) { 
        this.y = y; 
    }

    @Override
    public void draw(Graphics g, GamePanel gp) {
        // Draw the sign sprite (usually ID 11)
        g.drawImage(TextureLoader.get(11), x * 35, y * 35, null);
        
        // Debug mode name tag
        if (gp.isDebugEnabled()) {
            g.setColor(java.awt.Color.WHITE);
            g.drawString("SIGN", x * 35, (y * 35) - 5);
        }
    }

    @Override
    public void interact(GamePanel gp) {
        // Since we updated UIHandler, we pass the array and a title
        gp.getDialogueManager().startDialogue(this.text, "Sign");
        gp.getUIHandler().resetTypewriter();
        gp.getGSM().setState(GameStateManager.GameState.DIALOGUE);
    }
}