import java.io.*;
import java.util.zip.*;

public class CheckPaperCMD {
    public static void main(String[] args) throws Exception {
        File zip = new File("ResoursePack.zip");
        try (ZipFile zf = new ZipFile(zip)) {
            ZipEntry ze = zf.getEntry("assets/minecraft/models/item/paper.json");
            if (ze != null) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(zf.getInputStream(ze)))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.contains("custom_model_data")) {
                            System.out.println(line);
                        }
                    }
                }
            } else {
                System.out.println("paper.json not found!");
            }
        }
    }
}
