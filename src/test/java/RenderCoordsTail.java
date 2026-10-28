import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class RenderCoordsTail {
    public static void main(String[] args) throws Exception {
        BufferedImage img = ImageIO.read(new File("clan coords/photo_2026-10-26_19-06-00.jpg"));
        for (int y = 0; y < img.getHeight(); y++) {
            StringBuilder sb = new StringBuilder();
            for (int x = 140; x < img.getWidth(); x++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;
                if (r > 160 && g < 70 && b < 70) sb.append("R");
                else if (r > 180 && g > 180 && b < 70) sb.append("Y");
                else if (r > 180 && g > 180 && b > 180) sb.append("W");
                else if (r > 100 && g > 100 && b > 100) sb.append(".");
                else sb.append(" ");
            }
            System.out.println(sb.toString());
        }
    }
}
