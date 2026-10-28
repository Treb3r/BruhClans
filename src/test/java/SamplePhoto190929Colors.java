import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class SamplePhoto190929Colors {
    public static void main(String[] args) throws Exception {
        BufferedImage img = ImageIO.read(new File("clan chat или cc/photo_2026-10-26_19-09-29.jpg"));
        for (int x = 0; x < img.getWidth(); x += 5) {
            for (int y = 0; y < img.getHeight(); y++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;
                if (r > 60 || g > 60 || b > 60) {
                    System.out.printf("x=%3d, y=%2d: RGB=(%3d, %3d, %3d)%n", x, y, r, g, b);
                    break;
                }
            }
        }
    }
}
