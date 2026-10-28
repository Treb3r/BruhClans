import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;

public class OcrDelhomeDirect {
    public static void main(String[] args) throws Exception {
        ocr("clan delhome/image_2026-10-26_19-07-53.png");
        ocr("clan delhome/image_2026-10-26_19-08-16.png");
        ocr("clan delhome/image_2026-10-26_19-08-29.png");
    }

    static void ocr(String path) throws Exception {
        BufferedImage img = ImageIO.read(new File(path));
        System.out.println("==================================================");
        System.out.println("FILE: " + path);
        System.out.println("==================================================");

        // Find connected components or columns with pixels
        boolean[] hasPixel = new boolean[img.getWidth()];
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;
                if ((r > 100 || g > 100 || b > 100) && !(r > 200 && g > 200 && b > 200 && x < 5)) { // ignore scrollbar or left artifact if any
                    hasPixel[x] = true;
                    break;
                }
            }
        }

        // Print column segments
        int start = -1;
        for (int x = 0; x < img.getWidth(); x++) {
            if (hasPixel[x] && start == -1) {
                start = x;
            } else if (!hasPixel[x] && start != -1) {
                printChar(img, start, x - 1);
                start = -1;
            }
        }
        if (start != -1) {
            printChar(img, start, img.getWidth() - 1);
        }
    }

    static void printChar(BufferedImage img, int x1, int x2) {
        if (x2 - x1 > 30) {
            // Probably multiple characters stuck together or long symbol, let's print width
        }
        System.out.println("GLYPH [" + x1 + ".." + x2 + "] (w=" + (x2 - x1 + 1) + "):");
        for (int y = 2; y < img.getHeight() - 2; y++) {
            StringBuilder sb = new StringBuilder();
            for (int x = x1; x <= x2; x++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;
                if (r > 150 && g < 70 && b < 70) sb.append("R");
                else if (r > 180 && g > 70 && g < 170 && b < 70) sb.append("O");
                else if (r > 180 && g > 180 && b < 70) sb.append("Y");
                else if (r < 70 && g > 180 && b < 70) sb.append("G");
                else if (r > 180 && g > 180 && b > 180) sb.append("W");
                else if (r > 100 && g > 100 && b > 100) sb.append(".");
                else sb.append(" ");
            }
            if (!sb.toString().trim().isEmpty()) {
                System.out.println("  " + sb.toString());
            }
        }
    }
}
