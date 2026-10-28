import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class OcrDisbandExact {
    public static void main(String[] args) throws Exception {
        dump("clan disband/image_2026-10-26_19-04-46.png");
        dump("clan disband/image_2026-10-26_19-06-41.png");
        dump("clan disband/photo_2026-10-26_19-04-19.jpg");
        dump("clan disband/photo_2026-10-26_19-04-49.jpg");
    }

    static void dump(String path) throws Exception {
        BufferedImage img = ImageIO.read(new File(path));
        System.out.println("==================================================");
        System.out.println("FILE: " + path + " (" + img.getWidth() + "x" + img.getHeight() + ")");
        System.out.println("==================================================");

        // Find segments
        for (int chunk = 0; chunk < img.getWidth(); chunk += 70) {
            int end = Math.min(chunk + 70, img.getWidth());
            System.out.printf("--- Range %d..%d ---%n", chunk, end);
            for (int y = 0; y < Math.min(img.getHeight(), 20); y++) {
                StringBuilder sb = new StringBuilder();
                for (int x = chunk; x < end; x++) {
                    int rgb = img.getRGB(x, y);
                    int r = (rgb >> 16) & 0xff;
                    int g = (rgb >> 8) & 0xff;
                    int b = rgb & 0xff;
                    if (x < 5 && r > 200 && g > 200 && b > 200) { sb.append(" "); continue; }
                    if (r > 150 && g < 70 && b < 70) sb.append("R");
                    else if (r > 180 && g > 180 && b < 70) sb.append("Y");
                    else if (r < 70 && g > 180 && b < 70) sb.append("G");
                    else if (r > 180 && g > 180 && b > 180) sb.append("W");
                    else sb.append(" ");
                }
                String s = sb.toString();
                if (!s.trim().isEmpty()) System.out.println(s);
            }
        }
    }
}
