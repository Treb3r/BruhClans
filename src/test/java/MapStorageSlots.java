import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class MapStorageSlots {
    public static void main(String[] args) throws Exception {
        BufferedImage img = ImageIO.read(new File("\u0425\u0440\u0430\u043d\u0438\u043b\u0438\u0449\u0435/photo_2026-10-26_19-01-49.jpg"));
        // We know row 4 centers are at y=310, with x roughly:
        // col 1 (slot 37): x around 153
        // col 2 (slot 38): x around 189
        // col 3 (slot 39): x around 225
        // col 4 (slot 40): x around 261
        // col 5 (slot 41): x around 297
        // col 6 (slot 42): x around 333
        // col 7 (slot 43): x around 369
        // Spacing between columns is 36 px!
        // Spacing between rows: y=310 is row 4, row 3 is 310 - 36 = 274, row 2 is 238, row 1 is 202
        int startX = 153;
        int startY = 202;
        int stepX = 36;
        int stepY = 36;
        
        File outDir = new File("target/crops/grid");
        outDir.mkdirs();
        
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 7; c++) {
                int cx = startX + c * stepX;
                int cy = startY + r * stepY;
                BufferedImage crop = img.getSubimage(cx - 14, cy - 14, 28, 28);
                ImageIO.write(crop, "png", new File(outDir, "slot_r" + (r+1) + "_c" + (c+1) + ".png"));
                
                // Classify:
                // Check if has gold
                boolean hasGold = false;
                boolean hasGrayLock = false;
                for (int py = 0; py < crop.getHeight(); py++) {
                    for (int px = 0; px < crop.getWidth(); px++) {
                        int rgb = crop.getRGB(px, py);
                        int red = (rgb >> 16) & 0xff;
                        int green = (rgb >> 8) & 0xff;
                        int blue = rgb & 0xff;
                        if (red > 180 && green > 120 && green < 195 && blue < 70) hasGold = true;
                        // Gray lock has distinct blueish gray (55, 60, 75)
                        if (red >= 50 && red <= 65 && green >= 55 && green <= 70 && blue >= 70 && blue <= 85) hasGrayLock = true;
                    }
                }
                String type = hasGold ? "RENT (GOLD LOCK)" : (hasGrayLock ? "LOCKED (GRAY LOCK)" : "ITEM / UNLOCKED");
                System.out.printf("Row %d, Col %d (Slot %d): %s%n", r + 1, c + 1, (r + 1) * 9 + (c + 1), type);
            }
        }
    }
}
