import java.awt.*;

public class UIHandler {
    private String[] menuOptions = {"RESUME", "INVENTORY", "VOLUME", "QUIT"};
    private int currentOption = 0;
    private GamePanel gp;
    
    // Typewriter variables moved here
    private int charIndex = 0;
    private int charCounter = 0;
    private int TYPE_SPEED = 2;

    // From 0 to 10
    public int volumeScale = 5;

    public UIHandler(GamePanel gp) {
        this.gp = gp;
    }

    public void draw(Graphics2D g2) {
        // 1. Check the game state via the GameStateManager
        GameStateManager.GameState state = gp.getGSM().getState();

        switch (state) {
            case PLAYING:
                // Draw regular HUD elements here (e.g., mini-map, health)
                break;

            case DIALOGUE:
                // 2. Call your existing dialogue box logic
                // Use currentDialogue[dialogueIndex] here
                if (gp.getDialogueManager().isActive()) {
                    drawDialogueBox(g2);
                }
                break;

            case PAUSED:
                // Draw "PAUSED" overlay
                drawPauseScreen(g2);
                break;
                
            case TRANSITION:
                // The TransitionHandler usually draws its own fade, 
                // but you can add UI elements here if needed.
                break;
        }
    }

    // INDEV BUILD v0.9 - Moved Dialogue logic to DialogueManager

    public void resetTypewriter() {
        this.charIndex = 0;
        this.charCounter = 0;
    }

    public void updateDialogue() {
        DialogueManager dm = gp.getDialogueManager();
        if (!dm.isActive()) return;
        
        // Get the current page text from the manager
        String fullText = dm.getCurrentPage(); 

        if (charIndex < fullText.length()) {
            charCounter++;
            if (charCounter >= TYPE_SPEED) {
                charIndex++;
                charCounter = 0;
                
                // Play sound every 2 characters to avoid "machine gun" noise
                // Only play if the current character isn't a space
                if (charIndex > 0 && charIndex <= fullText.length()) {
                    char charAt = fullText.charAt(charIndex - 1);
                    if (charIndex % 2 == 0 && charAt != ' ') {
                        gp.playSE(6); 
                    }
                }
            }
        }
    }

    public void finishDialogueLocal() {
        DialogueManager dm = gp.getDialogueManager();
        if (dm.isActive()) {
            this.charIndex = dm.getCurrentPage().length();
        }
    }

    private void drawDialogueBox(Graphics2D g2) {
        DialogueManager dm = gp.getDialogueManager();

        // Draw the Box
        g2.setColor(new Color(0, 0, 0, 220));
        g2.fillRect(50, 580, 670, 150);
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(5));
        g2.drawRect(55, 585, 660, 140);

        // 2. Setup Text Logic
        g2.setFont(new Font("Monospaced", Font.BOLD, 22));
        
        // FETCH FROM MANAGER
        String speaker = dm.getSpeaker();
        String pageText = dm.getCurrentPage();
        String fullText = speaker + ": " + pageText;

        // Handle substring safely
        String visibleText = fullText.substring(0, Math.min(charIndex + speaker.length() + 2, fullText.length()));
        
        // Word Wrap Rendering
        int x = 80;
        int y = 630;
        int maxWidth = 600;
        String[] words = visibleText.split(" ");
        String line = "";

        for (String word : words) {
            if (g2.getFontMetrics().stringWidth(line + word) > maxWidth) {
                g2.drawString(line, x, y);
                y += 35;
                line = word + " ";
            } else {
                line += word + " ";
            }
        }
        g2.drawString(line, x, y);

        // 3. Prompt
        if (charIndex >= pageText.length()) {
            drawInputPrompt(g2);
        }
    }

    private void drawPauseScreen(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, gp.getWidth(), gp.getHeight());

        g2.setFont(new Font("Monospaced", Font.BOLD, 60));
        String title = "PAUSED";
        int x = getXForCenteredText(title, g2);
        
        g2.setColor(Color.GRAY);
        g2.drawString(title, x + 3, 153);
        g2.setColor(Color.WHITE);
        g2.drawString(title, x, 150);

        g2.setFont(new Font("Monospaced", Font.BOLD, 30));
        for (int i = 0; i < menuOptions.length; i++) {
            String opt = menuOptions[i];
            int optX = getXForCenteredText(opt, g2);
            int optY = 300 + (i * 60);

            if (i == currentOption) {
                g2.setColor(Color.YELLOW);
                g2.drawString("> ", optX - 40, optY);

                // If we are on the VOLUME option, draw the visual slider bar
                if (opt.equals("VOLUME")) {
                    drawVolumeSlider(g2, optX + 150, optY - 20);
                }
            } else {
                g2.setColor(Color.WHITE);
            }
            g2.drawString(opt, optX, optY);
        }
    }

    private void drawVolumeSlider(Graphics2D g2, int x, int y) {
        // Save current font so we can restore it later
        Font oldFont = g2.getFont();

        g2.setColor(Color.WHITE);
        g2.drawRect(x, y, 200, 20); 

        g2.setColor(Color.YELLOW);
        int fillWidth = 20 * volumeScale;
        g2.fillRect(x, y, fillWidth, 20);
        
        // Set small font for percentage
        g2.setFont(new Font("Monospaced", Font.BOLD, 20));
        g2.drawString(volumeScale * 10 + "%", x + 210, y + 17);

        // RESTORE the menu font before leaving the method
        g2.setFont(oldFont);
    }

    private int getXForCenteredText(String text, Graphics2D g2) {
        int length = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        return gp.getWidth() / 2 - length / 2;
    }

    /*
    *   MENU NAVIGATION FUNCTIONS
    */

    public void moveCursorUp() {
        currentOption--;
        if (currentOption < 0) currentOption = menuOptions.length - 1;
    }

    public void moveCursorDown() {
        currentOption++;
        if (currentOption >= menuOptions.length) currentOption = 0;
    }

    public int getSelectedOption() {
        return currentOption;
    }

    private void drawInputPrompt(Graphics2D g2) {
        int bob = (int) (Math.sin(System.currentTimeMillis() / 150.0) * 3);
        g2.setColor(Color.YELLOW);
        g2.setFont(new Font("Monospaced", Font.BOLD, 16));
        g2.drawString("[E]", 640, 700 + bob);
        
        int[] triX = {675, 685, 680};
        int[] triY = {690 + bob, 690 + bob, 695 + bob};
        g2.fillPolygon(triX, triY, 3);
    }

    public int getCharIndex() { 
        return charIndex; 
    }

    public GamePanel getGP() { 
        return gp; 
    }
}