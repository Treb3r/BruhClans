import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class PrintRentLines {
    public static void main(String[] args) throws Exception {
        BufferedImage img = ImageIO.read(new File("target/crops/tooltip_rent.png"));
        for (int y = 0; y < img.getHeight(); y++) {
            StringBuilder sb = new StringBuilder();
            boolean has = false;
            for (int x = 0; x < img.getWidth(); x++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;
                if (r > 160 && g < 70 && b < 70) {
                    sb.append("R"); has = true;
                } else if (r > 200 && g > 70 && g < 160 && b < 60) {
                    sb.append("O"); has = true;
                } else if (r > 180 && g > 180 && b < 60) {
                    sb.append("Y"); has = true;
                } else if (r < 70 && g > 180 && b < 70) {
                    sb.append("G"); has = true;
                } else if (r > 180 && g > 180 && b > 180) {
                    sb.append("W"); has = true;
                } else {
                    sb.append(" ");
                }
            }
            if (has) System.out.printf("%2d: %s%n", y, sb.toString());
        }
    }
}
