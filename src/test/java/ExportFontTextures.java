import java.io.*;
import java.util.zip.*;

public class ExportFontTextures {
    public static void main(String[] args) throws Exception {
        File zip = new File("C:/Users/uilya/OneDrive/Desktop/ResoursePack.zip");
        File outDir = new File("C:/Users/uilya/.gemini/antigravity/brain/23a70919-e9ec-47c1-bc92-15d21ec7f8e1");
        try (ZipFile zf = new ZipFile(zip)) {
            String[] files = {
                "41418af3df66c9963a3872e1.png",
                "1a4f22ee70ad74ed7255e21d.png",
                "099559846046702094adb90a.png",
                "ae310407eec37d532a335db6.png",
                "398c2e0e7634b61806e8421d.png"
            };
            for (String f : files) {
                ZipEntry ze = zf.getEntry("assets/minecraft/textures/items/" + f);
                if (ze != null) {
                    try (InputStream in = zf.getInputStream(ze);
                         OutputStream out = new FileOutputStream(new File(outDir, f))) {
                        in.transferTo(out);
                        System.out.println("Exported " + f);
                    }
                }
            }
        }
    }
}
