import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class PrintDelhome2Tail {
    public static void main(String[] args) throws Exception {
        BufferedImage img = ImageIO.read(new File("clan delhome/image_2026-10-26_19-08-16.png"));
        int start = -1;
        for (int x = 290; x < img.getWidth(); x++) {
            boolean has = false;
            for (int y = 0; y < img.getHeight(); y++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;
                if (r > 120 || g > 120 || b > 120) {
                    has = true;
                    break;
                }
            }
            if (has && start == -1) {
                start = x;
            } else if (!has && start != -1) {
                int w = x - start;
                System.out.println("GLYPH at " + start + ".." + (x-1) + " (w=" + w + "):");
                for (int y = 2; y < img.getHeight() - 2; y++) {
                    StringBuilder sb = new StringBuilder();
                    for (int gx = start; gx < x; gx++) {
                        int rgb = img.getRGB(gx, y);
                        int r = (rgb >> 16) & 0xff;
                        int g = (rgb >> 8) & 0xff;
                        int b = rgb & 0xff;
                        if (r > 180 && g > 180 && b > 180) sb.append("W");
                        else sb.append(" ");
                    }
                    if (!sb.toString().trim().isEmpty()) {
                        System.out.println("  |" + sb.toString() + "|");
                    }
                }
                start = -1;
            }
        }
    }
}
