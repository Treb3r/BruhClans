import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class CheckBottomRow {
    public static void main(String[] args) throws Exception {
        BufferedImage orig = ImageIO.read(new File("C:/Users/uilya/.gemini/antigravity/brain/23a70919-e9ec-47c1-bc92-15d21ec7f8e1/photo_2026-10-26_16-53-53.png"));
        
        // Slot 46..53 centers:
        for (int c = 0; c < 9; c++) {
            int slot = 45 + c;
            int ox = (int)Math.round(227 + (c - 4) * 36.25);
            int oy = (int)Math.round(174 + 4 * 36.25);
            
            // Check average color and variation
            int whitePixels = 0;
            int redPixels = 0;
            for (int dy = -14; dy <= 14; dy++) {
                for (int dx = -14; dx <= 14; dx++) {
                    int rgb = orig.getRGB(ox + dx, oy + dy);
                    int r = (rgb >> 16) & 0xFF;
                    int g = (rgb >> 8) & 0xFF;
                    int b = rgb & 0xFF;
                    if (r > 230 && g > 230 && b > 230) whitePixels++;
                    if (r > 180 && g < 50 && b < 50) redPixels++;
                }
            }
            System.out.printf("Orig Bottom Slot %2d (col %d): white=%d, red=%d%n", slot, c, whitePixels, redPixels);
        }
    }
}
