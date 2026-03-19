import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

public class TextureLoader {
    private static Map<Integer, BufferedImage> textures = new HashMap<>();
    private static BufferedImage fallback;

    static {
        try {
            fallback = ImageIO.read(new File("assets/missing_texture.png"));
        } catch (IOException e) { System.out.println("Missing fallback asset!"); }
    }

    public static void load(int id, String path) {
        try {
            textures.put(id, ImageIO.read(new File(path)));
        } catch (IOException e) {
            System.out.println("Failed to load " + path);
            textures.put(id, fallback);
        }
    }

    public static BufferedImage get(int id) {
        return textures.getOrDefault(id, fallback);
    }
}