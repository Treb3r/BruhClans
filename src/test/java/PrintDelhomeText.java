import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class PrintDelhomeText {
    public static void main(String[] args) throws Exception {
        printImage("clan delhome/image_2026-10-26_19-07-53.png");
        printImage("clan delhome/image_2026-10-26_19-08-16.png");
        printImage("clan delhome/image_2026-10-26_19-08-29.png");
    }
    static void printImage(String path) throws Exception {
        BufferedImage img = ImageIO.read(new File(path));
        System.out.println("==================================================");
        System.out.println("FILE: " + path + " (" + img.getWidth() + "x" + img.getHeight() + ")");
        System.out.println("==================================================");
        
        // Let's print in segments of 100 pixels wide so it fits in terminal nicely
        for (int startX = 0; startX < img.getWidth(); startX += 80) {
            int endX = Math.min(startX + 80, img.getWidth());
            System.out.println("--- Range X: " + startX + " to " + endX + " ---");
            for (int y = 0; y < img.getHeight(); y++) {
                StringBuilder sb = new StringBuilder();
                for (int x = startX; x < endX; x++) {
                    int rgb = img.getRGB(x, y);
                    int r = (rgb >> 16) & 0xff;
                    int g = (rgb >> 8) & 0xff;
                    int b = rgb & 0xff;
                    if (r > 150 && g < 70 && b < 70) sb.append("R");
                    else if (r > 180 && g > 70 && g < 170 && b < 70) sb.append("O");
                    else if (r > 180 && g > 180 && b < 70) sb.append("Y");
                    else if (r < 70 && g > 180 && b < 70) sb.append("G");
                    else if (r > 180 && g > 180 && b > 180) sb.append("W");
                    else if (r > 100 && g > 100 && b > 100) sb.append(".");
                    else sb.append(" ");
                }
                String line = sb.toString();
                if (!line.trim().isEmpty()) {
                    System.out.println(line);
                }
            }
        }
    }
}
