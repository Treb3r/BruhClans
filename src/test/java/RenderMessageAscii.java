import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class RenderMessageAscii {
    public static void main(String[] args) throws Exception {
        render("clan chat или cc/photo_2026-10-26_19-08-49.jpg");
        render("clan chat или cc/photo_2026-10-26_19-09-29.jpg");
        render("clan coords/photo_2026-10-26_19-06-00.jpg");
        render("clan coords/image_2026-10-26_19-06-14.png");
        render("clan coords/photo_2026-10-26_19-06-21.jpg");
        render("clan delhome/image_2026-10-26_19-07-53.png");
        render("clan delhome/image_2026-10-26_19-08-16.png");
        render("clan delhome/image_2026-10-26_19-08-29.png");
    }
    
    static void render(String path) throws Exception {
        BufferedImage img = ImageIO.read(new File(path));
        System.out.println("==================================================");
        System.out.println("FILE: " + path);
        System.out.println("==================================================");
        // Print 80 columns chunks
        int w = img.getWidth();
        int h = img.getHeight();
        for (int chunk = 0; chunk < w; chunk += 100) {
            int chunkEnd = Math.min(w, chunk + 100);
            System.out.println("--- Columns " + chunk + " to " + (chunkEnd - 1) + " ---");
            for (int y = 0; y < h; y++) {
                StringBuilder sb = new StringBuilder();
                boolean hasContent = false;
                for (int x = chunk; x < chunkEnd; x++) {
                    int rgb = img.getRGB(x, y);
                    int r = (rgb >> 16) & 0xff;
                    int g = (rgb >> 8) & 0xff;
                    int b = rgb & 0xff;
                    
                    if (r > 160 && g < 70 && b < 70) {
                        sb.append("R"); hasContent = true; // Red
                    } else if (r > 200 && g > 70 && g < 170 && b < 70) {
                        sb.append("O"); hasContent = true; // Orange
                    } else if (r > 180 && g > 180 && b < 70) {
                        sb.append("Y"); hasContent = true; // Yellow
                    } else if (r < 70 && g > 170 && b < 70) {
                        sb.append("G"); hasContent = true; // Green
                    } else if (r > 190 && g > 190 && b > 190) {
                        sb.append("W"); hasContent = true; // White
                    } else if (r > 110 && g > 110 && b > 110) {
                        sb.append("."); hasContent = true; // Gray
                    } else {
                        sb.append(" ");
                    }
                }
                if (hasContent) {
                    System.out.printf("%2d: %s%n", y, sb.toString());
                }
            }
        }
    }
}
