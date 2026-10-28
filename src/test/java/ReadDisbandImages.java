import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class ReadDisbandImages {
    public static void main(String[] args) throws Exception {
        readImg("clan disband/image_2026-10-26_19-04-46.png");
        readImg("clan disband/image_2026-10-26_19-06-41.png");
        readImg("clan disband/photo_2026-10-26_19-04-19.jpg");
        readImg("clan disband/photo_2026-10-26_19-04-49.jpg");
    }

    static void readImg(String path) throws Exception {
        BufferedImage img = ImageIO.read(new File(path));
        System.out.println("==================================================");
        System.out.println("PATH: " + path + " (" + img.getWidth() + "x" + img.getHeight() + ")");
        System.out.println("==================================================");
        // Print character segments
        int start = -1;
        for (int x = 0; x < img.getWidth(); x++) {
            boolean has = false;
            for (int y = 0; y < Math.min(img.getHeight(), 20); y++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xff, g = (rgb >> 8) & 0xff, b = rgb & 0xff;
                if ((r > 120 || g > 120 || b > 120) && !(x < 4 && r > 200)) {
                    has = true; break;
                }
            }
            if (has && start == -1) start = x;
            else if (!has && start != -1) {
                // print glyph width and dominant color
                int maxR = 0, maxG = 0, maxB = 0;
                for (int gx = start; gx < x; gx++) {
                    for (int y = 0; y < Math.min(img.getHeight(), 20); y++) {
                        int rgb = img.getRGB(gx, y);
                        int r = (rgb >> 16) & 0xff, g = (rgb >> 8) & 0xff, b = rgb & 0xff;
                        if (r + g + b > maxR + maxG + maxB) {
                            maxR = r; maxG = g; maxB = b;
                        }
                    }
                }
                String color = "W";
                if (maxR > 180 && maxG < 70 && maxB < 70) color = "RED";
                else if (maxR > 180 && maxG > 180 && maxB < 70) color = "YELLOW";
                else if (maxR < 70 && maxG > 180 && maxB < 70) color = "GREEN";
                else if (maxR > 180 && maxG > 180 && maxB > 180) color = "WHITE";

                System.out.printf("[%d..%d w=%d col=%s]%n", start, x-1, (x-start), color);
                start = -1;
            }
        }
    }
}
