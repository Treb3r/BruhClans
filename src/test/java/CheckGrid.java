import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class CheckGrid {
    public static void main(String[] args) throws Exception {
        BufferedImage img = ImageIO.read(new File("C:/Users/uilya/.gemini/antigravity/brain/23a70919-e9ec-47c1-bc92-15d21ec7f8e1/.user_uploaded/media_1793127932688_2ecb3023.png"));
        int w = img.getWidth();
        int h = img.getHeight();
        System.out.println("Image: " + w + "x" + h);
        
        // Let's find the hopper icon
        // A hopper has dark gray / metallic pixels
        // Where is the hopper?
        // Let's crop the top banner and save it
        BufferedImage banner = img.getSubimage(0, 0, w, (int)(h * 0.4));
        ImageIO.write(banner, "png", new File("C:/Users/uilya/.gemini/antigravity/brain/23a70919-e9ec-47c1-bc92-15d21ec7f8e1/banner_crop.png"));
        System.out.println("Banner crop saved!");
    }
}
