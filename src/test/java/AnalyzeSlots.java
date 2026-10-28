import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class AnalyzeSlots {
    public static void main(String[] args) throws Exception {
        BufferedImage orig = ImageIO.read(new File("C:/Users/uilya/.gemini/antigravity/brain/23a70919-e9ec-47c1-bc92-15d21ec7f8e1/photo_2026-10-26_16-53-53.png"));
        BufferedImage curr = ImageIO.read(new File("C:/Users/uilya/.gemini/antigravity/brain/23a70919-e9ec-47c1-bc92-15d21ec7f8e1/.user_uploaded/media_1793127932688_2ecb3023.png"));
        
        System.out.println("--- CHECKING ORIG ---");
        // In photo_2026-10-26_16-53-53.png, let's find the bottom row items
        // Let's create an image that shows both side by side
        int w = Math.max(orig.getWidth(), curr.getWidth());
        int h = orig.getHeight() + curr.getHeight() + 20;
        BufferedImage combined = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g = combined.createGraphics();
        g.drawImage(orig, 0, 0, null);
        g.drawImage(curr, 0, orig.getHeight() + 20, null);
        g.dispose();
        ImageIO.write(combined, "png", new File("C:/Users/uilya/.gemini/antigravity/brain/23a70919-e9ec-47c1-bc92-15d21ec7f8e1/comparison.png"));
        System.out.println("comparison.png written");
    }
}
