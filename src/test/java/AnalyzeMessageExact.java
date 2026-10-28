import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class AnalyzeMessageExact {
    public static void main(String[] args) throws Exception {
        dump("clan chat или cc/photo_2026-10-26_19-08-49.jpg");
        dump("clan chat или cc/photo_2026-10-26_19-09-29.jpg");
        dump("clan coords/photo_2026-10-26_19-06-00.jpg");
        dump("clan coords/image_2026-10-26_19-06-14.png");
        dump("clan coords/photo_2026-10-26_19-06-21.jpg");
        dump("clan delhome/image_2026-10-26_19-07-53.png");
        dump("clan delhome/image_2026-10-26_19-08-16.png");
        dump("clan delhome/image_2026-10-26_19-08-29.png");
    }
    
    static void dump(String path) throws Exception {
        File f = new File(path);
        if (!f.exists()) {
            System.out.println("NOT FOUND: " + path);
            return;
        }
        BufferedImage img = ImageIO.read(f);
        System.out.println("==================================================");
        System.out.println("FILE: " + path + " (" + img.getWidth() + "x" + img.getHeight() + ")");
        System.out.println("==================================================");
        
        // Scan horizontally to detect segments of color
        int lastR = -1, lastG = -1, lastB = -1;
        int segStart = 0;
        
        for (int x = 0; x < img.getWidth(); x++) {
            // Find max brightness pixel in this column
            int bestR = 0, bestG = 0, bestB = 0, maxBright = 0;
            for (int y = 0; y < img.getHeight(); y++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;
                int bright = r + g + b;
                if (bright > maxBright) {
                    maxBright = bright;
                    bestR = r; bestG = g; bestB = b;
                }
            }
            if (maxBright > 150) {
                // Classify color
                int cr = (bestR > 180) ? 255 : (bestR > 100 ? 170 : 0);
                int cg = (bestG > 180) ? 255 : (bestG > 100 ? 170 : 0);
                int cb = (bestB > 180) ? 255 : (bestB > 100 ? 170 : 0);
                // System.out.printf("x=%3d: RGB=(%3d, %3d, %3d)%n", x, bestR, bestG, bestB);
            }
        }
    }
}
