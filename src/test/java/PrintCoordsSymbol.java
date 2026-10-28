import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class PrintCoordsSymbol {
    public static void main(String[] args) throws Exception {
        BufferedImage img = ImageIO.read(new File("clan coords/photo_2026-10-26_19-06-00.jpg"));
        for (int y = 0; y < 20; y++) {
            StringBuilder sb = new StringBuilder();
            for (int x = 8; x <= 34; x++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;
                if (r > 160 && g < 70 && b < 70) sb.append("#");
                else sb.append(".");
            }
            System.out.println(sb.toString());
        }
    }
}
