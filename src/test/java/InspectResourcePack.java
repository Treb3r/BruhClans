import java.io.*;
import java.util.zip.*;

public class InspectResourcePack {
    public static void main(String[] args) throws Exception {
        File zip = new File("C:/Users/uilya/OneDrive/Desktop/ResoursePack.zip");
        if (!zip.exists()) {
            System.out.println("Zip not found: " + zip.getAbsolutePath());
            return;
        }
        try (ZipFile zf = new ZipFile(zip)) {
            zf.stream().forEach(e -> {
                String name = e.getName();
                if (name.contains("font") || name.contains("levels") || name.contains("level") || name.contains("clan") || name.contains("castle")) {
                    System.out.println("ENTRY: " + name);
                }
            });
        }
    }
}
