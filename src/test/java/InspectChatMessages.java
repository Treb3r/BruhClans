import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;

public class InspectChatMessages {
    public static void main(String[] args) throws Exception {
        String[] dirs = {"clan chat или cc", "clan coords", "clan delhome"};
        for (String dirName : dirs) {
            File dir = new File(dirName);
            File[] files = dir.listFiles();
            if (files == null) continue;
            for (File f : files) {
                if (!f.getName().endsWith(".jpg") && !f.getName().endsWith(".png")) continue;
                inspectImage(f);
            }
        }
    }
    
    static void inspectImage(File f) throws Exception {
        BufferedImage img = ImageIO.read(f);
        System.out.println("==================================================");
        System.out.println("FILE: " + f.getParentFile().getName() + " / " + f.getName() + " (" + img.getWidth() + "x" + img.getHeight() + ")");
        System.out.println("==================================================");
        
        // Scan text pixels
        for (int y = 0; y < img.getHeight(); y++) {
            StringBuilder sb = new StringBuilder();
            boolean has = false;
            for (int x = 0; x < img.getWidth(); x++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;
                
                // Color classification
                if (r > 160 && g < 70 && b < 70) {
                    sb.append("R"); has = true; // Red
                } else if (r > 200 && g > 70 && g < 170 && b < 70) {
                    sb.append("O"); has = true; // Orange
                } else if (r > 180 && g > 180 && b < 70) {
                    sb.append("Y"); has = true; // Yellow
                } else if (r < 70 && g > 170 && b < 70) {
                    sb.append("G"); has = true; // Green
                } else if (r > 190 && g > 190 && b > 190) {
                    sb.append("W"); has = true; // White
                } else if (r > 130 && g > 130 && b > 130) {
                    sb.append("."); has = true; // Gray
                } else {
                    sb.append(" ");
                }
            }
            if (has) {
                System.out.printf("%2d: %s%n", y, sb.toString());
            }
        }
    }
}
