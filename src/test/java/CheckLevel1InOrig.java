import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class CheckLevel1InOrig {
    public static void main(String[] args) throws Exception {
        // Let's check photo_2026-10-26_16-53-53.jpg (first screenshot in Уровень)
        BufferedImage img = ImageIO.read(new File("C:/Users/uilya/IdeaProjects/BruhClans/Уровень/photo_2026-10-26_16-53-53.jpg"));
        
        // Find anchor slot 13 (shield)
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
        
        // In photo_2026-10-26_16-53-53.jpg, what is at slot 9 (col 0, row 1)?
        // sx is col 4, so col 0 is sx - 4 * 36.25
        int col0X = (int)Math.round(sx - 4 * 36.25);
        int col1X = (int)Math.round(sx - 3 * 36.25);
        int row1Y = sy;
        
        System.out.println("Col 0 at x=" + col0X + ", y=" + row1Y);
        System.out.println("Col 1 at x=" + col1X + ", y=" + row1Y);
        
        // Crop col 0 and col 1
        ImageIO.write(img.getSubimage(col0X - 16, row1Y - 16, 32, 32), "png", new File("C:/Users/uilya/.gemini/antigravity/brain/23a70919-e9ec-47c1-bc92-15d21ec7f8e1/orig_col0.png"));
        ImageIO.write(img.getSubimage(col1X - 16, row1Y - 16, 32, 32), "png", new File("C:/Users/uilya/.gemini/antigravity/brain/23a70919-e9ec-47c1-bc92-15d21ec7f8e1/orig_col1.png"));
        System.out.println("Saved orig_col0 and orig_col1");
    }
}
