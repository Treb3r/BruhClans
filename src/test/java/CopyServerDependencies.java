import java.io.*;
import java.nio.file.*;

public class CopyServerDependencies {
    public static void main(String[] args) throws Exception {
        Path src = Paths.get("C:/Users/uilya/OneDrive/Desktop/Treb3rFuntime/plugins");
        Path dst = Paths.get("test_server/plugins");

        // 1. PlaceholderAPI expansions
        Path papiExpSrc = src.resolve("PlaceholderAPI/expansions");
        Path papiExpDst = dst.resolve("PlaceholderAPI/expansions");
        Files.createDirectories(papiExpDst);
        if (Files.exists(papiExpSrc)) {
            try (var stream = Files.list(papiExpSrc)) {
                stream.forEach(f -> {
                    try {
                        Files.copy(f, papiExpDst.resolve(f.getFileName()), StandardCopyOption.REPLACE_EXISTING);
                        System.out.println("Copied expansion: " + f.getFileName());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }

        // 2. PlaceholderAPI config
        copyIfExists(src.resolve("PlaceholderAPI/config.yml"), dst.resolve("PlaceholderAPI/config.yml"));

        // 3. LuckPerms config and databases
        Path lpSrc = src.resolve("LuckPerms");
        Path lpDst = dst.resolve("LuckPerms");
        copyDir(lpSrc, lpDst);

        // 4. BruhDesign folder
        Path bdSrc = src.resolve("BruhDesign");
        Path bdDst = dst.resolve("BruhDesign");
        copyDir(bdSrc, bdDst);

        // 5. BruhFunLootBox jar and folder
        copyIfExists(src.resolve("BruhFunLootBox-1.0.0.jar"), dst.resolve("BruhFunLootBox-1.0.0.jar"));
        copyDir(src.resolve("BruhFunLootBox"), dst.resolve("BruhFunLootBox"));

        // 6. LeakedChat config
        copyIfExists(src.resolve("LeakedChat/config.yml"), dst.resolve("LeakedChat/config.yml"));

        System.out.println("Dependencies copied successfully!");
    }

    private static void copyIfExists(Path s, Path d) throws IOException {
        if (Files.exists(s)) {
            Files.createDirectories(d.getParent());
            Files.copy(s, d, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Copied: " + s.getFileName() + " -> " + d);
        }
    }

    private static void copyDir(Path s, Path d) throws IOException {
        if (!Files.exists(s)) return;
        Files.walk(s).forEach(p -> {
            try {
                Path rel = s.relativize(p);
                Path target = d.resolve(rel);
                if (Files.isDirectory(p)) {
                    Files.createDirectories(target);
                } else {
                    Files.createDirectories(target.getParent());
                    Files.copy(p, target, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        System.out.println("Copied dir: " + s + " -> " + d);
    }
}
