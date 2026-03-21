package editor;

import java.awt.*;
import javax.swing.*;

public class NPCDialog extends JDialog {
    private JTextField nameField = new JTextField(15);
    private JTextArea dialogArea = new JTextArea(3, 15);
    private JCheckBox facePlayerCheck = new JCheckBox("Faces Player");
    
    // Type Selection
    private JRadioButton npcRadio = new JRadioButton("NPC", true);
    private JRadioButton triggerRadio = new JRadioButton("Trigger");
    private JRadioButton signRadio = new JRadioButton("Sign");
    
    private boolean confirmed = false;

    public NPCDialog(JFrame parent, int x, int y) {
        super(parent, "Entity Properties (" + x + "," + y + ")", true);
        setLayout(new BorderLayout(10, 10));

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- ROW 1: Type Selection ---
        gbc.gridy = 0; 
        gbc.gridx = 0; panel.add(new JLabel("Entity Type:"), gbc);
        gbc.gridx = 1;
        
        JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        ButtonGroup group = new ButtonGroup();
        group.add(npcRadio); 
        group.add(triggerRadio); 
        group.add(signRadio);
        
        typePanel.add(npcRadio); 
        typePanel.add(triggerRadio); 
        typePanel.add(signRadio);
        panel.add(typePanel, gbc);

        // --- ROW 2: Name ---
        gbc.gridy = 1;
        gbc.gridx = 0; panel.add(new JLabel("Name / ID:"), gbc);
        gbc.gridx = 1; panel.add(nameField, gbc);

        // --- ROW 3: Dialogue / Text ---
        JLabel dialogueLabel = new JLabel("Dialogue:");
        gbc.gridy = 2;
        gbc.gridx = 0; panel.add(dialogueLabel, gbc);
        gbc.gridx = 1;
        dialogArea.setLineWrap(true);
        dialogArea.setWrapStyleWord(true);
        panel.add(new JScrollPane(dialogArea), gbc);

        // --- ROW 4: Options ---
        gbc.gridy = 3;
        gbc.gridx = 1; panel.add(facePlayerCheck, gbc);

        // --- Logic Listeners ---
        npcRadio.addActionListener(e -> {
            dialogueLabel.setText("Dialogue (split by ;):");
            nameField.setEnabled(true);
            facePlayerCheck.setEnabled(true);
        });

        triggerRadio.addActionListener(e -> {
            dialogueLabel.setText("Destination Map:");
            nameField.setEnabled(true);
            facePlayerCheck.setSelected(false);
            facePlayerCheck.setEnabled(false);
        });

        signRadio.addActionListener(e -> {
            dialogueLabel.setText("Sign Text:");
            nameField.setText(""); // Signs don't use names in your syntax
            nameField.setEnabled(false);
            facePlayerCheck.setSelected(false);
            facePlayerCheck.setEnabled(false);
        });

        // --- BOTTOM: Confirm Button ---
        JButton okBtn = new JButton("Confirm Settings");
        okBtn.setPreferredSize(new Dimension(0, 40));
        okBtn.addActionListener(e -> { confirmed = true; dispose(); });
        
        add(panel, BorderLayout.CENTER);
        add(okBtn, BorderLayout.SOUTH);

        pack();
        setResizable(false);
        setLocationRelativeTo(parent);
    }

    // Getters for MapGrid/Exporter
    // These methods allow MapGrid to "ask" the dialog for the user's choices
    public boolean isConfirmed() { 
        return confirmed; 
    }

    public String getEntityName() { 
        return nameField.getText(); 
    }

    public String getDialogue() { 
        return dialogArea.getText(); 
    }

    public boolean isTrigger() { 
        return triggerRadio.isSelected(); 
    }

    public boolean isSign() { 
        return signRadio.isSelected(); 
    }

    public boolean facesPlayer() { 
        return facePlayerCheck.isSelected(); 
    }
}