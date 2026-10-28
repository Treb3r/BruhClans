import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class CheckHopper {
    public static void main(String[] args) throws Exception {
        BufferedImage orig = ImageIO.read(new File("C:/Users/uilya/.gemini/antigravity/brain/23a70919-e9ec-47c1-bc92-15d21ec7f8e1/orig_levels_page1.png"));
        BufferedImage curr = ImageIO.read(new File("C:/Users/uilya/.gemini/antigravity/brain/23a70919-e9ec-47c1-bc92-15d21ec7f8e1/.user_uploaded/media_1793127932688_2ecb3023.png"));
        
        // In curr, where is the hopper?
        // Crop the hopper region from curr
        // Look at curr banner around top right
        // The image is 565x407. Top banner is y=0..120.
        // Skull is at x ~ 370..450, y ~ 40..120.
        BufferedImage currHopper = curr.getSubimage(370, 40, 80, 80);
        ImageIO.write(currHopper, "png", new File("C:/Users/uilya/.gemini/antigravity/brain/23a70919-e9ec-47c1-bc92-15d21ec7f8e1/curr_hopper.png"));
        System.out.println("curr_hopper.png saved!");
        
        // Now check orig at relative position
        double scaleX = (double)orig.getWidth() / curr.getWidth();
        double scaleY = (double)orig.getHeight() / curr.getHeight();
        int ox = (int)(370 * scaleX);
        int oy = (int)(40 * scaleY);
        int ow = (int)(80 * scaleX);
        int oh = (int)(80 * scaleY);
        BufferedImage origRegion = orig.getSubimage(ox, oy, ow, oh);
        ImageIO.write(origRegion, "png", new File("C:/Users/uilya/.gemini/antigravity/brain/23a70919-e9ec-47c1-bc92-15d21ec7f8e1/orig_region.png"));
        System.out.println("orig_region.png saved!");
    }
}
