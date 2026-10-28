import java.io.*;
import java.util.zip.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class CheckClanImages {
    public static void main(String[] args) throws Exception {
        File zip = new File("C:/Users/uilya/OneDrive/Desktop/ResoursePack.zip");
        try (ZipFile zf = new ZipFile(zip)) {
            String[] files = {
                "007321961c0784ecd47bd13a.png", "0198429da03f48ec5eeecd3f.png",
                "bda96231a299ff3f646084ac.png", "1a4f22ee70ad74ed7255e21d.png",
                "dbc3d29b019316c576e68f49.png", "a7bddd268894b16c13e793bc.png",
                "428febdb680bb22495c5eafd.png", "a1aae147a45c722187302612.png",
                "398c2e0e7634b61806e8421d.png", "099559846046702094adb90a.png",
                "ae310407eec37d532a335db6.png", "7ada96bb697fe9b838fb8dc6.png",
                "41418af3df66c9963a3872e1.png"
            };
            for (String f : files) {
                ZipEntry ze = zf.getEntry("assets/minecraft/textures/items/" + f);
                if (ze == null) ze = zf.getEntry("assets/minecraft/textures/font/" + f);
                if (ze == null) ze = zf.getEntry("assets/minecraft/items/" + f);
                if (ze != null) {
                    BufferedImage img = ImageIO.read(zf.getInputStream(ze));
                    System.out.println(f + " -> " + img.getWidth() + "x" + img.getHeight());
                } else {
                    System.out.println(f + " -> NOT FOUND");
                }
            }
        }
    }
}
