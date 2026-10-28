import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class DecodeHistoryWords {
    public static void main(String[] args) throws Exception {
        BufferedImage img = ImageIO.read(new File("clan history/photo_2026-10-26_19-11-34.jpg"));
        // Print text in each line by slicing
        printRowGlyphs(img, 2, 17, "HEADER");
        printRowGlyphs(img, 20, 35, "LINE 1");
        printRowGlyphs(img, 38, 53, "LINE 2");
        printRowGlyphs(img, 56, 71, "LINE 3");
        printRowGlyphs(img, 74, 89, "LINE 4");
        printRowGlyphs(img, 110, 125, "LINE 5");
        printRowGlyphs(img, 128, 143, "PAGINATION");
    }

    static void printRowGlyphs(BufferedImage img, int y1, int y2, String title) {
        System.out.println("==================================================");
        System.out.println(title);
        System.out.println("==================================================");
        int start = -1;
        for (int x = 0; x < img.getWidth() - 15; x++) {
            boolean has = false;
            for (int y = y1; y <= y2; y++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xff, g = (rgb >> 8) & 0xff, b = rgb & 0xff;
                if ((r > 120 || g > 120 || b > 120) && !(x < 4 && r > 200)) {
                    has = true; break;
                }
            }
            if (has && start == -1) start = x;
            else if (!has && start != -1) {
                // print glyph width and ASCII
                System.out.printf("[%d..%d w=%d]:%n", start, x-1, (x-start));
                for (int y = y1; y <= y2; y++) {
                    StringBuilder sb = new StringBuilder();
                    for (int gx = start; gx < x; gx++) {
                        int rgb = img.getRGB(gx, y);
                        int r = (rgb >> 16) & 0xff, g = (rgb >> 8) & 0xff, b = rgb & 0xff;
                        if (r > 150 && g < 70 && b < 70) sb.append("R");
                        else if (r > 180 && g > 70 && g < 170 && b < 70) sb.append("O");
                        else if (r > 180 && g > 180 && b < 70) sb.append("Y");
                        else if (r < 70 && g > 180 && b < 70) sb.append("G");
                        else if (r > 180 && g > 180 && b > 180) sb.append("W");
                        else sb.append(" ");
                    }
                    if (!sb.toString().trim().isEmpty()) System.out.println("  |" + sb.toString() + "|");
                }
                start = -1;
            }
        }
    }
}
