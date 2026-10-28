import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class PrintLettersFromCoords {
    public static void main(String[] args) throws Exception {
        BufferedImage img = ImageIO.read(new File("clan coords/photo_2026-10-26_19-06-00.jpg"));
        // Slice characters
        int startX = -1;
        for (int x = 140; x < img.getWidth(); x++) {
            boolean has = false;
            for (int y = 0; y < img.getHeight(); y++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;
                if (r > 80 || g > 80 || b > 80) has = true;
            }
            if (has) {
                if (startX == -1) startX = x;
            } else {
                if (startX != -1) {
                    printSlice(img, startX, x - 1);
                    startX = -1;
                }
            }
        }
    }
    static void printSlice(BufferedImage img, int x1, int x2) {
        System.out.printf("--- Slice [%d..%d] (w=%d) ---%n", x1, x2, x2 - x1 + 1);
        for (int y = 2; y <= 17; y++) {
            StringBuilder sb = new StringBuilder();
            for (int x = x1; x <= x2; x++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;
                if (r > 180 && g > 180 && b < 70) sb.append("Y");
                else if (r > 100 && g > 100 && b > 100) sb.append(".");
                else sb.append(" ");
            }
            System.out.println(sb.toString());
        }
    }
}
