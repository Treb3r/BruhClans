import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class PrintCoordsText {
    public static void main(String[] args) throws Exception {
        dump("clan coords/photo_2026-10-26_19-06-00.jpg");
        dump("clan coords/image_2026-10-26_19-06-14.png");
        dump("clan coords/photo_2026-10-26_19-06-21.jpg");
    }
    static void dump(String path) throws Exception {
        BufferedImage img = ImageIO.read(new File(path));
        System.out.println("=== " + path + " (" + img.getWidth() + "x" + img.getHeight() + ") ===");
        for (int y = 0; y < img.getHeight(); y++) {
            StringBuilder sb = new StringBuilder();
            for (int x = 0; x < img.getWidth(); x++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;
                if (r > 160 && g < 70 && b < 70) sb.append("R");
                else if (r > 180 && g > 70 && g < 170 && b < 70) sb.append("O");
                else if (r > 180 && g > 180 && b < 70) sb.append("Y");
                else if (r < 70 && g > 180 && b < 70) sb.append("G");
                else if (r > 180 && g > 180 && b > 180) sb.append("W");
                else if (r > 90 && g > 90 && b > 90) sb.append(".");
                else sb.append(" ");
            }
            System.out.println(sb.toString());
        }
    }
}
