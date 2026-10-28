import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class Compare00And21 {
    public static void main(String[] args) throws Exception {
        BufferedImage img1 = ImageIO.read(new File("clan coords/photo_2026-10-26_19-06-00.jpg"));
        BufferedImage img2 = ImageIO.read(new File("clan coords/photo_2026-10-26_19-06-21.jpg"));
        int diff = 0;
        for (int y = 0; y < img1.getHeight(); y++) {
            for (int x = 0; x < img1.getWidth(); x++) {
                if (img1.getRGB(x, y) != img2.getRGB(x, y)) diff++;
            }
        }
        System.out.println("Diff count: " + diff);
    }
}
