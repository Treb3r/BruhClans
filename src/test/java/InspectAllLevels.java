import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class InspectAllLevels {
    public static void main(String[] args) throws Exception {
        File dir = new File("C:/Users/uilya/IdeaProjects/BruhClans/Уровень");
        File[] files = dir.listFiles((d, n) -> n.endsWith(".jpg") || n.endsWith(".png"));
        if (files == null) return;
        
        System.out.println("Found " + files.length + " files in Уровень");
        for (File f : files) {
            BufferedImage img = ImageIO.read(f);
            // Check if this image has an inventory open
            // Check title text
            System.out.println(f.getName() + " -> " + img.getWidth() + "x" + img.getHeight());
        }
    }
}
