import java.awt.*;

public class TransitionHandler {
    private GamePanel gp;
    private float alpha = 0f;
    private boolean active = false;
    private boolean fadingOut = true; // True = fading to black, False = fading back in
    
    // Storage for destination data
    private String targetMap;
    private int targetX, targetY;
    private float speed = 0.04f;

    public TransitionHandler(GamePanel gp) {
        this.gp = gp;
    }

    public void start(String mapName, int x, int y) {
        this.targetMap = mapName;
        this.targetX = x;
        this.targetY = y;
        this.active = true;
        this.fadingOut = true;
        this.alpha = 0f;
        
        // Freeze the game
        gp.getGSM().setState(GameStateManager.GameState.TRANSITION);
    }

    public void update() {
        if (!active) return;

        if (fadingOut) {
            alpha += speed;
            if (alpha >= 1f) {
                alpha = 1f;
                performSwap();
                fadingOut = false;
            }
        } else {
            alpha -= speed;
            if (alpha <= 0f) {
                alpha = 0f;
                active = false;
                // SET BACK TO PLAYING HERE
                gp.getGSM().setState(GameStateManager.GameState.PLAYING);
            }
        }
    }

    private void performSwap() {
        // Load the data
        MapData nextMap = MapLoader.load("maps/WorldData.txt", targetMap);
        
        //  Pass it to GamePanel
        if (nextMap != null) {
            gp.setBoard(nextMap); 
            
            gp.syncObjectsWithMap(targetMap);

            // Move the player (using their tiles-to-pixels math)
            // Assuming your tiles are 32x32 or 48x48
            gp.getPlayer().setPosition(new Point(targetX, targetY));

            // Get the current volume from the UI slider first
            float sliderVolume = gp.getUIHandler().volumeScale / 10f;
            
            // Ensure the Sound class knows this is the ceiling
            gp.getMusic().setVolume(sliderVolume); 

            // Now trigger the fade-in safely
            gp.updateMusicForMap(targetMap);
        }
    }

    public void draw(Graphics2D g2) {
        if (!active) return;

        g2.setColor(new Color(0, 0, 0, alpha));
        g2.fillRect(0, 0, gp.getWidth(), gp.getHeight());
    }

    public boolean isActive() { return active; }
}