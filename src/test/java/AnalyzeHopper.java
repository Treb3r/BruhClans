import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class AnalyzeHopper {
    public static void main(String[] args) throws Exception {
        BufferedImage ch = ImageIO.read(new File("C:/Users/uilya/.gemini/antigravity/brain/23a70919-e9ec-47c1-bc92-15d21ec7f8e1/curr_hopper.png"));
        BufferedImage oh = ImageIO.read(new File("C:/Users/uilya/.gemini/antigravity/brain/23a70919-e9ec-47c1-bc92-15d21ec7f8e1/orig_region.png"));
        
        System.out.println("Curr hopper size: " + ch.getWidth() + "x" + ch.getHeight());
        System.out.println("Orig region size: " + oh.getWidth() + "x" + oh.getHeight());
        
        // Find if there is a hopper in oh
        // A hopper has pixels of dark gray / funnel shape
        // In ch, where is the hopper?
        // Let's print colors of ch vs oh
    }
}
