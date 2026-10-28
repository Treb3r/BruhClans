import java.io.*;
import java.util.zip.*;

public class CheckAllPaperModels {
    public static void main(String[] args) throws Exception {
        File zip = new File("C:/Users/uilya/OneDrive/Desktop/ResoursePack.zip");
        try (ZipFile zf = new ZipFile(zip)) {
            ZipEntry ze = zf.getEntry("assets/minecraft/items/paper.json");
            if (ze != null) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(zf.getInputStream(ze), "UTF-8"))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        if (line.contains("model") || line.contains("custom_model_data")) {
                            System.out.println(line.trim());
                        }
                    }
                }
            } else {
                System.out.println("paper.json not in items");
            }
        }
    }
}
