import java.io.*;
import java.util.zip.*;

public class FindLelDesignJar {
    public static void main(String[] args) throws Exception {
        File dir = new File("C:/Users/uilya/OneDrive/Desktop/Treb3rFuntime/plugins");
        File[] jars = dir.listFiles((d, n) -> n.endsWith(".jar"));
        if (jars == null) return;
        for (File jar : jars) {
            try (ZipFile zf = new ZipFile(jar)) {
                zf.stream().forEach(e -> {
                    String name = e.getName().toLowerCase();
                    if (name.contains("leldesign") || name.contains("bruhdesign") || name.contains("design")) {
                        System.out.println("FOUND in " + jar.getName() + " -> " + e.getName());
                    }
                });
            } catch (Exception ignored) {}
        }
    }
}
