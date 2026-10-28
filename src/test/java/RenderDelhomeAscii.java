import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class RenderDelhomeAscii {
    public static void main(String[] args) throws Exception {
        render("clan delhome/image_2026-10-26_19-07-53.png");
        render("clan delhome/image_2026-10-26_19-08-16.png");
        render("clan delhome/image_2026-10-26_19-08-29.png");
    }

    static void render(String path) throws Exception {
        BufferedImage img = ImageIO.read(new File(path));
        System.out.println("==================================================");
        System.out.println("FILE: " + path);
        System.out.println("==================================================");

        int minY = 2, maxY = img.getHeight() - 3;

        // Print rows of 50 columns
        for (int chunk = 0; chunk < img.getWidth(); chunk += 60) {
            int end = Math.min(chunk + 60, img.getWidth());
            System.out.printf("--- Chunk %d..%d ---%n", chunk, end);
            for (int y = minY; y <= maxY; y++) {
                StringBuilder sb = new StringBuilder();
                for (int x = chunk; x < end; x++) {
                    int rgb = img.getRGB(x, y);
                    int r = (rgb >> 16) & 0xff;
                    int g = (rgb >> 8) & 0xff;
                    int b = rgb & 0xff;
                    if (x < 5 && r > 200 && g > 200 && b > 200) {
                        sb.append(" ");
                        continue;
                    }
                    if (r > 150 && g < 70 && b < 70) sb.append("R");
                    else if (r > 180 && g > 70 && g < 170 && b < 70) sb.append("O");
                    else if (r > 180 && g > 180 && b < 70) sb.append("Y");
                    else if (r < 70 && g > 180 && b < 70) sb.append("G");
                    else if (r > 180 && g > 180 && b > 180) sb.append("W");
                    else if (r > 100 && g > 100 && b > 100) sb.append(".");
                    else sb.append(" ");
                }
                String s = sb.toString();
                if (!s.trim().isEmpty()) {
                    System.out.println(s);
                }
            }
        }
    }
}
