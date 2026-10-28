import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class InspectScreenshot {
    public static void main(String[] args) throws Exception {
        File f = new File("C:/Users/uilya/IdeaProjects/BruhClans/Уровень/photo_2026-10-26_16-53-53.jpg");
        BufferedImage img = ImageIO.read(f);
        System.out.println("Original screenshot size: " + img.getWidth() + "x" + img.getHeight());
        // Save cropped image of the inventory
        File out = new File("C:/Users/uilya/.gemini/antigravity/brain/23a70919-e9ec-47c1-bc92-15d21ec7f8e1/orig_levels_page1.png");
        ImageIO.write(img, "png", out);
        System.out.println("Saved to " + out.getAbsolutePath());
    }
}
