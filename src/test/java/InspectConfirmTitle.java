import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class InspectConfirmTitle {
    public static void main(String[] args) throws Exception {
        BufferedImage img = ImageIO.read(new File("\u0425\u0440\u0430\u043d\u0438\u043b\u0438\u0449\u0435/photo_2026-10-27_23-24-37.jpg"));
        // Top banner area y=0..35
        for (int y = 0; y < 35; y++) {
            StringBuilder sb = new StringBuilder();
            boolean has = false;
            for (int x = 0; x < img.getWidth(); x++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;
                if (r > 150 && g < 80 && b < 80) { // Red text
                    sb.append("R"); has = true;
                } else if (r < 80 && g < 80 && b < 80) { // Dark title text?
                    // sb.append(".");
                }
            }
            if (has) System.out.printf("%2d: %s%n", y, sb.toString());
        }
        
        // Also check unique colors in the top 30 rows
        for (int y = 5; y < 25; y++) {
            for (int x = 10; x < img.getWidth() - 10; x++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;
                if (r > 180 && g < 60 && b < 60) {
                    System.out.printf("Red text at x=%d, y=%d: RGB=(%d, %d, %d)%n", x, y, r, g, b);
                    break;
                }
            }
        }
    }
}
