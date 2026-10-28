import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class FindCompletedLevelInPhotos {
    public static void main(String[] args) throws Exception {
        File dir = new File("C:/Users/uilya/IdeaProjects/BruhClans/Уровень");
        File[] files = dir.listFiles((d, n) -> n.endsWith(".jpg"));
        if (files == null) return;
        
        for (File f : files) {
            BufferedImage img = ImageIO.read(f);
            // Check if this image has level 1 slot
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
            if (shieldCount < 10) continue;
            int sx = shieldSumX / shieldCount;
            int sy = shieldSumY / shieldCount;
            
            // Col 0 slot
            int c0x = (int)Math.round(sx - 4 * 36.25);
            int c0y = sy;
            if (c0x < 16 || c0x >= img.getWidth() - 16 || c0y < 16 || c0y >= img.getHeight() - 16) continue;
            
            BufferedImage slot0 = img.getSubimage(c0x - 14, c0y - 14, 28, 28);
            boolean hasWhite = false;
            for (int y = 0; y < 28; y++) {
                for (int x = 0; x < 28; x++) {
                    int rgb = slot0.getRGB(x, y);
                    int r = (rgb >> 16) & 0xFF, g = (rgb >> 8) & 0xFF, b = rgb & 0xFF;
                    if (r > 245 && g > 245 && b > 245) hasWhite = true;
                }
            }
            if (!hasWhite) {
                System.out.println("PHOTO WITH COMPLETED (OR LOCKED) SLOT 0: " + f.getName());
            }
        }
    }
}
