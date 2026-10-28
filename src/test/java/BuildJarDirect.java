import java.io.*;
import java.nio.file.*;
import java.util.zip.*;

public class BuildJarDirect {
    public static void main(String[] args) throws Exception {
        Path classDir = Paths.get("target/classes");
        Path jarPath = Paths.get("target/bruhstudio-1.0.0.jar");
        Path testServerJar = Paths.get("test_server/plugins/BruhClans.jar");
        Path userServerJar = Paths.get("C:/Users/uilya/OneDrive/Desktop/Treb3rFuntime/plugins/bruhclans-1.0.0.jar");

        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(jarPath.toFile()))) {
            Files.walk(classDir).forEach(p -> {
                if (!Files.isDirectory(p)) {
                    String rel = classDir.relativize(p).toString().replace("\\", "/");
                    try {
                        zos.putNextEntry(new ZipEntry(rel));
                        Files.copy(p, zos);
                        zos.closeEntry();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        System.out.println("BUILT JAR: " + Files.size(jarPath) + " bytes");

        if (Files.exists(testServerJar.getParent())) {
            try {
                Files.copy(jarPath, testServerJar, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("DEPLOYED TO TEST SERVER: " + testServerJar);
            } catch (Exception e) {
                System.out.println("Could not copy to test server: " + e.getMessage());
            }
        }

        if (Files.exists(userServerJar.getParent())) {
            try {
                Files.copy(jarPath, userServerJar, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("DEPLOYED TO USER SERVER: " + userServerJar);
            } catch (Exception e) {
                System.out.println("Could not copy to user server: " + e.getMessage());
            }
        }
    }
}
