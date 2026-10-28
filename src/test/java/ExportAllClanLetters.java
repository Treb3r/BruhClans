import java.io.*;
import java.util.zip.*;

public class ExportAllClanLetters {
    public static void main(String[] args) throws Exception {
        File zip = new File("C:/Users/uilya/OneDrive/Desktop/ResoursePack.zip");
        File outDir = new File("C:/Users/uilya/.gemini/antigravity/brain/23a70919-e9ec-47c1-bc92-15d21ec7f8e1/clan_letters");
        outDir.mkdirs();
        try (ZipFile zf = new ZipFile(zip)) {
            String[] mapping = {
                "A:007321961c0784ecd47bd13a.png",
                "B:0198429da03f48ec5eeecd3f.png",
                "C:bda96231a299ff3f646084ac.png",
                "D:1a4f22ee70ad74ed7255e21d.png",
                "E:dbc3d29b019316c576e68f49.png",
                "F:a7bddd268894b16c13e793bc.png",
                "G:428febdb680bb22495c5eafd.png",
                "H:a1aae147a45c722187302612.png",
                "I:767d5d5deac861ced24cc531.png",
                "J:e3d85d6233fefd670f0fff05.png",
                "K:108a25c19a0af6fd3fb64aa6.png",
                "L:ed375f65dc68d5261dd08374.png",
                "M:06b3bc628480e05638ee8214.png",
                "N:d92faed3cb7b05ac97af0cd6.png",
                "O:4c2ee04301af7faf091583fb.png",
                "a:6f98f0ab82b6cd67ec5d02fe.png",
                "z:398c2e0e7634b61806e8421d.png",
                "P:c31e0c3a45f838eb2e7f1bd9.png",
                "Q:e0186cb47989af10e1b36fe1.png",
                "R:e6df1765dd6c01bea6d7d337.png",
                "S:dda4b25ddcb15c6edd22dfda.png",
                "T:04095aca433e0b68658a3f52.png",
                "U:4f1702eacb240ca74d3ee7e8.png",
                "V:86cc8e073d30963af71e2a24.png",
                "W:5728b3fba16b26c7a1d3b7aa.png",
                "X:099559846046702094adb90a.png",
                "Y:ae310407eec37d532a335db6.png"
            };
            for (String pair : mapping) {
                String[] parts = pair.split(":");
                String ch = parts[0];
                String f = parts[1];
                ZipEntry ze = zf.getEntry("assets/minecraft/textures/items/" + f);
                if (ze != null) {
                    try (InputStream in = zf.getInputStream(ze);
                         OutputStream out = new FileOutputStream(new File(outDir, ch + "_" + f))) {
                        in.transferTo(out);
                        System.out.println("Exported " + ch);
                    }
                }
            }
        }
    }
}
