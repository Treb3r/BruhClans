import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class CheckPage2Bottom {
    public static void main(String[] args) throws Exception {
        BufferedImage img = ImageIO.read(new File("C:/Users/uilya/.gemini/antigravity/brain/23a70919-e9ec-47c1-bc92-15d21ec7f8e1/photo_2026-10-26_16-54-09.png"));
        // Find door in this image:
        int doorSumX = 0, doorSumY = 0, doorCount = 0;
        for (int y = (int)(img.getHeight() * 0.6); y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF, g = (rgb >> 8) & 0xFF, b = rgb & 0xFF;
                if (r > 200 && g < 60 && b < 60) {
                    doorSumX += x; doorSumY += y; doorCount++;
                }
            }
        }
        int doorX = doorSumX / doorCount;
        int doorY = doorSumY / doorCount;
        System.out.println("Page 2 Door at: " + doorX + ", " + doorY);
        
        // Scan bottom row cols
        for (int c = 0; c < 9; c++) {
            int slot = 45 + c;
            int ox = (int)Math.round(doorX + (c - 4) * 36.25);
            int oy = doorY;
            int whitePixels = 0;
            for (int dy = -14; dy <= 14; dy++) {
                for (int dx = -14; dx <= 14; dx++) {
                    int rgb = img.getRGB(ox + dx, oy + dy);
                    int r = (rgb >> 16) & 0xFF, g = (rgb >> 8) & 0xFF, b = rgb & 0xFF;
                    if (r > 230 && g > 230 && b > 230) whitePixels++;
                }
            }
            System.out.printf("Page 2 Bottom Slot %2d (col %d): white=%d%n", slot, c, whitePixels);
        }
    }
}
