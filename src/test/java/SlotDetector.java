import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class SlotDetector {
    public static void main(String[] args) throws Exception {
        BufferedImage orig = ImageIO.read(new File("C:/Users/uilya/.gemini/antigravity/brain/23a70919-e9ec-47c1-bc92-15d21ec7f8e1/photo_2026-10-26_16-53-53.png"));
        BufferedImage curr = ImageIO.read(new File("C:/Users/uilya/.gemini/antigravity/brain/23a70919-e9ec-47c1-bc92-15d21ec7f8e1/.user_uploaded/media_1793127932688_2ecb3023.png"));
        
        System.out.println("Orig size: " + orig.getWidth() + "x" + orig.getHeight());
        System.out.println("Curr size: " + curr.getWidth() + "x" + curr.getHeight());
        
        // Let's crop the bottom row of orig and curr
        // In orig (467x371), where is the bottom row?
        // Let's find white pixels near bottom
        // Let's crop out 6 rows of inventory from orig
    }
}
