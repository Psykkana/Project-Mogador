package editor;

import java.awt.*;
import javax.swing.*;

/*
    Project Kumano
        A small map editor for Project Mogador
        Run with java editor.KumanoDriver
*/

public class KumanoDriver extends JFrame {

    public KumanoDriver() {
        game.AssetManager.loadAll();

        setTitle("Project Kumano | v0.1");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Setup the Map
        MapGrid grid = new MapGrid();
        add(grid, BorderLayout.CENTER);

        // Setup the Palette
        TilePalette palette = new TilePalette(grid);
        
        // Setup a small bottom panel for the Save Button
        JPanel footer = new JPanel();
        JButton saveBtn = new JButton("Export World Data");
        saveBtn.addActionListener(e -> exportMap(grid.getMapData()));
        footer.add(saveBtn);

        // Organize Layout
        // Inside KumanoDriver constructor
        add(palette, BorderLayout.EAST);
        add(footer, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        new KumanoDriver();
    }

    private void exportMap(int[][] map) {
        StringBuilder sb = new StringBuilder();

        // Place world name first
        sb.append("[NewWorld]\n");

        // THE GRID (22x22)
        for (int r = 0; r < 22; r++) {
            for (int c = 0; c < 22; c++) {
                sb.append(map[r][c]);
                
                // Add a space between numbers, but not after the last number in a row
                if (c < 21) {
                    sb.append(" ");
                }
            }
            sb.append("\n"); // New line after every 22 tiles
        }

        // Placeholder music
        sb.append("MUSIC: 1\n");
        
        // TO BE DONE: DYNAMIC ADDING OF NPCs, TRIGGERs, ETC.

        //  WRITE TO FILE
        try (java.io.PrintWriter out = new java.io.PrintWriter("maps/ExportedWorld.txt")) {
            out.print(sb.toString());
            JOptionPane.showMessageDialog(this, "Map Saved to maps/ExportedWorld.txt!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saving map: " + ex.getMessage());
        }
    }

}