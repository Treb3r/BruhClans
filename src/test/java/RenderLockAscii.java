import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class RenderLockAscii {
    public static void main(String[] args) throws Exception {
        int[] cmds = {394, 396, 401, 402};
        for (int cmd : cmds) {
            File[] files = new File("scratch/lock_textures").listFiles((dir, name) -> name.startsWith(cmd + "_"));
            if (files == null || files.length == 0) continue;
            BufferedImage img = ImageIO.read(files[0]);
            System.out.println("=== CMD " + cmd + " (" + files[0].getName() + ") ===");
            for (int y = 0; y < img.getHeight(); y++) {
                StringBuilder sb = new StringBuilder();
                for (int x = 0; x < img.getWidth(); x++) {
                    int argb = img.getRGB(x, y);
                    int a = (argb >> 24) & 0xff;
                    if (a < 50) {
                        sb.append("  ");
                    } else {
                        int r = (argb >> 16) & 0xff;
                        int g = (argb >> 8) & 0xff;
                        int b = argb & 0xff;
                        if (r > 150 && g > 100 && b < 100) {
                            sb.append("##"); // Gold
                        } else {
                            sb.append("..");
                        }
                    }
                }
                System.out.println(sb.toString());
            }
        }
    }
}
