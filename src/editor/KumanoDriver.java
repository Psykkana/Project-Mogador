package editor;

import java.awt.*;
import javax.swing.*;

/*
    Project Kumano
        A small map editor for Project Mogador
        Run with java editor.KumanoDriver
*/

public class KumanoDriver extends JFrame {
    private MapGrid grid;

    public KumanoDriver() {
        // Initialize textures from your game package
        game.AssetManager.loadAll();

        setTitle("Project Kumano | v0.2");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 2. Setup the Map (Center)
        grid = new MapGrid();
        add(grid, BorderLayout.CENTER);

        // 3. Setup the Palette (Right Sidebar)
        TilePalette palette = new TilePalette(grid);
        grid.setPalette(palette);
        add(palette, BorderLayout.EAST);
        
        // 4. Setup the Footer
        // We use BorderLayout for the footer so the Status is on the left 
        // and the buttons are on the right.
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // --- Left side: Mode Indicator ---
        JLabel modeStatus = new JLabel(" MODE: Tile Painting ");
        modeStatus.setFont(new Font("Monospaced", Font.BOLD, 12));
        modeStatus.setBorder(BorderFactory.createEtchedBorder());
        grid.setStatusLabel(modeStatus); // Ensure this method exists in MapGrid
        footer.add(modeStatus, BorderLayout.WEST);

        // --- Right side: Button Container ---
        // We need a sub-panel to hold multiple buttons together
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton tileModeBtn = new JButton("Tile Mode");
        tileModeBtn.addActionListener(e -> {
            game.AssetManager.sound.playSE(7);  // buttonclick.wav            
            grid.setNpcMode(false);
        });

        JButton npcModeBtn = new JButton("Entity Mode");
        npcModeBtn.addActionListener(e -> {
            game.AssetManager.sound.playSE(7);            
            grid.setNpcMode(true);
        });

        JButton saveBtn = new JButton("Export World Data");
        saveBtn.addActionListener(e -> {
            game.AssetManager.sound.playSE(7);            
            exportMap();
        });

        buttonPanel.add(tileModeBtn);
        buttonPanel.add(npcModeBtn);
        buttonPanel.add(new JSeparator(SwingConstants.VERTICAL)); // Visual gap
        buttonPanel.add(saveBtn);

        // Add the cluster of buttons to the east side of the footer
        footer.add(buttonPanel, BorderLayout.EAST);

        // Add the final footer to the window
        add(footer, BorderLayout.SOUTH);

        // Finalize Window
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new KumanoDriver());
    }

    private void exportMap() {
        StringBuilder sb = new StringBuilder();
        int[][] map = grid.getMapData();

        // 1. Header
        sb.append("[NewWorld]\n");

        // 2. THE GRID (22x22)
        for (int r = 0; r < 22; r++) {
            for (int c = 0; c < 22; c++) {
                sb.append(map[r][c]);
                
                // Add a space between numbers for readability
                if (c < 21) {
                    sb.append(" ");
                }
            }
            sb.append("\n"); 
        }

        // 3. Metadata (Music)
        sb.append("MUSIC: 1\n");
        
        // 4. THE OBJECT LAYER (NPCs and Triggers)
        sb.append("# NPC: x, y, spriteID, Name, isTrigger, Dialogue\n");
        for (EditorObject obj : grid.getObjectList()) {
            sb.append("NPC: ")
            .append(obj.x).append(", ")
            .append(obj.y).append(", ")
            .append(obj.spriteID).append(", ")
            .append(obj.name).append(", ")
            .append(obj.isTrigger).append(", ")
            .append(obj.facePlayer).append(", ") 
            .append(obj.dialogue).append("\n");
        }

        // 5. Check if the maps folder exists in the project root
        java.io.File dir = new java.io.File("maps");
        if (!dir.exists()) {
            dir.mkdir(); 
        }

        // 6. WRITE TO FILE
        try (java.io.PrintWriter out = new java.io.PrintWriter("maps/ExportedWorld.txt")) {
            out.print(sb.toString());
            JOptionPane.showMessageDialog(this, "Map Saved to maps/ExportedWorld.txt!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saving map: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

}