import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class CropLore4 {
    public static void main(String[] args) throws Exception {
        BufferedImage img = ImageIO.read(new File("Уровень/photo_2026-10-26_17-00-22.jpg"));
        // Find lore area
        BufferedImage crop = img.getSubimage(100, 200, 380, 200);
        ImageIO.write(crop, "png", new File("C:/Users/uilya/.gemini/antigravity/brain/23a70919-e9ec-47c1-bc92-15d21ec7f8e1/crop_lore4.png"));
    }
}
