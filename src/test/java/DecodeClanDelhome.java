import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class DecodeClanDelhome {
    public static void main(String[] args) throws Exception {
        decode("clan delhome/image_2026-10-26_19-07-53.png");
        decode("clan delhome/image_2026-10-26_19-08-16.png");
        decode("clan delhome/image_2026-10-26_19-08-29.png");
    }
    static void decode(String path) throws Exception {
        BufferedImage img = ImageIO.read(new File(path));
        System.out.println("==================================================");
        System.out.println("FILE: " + path + " (" + img.getWidth() + "x" + img.getHeight() + ")");
        System.out.println("==================================================");
        for (int y = 0; y < img.getHeight(); y++) {
            StringBuilder sb = new StringBuilder();
            for (int x = 0; x < img.getWidth(); x++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;
                if (r > 150 && g < 70 && b < 70) sb.append("R");
                else if (r > 180 && g > 70 && g < 170 && b < 70) sb.append("O");
                else if (r > 180 && g > 180 && b < 70) sb.append("Y");
                else if (r < 70 && g > 180 && b < 70) sb.append("G");
                else if (r > 180 && g > 180 && b > 180) sb.append("W");
                else if (r > 100 && g > 100 && b > 100) sb.append(".");
                else sb.append(" ");
            }
            System.out.println(sb.toString());
        }
    }
}
