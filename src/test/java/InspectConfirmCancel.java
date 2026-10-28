import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class InspectConfirmCancel {
    public static void main(String[] args) throws Exception {
        checkImg("\u0425\u0440\u0430\u043d\u0438\u043b\u0438\u0449\u0435/photo_2026-10-27_23-24-48.jpg", "CANCEL");
        checkImg("\u0425\u0440\u0430\u043d\u0438\u043b\u0438\u0449\u0435/photo_2026-10-27_23-24-51.jpg", "CONFIRM");
    }
    static void checkImg(String path, String label) throws Exception {
        BufferedImage img = ImageIO.read(new File(path));
        System.out.println("=== " + label + " (" + img.getWidth() + "x" + img.getHeight() + ") ===");
        // Find text lines
        int[] rowCounts = new int[img.getHeight()];
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;
                if ((r > 150 && g < 60 && b < 60) || (r < 60 && g > 150 && b < 60) || (r > 200 && g > 200 && b > 200)) {
                    rowCounts[y]++;
                }
            }
        }
        for (int y = 0; y < img.getHeight(); y++) {
            if (rowCounts[y] > 5) {
                // Find start & end x
                int minX = img.getWidth(), maxX = 0;
                for (int x = 0; x < img.getWidth(); x++) {
                    int rgb = img.getRGB(x, y);
                    int r = (rgb >> 16) & 0xff;
                    int g = (rgb >> 8) & 0xff;
                    int b = rgb & 0xff;
                    if ((r > 150 && g < 60 && b < 60) || (r < 60 && g > 150 && b < 60) || (r > 200 && g > 200 && b > 200)) {
                        if (x < minX) minX = x;
                        if (x > maxX) maxX = x;
                    }
                }
                // sample color at minX
                int rgb = img.getRGB(minX, y);
                System.out.printf("  y=%3d: x=[%3d..%3d], color=(%d,%d,%d)%n", y, minX, maxX, (rgb>>16)&0xff, (rgb>>8)&0xff, rgb&0xff);
            }
        }
    }
}
