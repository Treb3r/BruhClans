import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class InspectPhoto1And2 {
    public static void main(String[] args) throws Exception {
        BufferedImage img1 = ImageIO.read(new File("C:/Users/uilya/.gemini/antigravity/brain/23a70919-e9ec-47c1-bc92-15d21ec7f8e1/.user_uploaded/media_1793143975745_886106b7.png"));
        BufferedImage img2 = ImageIO.read(new File("C:/Users/uilya/.gemini/antigravity/brain/23a70919-e9ec-47c1-bc92-15d21ec7f8e1/.user_uploaded/media_1793143987028_ea2791c1.png"));
        System.out.println("IMG 1: " + img1.getWidth() + "x" + img1.getHeight());
        System.out.println("IMG 2: " + img2.getWidth() + "x" + img2.getHeight());
    }
}
