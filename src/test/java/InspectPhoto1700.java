import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class InspectPhoto1700 {
    public static void main(String[] args) throws Exception {
        // Let's inspect photo_2026-10-26_17-00-04.jpg
        BufferedImage img = ImageIO.read(new File("C:/Users/uilya/IdeaProjects/BruhClans/Уровень/photo_2026-10-26_17-00-04.jpg"));
        System.out.println("photo_2026-10-26_17-00-04.jpg size: " + img.getWidth() + "x" + img.getHeight());
        // Save cropped image to brain
        ImageIO.write(img, "png", new File("C:/Users/uilya/.gemini/antigravity/brain/23a70919-e9ec-47c1-bc92-15d21ec7f8e1/photo_170004.png"));
    }
}
