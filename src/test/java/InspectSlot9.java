import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class InspectSlot9 {
    public static void main(String[] args) throws Exception {
        BufferedImage orig = ImageIO.read(new File("C:/Users/uilya/.gemini/antigravity/brain/23a70919-e9ec-47c1-bc92-15d21ec7f8e1/photo_2026-10-26_16-53-53.png"));
        BufferedImage curr = ImageIO.read(new File("C:/Users/uilya/.gemini/antigravity/brain/23a70919-e9ec-47c1-bc92-15d21ec7f8e1/.user_uploaded/media_1793127932688_2ecb3023.png"));
        
        // Slot 9 is row 1, col 0
        int ox = (int)Math.round(227 - 4 * 36.25);
        int oy = 174;
        BufferedImage s9orig = orig.getSubimage(ox - 16, oy - 16, 32, 32);
        ImageIO.write(s9orig, "png", new File("C:/Users/uilya/.gemini/antigravity/brain/23a70919-e9ec-47c1-bc92-15d21ec7f8e1/s9_orig.png"));
        
        int cx = (int)Math.round(260 - 4 * 36.5);
        int cy = 201;
        BufferedImage s9curr = curr.getSubimage(cx - 16, cy - 16, 32, 32);
        ImageIO.write(s9curr, "png", new File("C:/Users/uilya/.gemini/antigravity/brain/23a70919-e9ec-47c1-bc92-15d21ec7f8e1/s9_curr.png"));
        
        // Also slot 10 (row 1, col 1)
        BufferedImage s10orig = orig.getSubimage(ox + 36 - 16, oy - 16, 32, 32);
        ImageIO.write(s10orig, "png", new File("C:/Users/uilya/.gemini/antigravity/brain/23a70919-e9ec-47c1-bc92-15d21ec7f8e1/s10_orig.png"));
        
        BufferedImage s10curr = curr.getSubimage(cx + 36 - 16, cy - 16, 32, 32);
        ImageIO.write(s10curr, "png", new File("C:/Users/uilya/.gemini/antigravity/brain/23a70919-e9ec-47c1-bc92-15d21ec7f8e1/s10_curr.png"));
        
        System.out.println("Saved s9_orig, s9_curr, s10_orig, s10_curr");
    }
}
