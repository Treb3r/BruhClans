import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class InspectAllStoragePhotos {
    public static void main(String[] args) throws Exception {
        File dir = new File("Хранилище");
        for (File f : dir.listFiles()) {
            if (!f.getName().endsWith(".jpg") && !f.getName().endsWith(".png")) continue;
            BufferedImage img = ImageIO.read(f);
            System.out.println(f.getName() + " -> " + img.getWidth() + "x" + img.getHeight());
        }
    }
}
