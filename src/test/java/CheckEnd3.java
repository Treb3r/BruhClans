import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class CheckEnd3 {
    public static void main(String[] args) throws Exception {
        BufferedImage img = ImageIO.read(new File("clan delhome/image_2026-10-26_19-08-29.png"));
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 425; x < img.getWidth(); x++) {
                int rgb = img.getRGB(x, y);
                int r = (rgb>>16)&0xff, g = (rgb>>8)&0xff, b = rgb&0xff;
                System.out.print((r>150&&g>150&&b>150)?'W':' ');
            }
            System.out.println();
        }
    }
}
