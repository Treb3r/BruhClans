import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class InspectDelhomeAll {
    public static void main(String[] args) throws Exception {
        dump("clan delhome/image_2026-10-26_19-08-16.png");
        dump("clan delhome/image_2026-10-26_19-08-29.png");
    }

    static void dump(String path) throws Exception {
        BufferedImage img = ImageIO.read(new File(path));
        System.out.println("==================================================");
        System.out.println("FILE: " + path + " width=" + img.getWidth());
        System.out.println("==================================================");
        
        // Print 80 cols at a time
        for (int x0 = 0; x0 < img.getWidth(); x0 += 80) {
            int x1 = Math.min(x0 + 80, img.getWidth());
            System.out.println("X=" + x0 + ".." + x1);
            for (int y = 0; y < img.getHeight(); y++) {
                StringBuilder sb = new StringBuilder();
                for (int x = x0; x < x1; x++) {
                    int rgb = img.getRGB(x, y);
                    int r = (rgb >> 16) & 0xff;
                    int g = (rgb >> 8) & 0xff;
                    int b = rgb & 0xff;
                    if (x < 5 && r > 200 && g > 200 && b > 200) { sb.append(" "); continue; }
                    if (r > 150 && g < 70 && b < 70) sb.append("R");
                    else if (r > 180 && g > 180 && b < 70) sb.append("Y");
                    else if (r > 150 && g > 150 && b > 150) sb.append("W");
                    else if (r > 80 && g > 80 && b > 80) sb.append(".");
                    else sb.append(" ");
                }
                String line = sb.toString();
                if (!line.trim().isEmpty()) System.out.println(line);
            }
        }
    }
}
