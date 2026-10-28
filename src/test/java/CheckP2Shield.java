import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class CheckP2Shield {
    public static void main(String[] args) throws Exception {
        BufferedImage img = ImageIO.read(new File("C:/Users/uilya/IdeaProjects/BruhClans/Уровень/photo_2026-10-26_16-54-09.jpg"));
        int shieldSumX = 0, shieldSumY = 0, shieldCount = 0;
        int doorSumX = 0, doorSumY = 0, doorCount = 0;
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF, g = (rgb >> 8) & 0xFF, b = rgb & 0xFF;
                if (y < img.getHeight() * 0.6 && g > 180 && r < 100 && b < 100) {
                    shieldSumX += x; shieldSumY += y; shieldCount++;
                }
                if (y > img.getHeight() * 0.6 && r > 200 && g < 60 && b < 60) {
                    doorSumX += x; doorSumY += y; doorCount++;
                }
            }
        }
        System.out.println("Shield X: " + (shieldSumX / shieldCount));
        System.out.println("Door X: " + (doorSumX / doorCount));
    }
}
