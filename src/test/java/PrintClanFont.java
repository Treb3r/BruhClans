import java.io.*;
import java.util.zip.*;

public class PrintClanFont {
    public static void main(String[] args) throws Exception {
        File zip = new File("C:/Users/uilya/OneDrive/Desktop/ResoursePack.zip");
        try (ZipFile zf = new ZipFile(zip)) {
            ZipEntry ze = zf.getEntry("assets/minecraft/font/clan.json");
            if (ze != null) {
                try (InputStream in = zf.getInputStream(ze)) {
                    byte[] b = in.readAllBytes();
                    System.out.println(new String(b, "UTF-8"));
                }
            } else {
                System.out.println("assets/minecraft/font/clan.json not found!");
            }
        }
    }
}
