import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class OcrTooltipCrops {
    public static void main(String[] args) throws Exception {
        String[] files = {"tooltip_locked.png", "tooltip_rent.png", "tooltip_back.png", "tooltip_cancel.png", "tooltip_confirm.png"};
        for (String fn : files) {
            File f = new File("target/crops/" + fn);
            if (!f.exists()) continue;
            BufferedImage img = ImageIO.read(f);
            System.out.println("==================================================");
            System.out.println("FILE: " + fn + " (" + img.getWidth() + "x" + img.getHeight() + ")");
            System.out.println("==================================================");
            for (int y = 0; y < img.getHeight(); y++) {
                StringBuilder sb = new StringBuilder();
                boolean hasContent = false;
                for (int x = 0; x < img.getWidth(); x++) {
                    int rgb = img.getRGB(x, y);
                    int r = (rgb >> 16) & 0xff;
                    int g = (rgb >> 8) & 0xff;
                    int b = rgb & 0xff;
                    if (r > 160 && g < 70 && b < 70) { // Red
                        sb.append("R");
                        hasContent = true;
                    } else if (r > 200 && g > 70 && g < 160 && b < 60) { // Orange
                        sb.append("O");
                        hasContent = true;
                    } else if (r > 180 && g > 180 && b < 60) { // Yellow
                        sb.append("Y");
                        hasContent = true;
                    } else if (r < 70 && g > 180 && b < 70) { // Green
                        sb.append("G");
                        hasContent = true;
                    } else if (r > 180 && g > 180 && b > 180) { // White
                        sb.append("W");
                        hasContent = true;
                    } else if (r > 120 && g > 120 && b > 120) { // Light gray
                        sb.append(".");
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
