import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class DrawBottomRowSlots {
    public static void main(String[] args) throws Exception {
        for (String name : new String[]{
            "bottom_photo_2026-10-26_16-53-53.png",
            "bottom_photo_2026-10-26_16-54-09.png"
        }) {
            File f = new File("C:/Users/uilya/.gemini/antigravity/brain/23a70919-e9ec-47c1-bc92-15d21ec7f8e1/" + name);
            BufferedImage img = ImageIO.read(f);
            Graphics2D g = img.createGraphics();
            g.setColor(Color.RED);
            for (int i = 0; i <= 9; i++) {
                int x = i * 36;
                g.drawLine(x, 0, x, img.getHeight());
            }
            g.dispose();
            ImageIO.write(img, "png", new File("C:/Users/uilya/.gemini/antigravity/brain/23a70919-e9ec-47c1-bc92-15d21ec7f8e1/grid_" + name));
            System.out.println("Saved grid_" + name);
        }
    }
}
