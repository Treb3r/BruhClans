import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class CheckLockColors {
    public static void main(String[] args) throws Exception {
        File dir = new File("scratch/lock_textures");
        for (File f : dir.listFiles()) {
            if (!f.getName().endsWith(".png")) continue;
            BufferedImage img = ImageIO.read(f);
            long rSum = 0, gSum = 0, bSum = 0, count = 0;
            for (int y = 0; y < img.getHeight(); y++) {
                for (int x = 0; x < img.getWidth(); x++) {
                    int argb = img.getRGB(x, y);
                    int a = (argb >> 24) & 0xff;
                    if (a > 30) {
                        rSum += (argb >> 16) & 0xff;
                        gSum += (argb >> 8) & 0xff;
                        bSum += argb & 0xff;
                        count++;
                    }
                }
            }
            if (count > 0) {
                int r = (int)(rSum / count);
                int g = (int)(gSum / count);
                int b = (int)(bSum / count);
                System.out.printf("%s: %dx%d, avg non-transparent RGB=(%d, %d, %d), count=%d%n",
                    f.getName(), img.getWidth(), img.getHeight(), r, g, b, count);
            }
        }
    }
}
