import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class CompareDetails {
    public static void main(String[] args) throws Exception {
        BufferedImage orig = ImageIO.read(new File("C:/Users/uilya/.gemini/antigravity/brain/23a70919-e9ec-47c1-bc92-15d21ec7f8e1/photo_2026-10-26_16-53-53.png"));
        BufferedImage curr = ImageIO.read(new File("C:/Users/uilya/.gemini/antigravity/brain/23a70919-e9ec-47c1-bc92-15d21ec7f8e1/.user_uploaded/media_1793127932688_2ecb3023.png"));
        
        // Find top-left of the inventory container in orig vs curr
        // The container starts with the white border
        System.out.println("Orig: " + orig.getWidth() + "x" + orig.getHeight());
        System.out.println("Curr: " + curr.getWidth() + "x" + curr.getHeight());
        
        // Also let's check all other photos: photo_2026-10-26_16-54-09, photo_2026-10-26_16-54-18, etc.
        for (String fName : new String[]{
            "photo_2026-10-26_16-53-53.png",
            "photo_2026-10-26_16-54-09.png",
            "photo_2026-10-26_16-54-18.png",
            "photo_2026-10-26_16-54-29.png"
        }) {
            File f = new File("C:/Users/uilya/.gemini/antigravity/brain/23a70919-e9ec-47c1-bc92-15d21ec7f8e1/" + fName);
            if (f.exists()) {
                BufferedImage bi = ImageIO.read(f);
                System.out.println(fName + " loaded: " + bi.getWidth() + "x" + bi.getHeight());
            }
        }
    }
}
