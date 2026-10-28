import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class DetailedDisbandGlowHistory {
    public static void main(String[] args) throws Exception {
        System.out.println("--- 1. DISBAND 19-04-19 (Question / Confirm) ---");
        printLines("clan disband/photo_2026-10-26_19-04-19.jpg");

        System.out.println("--- 2. DISBAND 19-04-46 (Cancel) ---");
        printLines("clan disband/image_2026-10-26_19-04-46.png");

        System.out.println("--- 3. DISBAND 19-04-49 (Success) ---");
        printLines("clan disband/photo_2026-10-26_19-04-49.jpg");

        System.out.println("--- 4. DISBAND 19-06-41 (Not owner error) ---");
        printLines("clan disband/image_2026-10-26_19-06-41.png");

        System.out.println("--- 5. GLOW 19-10-37 ---");
        printLines("clan glow/photo_2026-10-26_19-10-37.jpg");

        System.out.println("--- 6. HISTORY 19-11-34 ---");
        printLines("clan history/photo_2026-10-26_19-11-34.jpg");
    }

    static void printLines(String path) throws Exception {
        BufferedImage img = ImageIO.read(new File(path));
        System.out.println("FILE: " + path + " (" + img.getWidth() + "x" + img.getHeight() + ")");
        int startY = -1;
        for (int y = 0; y < img.getHeight(); y++) {
            boolean has = false;
            for (int x = 0; x < img.getWidth(); x++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xff, g = (rgb >> 8) & 0xff, b = rgb & 0xff;
                if ((r > 120 || g > 120 || b > 120) && !(x < 4 && r > 200)) {
                    has = true; break;
                }
            }
            if (has && startY == -1) startY = y;
            else if (!has && startY != -1) {
                if (y - startY >= 6) {
                    dumpRow(img, startY, y - 1);
                }
                startY = -1;
            }
        }
        if (startY != -1 && img.getHeight() - startY >= 6) {
            dumpRow(img, startY, img.getHeight() - 1);
        }
    }

    static void dumpRow(BufferedImage img, int y1, int y2) {
        System.out.printf("Row Y=%d..%d:%n", y1, y2);
        for (int chunk = 0; chunk < img.getWidth(); chunk += 75) {
            int end = Math.min(chunk + 75, img.getWidth());
            boolean hasP = false;
            for (int y = y1; y <= y2; y++) {
                for (int x = chunk; x < end; x++) {
                    int rgb = img.getRGB(x, y);
                    int r = (rgb >> 16) & 0xff, g = (rgb >> 8) & 0xff, b = rgb & 0xff;
                    if ((r > 120 || g > 120 || b > 120) && !(x < 4 && r > 200)) { hasP = true; break; }
                }
            }
            if (!hasP) continue;
            System.out.printf("  [%d..%d]:%n", chunk, end);
            for (int y = y1; y <= y2; y++) {
                StringBuilder sb = new StringBuilder();
                for (int x = chunk; x < end; x++) {
                    int rgb = img.getRGB(x, y);
                    int r = (rgb >> 16) & 0xff, g = (rgb >> 8) & 0xff, b = rgb & 0xff;
                    if (x < 4 && r > 200 && g > 200 && b > 200) { sb.append(" "); continue; }
                    if (r > 150 && g < 70 && b < 70) sb.append("R");
                    else if (r > 180 && g > 70 && g < 170 && b < 70) sb.append("O");
                    else if (r > 180 && g > 180 && b < 70) sb.append("Y");
                    else if (r < 70 && g > 180 && b < 70) sb.append("G");
                    else if (r > 180 && g > 180 && b > 180) sb.append("W");
                    else if (r > 100 && g > 100 && b > 100) sb.append(".");
                    else sb.append(" ");
                }
                if (!sb.toString().trim().isEmpty()) System.out.println("    " + sb.toString());
            }
        }
    }
}
