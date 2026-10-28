import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class PrintLine2Exact {
    public static void main(String[] args) throws Exception {
        BufferedImage img = ImageIO.read(new File("C:/Users/uilya/.gemini/antigravity/brain/23a70919-e9ec-47c1-bc92-15d21ec7f8e1/.user_uploaded/media_1793144063264_6f8d5ba9.png"));
        for (int chunk = 450; chunk < 600; chunk += 80) {
            int end = Math.min(chunk + 80, img.getWidth());
            System.out.println("--- X: " + chunk + ".." + end + " ---");
            for (int y = 21; y <= 35; y++) {
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
