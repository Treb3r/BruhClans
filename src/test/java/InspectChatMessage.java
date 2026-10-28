import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class InspectChatMessage {
    public static void main(String[] args) throws Exception {
        BufferedImage img = ImageIO.read(new File("Хранилище/image_2026-10-27_23-24-24.png"));
        // print columns that have high brightness
        for (int x = 0; x < img.getWidth(); x++) {
            int maxBrightness = 0;
            int bestR = 0, bestG = 0, bestB = 0;
            for (int y = 0; y < img.getHeight(); y++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;
                int bright = r + g + b;
                if (bright > maxBrightness) {
                    maxBrightness = bright;
                    bestR = r; bestG = g; bestB = b;
                }
            }
            if (maxBrightness > 250 && x % 4 == 0) {
                System.out.printf("x=%3d: RGB=(%3d, %3d, %3d)%n", x, bestR, bestG, bestB);
            }
        }
    }
}
