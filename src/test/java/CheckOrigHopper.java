import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class CheckOrigHopper {
    public static void main(String[] args) throws Exception {
        BufferedImage orig = ImageIO.read(new File("C:/Users/uilya/.gemini/antigravity/brain/23a70919-e9ec-47c1-bc92-15d21ec7f8e1/orig_levels_page1.png"));
        // Scan entire orig for hopper-like pattern or funnel
        // Print min and max colors in the top banner of orig
        int w = orig.getWidth();
        int h = orig.getHeight();
        for (int y = 0; y < h * 0.4; y++) {
            for (int x = (int)(w * 0.65); x < (int)(w * 0.9); x++) {
                int rgb = orig.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF, g = (rgb >> 8) & 0xFF, b = rgb & 0xFF;
                // Check if there is a white box (r>230, g>230, b>230) on the skull
                if (r > 240 && g > 240 && b > 240) {
                    // Check if it's skull bone (beige ~ 240, 235, 215) or pure white
                    if (Math.abs(r - b) < 5 && Math.abs(r - g) < 5) {
                        System.out.printf("Pure white in orig banner at x=%d, y=%d (r=%d, g=%d, b=%d)%n", x, y, r, g, b);
                    }
                }
            }
        }
    }
}
