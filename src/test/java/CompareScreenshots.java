import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class CompareScreenshots {
    public static void main(String[] args) throws Exception {
        BufferedImage orig = ImageIO.read(new File("C:/Users/uilya/.gemini/antigravity/brain/23a70919-e9ec-47c1-bc92-15d21ec7f8e1/orig_levels_page1.png"));
        BufferedImage curr = ImageIO.read(new File("C:/Users/uilya/.gemini/antigravity/brain/23a70919-e9ec-47c1-bc92-15d21ec7f8e1/.user_uploaded/media_1793127932688_2ecb3023.png"));
        
        System.out.println("Orig: " + orig.getWidth() + "x" + orig.getHeight());
        System.out.println("Curr: " + curr.getWidth() + "x" + curr.getHeight());
        
        // Also let's check photo_2026-10-26_16-54-09.jpg and photo_2026-10-26_16-54-18.jpg
        for (String name : new String[]{
            "photo_2026-10-26_16-53-53.jpg",
            "photo_2026-10-26_16-54-09.jpg",
            "photo_2026-10-26_16-54-18.jpg",
            "photo_2026-10-26_16-54-29.jpg",
            "photo_2026-10-26_16-54-39.jpg",
            "photo_2026-10-26_16-54-52.jpg"
        }) {
            File f = new File("C:/Users/uilya/IdeaProjects/BruhClans/Уровень/" + name);
            if (f.exists()) {
                BufferedImage bi = ImageIO.read(f);
                File o = new File("C:/Users/uilya/.gemini/antigravity/brain/23a70919-e9ec-47c1-bc92-15d21ec7f8e1/" + name.replace(".jpg", ".png"));
                ImageIO.write(bi, "png", o);
                System.out.println("Exported " + name + " (" + bi.getWidth() + "x" + bi.getHeight() + ")");
            }
        }
    }
}
