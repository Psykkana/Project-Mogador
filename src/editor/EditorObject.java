package editor;

public class EditorObject {
    public int x, y, spriteID;
    public String name, dialogue;
    public boolean isTrigger, facePlayer, isSign;

    // Updated Constructor
    public EditorObject(int x, int y, int id, String name, String chat, boolean isTrigger, boolean facePlayer, boolean isSign) {
        this.x = x;
        this.y = y;
        this.spriteID = id;
        this.name = name;
        this.dialogue = chat;
        this.isTrigger = isTrigger;
        this.facePlayer = facePlayer;
        this.isSign = isSign;
    }
}