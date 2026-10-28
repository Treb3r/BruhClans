import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class InspectTooltips {
    public static void main(String[] args) throws Exception {
        inspect("Хранилище/photo_2026-10-26_19-02-03.jpg", "crop_locked.png");
        inspect("Хранилище/photo_2026-10-26_19-02-13.jpg", "crop_rent.png");
        inspect("Хранилище/photo_2026-10-26_19-02-22.jpg", "crop_back.png");
        inspect("Хранилище/photo_2026-10-27_23-24-48.jpg", "crop_cancel.png");
        inspect("Хранилище/photo_2026-10-27_23-24-51.jpg", "crop_confirm.png");
    }
    static void inspect(String path, String outName) throws Exception {
        BufferedImage img = ImageIO.read(new File(path));
        // Tooltip has dark purple/blue border or background: RGB around (16, 0, 16) / (40, 0, 80)
        // Let's find minX, maxX, minY, maxY of tooltip or save the full image into target/crops
        File outDir = new File("target/crops");
        outDir.mkdirs();
        ImageIO.write(img, "png", new File(outDir, outName));
        System.out.println("Wrote " + outName);
    }
}
