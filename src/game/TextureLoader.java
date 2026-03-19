package game;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

public class TextureLoader {
    private static Map<Integer, BufferedImage> textures = new HashMap<>();
    private static BufferedImage fallback;

    static {
        try {
            InputStream is = TextureLoader.class.getResourceAsStream("/assets/missing_texture.png");
            fallback = ImageIO.read(is);
        } catch (IOException e) { System.out.println("Missing fallback asset!"); }
    }

    public static void load(int id, String path) {
        try {
            InputStream is = TextureLoader.class.getResourceAsStream(path);
            textures.put(id, ImageIO.read(is));
        } catch (IOException e) {
            System.out.println("Failed to load " + path);
            textures.put(id, fallback);
        }
    }

    public static BufferedImage get(int id) {
        return textures.getOrDefault(id, fallback);
    }
}