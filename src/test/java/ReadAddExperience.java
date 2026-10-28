import java.io.*;

public class ReadAddExperience {
    public static void main(String[] args) {
        File f = new File("src/main/java/t/me/bruhstudio/ClanService.java");
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            int ln = 0;
            while ((line = br.readLine()) != null) {
                ln++;
                if (line.contains("addExperience") || line.contains("experience")) {
                    System.out.println(ln + ": " + line.trim());
                }
            }
        } catch (Exception e) {}
    }
}
