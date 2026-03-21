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

    private java.util.ArrayList<EditorObject> objectList = new java.util.ArrayList<>();
    private boolean npcMode = false; // Toggle this from the sidebar
    private JLabel statusLabel;

    private int hoverCol = -1;
    private int hoverRow = -1;

    // For when you switch to Entity Mode
    private final int[] ALLOWED_ENTITIES = {10, 11, 91, 92, 93, 94, };
    private TilePalette palette;

    public MapGrid() {
        setPreferredSize(new Dimension(COLS * TILE_SIZE, ROWS * TILE_SIZE));
        setBackground(Color.BLACK);

        // 1. CLICK TO PLACE (Handles both Tiles and NPCs)
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (npcMode) {
                    placeNPC(e.getX(), e.getY()); // Popups only happen here!
                } else {
                    paintTile(e.getX(), e.getY());
                }
            }
        });

        // 2. DRAG TO PAINT (Tiles ONLY)
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                hoverCol = e.getX() / TILE_SIZE;
                hoverRow = e.getY() / TILE_SIZE;
                
                if (statusLabel != null) {
                    String mode = npcMode ? "ENTITY" : "TILE";
                    statusLabel.setText(String.format(" MODE: %s | POS: [%d, %d]", mode, hoverCol, hoverRow));
                }

                // Only repaint if the mouse is within the 22x22 bounds
                if (hoverCol >= 0 && hoverCol < COLS && hoverRow >= 0 && hoverRow < ROWS) {
                    repaint(); 
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                // Keep the cursor updated while painting tiles
                hoverCol = e.getX() / TILE_SIZE;
                hoverRow = e.getY() / TILE_SIZE;
                paintTile(e.getX(), e.getY());
            }
        });

        
    }

    public boolean isAllowedEntity(int id) {
        for (int allowed : ALLOWED_ENTITIES) {
            if (id == allowed) return true;
        }
        return false;
    }

    public void setPalette(TilePalette palette) {
        this.palette = palette;
    }

    public void setNpcMode(boolean active) {
        this.npcMode = active;
        updateStatusText();
        if (palette != null) {
            palette.refreshButtons(); // Tell the palette to update its look
        }
    }

    private void paintTile(int x, int y) {
        // 1. If we are in NPC mode, STOP. We don't want to paint tiles.
        if (npcMode) return; 

        int col = x / TILE_SIZE;
        int row = y / TILE_SIZE;

        // 2. Standard Bounds Check
        if (col >= 0 && col < COLS && row >= 0 && row < ROWS) {
            // 3. Update the data and tell Java to redraw the screen
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

        // This draws ON TOP of the tiles
        for (EditorObject obj : objectList) {
            Image npcImg = AssetManager.getTile(obj.spriteID);
            
            if (npcImg != null) {
                // Draw the actual sprite
                g.drawImage(npcImg, obj.x * TILE_SIZE, obj.y * TILE_SIZE, TILE_SIZE, TILE_SIZE, null);
            }

            // Add a colored border so we know it's an interactive object
            // NPCs get Yellow, Triggers get Cyan
            g.setColor(obj.isTrigger ? Color.CYAN : Color.YELLOW);
            g.drawRect(obj.x * TILE_SIZE, obj.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            
            // Optional: Draw the NPC's name above their head
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 10));
            g.drawString(obj.name, obj.x * TILE_SIZE, obj.y * TILE_SIZE - 2);
        }

        // --- DRAW MAP CURSOR ---
        if (hoverCol >= 0 && hoverCol < COLS && hoverRow >= 0 && hoverRow < ROWS) {
            // Choose color based on mode
            if (npcMode) {
                g.setColor(new Color(255, 255, 0, 100)); // Semi-transparent Yellow for NPCs
            } else {
                g.setColor(new Color(255, 255, 255, 100)); // Semi-transparent White for Tiles
            }

            // Draw the "Fill"
            g.fillRect(hoverCol * TILE_SIZE, hoverRow * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            
            // Draw a thick border
            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(2));
            g2.setColor(Color.WHITE);
            g2.drawRect(hoverCol * TILE_SIZE, hoverRow * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        }
    }

    private void placeNPC(int x, int y) {
        int col = x / TILE_SIZE;
        int row = y / TILE_SIZE;

        // Logic Check: Is the current brush a valid entity?
        if (!isAllowedEntity(selectedTileID)) {
            // Play an error sound (maybe the button rollover or a buzzer if you have one)
            game.AssetManager.sound.playSE(9); 
            
            // Show a quick message so the user isn't confused
            JOptionPane.showMessageDialog(this, 
                "Tile ID " + selectedTileID + " cannot be an Entity.\nSelect a character or object sprite first.",
                "Invalid Entity", 
                JOptionPane.WARNING_MESSAGE);
            return; // Stop the method here!
        }

        if (col >= 0 && col < COLS && row >= 0 && row < ROWS) {
            // Find the parent JFrame for the dialog
            JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
            NPCDialog dialog = new NPCDialog(parent, col, row);
            dialog.setVisible(true); // This waits until the dialog is closed

            if (dialog.isConfirmed()) {
                // Collect all data from the dialog's methods
                String name = dialog.getEntityName();
                String chat = dialog.getDialogue();
                boolean trigger = dialog.isTrigger();
                boolean sign = dialog.isSign();
                boolean face = dialog.facesPlayer();

                // Add to the list (Ensure EditorObject constructor matches this order!)
                objectList.add(new EditorObject(col, row, selectedTileID, name, chat, trigger, face, sign));
                
                if (dialog.isTrigger()) {
                    game.AssetManager.sound.playSE(10); // door_sfx.wav
                } else {
                    game.AssetManager.sound.playSE(6);  // textsingle.wav
                }

                repaint();
            }
        }
    }

    public void setStatusLabel(JLabel label) {
        this.statusLabel = label;
        updateStatusText();
    }

    private void updateStatusText() {
        if (statusLabel == null) return;
        
        if (npcMode) {
            statusLabel.setText(" MODE: NPC/Entity ");
            statusLabel.setForeground(new Color(218, 165, 32)); // Goldenrod
        } else {
            statusLabel.setText(" MODE: Tile Painting ");
            statusLabel.setForeground(new Color(34, 139, 34)); // Forest Green
        }
    }

    public int[][] getMapData() { return mapData; }
    public void setSelectedTileID(int id) { this.selectedTileID = id; }
    public boolean isNpcMode() { return npcMode; }
    public java.util.ArrayList<EditorObject> getObjectList() { return objectList; }
}