package editor;

import game.AssetManager;
import game.Board;
import java.awt.*;
import javax.swing.*;

public class TilePalette extends JPanel {
    private MapGrid grid;
    private JLabel selectedPreview; // The "Current Brush" display

    private java.util.List<JButton> tileButtons = new java.util.ArrayList<>();

    public TilePalette(MapGrid grid) {
        this.grid = grid;
        
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(250, 704));
        setBorder(BorderFactory.createTitledBorder("Tile Palette"));

        selectedPreview = new JLabel("", SwingConstants.CENTER);
        selectedPreview.setVerticalTextPosition(JLabel.BOTTOM);
        selectedPreview.setHorizontalTextPosition(JLabel.CENTER);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder("Current Brush"));
        topPanel.add(selectedPreview, BorderLayout.CENTER);

        // Set initial icon (ID 0 - Grass)
        updatePreview(0);
        add(topPanel, BorderLayout.NORTH);

        // The Container for buttons
        // GridLayout(0, 2) means "infinite rows, 2 columns"
        JPanel buttonGrid = new JPanel(new GridLayout(0, 2, 5, 5));
        buttonGrid.setBackground(Color.GRAY);

        // Define all the IDs
        int[] allTileIDs = {0, 1, 2, 3, 4, 8, 9, 10, 
                            11, 20, 21, 22, 23, 24, 25, 26, 27, 28,
                            91, 92, 93, 94
                            };

        for (int id : allTileIDs) {
            JButton btn = createTileButton(id);
            tileButtons.add(btn); // Store reference
            buttonGrid.add(btn);
        }

        // Wrap the grid in a JScrollPane
        JScrollPane scrollPane = new JScrollPane(buttonGrid);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Makes scrolling smooth

        add(scrollPane, BorderLayout.CENTER);
    }

    private void updatePreview(int id) {
        Image img = AssetManager.getTile(id);
        String name = Board.getTileNameByID(id);
        
        // Make the preview icon a bit larger than the buttons
        ImageIcon icon = new ImageIcon(img.getScaledInstance(64, 64, Image.SCALE_SMOOTH));
        selectedPreview.setIcon(icon);
        selectedPreview.setText(name);
    }

    public void refreshButtons() {
        boolean npcMode = grid.isNpcMode();

        for (JButton btn : tileButtons) {
            int id = (int) btn.getClientProperty("tileID");
            boolean isEntity = grid.isAllowedEntity(id);

            // Determine if this button should be active
            boolean shouldEnable = !npcMode || isEntity;
            
            btn.setEnabled(shouldEnable);

            // Logic: Manually dim the custom labels inside the button
            for (Component c : btn.getComponents()) {
                c.setEnabled(shouldEnable); 
            }
        }
    }

    private JButton createTileButton(int id) {
        // 1. Get Data from Game
        Image img = AssetManager.getTile(id);
        String name = game.Board.getTileNameByID(id); // Pull from your new static method

        // 2. Setup Button
        JButton btn = new JButton();
        btn.putClientProperty("tileID", id);
        btn.setLayout(new BorderLayout());
        btn.setBackground(new Color(240, 240, 240));
        
        // 3. Create the Visuals
        ImageIcon icon = new ImageIcon(img.getScaledInstance(32, 32, Image.SCALE_SMOOTH));
        JLabel iconLabel = new JLabel(icon);
        
        JLabel textLabel = new JLabel(name, SwingConstants.CENTER);
        textLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        textLabel.setForeground(Color.DARK_GRAY);

        // 4. Assemble
        btn.add(iconLabel, BorderLayout.CENTER);
        btn.add(textLabel, BorderLayout.SOUTH);
        
        btn.setPreferredSize(new Dimension(90, 70));
        btn.setToolTipText("Select Tile " + id);
        
        btn.addActionListener(e -> {
            grid.setSelectedTileID(id);
            game.AssetManager.sound.playSE(7);
            updatePreview(id); 
        });
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                game.AssetManager.sound.playSE(9); // buttonrollover.wav
            }
        });

        return btn;
    }
}