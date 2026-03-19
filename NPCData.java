public class NPCData {
    public String name;
    public int x, y, spriteID;
    public boolean isDirectional;
    public String[] dialogue;

    public NPCData(String name, int x, int y, int spriteID, boolean isDirectional, String[] dialogue) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.spriteID = spriteID;
        this.isDirectional = isDirectional;
        this.dialogue = dialogue;
    }
}