import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class ScanTextClusters {
    public static void main(String[] args) throws Exception {
        scan("Хранилище/photo_2026-10-26_19-02-03.jpg", "LOCKED");
        scan("Хранилище/photo_2026-10-26_19-02-13.jpg", "RENT");
        scan("Хранилище/photo_2026-10-26_19-02-22.jpg", "BACK");
        scan("Хранилище/photo_2026-10-27_23-24-48.jpg", "CANCEL");
        scan("Хранилище/photo_2026-10-27_23-24-51.jpg", "CONFIRM");
    }
    static void scan(String path, String label) throws Exception {
        BufferedImage img = ImageIO.read(new File(path));
        // Project text pixels horizontally (rows with text)
        System.out.println("=== " + label + " ===");
        int[] rowCounts = new int[img.getHeight()];
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;
                // Text pixel in Minecraft: bright color or pure white/yellow/red/green/orange
                if ((r > 180 && g < 50 && b < 50) || // red
                    (r > 200 && g > 80 && g < 150 && b < 50) || // orange
                    (r > 200 && g > 200 && b < 50) || // yellow
                    (r < 50 && g > 200 && b < 50) || // green
                    (r > 200 && g > 200 && b > 200)) { // white
                    rowCounts[y]++;
                }
            }
        }
        // Group consecutive active rows
        int start = -1;
        for (int y = 0; y < img.getHeight(); y++) {
            if (rowCounts[y] > 5) {
                if (start == -1) start = y;
            } else {
                if (start != -1) {
                    if (y - start >= 5) {
                        printLine(img, start, y - 1, label);
                    }
                    start = -1;
                }
            }
        }
        if (start != -1 && img.getHeight() - start >= 5) {
            printLine(img, start, img.getHeight() - 1, label);
        }
    }
    static void printLine(BufferedImage img, int y1, int y2, String label) {
        // Find x range
        int minX = img.getWidth(), maxX = 0;
        for (int y = y1; y <= y2; y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;
                if ((r > 180 && g < 50 && b < 50) ||
                    (r > 200 && g > 80 && g < 150 && b < 50) ||
                    (r > 200 && g > 200 && b < 50) ||
                    (r < 50 && g > 200 && b < 50) ||
                    (r > 200 && g > 200 && b > 200)) {
                    if (x < minX) minX = x;
                    if (x > maxX) maxX = x;
                }
            }
        }
        System.out.printf("  Line y=[%d..%d], x=[%d..%d], w=%d%n", y1, y2, minX, maxX, maxX - minX + 1);
    }
}
