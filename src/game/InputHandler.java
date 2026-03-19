package game;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class InputHandler extends KeyAdapter {
    private GamePanel gp;

    public InputHandler(GamePanel gp) {
        this.gp = gp;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        GameStateManager.GameState state = gp.getGSM().getState();

        // Block ALL input if transitioning
        if (state == GameStateManager.GameState.TRANSITION) {
            return; 
        }

        // Enter Debug World with Shift + F3
        if (code == KeyEvent.VK_F3) {
            if (e.isShiftDown()) {
                gp.warpToDebug();
                gp.resumeMusicForCurrentMap();
            } else {
                gp.toggleDebug();
            }
        }

        // DEBUG ENTITY TEST (PRESS P TO ADD A GHOST SIGN)
        if (code == KeyEvent.VK_P) {
            // Wrap the text in an array: new String[] {"..."}
            gp.getObjectManager().addObject(gp.getPlayer().getX(), gp.getPlayer().getY(), 
                new Signboard(new String[] {"This is a Debug Sign!", "This is page 2 of the Debug Sign."}));
            System.out.println("DEBUG: Spawned Debug Sign");
        }

        // TRANSITION
        if (state == GameStateManager.GameState.TRANSITION) {
            return; // Do nothing while the screen is fading
        } else if (code == KeyEvent.VK_ESCAPE) {   // GLOBAL ESCAPE TOGGLE
            if (state == GameStateManager.GameState.PLAYING) {
                System.out.println("DEBUG: Pausing Game");
                gp.getGSM().setState(GameStateManager.GameState.PAUSED);
                gp.stopMusic(); // stop the bgm
            } else if (state == GameStateManager.GameState.PAUSED) {
                System.out.println("DEBUG: Unpausing Game");
                gp.getGSM().setState(GameStateManager.GameState.PLAYING);
                gp.resumeMusicForCurrentMap();
            }
            return; 
        } if (state == GameStateManager.GameState.DIALOGUE) {   // DIALOGUE STATE CONTROLS
            if (state == GameStateManager.GameState.DIALOGUE) {
                if (code == KeyEvent.VK_ENTER || code == KeyEvent.VK_E || code == KeyEvent.VK_SPACE) {
                    DialogueManager dm = gp.getDialogueManager();
                    UIHandler ui = gp.getUIHandler();

                    // If text is still typing, finish it immediately
                    if (ui.getCharIndex() < dm.getCurrentPage().length()) {
                        ui.finishDialogueLocal();
                    } 
                    // If page is done, try to move to the next page
                    else {
                        dm.next(); // Manager flips the internal index
                        
                        if (dm.isActive()) {
                            // There is another page, so reset typewriter for the new text
                            ui.resetTypewriter();
                            gp.playSE(9); // Optional: play a "next" sound
                        } else {
                            // No more pages, return to the game
                            gp.getGSM().setState(GameStateManager.GameState.PLAYING);
                            // Reset where the NPC is facing
                            gp.getObjectManager().resetAllNPCDirections();
                        }
                    }
                }
                return; 
            }
            return; 
        } if (state == GameStateManager.GameState.PAUSED) {   // PAUSED (MENU) STATE CONTROLS
            UIHandler ui = gp.getUIHandler();

            // 8 - buttonclick.wav

            if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) {
                System.out.println("DEBUG: Playing SE index 9"); // See if this triggers in console
                ui.moveCursorUp();
                gp.playSE(9);   // buttonrollover.wav
            }
            if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) {
                System.out.println("DEBUG: Playing SE index 9"); // See if this triggers in console
                ui.moveCursorDown();
                gp.playSE(9);   // buttonrollover.wav
            }

            // --- VOLUME SLIDER LOGIC ---
            // Check if the cursor is currently on the "VOLUME" option (Index 2)
            if (ui.getSelectedOption() == 2) {
                if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) {
                    if (ui.volumeScale > 0) {
                        ui.volumeScale--;
                        
                        // KILL THE AUTO-FADE FIRST
                        gp.getMusic().stopFade(); 
                        
                        gp.getMusic().setVolume(ui.volumeScale / 10f);
                        gp.playSE(9);
                    }
                }
                if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) {
                    if (ui.volumeScale < 10) {
                        ui.volumeScale++;
                        
                        // KILL THE AUTO-FADE FIRST
                        gp.getMusic().stopFade(); 
                        
                        gp.getMusic().setVolume(ui.volumeScale / 10f);
                        gp.playSE(9);
                    }
                }
            }

            if (code == KeyEvent.VK_ENTER || code == KeyEvent.VK_E) {
                System.out.println("DEBUG: Playing SE index 8"); // See if this triggers in console
                executeMenuAction(ui.getSelectedOption());
                gp.playSE(8);   // buttonclickrelease.wav
            }
            return; // Important: Exit so we don't move the player while paused
        } else if (state == GameStateManager.GameState.PLAYING) {  // PLAYING STATE CONTROLS
            handlePlayerMovement(code);
            handleInteractions(code);
        }
    }

    // New Helper Method to handle Menu Logic
    private void executeMenuAction(int selection) {
        switch (selection) {
            case 0 -> {
                System.out.println("DEBUG: Resuming Game");
                gp.getGSM().setState(GameStateManager.GameState.PLAYING);
                gp.resumeMusicForCurrentMap();    // Resume the bgm
            }
            case 1 -> System.out.println("Inventory logic goes here!");
            case 2 -> System.out.println("DEBUG: Adjusting Volume: " + gp.getUIHandler().volumeScale * 10 + "%");
            case 3 -> {
                System.out.println("DEBUG: Exiting Game");
                gp.playSE(8);   // So u can still hear the beep when clicking quit
                System.exit(0);
            }
        }
    }

    private void handleInteractions(int code) {
        // INTERACT KEY
        if (code == KeyEvent.VK_E) {
            java.awt.Point f = gp.getPlayer().getFacingTile();
            // Ask the ObjectManager if there is an object EXACTLY at those coordinates
            Interactable obj = gp.getObjectManager().getObjectAt(f.x, f.y);
                
            if (obj != null) {
                obj.interact(gp); // This triggers the startDialogue() in GamePanel
            } else {
                System.out.println("Nothing to interact with at: " + f.x + "," + f.y);
            }
        }

    }

    private void handlePlayerMovement(int code) {
        if (code == KeyEvent.VK_W) {
            System.out.println("DEBUG: MOVE UP");
            gp.getPlayer().move(0, -1);
        }
        if (code == KeyEvent.VK_S) {
            System.out.println("DEBUG: MOVE DOWN");
            gp.getPlayer().move(0, 1);
        }
        if (code == KeyEvent.VK_A) {
            System.out.println("DEBUG: MOVE LEFT");
            gp.getPlayer().move(-1, 0);
        }
        if (code == KeyEvent.VK_D) {
            System.out.println("DEBUG: MOVE RIGHT");
            gp.getPlayer().move(1, 0);
        }

        if (code == KeyEvent.VK_I) { 
            System.out.println("DEBUG: LOOK UP");
            gp.getPlayer().setDirection(0, -1);
        }
        if (code == KeyEvent.VK_K) {
            System.out.println("DEBUG: LOOK DOWN");
            gp.getPlayer().setDirection(0, 1);
        }
        if (code == KeyEvent.VK_J) {
            System.out.println("DEBUG: LOOK LEFT");
            gp.getPlayer().setDirection(-1, 0);
        }
        if (code == KeyEvent.VK_L) {
            System.out.println("DEBUG: LOOK RIGHT");
            gp.getPlayer().setDirection(1, 0);
        }
    }
}