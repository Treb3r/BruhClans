import java.io.*;
import java.util.zip.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class CheckImage2c {
    public static void main(String[] args) throws Exception {
        File zip = new File("C:/Users/uilya/OneDrive/Desktop/ResoursePack.zip");
        try (ZipFile zf = new ZipFile(zip)) {
            String path = "assets/minecraft/textures/items/2c1909367cabe233cf4cdea9.png";
            ZipEntry ze = zf.getEntry(path);
            if (ze != null) {
                BufferedImage img = ImageIO.read(zf.getInputStream(ze));
                int nonTrans = 0;
                for (int y = 0; y < img.getHeight(); y++) {
                    for (int x = 0; x < img.getWidth(); x++) {
                        if (((img.getRGB(x, y) >> 24) & 0xff) > 10) nonTrans++;
                    }
                }
                System.out.println("2c19... -> " + img.getWidth() + "x" + img.getHeight() + " nonTrans=" + nonTrans);
            } else {
                System.out.println("Entry not found: " + path);
            }
        }
    }
}
