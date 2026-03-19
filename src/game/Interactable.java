package game;

import java.awt.Graphics;

public interface Interactable {
    // Existing method
    public abstract void interact(GamePanel gp);

    // ADD THIS: Now every Interactable MUST be able to draw itself
    void draw(Graphics g, GamePanel gp);
    
    // Recommended: Add these so the ObjectManager knows where to draw them
    void setX(int x);
    void setY(int y);
}