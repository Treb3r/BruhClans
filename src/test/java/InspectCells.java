import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class InspectCells {
    public static void main(String[] args) throws Exception {
        for (String name : new String[]{
            "grid_bottom_photo_2026-10-26_16-53-53.png",
            "grid_bottom_photo_2026-10-26_16-54-09.png"
        }) {
            BufferedImage img = ImageIO.read(new File("C:/Users/uilya/.gemini/antigravity/brain/23a70919-e9ec-47c1-bc92-15d21ec7f8e1/" + name));
            System.out.println("FILE: " + name);
            for (int col = 0; col < 9; col++) {
                int startX = col * 36;
                int endX = (col + 1) * 36;
                int white = 0;
                int red = 0;
                for (int x = startX + 4; x < endX - 4; x++) {
                    for (int y = 4; y < img.getHeight() - 4; y++) {
                        int rgb = img.getRGB(x, y);
                        int r = (rgb >> 16) & 0xFF, g = (rgb >> 8) & 0xFF, b = rgb & 0xFF;
                        if (r > 220 && g > 220 && b > 220) white++;
                        if (r > 180 && g < 60 && b < 60) red++;
                    }
                }
                if (white > 20 || red > 20) {
                    System.out.printf("  Cell col %d (slot %d): white=%d, red=%d%n", col, 45 + col, white, red);
                }
            }
        }
    }
}
