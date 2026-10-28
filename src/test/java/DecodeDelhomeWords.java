import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class DecodeDelhomeWords {
    public static void main(String[] args) throws Exception {
        dumpRows("clan delhome/image_2026-10-26_19-07-53.png");
        dumpRows("clan delhome/image_2026-10-26_19-08-16.png");
        dumpRows("clan delhome/image_2026-10-26_19-08-29.png");
    }

    static void dumpRows(String path) throws Exception {
        BufferedImage img = ImageIO.read(new File(path));
        System.out.println("==================================================");
        System.out.println("FILE: " + path);
        System.out.println("==================================================");

        // Find active Y range
        int minY = img.getHeight(), maxY = 0;
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;
                if ((r > 120 || g > 120 || b > 120) && !(x < 4 && r > 200)) {
                    if (y < minY) minY = y;
                    if (y > maxY) maxY = y;
                }
            }
        }

        // Print character columns grouped
        int startX = -1;
        for (int x = 0; x < img.getWidth(); x++) {
            boolean colHas = false;
            for (int y = minY; y <= maxY; y++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;
                if ((r > 120 || g > 120 || b > 120) && !(x < 4 && r > 200)) {
                    colHas = true;
                    break;
                }
            }
            if (colHas && startX == -1) startX = x;
            else if (!colHas && startX != -1) {
                // print glyph
                int w = x - startX;
                System.out.printf("[%3d..%3d] (w=%2d): ", startX, x - 1, w);
                // determine dominant color
                int maxR = 0, maxG = 0, maxB = 0;
                for (int gx = startX; gx < x; gx++) {
                    for (int gy = minY; gy <= maxY; gy++) {
                        int rgb = img.getRGB(gx, gy);
                        int r = (rgb >> 16) & 0xff;
                        int g = (rgb >> 8) & 0xff;
                        int b = rgb & 0xff;
                        if (r + g + b > maxR + maxG + maxB) {
                            maxR = r; maxG = g; maxB = b;
                        }
                    }
                }
                String col = "W";
                if (maxR > 180 && maxG < 60 && maxB < 60) col = "RED";
                else if (maxR > 180 && maxG > 180 && maxB < 60) col = "YELLOW";
                else if (maxR > 180 && maxG > 60 && maxG < 160 && maxB < 60) col = "ORANGE";
                else if (maxR < 60 && maxG > 180 && maxB < 60) col = "GREEN";
                else if (maxR > 120 && maxG > 120 && maxB > 120) col = "WHITE";
                System.out.println("color=" + col);
                startX = -1;
            }
        }
    }
}
