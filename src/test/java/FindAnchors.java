import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class FindAnchors {
    public static void main(String[] args) throws Exception {
        BufferedImage orig = ImageIO.read(new File("C:/Users/uilya/.gemini/antigravity/brain/23a70919-e9ec-47c1-bc92-15d21ec7f8e1/photo_2026-10-26_16-53-53.png"));
        BufferedImage curr = ImageIO.read(new File("C:/Users/uilya/.gemini/antigravity/brain/23a70919-e9ec-47c1-bc92-15d21ec7f8e1/.user_uploaded/media_1793127932688_2ecb3023.png"));
        
        System.out.println("--- CURR ANCHORS ---");
        findShieldAndDoor(curr, "curr");
        
        System.out.println("--- ORIG ANCHORS ---");
        findShieldAndDoor(orig, "orig");
    }

    static void findShieldAndDoor(BufferedImage img, String name) {
        int w = img.getWidth();
        int h = img.getHeight();
        
        // Find green pixels in top half (shield arrow is bright lime green ~ RGB(0, 255, 0))
        int shieldSumX = 0, shieldSumY = 0, shieldCount = 0;
        // Find red pixels in bottom half (door has bright red ~ RGB(230..255, 0..50, 0..50))
        int doorSumX = 0, doorSumY = 0, doorCount = 0;
        
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                
                // Shield green arrow
                if (y < h * 0.6 && g > 180 && r < 100 && b < 100) {
                    shieldSumX += x;
                    shieldSumY += y;
                    shieldCount++;
                }
                
                // Door red
                if (y > h * 0.6 && r > 200 && g < 60 && b < 60) {
                    doorSumX += x;
                    doorSumY += y;
                    doorCount++;
                }
            }
        }
        
        int shieldX = shieldCount > 0 ? shieldSumX / shieldCount : -1;
        int shieldY = shieldCount > 0 ? shieldSumY / shieldCount : -1;
        int doorX = doorCount > 0 ? doorSumX / doorCount : -1;
        int doorY = doorCount > 0 ? doorSumY / doorCount : -1;
        
        System.out.println(name + " Shield at (" + shieldX + ", " + shieldY + "), Door at (" + doorX + ", " + doorY + ")");
        if (shieldY > 0 && doorY > 0) {
            double slotPitch = (doorY - shieldY) / 4.0;
            System.out.println(name + " Slot pitch Y = " + slotPitch);
        }
    }
}
