package game;

public class AssetManager {
    public static void loadAll() {
        // Tiles
        TextureLoader.load(0, "/assets/grass.png");
        TextureLoader.load(1, "/assets/wall.png");
        TextureLoader.load(2, "/assets/wood.png");
        TextureLoader.load(3, "/assets/log.png");
        TextureLoader.load(4, "/assets/wood_floor.png");
        TextureLoader.load(8, "/assets/sand.png");
        TextureLoader.load(9, "/assets/water.png");
        TextureLoader.load(10, "/assets/door.png");
        TextureLoader.load(11, "/assets/signboard.png");
        TextureLoader.load(20, "/assets/tree.png");
        TextureLoader.load(21, "/assets/cobblestone.png");
        TextureLoader.load(22, "/assets/wood_roof.png");
        TextureLoader.load(23, "/assets/wood_roof_left.png");
        TextureLoader.load(24, "/assets/wood_roof_right.png");
        TextureLoader.load(25, "/assets/tree_base.png");
        TextureLoader.load(26, "/assets/tree_middle.png");
        TextureLoader.load(27, "/assets/tree_leaves.png");
        TextureLoader.load(28, "/assets/metal_fence.png");

        // Entities / Player
        TextureLoader.load(90, "/assets/debugfloor.png");
        TextureLoader.load(91, "/assets/player_up.png");
        TextureLoader.load(92, "/assets/player_down.png");
        TextureLoader.load(93, "/assets/player_left.png");
        TextureLoader.load(94, "/assets/player_right.png");
        
        // UI & System
        TextureLoader.load(99, "/assets/missing_texture.png");

        System.out.println("System: All assets loaded into memory.");
    }

    public static java.awt.image.BufferedImage getTile(int id) {
        // Pass the request to the TextureLoader
        return TextureLoader.get(id);
    }
    
    public static String getTileName(int id) {
        // You can hardcode this or point to Board.getTileName(id)
        return Board.getTileNameByID(id);
    }
}