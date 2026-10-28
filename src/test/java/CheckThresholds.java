import java.io.*;
import java.util.zip.*;
import java.util.regex.*;

public class CheckThresholds {
    public static void main(String[] args) throws Exception {
        File zip = new File("C:/Users/uilya/OneDrive/Desktop/ResoursePack.zip");
        try (ZipFile zf = new ZipFile(zip)) {
            ZipEntry ze = zf.getEntry("assets/minecraft/items/paper.json");
            String json = new String(zf.getInputStream(ze).readAllBytes(), "UTF-8");
            Matcher m = Pattern.compile("\\{\"threshold\":(\\d+),\"model\":\\{\"type\":\"[^\"]+\",\"model\":\"([^\"]+)\"\\}\\}").matcher(json);
            while (m.find()) {
                int t = Integer.parseInt(m.group(1));
                if ((t >= 390 && t <= 420) || t == 1) {
                    System.out.println("threshold: " + t + " -> " + m.group(2));
                }
            }
        }
    }
}
