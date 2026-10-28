import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class DiffBanners {
    public static void main(String[] args) throws Exception {
        BufferedImage orig = ImageIO.read(new File("C:/Users/uilya/.gemini/antigravity/brain/23a70919-e9ec-47c1-bc92-15d21ec7f8e1/orig_banner_crop.png"));
        BufferedImage curr = ImageIO.read(new File("C:/Users/uilya/.gemini/antigravity/brain/23a70919-e9ec-47c1-bc92-15d21ec7f8e1/banner_crop.png"));
        
        System.out.println("Orig: " + orig.getWidth() + "x" + orig.getHeight());
        System.out.println("Curr: " + curr.getWidth() + "x" + curr.getHeight());
        
        // Check if orig has the hopper/filter icon on the right
        // The right skull in curr has a hopper icon at x ~ 380..420, y ~ 40..80 (relative to 565x407)
        // Let's check orig at corresponding relative position (x ~ 0.7 * w)
        // Let's check pixels in orig around (0.7 * w, 0.15 * h)
        int xOrig = (int)(orig.getWidth() * (396.0 / 565.0));
        int yOrig = (int)(orig.getHeight() * (58.0 / 162.0));
        System.out.println("Checking orig around x=" + xOrig + ", y=" + yOrig);
        // Is there a hopper icon in orig?
        // Let's print out what glyph or texture is there in orig!
    }
}
