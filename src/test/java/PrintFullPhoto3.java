import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class PrintFullPhoto3 {
    public static void main(String[] args) throws Exception {
        BufferedImage img = ImageIO.read(new File("C:/Users/uilya/.gemini/antigravity/brain/23a70919-e9ec-47c1-bc92-15d21ec7f8e1/.user_uploaded/media_1793144063264_6f8d5ba9.png"));
        // Print 3 line bands:
        // Band 0: y=3..18
        // Band 1: y=21..35
        // Band 2: y=39..54
        printBand(img, 3, 18, "LINE 1 (Header)");
        printBand(img, 21, 35, "LINE 2 (create)");
        printBand(img, 39, 54, "LINE 3 (top)");
    }

    static void printBand(BufferedImage img, int y1, int y2, String title) {
        System.out.println("==================================================");
        System.out.println(title);
        System.out.println("==================================================");
        for (int chunk = 0; chunk < img.getWidth(); chunk += 80) {
            int end = Math.min(chunk + 80, img.getWidth());
            System.out.println("--- X: " + chunk + ".." + end + " ---");
            for (int y = y1; y <= y2; y++) {
                StringBuilder sb = new StringBuilder();
                for (int x = chunk; x < end; x++) {
                    int rgb = img.getRGB(x, y);
                    int r = (rgb >> 16) & 0xff;
                    int g = (rgb >> 8) & 0xff;
                    int b = rgb & 0xff;
                    if (r > 150 && g < 70 && b < 70) sb.append("R");
                    else if (r > 180 && g > 180 && b < 70) sb.append("Y");
                    else if (r > 180 && g > 180 && b > 180) sb.append("W");
                    else if (r > 100 && g > 100 && b > 100) sb.append(".");
                    else sb.append(" ");
                }
                if (!sb.toString().trim().isEmpty()) System.out.println(sb.toString());
            }
        }
    }
}
