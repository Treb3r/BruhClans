import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class PrintP165932 {
    public static void main(String[] args) throws Exception {
        BufferedImage c0 = ImageIO.read(new File("C:/Users/uilya/.gemini/antigravity/brain/23a70919-e9ec-47c1-bc92-15d21ec7f8e1/p165932_col0.png"));
        BufferedImage c1 = ImageIO.read(new File("C:/Users/uilya/.gemini/antigravity/brain/23a70919-e9ec-47c1-bc92-15d21ec7f8e1/p165932_col1.png"));
        
        System.out.println("c0 has white border: " + hasWhite(c0));
        System.out.println("c1 has white border: " + hasWhite(c1));
        
        // Print colors in c0
        printAscii(c0);
        System.out.println("-----------------");
        printAscii(c1);
    }
    static boolean hasWhite(BufferedImage img) {
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF, g = (rgb >> 8) & 0xFF, b = rgb & 0xFF;
                if (r > 245 && g > 245 && b > 245) return true;
            }
        }
        return false;
    }
    static void printAscii(BufferedImage img) {
        for (int y = 0; y < img.getHeight(); y += 2) {
            StringBuilder sb = new StringBuilder();
            for (int x = 0; x < img.getWidth(); x += 2) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF, g = (rgb >> 8) & 0xFF, b = rgb & 0xFF;
                if (r > 230 && g > 230 && b > 230) sb.append("#");
                else if (r > 100 || g > 100 || b > 100) sb.append("*");
                else sb.append(" ");
            }
            System.out.println(sb.toString());
        }
    }
}
