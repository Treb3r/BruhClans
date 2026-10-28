import java.nio.file.*;

public class CopyJar {
    public static void main(String[] args) throws Exception {
        Files.copy(Paths.get("target/bruhstudio-1.0.0.jar"), Paths.get("test_server/plugins/BruhClans.jar"), StandardCopyOption.REPLACE_EXISTING);
        System.out.println("COPIED SUCCESSFULLY");
    }
}
