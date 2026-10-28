import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class FindArrow {
    public static void main(String[] args) throws Exception {
        BufferedImage img = ImageIO.read(new File("C:/Users/uilya/.gemini/antigravity/brain/23a70919-e9ec-47c1-bc92-15d21ec7f8e1/photo_2026-10-26_16-53-53.png"));
        int w = img.getWidth();
        int h = img.getHeight();
        
        // Scan bottom third of the image for white pixels
        for (int y = (int)(h * 0.7); y < h; y++) {
            for (int x = 0; x < w; x++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                if (r > 240 && g > 240 && b > 240) {
                    System.out.println("White pixel at x=" + x + ", y=" + y + " (" + ((double)x/w) + ", " + ((double)y/h) + ")");
                }
            }
        }
    }
}
