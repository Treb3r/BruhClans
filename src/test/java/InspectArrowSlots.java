import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class InspectArrowSlots {
    public static void main(String[] args) throws Exception {
        String[] rows = {
            "bottom_photo_2026-10-26_16-53-53.png",
            "bottom_photo_2026-10-26_16-54-09.png",
            "bottom_photo_2026-10-26_16-54-18.png",
            "bottom_photo_2026-10-26_16-54-29.png"
        };
        for (String rName : rows) {
            File f = new File("C:/Users/uilya/.gemini/antigravity/brain/23a70919-e9ec-47c1-bc92-15d21ec7f8e1/" + rName);
            if (!f.exists()) continue;
            BufferedImage img = ImageIO.read(f);
            System.out.println("=== " + rName + " (width=" + img.getWidth() + ") ===");
            // Width is divided into 9 slots: 0 to 8
            double slotW = img.getWidth() / 9.0;
            for (int col = 0; col < 9; col++) {
                int startX = (int)(col * slotW);
                int endX = (int)((col + 1) * slotW);
                int white = 0;
                for (int x = startX; x < endX; x++) {
                    for (int y = 0; y < img.getHeight(); y++) {
                        int rgb = img.getRGB(x, y);
                        int r = (rgb >> 16) & 0xFF, g = (rgb >> 8) & 0xFF, b = rgb & 0xFF;
                        if (r > 220 && g > 220 && b > 220) white++;
                    }
                }
                if (white > 30) {
                    System.out.printf("  Col %d (slot %d): white pixels = %d%n", col, 45 + col, white);
                }
            }
        }
    }
}
