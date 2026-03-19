package editor;

import game.AssetManager;   // load the textures from game
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MapGrid extends JPanel {
    private final int ROWS = 22;
    private final int COLS = 22;
    private final int TILE_SIZE = 32;
    private int[][] mapData = new int[ROWS][COLS];
    private int selectedTileID = 1; // Default to 'Wall' or 'Grass'

    public MapGrid() {
        setPreferredSize(new Dimension(COLS * TILE_SIZE, ROWS * TILE_SIZE));
        setBackground(Color.BLACK);

        // Click to Paint
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                paintTile(e.getX(), e.getY());
            }
        });

        // Drag to Paint (Like a brush)
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                paintTile(e.getX(), e.getY());
            }
        });
    }

    private void paintTile(int x, int y) {
        int col = x / TILE_SIZE;
        int row = y / TILE_SIZE;

        // Bounds check to prevent crashing if clicking outside the grid
        if (col >= 0 && col < COLS && row >= 0 && row < ROWS) {
            mapData[row][col] = selectedTileID;
            repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                int tileID = mapData[r][c];

                Image img = AssetManager.getTile(tileID); 
                    
                if (img != null) {
                    g.drawImage(img, c * TILE_SIZE, r * TILE_SIZE, TILE_SIZE, TILE_SIZE, null);
                }

                // Keep the grid lines so you can see the boundaries
                g.setColor(new Color(255, 255, 255, 50)); // Faint white
                g.drawRect(c * TILE_SIZE, r * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }
    }

    public int[][] getMapData() { return mapData; }
    public void setSelectedTileID(int id) { this.selectedTileID = id; }
}