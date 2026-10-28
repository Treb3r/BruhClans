import java.nio.file.*;

public class Deploy {
    public static void main(String[] args) throws Exception {
        Path src = Paths.get("target/bruhstudio-1.0.0.jar");
        Path dst = Paths.get("test_server/plugins/BruhClans.jar");
        Files.copy(src, dst, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("COPIED " + Files.size(dst) + " bytes to " + dst);
    }
}
