import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class DumpTooltipAscii {
    public static void main(String[] args) throws Exception {
        dump("Хранилище/photo_2026-10-26_19-02-03.jpg", "LOCKED");
        dump("Хранилище/photo_2026-10-26_19-02-13.jpg", "RENT");
        dump("Хранилище/photo_2026-10-26_19-02-22.jpg", "BACK");
        dump("Хранилище/photo_2026-10-27_23-24-48.jpg", "CANCEL");
        dump("Хранилище/photo_2026-10-27_23-24-51.jpg", "CONFIRM");
    }
    static void dump(String path, String label) throws Exception {
        BufferedImage img = ImageIO.read(new File(path));
        // Tooltip box has border with color near (40, 0, 80) or background near (16, 0, 16)
        int minX = img.getWidth(), maxX = 0, minY = img.getHeight(), maxY = 0;
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;
                if ((r >= 30 && r <= 60 && g <= 20 && b >= 60 && b <= 120) ||
                    (r >= 10 && r <= 25 && g <= 10 && b >= 10 && b <= 25)) {
                    if (x < minX) minX = x;
                    if (x > maxX) maxX = x;
                    if (y < minY) minY = y;
                    if (y > maxY) maxY = y;
                }
            }
        }
        System.out.printf("=== %s: tooltip bounds [%d, %d] to [%d, %d] (size %dx%d) ===%n",
            label, minX, minY, maxX, maxY, (maxX - minX + 1), (maxY - minY + 1));
        if (maxX > minX && maxY > minY) {
            BufferedImage crop = img.getSubimage(minX, minY, maxX - minX + 1, maxY - minY + 1);
            File outDir = new File("target/crops");
            outDir.mkdirs();
            ImageIO.write(crop, "png", new File(outDir, label + "_tooltip.png"));
        }
    }
}
