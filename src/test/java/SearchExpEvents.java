import java.io.*;

public class SearchExpEvents {
    public static void main(String[] args) {
        File f = new File("src/main/java/t/me/bruhstudio/ClanEvents.java");
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            int ln = 0;
            while ((line = br.readLine()) != null) {
                ln++;
                if (line.toLowerCase().contains("rating") || line.toLowerCase().contains("level") || line.toLowerCase().contains("exp")) {
                    System.out.println(ln + ": " + line.trim());
                }
            }
        } catch (Exception e) {}
    }
}
