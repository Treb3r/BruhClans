import java.io.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class SampleScreenshotColors {
    public static void main(String[] args) throws Exception {
        BufferedImage img = ImageIO.read(new File("C:/Users/uilya/.gemini/antigravity/brain/23a70919-e9ec-47c1-bc92-15d21ec7f8e1/.user_uploaded/media_1793137399869_6af83215.png"));
        System.out.println("Image size: " + img.getWidth() + "x" + img.getHeight());

        // Let's sample colors across the lines
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;
                // find prominent non-gray/non-white colors
                if (r > 180 && g < 80 && b < 80) { // red
                    // sample
                }
            }
        }

        // Print colors at specific regions:
        // Red "[✖] Ошибка!"
        printHexAt("Red text", img, 15, 6);
        // White "У Вас..."
        printHexAt("White text", img, 100, 6);
        // Green "Токенов!"
        printHexAt("Green text", img, 230, 6);
        // Yellow "FunTime.su"
        printHexAt("Yellow text", img, 230, 16);
    }

    static void printHexAt(String label, BufferedImage img, int x, int y) {
        int maxR = 0, maxG = 0, maxB = 0;
        for (int dy = -3; dy <= 3; dy++) {
            for (int dx = -3; dx <= 3; dx++) {
                int px = x + dx;
                int py = y + dy;
                if (px >= 0 && px < img.getWidth() && py >= 0 && py < img.getHeight()) {
                    int rgb = img.getRGB(px, py);
                    int r = (rgb >> 16) & 0xff;
                    int g = (rgb >> 8) & 0xff;
                    int b = rgb & 0xff;
                    if (r + g + b > maxR + maxG + maxB) {
                        maxR = r; maxG = g; maxB = b;
                    }
                }
            }
        }
        System.out.printf("%s at (~%d,~%d): #%02X%02X%02X (R=%d, G=%d, B=%d)%n", label, x, y, maxR, maxG, maxB, maxR, maxG, maxB);
    }
}
