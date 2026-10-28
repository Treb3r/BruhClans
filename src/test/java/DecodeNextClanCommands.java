import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class DecodeNextClanCommands {
    public static void main(String[] args) throws Exception {
        System.out.println("=== CLAN DISBAND ===");
        scan("clan disband/image_2026-10-26_19-04-46.png");
        scan("clan disband/image_2026-10-26_19-06-41.png");
        scan("clan disband/photo_2026-10-26_19-04-19.jpg");
        scan("clan disband/photo_2026-10-26_19-04-49.jpg");

        System.out.println("=== CLAN GLOW ===");
        scan("clan glow/photo_2026-10-26_19-10-37.jpg");

        System.out.println("=== CLAN HISTORY ===");
        scan("clan history/photo_2026-10-26_19-11-34.jpg");
    }

    static void scan(String path) throws Exception {
        File f = new File(path);
        if (!f.exists()) {
            System.out.println("NOT FOUND: " + path);
            return;
        }
        BufferedImage img = ImageIO.read(f);
        System.out.println("--------------------------------------------------");
        System.out.println("FILE: " + path + " (" + img.getWidth() + "x" + img.getHeight() + ")");
        System.out.println("--------------------------------------------------");

        // Detect text lines by row brightness
        int startY = -1;
        for (int y = 0; y < img.getHeight(); y++) {
            boolean has = false;
            for (int x = 0; x < img.getWidth(); x++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xff, g = (rgb >> 8) & 0xff, b = rgb & 0xff;
                if ((r > 120 || g > 120 || b > 120) && !(x < 4 && r > 200)) {
                    has = true;
                    break;
                }
            }
            if (has && startY == -1) startY = y;
            else if (!has && startY != -1) {
                if (y - startY >= 5) {
                    System.out.printf("Line at Y: %d..%d (h=%d)%n", startY, y - 1, y - startY);
                }
                startY = -1;
            }
        }
        if (startY != -1 && img.getHeight() - startY >= 5) {
            System.out.printf("Line at Y: %d..%d (h=%d)%n", startY, img.getHeight() - 1, img.getHeight() - startY);
        }
    }
}
