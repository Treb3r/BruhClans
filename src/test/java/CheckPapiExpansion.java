import java.io.*;

public class CheckPapiExpansion {
    public static void main(String[] args) {
        File dir = new File("src/main/java/t/me/bruhstudio");
        for (File f : dir.listFiles()) {
            if (f.getName().endsWith(".java")) {
                try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        if (line.contains("PlaceholderExpansion") || line.contains("register()")) {
                            System.out.println(f.getName() + " -> " + line.trim());
                        }
                    }
                } catch (Exception e) {}
            }
        }
    }
}
