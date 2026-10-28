import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class RenderDisbandImages {
    public static void main(String[] args) throws Exception {
        render("clan disband/image_2026-10-26_19-04-46.png");
        render("clan disband/image_2026-10-26_19-06-41.png");
        render("clan disband/photo_2026-10-26_19-04-19.jpg");
        render("clan disband/photo_2026-10-26_19-04-49.jpg");
    }

    static void render(String path) throws Exception {
        BufferedImage img = ImageIO.read(new File(path));
        System.out.println("==================================================");
        System.out.println("PATH: " + path + " (" + img.getWidth() + "x" + img.getHeight() + ")");
        System.out.println("==================================================");

        for (int chunk = 0; chunk < img.getWidth(); chunk += 80) {
            int end = Math.min(chunk + 80, img.getWidth());
            System.out.println("--- X: " + chunk + ".." + end + " ---");
            for (int y = 0; y < img.getHeight(); y++) {
                StringBuilder sb = new StringBuilder();
                for (int x = chunk; x < end; x++) {
                    int rgb = img.getRGB(x, y);
                    int r = (rgb >> 16) & 0xff;
                    int g = (rgb >> 8) & 0xff;
                    int b = rgb & 0xff;
                    if (x < 5 && r > 200 && g > 200 && b > 200) { sb.append(" "); continue; }
                    if (r > 150 && g < 70 && b < 70) sb.append("R");
                    else if (r > 180 && g > 70 && g < 170 && b < 70) sb.append("O");
                    else if (r > 180 && g > 180 && b < 70) sb.append("Y");
                    else if (r < 70 && g > 180 && b < 70) sb.append("G");
                    else if (r > 180 && g > 180 && b > 180) sb.append("W");
                    else if (r > 100 && g > 100 && b > 100) sb.append(".");
                    else sb.append(" ");
                }
                String line = sb.toString();
                if (!line.trim().isEmpty()) {
                    System.out.println(line);
                }
            }
        }
    }
}
