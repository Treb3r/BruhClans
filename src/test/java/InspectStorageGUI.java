import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class InspectStorageGUI {
    public static void main(String[] args) throws Exception {
        BufferedImage img = ImageIO.read(new File("\u0425\u0440\u0430\u043d\u0438\u043b\u0438\u0449\u0435/photo_2026-10-26_19-01-49.jpg"));
        System.out.println("Storage overview: " + img.getWidth() + "x" + img.getHeight());
        // Let's check the title banner:
        // Top banner is castle background "B" with "C" banner and "ХРАНИЛИЩЕ"
        // Let's crop title area
        File outDir = new File("target/crops");
        outDir.mkdirs();
        ImageIO.write(img.getSubimage(40, 20, 500, 70), "png", new File(outDir, "storage_title_area.png"));
        System.out.println("Saved storage_title_area.png");
    }
}
