import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class InspectGrayLock {
    public static void main(String[] args) throws Exception {
        BufferedImage img = ImageIO.read(new File("\u0425\u0440\u0430\u043d\u0438\u043b\u0438\u0449\u0435/photo_2026-10-26_19-01-49.jpg"));
        // Crop slot 10 (col 1, row 1)
        // Center around (153, 202)
        BufferedImage crop = img.getSubimage(153 - 10, 202 - 10, 20, 20);
        ImageIO.write(crop, "png", new File("target/crops/slot10_gray_lock.png"));
        System.out.println("Saved slot10_gray_lock.png");
        
        // Print pixel colors
        for (int y = 0; y < crop.getHeight(); y++) {
            StringBuilder sb = new StringBuilder();
            for (int x = 0; x < crop.getWidth(); x++) {
                int rgb = crop.getRGB(x, y);
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;
                if (r > 30 && r < 90 && g > 30 && g < 90 && b > 40 && b < 100) {
                    sb.append("##");
                } else {
                    sb.append("..");
                }
            }
            System.out.println(sb.toString());
        }
    }
}
