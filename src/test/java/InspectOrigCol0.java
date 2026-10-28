import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class InspectOrigCol0 {
    public static void main(String[] args) throws Exception {
        BufferedImage col0 = ImageIO.read(new File("C:/Users/uilya/.gemini/antigravity/brain/23a70919-e9ec-47c1-bc92-15d21ec7f8e1/orig_col0.png"));
        BufferedImage col1 = ImageIO.read(new File("C:/Users/uilya/.gemini/antigravity/brain/23a70919-e9ec-47c1-bc92-15d21ec7f8e1/orig_col1.png"));
        
        // Check what is in col0
        // Does col0 have the white flask (CMD 395) or does col1 have it?
        System.out.println("Col0 has white border? " + hasWhite(col0));
        System.out.println("Col1 has white border? " + hasWhite(col1));
        
        // Print colors in center of col0
        System.out.println("Col0 center RGB: " + Integer.toHexString(col0.getRGB(16, 16)));
        System.out.println("Col1 center RGB: " + Integer.toHexString(col1.getRGB(16, 16)));
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
}
