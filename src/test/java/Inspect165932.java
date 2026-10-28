import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class Inspect165932 {
    public static void main(String[] args) throws Exception {
        BufferedImage img = ImageIO.read(new File("C:/Users/uilya/IdeaProjects/BruhClans/Уровень/photo_2026-10-26_16-59-32.jpg"));
        ImageIO.write(img, "png", new File("C:/Users/uilya/.gemini/antigravity/brain/23a70919-e9ec-47c1-bc92-15d21ec7f8e1/photo_165932.png"));
        System.out.println("photo_165932 saved (" + img.getWidth() + "x" + img.getHeight() + ")");
    }
}
