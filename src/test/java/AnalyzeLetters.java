import java.io.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class AnalyzeLetters {
    public static void main(String[] args) throws Exception {
        File dir = new File("C:/Users/uilya/.gemini/antigravity/brain/23a70919-e9ec-47c1-bc92-15d21ec7f8e1/clan_letters");
        for (File f : dir.listFiles()) {
            if (f.getName().endsWith(".png")) {
                BufferedImage img = ImageIO.read(f);
                int nonTrans = 0;
                long rSum = 0, gSum = 0, bSum = 0;
                for (int y = 0; y < img.getHeight(); y++) {
                    for (int x = 0; x < img.getWidth(); x++) {
                        int argb = img.getRGB(x, y);
                        int a = (argb >> 24) & 0xff;
                        if (a > 10) {
                            nonTrans++;
                            rSum += (argb >> 16) & 0xff;
                            gSum += (argb >> 8) & 0xff;
                            bSum += argb & 0xff;
                        }
                    }
                }
                if (nonTrans > 0) {
                    System.out.printf("%s: nonTrans=%d avgR=%d avgG=%d avgB=%d%n",
                        f.getName().substring(0, 3), nonTrans, rSum/nonTrans, gSum/nonTrans, bSum/nonTrans);
                } else {
                    System.out.println(f.getName().substring(0, 3) + ": ALL TRANSPARENT");
                }
            }
        }
    }
}
