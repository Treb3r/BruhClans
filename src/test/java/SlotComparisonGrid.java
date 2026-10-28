import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class SlotComparisonGrid {
    public static void main(String[] args) throws Exception {
        BufferedImage orig = ImageIO.read(new File("C:/Users/uilya/.gemini/antigravity/brain/23a70919-e9ec-47c1-bc92-15d21ec7f8e1/photo_2026-10-26_16-53-53.png"));
        BufferedImage curr = ImageIO.read(new File("C:/Users/uilya/.gemini/antigravity/brain/23a70919-e9ec-47c1-bc92-15d21ec7f8e1/.user_uploaded/media_1793127932688_2ecb3023.png"));
        
        // Orig: center of slot 13 is (227, 174)
        // Curr: center of slot 13 is (260, 201)
        double pitch = 36.0;
        
        System.out.println("--- COMPARING SLOTS (row 0..5, col 0..8) ---");
        for (int r = 0; r < 6; r++) {
            for (int c = 0; c < 9; c++) {
                int slot = r * 9 + c;
                
                int origX = (int)Math.round(227 + (c - 4) * pitch);
                int origY = (int)Math.round(174 + (r - 1) * pitch);
                
                int currX = (int)Math.round(260 + (c - 4) * pitch);
                int currY = (int)Math.round(201 + (r - 1) * pitch);
                
                // Crop 28x28 around center
                BufferedImage origSlot = crop(orig, origX, origY, 28);
                BufferedImage currSlot = crop(curr, currX, currY, 28);
                
                boolean origHasItem = hasItem(origSlot);
                boolean currHasItem = hasItem(currSlot);
                
                if (origHasItem || currHasItem) {
                    System.out.printf("Slot %2d (r=%d, c=%d): Orig=%-5b, Curr=%-5b%n", slot, r, c, origHasItem, currHasItem);
                }
            }
        }
    }

    static BufferedImage crop(BufferedImage img, int cx, int cy, int size) {
        int x = cx - size / 2;
        int y = cy - size / 2;
        x = Math.max(0, Math.min(img.getWidth() - size, x));
        y = Math.max(0, Math.min(img.getHeight() - size, y));
        return img.getSubimage(x, y, size, size);
    }

    static boolean hasItem(BufferedImage slot) {
        // Check if there is variation in pixel color beyond flat background
        int minR = 255, maxR = 0;
        int minG = 255, maxG = 0;
        int minB = 255, maxB = 0;
        for (int y = 4; y < slot.getHeight() - 4; y++) {
            for (int x = 4; x < slot.getWidth() - 4; x++) {
                int rgb = slot.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                minR = Math.min(minR, r); maxR = Math.max(maxR, r);
                minG = Math.min(minG, g); maxG = Math.max(maxG, g);
                minB = Math.min(minB, b); maxB = Math.max(maxB, b);
            }
        }
        return (maxR - minR > 35) || (maxG - minG > 35) || (maxB - minB > 35);
    }
}
