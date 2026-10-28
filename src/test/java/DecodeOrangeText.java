import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class DecodeOrangeText {
    public static void main(String[] args) throws Exception {
        BufferedImage img = ImageIO.read(new File("clan chat или cc/photo_2026-10-26_19-09-29.jpg"));
        for (int y = 0; y < img.getHeight(); y++) {
            StringBuilder sb = new StringBuilder();
            for (int x = 65; x < 205; x++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;
                if (r > 150 && g > 60 && g < 150 && b < 60) {
                    sb.append("#");
                } else {
                    sb.append(" ");
                }
            }
            System.out.println(sb.toString());
        }
    }
}
