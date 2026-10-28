import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class ScanLevelsScreenshots {
    public static void main(String[] args) throws Exception {
        File dir = new File("Уровень");
        File[] files = dir.listFiles((d, name) -> name.endsWith(".jpg") || name.endsWith(".png"));
        if (files == null) return;
        for (File f : files) {
            BufferedImage img = ImageIO.read(f);
            System.out.println(f.getName() + " -> " + img.getWidth() + "x" + img.getHeight());
        }
    }
}
