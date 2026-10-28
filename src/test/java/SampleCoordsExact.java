import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class SampleCoordsExact {
    public static void main(String[] args) throws Exception {
        BufferedImage img = ImageIO.read(new File("clan coords/photo_2026-10-26_19-06-00.jpg"));
        for (int x = 0; x < img.getWidth(); x += 3) {
            int maxBright = 0, bestR = 0, bestG = 0, bestB = 0;
            for (int y = 0; y < img.getHeight(); y++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;
                if (r + g + b > maxBright) {
                    maxBright = r + g + b;
                    bestR = r; bestG = g; bestB = b;
                }
            }
            if (maxBright > 150) {
                System.out.printf("x=%3d: RGB=(%3d, %3d, %3d)%n", x, bestR, bestG, bestB);
            }
        }
    }
}
