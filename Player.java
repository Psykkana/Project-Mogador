import java.awt.Graphics;
import java.awt.Point;

/*
 * PROJECT Mogador
 * Player.java
 */
public class Player {
    // Grid Coordinates
    private int x, y;

    // Store reference to GamePanel
    private GamePanel gp;
    
    // Directional vectors (used for interaction and animation)
    private int dirX = 0;
    private int dirY = 1; // Default facing down

    private int moveCounter = 0;
    private final int MOVE_DELAY = 6; // Frames to wait between moves

    public Player(int x, int y, GamePanel gp) {
        this.gp = gp;
        this.x = x;
        this.y = y;
    }

    // PURELY FOR MOVEMENT (WALKING)
    public void move(int dx, int dy) {
        if (moveCounter > 0) return;

        this.dirX = dx;
        this.dirY = dy;

        int targetX = this.x + dx;
        int targetY = this.y + dy;

        // Use the stored 'gp' to get the board
        if (gp.getBoard().isWalkable(targetX, targetY, gp)) {
            this.x = targetX;
            this.y = targetY;
            moveCounter = MOVE_DELAY;
        }
    }


    // Call in GamePanel.update()
    public void update() {
        if (moveCounter > 0) moveCounter--;
    }

    public void draw(Graphics g) {
        int spriteId;

        if (dirY == -1) {
            spriteId = 91; // Up
        } else if (dirY == 1) {
            spriteId = 92; // Down
        } else if (dirX == -1) {
            spriteId = 93; // Left
        } else if (dirX == 1) {
            spriteId = 94; // Right
        } else {
            spriteId = 92; // Default to Down if standing still
        }

        g.drawImage(TextureLoader.get(spriteId), x * 35, y * 35, null);
    }

    public void stopMovement() {
        this.moveCounter = 0;
    }

    // Getters and Setters
    public Point getPosition() {
        return new Point(x, y);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Point getFacingTile() {
        return new Point(x + dirX, y + dirY);
    }
    
    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    // Separate from move
    public void setPosition(Point p) {
        this.x = p.x;
        this.y = p.y;
    }

    public void setDirection(int dx, int dy) {
        this.dirX = dx;
        this.dirY = dy;
    }

}