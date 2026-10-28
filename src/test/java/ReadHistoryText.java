import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class ReadHistoryText {
    public static void main(String[] args) throws Exception {
        BufferedImage img = ImageIO.read(new File("clan history/photo_2026-10-26_19-11-34.jpg"));
        // Band 1: y=20..35
        // Band 2: y=38..53
        // Band 3: y=56..71
        // Band 4: y=74..89
        // Band 5: y=92..107
        // Band 6: y=110..125
        // Band 7: y=128..143
        readBand(img, 20, 35, "ENTRY 1");
        readBand(img, 38, 53, "ENTRY 2");
        readBand(img, 56, 71, "ENTRY 3");
        readBand(img, 74, 89, "ENTRY 4");
        readBand(img, 92, 107, "ENTRY 5");
        readBand(img, 110, 125, "ENTRY 6");
        readBand(img, 128, 143, "ENTRY 7 (Pagination?)");
    }

    static void readBand(BufferedImage img, int y1, int y2, String label) {
        System.out.println("================ " + label + " ================");
        // Print character segments
        int start = -1;
        for (int x = 0; x < img.getWidth() - 20; x++) {
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
                // sample color
                int maxR = 0, maxG = 0, maxB = 0;
                for (int gx = start; gx < x; gx++) {
                    for (int y = y1; y <= y2; y++) {
                        int rgb = img.getRGB(gx, y);
                        int r = (rgb >> 16) & 0xff, g = (rgb >> 8) & 0xff, b = rgb & 0xff;
                        if (r + g + b > maxR + maxG + maxB) {
                            maxR = r; maxG = g; maxB = b;
                        }
                    }
                }
                String c = "W";
                if (maxR > 180 && maxG < 70 && maxB < 70) c = "R";
                else if (maxR > 180 && maxG > 70 && maxG < 170 && maxB < 70) c = "O";
                else if (maxR > 180 && maxG > 180 && maxB < 70) c = "Y";
                else if (maxR < 70 && maxG > 180 && maxB < 70) c = "G";
                else if (maxR > 180 && maxG > 180 && maxB > 180) c = "W";
                else if (maxR > 100 && maxG > 100 && maxB > 100) c = "GRAY";

                System.out.printf("[%d..%d w=%d %s] ", start, x-1, (x-start), c);
                start = -1;
            }
        }
        System.out.println();
    }
}
