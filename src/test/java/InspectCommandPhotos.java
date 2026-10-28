import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class InspectCommandPhotos {
    public static void main(String[] args) throws Exception {
        String[] dirs = {"clan chat или cc", "clan coords", "clan delhome"};
        for (String dirName : dirs) {
            File dir = new File(dirName);
            System.out.println("==================================================");
            System.out.println("DIRECTORY: " + dirName);
            System.out.println("==================================================");
            File[] files = dir.listFiles();
            if (files == null) continue;
            for (File f : files) {
                if (!f.getName().endsWith(".jpg") && !f.getName().endsWith(".png")) continue;
                BufferedImage img = ImageIO.read(f);
                System.out.println("  " + f.getName() + " -> " + img.getWidth() + "x" + img.getHeight());
            }
        }
    }
}
