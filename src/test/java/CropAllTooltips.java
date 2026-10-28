import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class CropAllTooltips {
    public static void main(String[] args) throws Exception {
        File outDir = new File("target/crops");
        outDir.mkdirs();
        
        // Locked
        BufferedImage imgL = ImageIO.read(new File("\u0425\u0440\u0430\u043d\u0438\u043b\u0438\u0449\u0435/photo_2026-10-26_19-02-03.jpg"));
        ImageIO.write(imgL.getSubimage(360, 140, 300, 40), "png", new File(outDir, "tooltip_locked.png"));
        
        // Rent
        BufferedImage imgR = ImageIO.read(new File("\u0425\u0440\u0430\u043d\u0438\u043b\u0438\u0449\u0435/photo_2026-10-26_19-02-13.jpg"));
        ImageIO.write(imgR.getSubimage(360, 240, 340, 120), "png", new File(outDir, "tooltip_rent.png"));
        
        // Back
        BufferedImage imgB = ImageIO.read(new File("\u0425\u0440\u0430\u043d\u0438\u043b\u0438\u0449\u0435/photo_2026-10-26_19-02-22.jpg"));
        ImageIO.write(imgB.getSubimage(170, 290, 250, 50), "png", new File(outDir, "tooltip_back.png"));
        
        // Cancel
        BufferedImage imgC = ImageIO.read(new File("\u0425\u0440\u0430\u043d\u0438\u043b\u0438\u0449\u0435/photo_2026-10-27_23-24-48.jpg"));
        ImageIO.write(imgC.getSubimage(315, 0, 70, 30), "png", new File(outDir, "tooltip_cancel.png"));
        
        // Confirm
        BufferedImage imgCf = ImageIO.read(new File("\u0425\u0440\u0430\u043d\u0438\u043b\u0438\u0449\u0435/photo_2026-10-27_23-24-51.jpg"));
        ImageIO.write(imgCf.getSubimage(290, 0, 95, 30), "png", new File(outDir, "tooltip_confirm.png"));
        
        System.out.println("Done cropping tooltips.");
    }
}
