import java.awt.Dimension;
import javax.swing.*;

/*
 *  PROJECT MOGADOR
 *  Driver.java
 *      The engine of the program, responsible for creating the window and 
 *      starting the game loop.
 *  
 */

class Driver {

    private static void Start() {

        // Starting resolution
        int width = 784;
        int height = 807;

        // Create window frame with title at toolbar
        JFrame window = new JFrame("Project Mogador INDEV v0.9");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // Close app upon pressing exit button
        
        GamePanel gamePanel = new GamePanel(); // Create our custom JPanel
        window.setContentPane(gamePanel); // Add our JPanel to the window

        // Use setPreferredSize so it doesn't start as a shrunken window
        // setSize() can be used but it may cause issues on some platforms and is not recommended
        window.setPreferredSize(new Dimension(width, height));
            
        // pack() fits window size around components (just the JPanel)
        // pack() should be called after setResizable() to avoid issues on some platforms
        window.pack();
        window.setResizable(false);
        window.setLocationRelativeTo(null);     // Open window in the center of the screen
        window.setVisible(true);    // Display the window
        gamePanel.requestFocus(); 
        gamePanel.startGameThread();
    }

    public static void main(String[] args) {
        // invokeLater() to prevent graphics processing from blocking the GUI
        // Just a lot of boilerplate code
        // When main runs it will call Start() once
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Start();
            }
        });
    }
}