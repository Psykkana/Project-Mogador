import java.awt.*;
import javax.swing.*;

public class GamePanel extends JPanel implements Runnable {

    // Core Game Components
    private Thread gameThread;
    private Board board;
    private Player player;
    private ObjectManager objectManager;
    private GameStateManager gsm;
    private DialogueManager dialogueManager;
    private TransitionHandler transitionHandler = new TransitionHandler(this);
    private InputHandler inputHandler;
    private DebugHandler debugHandler = new DebugHandler();
    private MapData currentMapData;
    private UIHandler uiHandler;
    private Sound music = new Sound();
    private Sound se = new Sound();

    // Settings
    private final int FPS = 60; // The fps count
    private boolean showDebug = false;

    // Debug details
    private int fps = 0;
    private int frameCount = 0;
    private long timer = System.currentTimeMillis();

    public GamePanel() {
        this.setPreferredSize(new Dimension(770, 770));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        
        // Focus settings
        this.setFocusable(true); 
        this.requestFocusInWindow();
        this.setFocusTraversalKeysEnabled(false); 

        AssetManager.loadAll();    
        dialogueManager = new DialogueManager();
        initGame(); // initGame handles the rest, don't duplicate logic here!
    }  

    private void initGame() {
        gsm = new GameStateManager();
        inputHandler = new InputHandler(this);
        this.addKeyListener(inputHandler);

        // Ensure board is created BEFORE player
        // player will try to access the board immediately.
        board = new Board(); 
        player = new Player(11, 11, this); 

        objectManager = new ObjectManager();
        uiHandler = new UIHandler(this);
        syncObjectsWithMap("World1");
        playMusic(0);
    }

    public void playMusic(int i) {
        music.setFile(i);
        music.play();
        music.loop();
        
        // Force a tiny update to the volume logic
        float volume = uiHandler.volumeScale / 10f;
        music.setVolume(volume);
        
        // Safety: If it's a very low volume, ensure it's not rounding to absolute zero
        if (uiHandler.volumeScale > 0 && volume < 0.1f) {
            music.setVolume(0.05f); 
        }
    }

    public void playSE(int i) {
        // This now calls the pre-loaded Map version
        se.playSE(i); 
    }
    public void stopMusic() {
        music.stopFade();
        music.stop();
    }

    public void syncObjectsWithMap(String mapName) {
        currentMapData = MapLoader.load("maps/WorldData.txt", mapName);
        
        if (currentMapData != null) {
            // Placement of Tiles
            board.setMapData(currentMapData.getTileMap());
            
            // Placement of Signboards
            objectManager.clear();  // Clear the map
            for (MapData.SignData s : currentMapData.getSigns()) {
                objectManager.addObject(s.x, s.y, new Signboard(s.text));   // add signboards
            }

            // Load NPCs
            // Remove MapData. from the type here
            for (NPCData n : currentMapData.getNPCs()) { 
                NPC newLiveNpc;

                // Use n.isDirectional (ensure casing matches your NPCData file)
                if (n.isDirectional) {
                    // Calculation based on Down sprite as base ID
                    newLiveNpc = new NPC(n.name, n.dialogue, n.spriteID, n.spriteID - 1, n.spriteID + 1, n.spriteID + 2);
                } else {
                    newLiveNpc = new NPC(n.name, n.dialogue, n.spriteID);
                }

                newLiveNpc.setX(n.x);
                newLiveNpc.setY(n.y);

                objectManager.addObject(n.x, n.y, newLiveNpc);
            }
            resumeMusicForCurrentMap();
        }
    }

    // GETTERS for InputHandler to use
    public GameStateManager getGSM() { 
        return gsm; 
    }

    public Player getPlayer() { 
        return player; 
    }

    public ObjectManager getObjectManager() { 
        return objectManager; 
    }

    public UIHandler getUIHandler() { 
        return uiHandler; 
    }

    public Board getBoard() { 
        return board; 
    }

    // Getter for Music
    public Sound getMusic() {
        return music;
    }

    // Add a getter for the SE instance
    public Sound getSE() {
        return se;
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void setBoard(MapData newData) {
        // Changed this from currentMap to currentMapData
        this.currentMapData = newData; 
    }

    public void updateMusicForMap(String mapName) {        
        if (currentMapData == null) return;

        int targetTrack = currentMapData.getMusicIndex();
        
        // Only trigger a fade if the song is actually changing!
        if (music.getCurrentTrackIndex() != targetTrack) {
            music.fadeOut(); // Fade out the old one
            music.fadeIn(targetTrack); // Fade in the new one
        } else {
            // If it's the same song, just make sure it's playing at the right volume
            music.setVolume(uiHandler.volumeScale / 10f);
        }
    }

    public void resumeMusicForCurrentMap() {
        if (music == null || currentMapData == null) return;

        // Pull the latest slider value from UIHandler
        float currentVol = uiHandler.volumeScale / 10f;
        
        // Apply it to the music object before playing/looping
        music.setVolume(currentVol);

        int mapMusicIndex = currentMapData.getMusicIndex();

        // Only restart if the track is actually different
        if (music.getCurrentTrackIndex() != mapMusicIndex) {
            music.stop();
            playMusic(mapMusicIndex);
        } else if (!music.isPlaying()) {
            music.play();
        }
        
        // Final safety check: set volume one more time
        music.setVolume(currentVol);
    }

    public int getFPS() {
        return fps;
    }

    @Override
    public void run() {
        double drawInterval = 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime(); // This is for frame timing

        while (gameThread != null) {
            long currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();

                // DEBUG FPS COUNTER
                frameCount++;
                
                // Use the 'timer' variable here instead of 'lastTime'
                if (System.currentTimeMillis() - timer >= 1000) {
                    fps = frameCount;
                    frameCount = 0;
                    timer = System.currentTimeMillis(); // Reset the 1-second timer
                }

                delta--;
            }
        }
    }

    public void update() {
        // DIALOGUE
        if (gsm.getState() == GameStateManager.GameState.DIALOGUE) {
            uiHandler.updateDialogue();
            return; // take note
        }

        // TRANSITION (Handle fade here)
        if (gsm.getState() == GameStateManager.GameState.TRANSITION) {
            transitionHandler.update();
            return; // take note
        }

        /*
            the return inside stops the game from checking doors 
            or moving the player while talking.
            However it also means if we ever want something to happen 
            in the background while a dialogue box is open 
            the return statements will block it.

            if we want to address this, use if (state == PLAYING) instead of return
            to bail out early
        */

        // PLAYING
        if (gsm.getState() == GameStateManager.GameState.PLAYING) {
            player.update();
            
            // Door check
            MapTrigger trigger = currentMapData.getTriggerAt(player.getX(), player.getY());
            
            if (trigger != null) {
                // Extract the details from the trigger object
                String destination = trigger.getTargetMap();
                int spawnX = trigger.getSpawnPoint().x;
                int spawnY = trigger.getSpawnPoint().y;

                System.out.println("DEBUG: Stepped on trigger! Destination: " + destination + " at " + spawnX + "," + spawnY);
                
                playSE(10); // Your door sound
                gsm.setState(GameStateManager.GameState.TRANSITION);
                
                // Pass the REAL coordinates to the transition handler
                transitionHandler.start(destination, spawnX, spawnY);
            }
        }
    }

    public void startDialogue(String text, String name) {
        // BUG FIX: If we are already in DIALOGUE state, do NOT reset the dialogue!
        if (gsm.getState() == GameStateManager.GameState.DIALOGUE) {
            return;
        }

        player.stopMovement();

        // Create a temporary array with 1 line so it matches the new UIHandler signature
        String[] lines = { text };
        dialogueManager.startDialogue(lines, name);
        uiHandler.resetTypewriter();
        gsm.setState(GameStateManager.GameState.DIALOGUE);
    }

    public DialogueManager getDialogueManager() {
        return dialogueManager;
    }

/*
 *  DEBUG FUNCTIONS
*/

    public void toggleDebug() {
        this.showDebug = !this.showDebug;
    }

    // "Getter" method that the paintComponent is looking for
    public boolean isDebugEnabled() {
        return showDebug;
    }

    public void warpToDebug() {
        // Lock state immediately
        gsm.setState(GameStateManager.GameState.TRANSITION);
        playSE(7); 
        
        transitionHandler.start("DebugWorld", 11, 11);
        
        resumeMusicForCurrentMap();
        System.out.println("DEBUG CHECK: Transitioning to DebugWorld");
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;      

        // Draw the base floor layer
        board.draw(g);

        // Draw objects and player
        objectManager.draw(g, this);
        player.draw(g);

        // Draw the roof layer
        board.drawFringe(g);

        // This replaces the old drawUI(g)
        uiHandler.draw(g2);

        if (isDebugEnabled()) {
            debugHandler.draw(g, this, currentMapData);
        }

        // Renders the fade to black
        transitionHandler.draw(g2);

    }
}
