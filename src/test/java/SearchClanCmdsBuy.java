import java.io.*;

public class SearchClanCmdsBuy {
    public static void main(String[] args) {
        File f = new File("src/main/java/t/me/bruhstudio/ClanCommands.java");
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            int ln = 0;
            while ((line = br.readLine()) != null) {
                ln++;
                if (line.contains("buyLevel") || line.contains("rating") || line.contains("level =") || line.contains("level++") || line.contains("addExperience")) {
                    System.out.println(ln + ": " + line.trim());
                }
            }
        } catch (Exception e) {}
    }
}
