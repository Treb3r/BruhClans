import java.io.*;
import java.util.zip.*;

public class CheckModel1 {
    public static void main(String[] args) throws Exception {
        File zip = new File("C:/Users/uilya/OneDrive/Desktop/ResoursePack.zip");
        try (ZipFile zf = new ZipFile(zip)) {
            String path = "assets/z/models/_/8c382d1adad68d.json";
            ZipEntry ze = zf.getEntry(path);
            if (ze != null) {
                try (InputStream in = zf.getInputStream(ze)) {
                    System.out.println(new String(in.readAllBytes(), "UTF-8"));
                }
            } else {
                System.out.println("Entry not found: " + path);
            }
        }
    }
}
