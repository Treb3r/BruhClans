import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class ExportAllBottomRows {
    public static void main(String[] args) throws Exception {
        String[] files = {
            "photo_2026-10-26_16-53-53.jpg",
            "photo_2026-10-26_16-54-09.jpg",
            "photo_2026-10-26_16-54-18.jpg",
            "photo_2026-10-26_16-54-29.jpg",
            "photo_2026-10-26_16-54-39.jpg",
            "photo_2026-10-26_16-54-52.jpg"
        };
        
        for (String fName : files) {
            File f = new File("C:/Users/uilya/IdeaProjects/BruhClans/Уровень/" + fName);
            if (!f.exists()) continue;
            BufferedImage img = ImageIO.read(f);
            
            // Find door
            int doorSumX = 0, doorSumY = 0, doorCount = 0;
            for (int y = (int)(img.getHeight() * 0.6); y < img.getHeight(); y++) {
                for (int x = 0; x < img.getWidth(); x++) {
                    int rgb = img.getRGB(x, y);
                    int r = (rgb >> 16) & 0xFF, g = (rgb >> 8) & 0xFF, b = rgb & 0xFF;
                    if (r > 200 && g < 60 && b < 60) {
                        doorSumX += x; doorSumY += y; doorCount++;
                    }
                }
            }
            if (doorCount == 0) continue;
            int dx = doorSumX / doorCount;
            int dy = doorSumY / doorCount;
            
            // Crop bottom row: 9 slots
            int pitch = 36;
            int startX = dx - 4 * pitch - 18;
            int startY = dy - 18;
            int bw = pitch * 9;
            int bh = pitch;
            
            startX = Math.max(0, Math.min(img.getWidth() - bw, startX));
            startY = Math.max(0, Math.min(img.getHeight() - bh, startY));
            
            BufferedImage row = img.getSubimage(startX, startY, bw, bh);
            ImageIO.write(row, "png", new File("C:/Users/uilya/.gemini/antigravity/brain/23a70919-e9ec-47c1-bc92-15d21ec7f8e1/bottom_" + fName.replace(".jpg", ".png")));
            System.out.println("Saved bottom row for " + fName);
        }
    }
}
