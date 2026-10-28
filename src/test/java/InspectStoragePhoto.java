import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class InspectStoragePhoto {
    public static void main(String[] args) throws Exception {
        BufferedImage img = ImageIO.read(new File("Хранилище/photo_2026-10-26_19-01-49.jpg"));
        System.out.println("Image size: " + img.getWidth() + "x" + img.getHeight());
        // Let's crop individual slots or save parts
        // The GUI is in the center
    }
}
