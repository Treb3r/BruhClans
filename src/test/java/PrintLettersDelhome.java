import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class PrintLettersDelhome {
    public static void main(String[] args) throws Exception {
        dumpChars("clan delhome/image_2026-10-26_19-08-16.png");
        dumpChars("clan delhome/image_2026-10-26_19-08-29.png");
    }

    static void dumpChars(String path) throws Exception {
        BufferedImage img = ImageIO.read(new File(path));
        System.out.println("==================================================");
        System.out.println("PATH: " + path);
        System.out.println("==================================================");
        int start = -1;
        for (int x = 0; x < img.getWidth(); x++) {
            boolean has = false;
            for (int y = 0; y < img.getHeight(); y++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;
                if ((r > 120 || g > 120 || b > 120) && !(x < 4 && r > 200)) {
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
                        if (r > 150 && g < 70 && b < 70) sb.append("R");
                        else if (r > 180 && g > 180 && b < 70) sb.append("Y");
                        else if (r > 150 && g > 150 && b > 150) sb.append("W");
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
