import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class CheckS9S10 {
    public static void main(String[] args) throws Exception {
        BufferedImage s9o = ImageIO.read(new File("C:/Users/uilya/.gemini/antigravity/brain/23a70919-e9ec-47c1-bc92-15d21ec7f8e1/s9_orig.png"));
        BufferedImage s9c = ImageIO.read(new File("C:/Users/uilya/.gemini/antigravity/brain/23a70919-e9ec-47c1-bc92-15d21ec7f8e1/s9_curr.png"));
        BufferedImage s10o = ImageIO.read(new File("C:/Users/uilya/.gemini/antigravity/brain/23a70919-e9ec-47c1-bc92-15d21ec7f8e1/s10_orig.png"));
        BufferedImage s10c = ImageIO.read(new File("C:/Users/uilya/.gemini/antigravity/brain/23a70919-e9ec-47c1-bc92-15d21ec7f8e1/s10_curr.png"));
        
        System.out.println("s9_orig has white outline? " + hasWhite(s9o));
        System.out.println("s9_curr has white outline? " + hasWhite(s9c));
        System.out.println("s10_orig has white outline? " + hasWhite(s10o));
        System.out.println("s10_curr has white outline? " + hasWhite(s10c));
        
        // Also check if s9o has a bottle or is it "1"?
        System.out.println("s9_orig is bottle or digit? Let's check non-gray pixels in s9o: " + countNonGray(s9o));
        System.out.println("s9_curr non-gray pixels: " + countNonGray(s9c));
        System.out.println("s10_orig non-gray pixels: " + countNonGray(s10o));
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
    
    static int countNonGray(BufferedImage img) {
        int count = 0;
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF, g = (rgb >> 8) & 0xFF, b = rgb & 0xFF;
                if (Math.abs(r - g) > 20 || Math.abs(r - b) > 20) count++;
            }
        }
        return count;
    }
}
