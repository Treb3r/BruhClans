import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class DumpOrigSlots {
    public static void main(String[] args) throws Exception {
        BufferedImage orig = ImageIO.read(new File("C:/Users/uilya/.gemini/antigravity/brain/23a70919-e9ec-47c1-bc92-15d21ec7f8e1/photo_2026-10-26_16-53-53.png"));
        BufferedImage curr = ImageIO.read(new File("C:/Users/uilya/.gemini/antigravity/brain/23a70919-e9ec-47c1-bc92-15d21ec7f8e1/.user_uploaded/media_1793127932688_2ecb3023.png"));
        
        // Let's create an image showing the entire inventory side by side
        int slotSize = 36;
        BufferedImage dump = new BufferedImage(slotSize * 9 * 2 + 50, slotSize * 6, BufferedImage.TYPE_INT_ARGB);
        java.awt.Graphics2D g = dump.createGraphics();
        
        // Draw orig slots
        for (int r = 0; r < 6; r++) {
            for (int c = 0; c < 9; c++) {
                int ox = (int)Math.round(227 + (c - 4) * 36.25);
                int oy = (int)Math.round(174 + (r - 1) * 36.25);
                BufferedImage sub = crop(orig, ox, oy, 32);
                g.drawImage(sub, c * slotSize, r * slotSize, null);
                
                int cx = (int)Math.round(260 + (c - 4) * 36.5);
                int cy = (int)Math.round(201 + (r - 1) * 36.5);
                BufferedImage csub = crop(curr, cx, cy, 32);
                g.drawImage(csub, slotSize * 9 + 50 + c * slotSize, r * slotSize, null);
            }
        }
        g.dispose();
        ImageIO.write(dump, "png", new File("C:/Users/uilya/.gemini/antigravity/brain/23a70919-e9ec-47c1-bc92-15d21ec7f8e1/dump_slots.png"));
        System.out.println("Dump saved to dump_slots.png");
    }

    static BufferedImage crop(BufferedImage img, int cx, int cy, int size) {
        int x = cx - size / 2;
        int y = cy - size / 2;
        x = Math.max(0, Math.min(img.getWidth() - size, x));
        y = Math.max(0, Math.min(img.getHeight() - size, y));
        return img.getSubimage(x, y, size, size);
    }
}
