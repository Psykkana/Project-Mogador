package game;

/*
 * GameStateManager.java
 */
public class GameStateManager {
    
    private GamePanel gp;

    // Define the possible states of your game
    public enum GameState {
        PLAYING,
        PAUSED,        
        DIALOGUE,
        TRANSITION,
        MENU
    }

    private GameState currentState;

    public GameStateManager() {
        // Game starts in playing mode
        this.currentState = GameState.PLAYING;
    }

    public void setState(GameState newState) {
        this.currentState = newState;
    
        // Auto-handle music based on state
        if (this.currentState != newState) {
            this.currentState = newState;

        if (newState == GameState.PAUSED) {
            gp.stopMusic();
        } else if (newState == GameState.PLAYING) {
            gp.playMusic(0);
        }
    }
    }

    public GameState getState() {
        return currentState;
    }
}