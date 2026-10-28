import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class DecodeImage2Exact {
    public static void main(String[] args) throws Exception {
        BufferedImage img = ImageIO.read(new File("clan delhome/image_2026-10-26_19-08-16.png"));
        // print columns with color details
        System.out.println("WIDTH: " + img.getWidth());
        // Let's print ASCII art with 1 char per pixel, but width is 399
        // Let's print in 4 lines of 100 width:
        for (int row = 0; row < 4; row++) {
            int xStart = row * 100;
            int xEnd = Math.min(xStart + 100, img.getWidth());
            System.out.printf("=== X: %d to %d ===%n", xStart, xEnd);
            for (int y = 3; y < 15; y++) {
                StringBuilder sb = new StringBuilder();
                for (int x = xStart; x < xEnd; x++) {
                    int rgb = img.getRGB(x, y);
                    int r = (rgb >> 16) & 0xff;
                    int g = (rgb >> 8) & 0xff;
                    int b = rgb & 0xff;
                    if (r > 180 && g < 70 && b < 70) sb.append("R");
                    else if (r > 180 && g > 180 && b < 70) sb.append("Y");
                    else if (r > 180 && g > 180 && b > 180) sb.append("W");
                    else if (r > 100 && g > 100 && b > 100) sb.append(".");
                    else sb.append(" ");
                }
                System.out.println(sb.toString());
            }
        }
    }
}
