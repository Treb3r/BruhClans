import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class StorageGridAnalyzer {
    public static void main(String[] args) throws Exception {
        BufferedImage img = ImageIO.read(new File("Хранилище/photo_2026-10-26_19-01-49.jpg"));
        System.out.println("Storage photo size: " + img.getWidth() + "x" + img.getHeight());
        
        File outDir = new File("target/crops");
        outDir.mkdirs();
        ImageIO.write(img, "png", new File(outDir, "full_storage.png"));
        System.out.println("Saved full_storage.png");
    }
}
