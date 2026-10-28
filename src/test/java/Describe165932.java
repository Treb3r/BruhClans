import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class Describe165932 {
    public static void main(String[] args) throws Exception {
        BufferedImage img = ImageIO.read(new File("C:/Users/uilya/.gemini/antigravity/brain/23a70919-e9ec-47c1-bc92-15d21ec7f8e1/photo_165932.png"));
        // Check slot 9 (col 0, row 1)
        // Find shield
        int shieldSumX = 0, shieldSumY = 0, shieldCount = 0;
        for (int y = 0; y < img.getHeight() * 0.6; y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF, g = (rgb >> 8) & 0xFF, b = rgb & 0xFF;
                if (g > 180 && r < 100 && b < 100) {
                    shieldSumX += x; shieldSumY += y; shieldCount++;
                }
            }
        }
        int sx = shieldSumX / shieldCount;
        int sy = shieldSumY / shieldCount;
        
        int col0X = (int)Math.round(sx - 4 * 36.25);
        int col1X = (int)Math.round(sx - 3 * 36.25);
        int row1Y = sy;
        
        System.out.println("Col 0 at (" + col0X + ", " + row1Y + ")");
        System.out.println("Col 1 at (" + col1X + ", " + row1Y + ")");
        
        // Save cropped col 0 and col 1 of photo_165932
        ImageIO.write(img.getSubimage(col0X - 16, row1Y - 16, 32, 32), "png", new File("C:/Users/uilya/.gemini/antigravity/brain/23a70919-e9ec-47c1-bc92-15d21ec7f8e1/p165932_col0.png"));
        ImageIO.write(img.getSubimage(col1X - 16, row1Y - 16, 32, 32), "png", new File("C:/Users/uilya/.gemini/antigravity/brain/23a70919-e9ec-47c1-bc92-15d21ec7f8e1/p165932_col1.png"));
        System.out.println("Saved p165932_col0 and col1");
    }
}
