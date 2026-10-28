import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;

public class ReadTooltips {
    public static void main(String[] args) throws Exception {
        readImg("Хранилище/photo_2026-10-26_19-02-03.jpg", "LOCKED");
        readImg("Хранилище/photo_2026-10-26_19-02-13.jpg", "RENT");
        readImg("Хранилище/photo_2026-10-26_19-02-22.jpg", "BACK");
        readImg("Хранилище/photo_2026-10-27_23-24-48.jpg", "CANCEL");
        readImg("Хранилище/photo_2026-10-27_23-24-51.jpg", "CONFIRM");
    }
    static void readImg(String path, String label) throws Exception {
        BufferedImage img = ImageIO.read(new File(path));
        System.out.println("=== " + label + " (" + path + ") ===");
        // Find text colors by looking at high contrast pixels
        Map<Integer, Integer> colorCounts = new HashMap<>();
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;
                if ((r > 150 && g < 80 && b < 80) || // Red
                    (r > 200 && g > 100 && g < 180 && b < 60) || // Orange
                    (r > 200 && g > 200 && b < 60) || // Yellow
                    (r < 60 && g > 180 && b < 60) || // Green
                    (r > 200 && g > 200 && b > 200) || // White
                    (r > 150 && g > 150 && b > 150)) { // Gray
                    int quantized = ((r / 16) << 8) | ((g / 16) << 4) | (b / 16);
                    colorCounts.put(quantized, colorCounts.getOrDefault(quantized, 0) + 1);
                }
            }
        }
        System.out.println("Top colors (r/16, g/16, b/16):");
        colorCounts.entrySet().stream()
            .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
            .limit(10)
            .forEach(e -> {
                int q = e.getKey();
                int r = (q >> 8) * 16;
                int g = ((q >> 4) & 0xf) * 16;
                int b = (q & 0xf) * 16;
                System.out.printf("  RGB=(%d, %d, %d): %d%n", r, g, b, e.getValue());
            });
    }
}
