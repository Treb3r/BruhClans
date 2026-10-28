import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class PrintDisbandButtons {
    public static void main(String[] args) throws Exception {
        BufferedImage img = ImageIO.read(new File("clan disband/photo_2026-10-26_19-04-19.jpg"));
        // Print row 18..36
        for (int chunk = 0; chunk < img.getWidth(); chunk += 80) {
            int end = Math.min(chunk + 80, img.getWidth());
            System.out.printf("--- Range %d..%d ---%n", chunk, end);
            for (int y = 16; y <= 32; y++) {
                StringBuilder sb = new StringBuilder();
                for (int x = chunk; x < end; x++) {
                    int rgb = img.getRGB(x, y);
                    int r = (rgb >> 16) & 0xff;
                    int g = (rgb >> 8) & 0xff;
                    int b = rgb & 0xff;
                    if (r > 150 && g < 70 && b < 70) sb.append("R");
                    else if (r < 70 && g > 180 && b < 70) sb.append("G");
                    else if (r > 180 && g > 180 && b < 70) sb.append("Y");
                    else if (r > 180 && g > 180 && b > 180) sb.append("W");
                    else sb.append(" ");
                }
                if (!sb.toString().trim().isEmpty()) System.out.println(sb.toString());
            }
        }
    }
}
