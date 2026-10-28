import java.io.*;
import java.util.zip.*;
import java.util.regex.*;

public class Check395396 {
    public static void main(String[] args) throws Exception {
        File zip = new File("C:/Users/uilya/OneDrive/Desktop/ResoursePack.zip");
        try (ZipFile zf = new ZipFile(zip)) {
            ZipEntry ze = zf.getEntry("assets/minecraft/items/paper.json");
            String content;
            try (BufferedReader br = new BufferedReader(new InputStreamReader(zf.getInputStream(ze), "UTF-8"))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
                content = sb.toString();
            }
            
            Pattern p = Pattern.compile("\\{\"threshold\":(\\d+),\"model\":\\{\"type\":\"[^\"]*\",\"model\":\"([^\"]+)\"\\}\\}");
            Matcher m = p.matcher(content);
            while (m.find()) {
                int threshold = Integer.parseInt(m.group(1));
                String modelPath = m.group(2);
                if (threshold >= 370 && threshold <= 410) {
                    // Check model file inside zip
                    String modelFile = "assets/" + (modelPath.contains(":") ? modelPath.replace(":", "/models/") : "minecraft/models/" + modelPath) + ".json";
                    ZipEntry me = zf.getEntry(modelFile);
                    String textures = "";
                    if (me != null) {
                        try (BufferedReader mbr = new BufferedReader(new InputStreamReader(zf.getInputStream(me), "UTF-8"))) {
                            StringBuilder msb = new StringBuilder();
                            String mline;
                            while ((mline = mbr.readLine()) != null) msb.append(mline);
                            textures = msb.toString();
                        }
                    }
                    System.out.println("CMD " + threshold + " -> model: " + modelPath + " (file: " + modelFile + ") -> " + textures);
                }
            }
        }
    }
}
