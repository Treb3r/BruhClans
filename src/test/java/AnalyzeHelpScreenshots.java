import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class AnalyzeHelpScreenshots {
    public static void main(String[] args) throws Exception {
        inspect("C:/Users/uilya/.gemini/antigravity/brain/23a70919-e9ec-47c1-bc92-15d21ec7f8e1/.user_uploaded/media_1793144063264_6f8d5ba9.png", "PHOTO 3 (Non-clan)");
        inspect("C:/Users/uilya/.gemini/antigravity/brain/23a70919-e9ec-47c1-bc92-15d21ec7f8e1/.user_uploaded/media_1793143975745_886106b7.png", "PHOTO 1 (Clan full access 1)");
        inspect("C:/Users/uilya/.gemini/antigravity/brain/23a70919-e9ec-47c1-bc92-15d21ec7f8e1/.user_uploaded/media_1793143987028_ea2791c1.png", "PHOTO 2 (Clan full access 2)");
    }

    static void inspect(String path, String label) throws Exception {
        BufferedImage img = ImageIO.read(new File(path));
        System.out.println("==================================================");
        System.out.println(label + " -> " + img.getWidth() + "x" + img.getHeight());
        System.out.println("==================================================");

        // Let's sample colors for each line
        // First find row bands with text
        for (int y = 0; y < img.getHeight(); y++) {
            boolean has = false;
            for (int x = 0; x < img.getWidth(); x++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xff, g = (rgb >> 8) & 0xff, b = rgb & 0xff;
                if (r > 120 || g > 120 || b > 120) { has = true; break; }
            }
        }
    }
}
