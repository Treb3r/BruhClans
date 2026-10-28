import java.io.*;

public class SearchMinusEquals {
    public static void main(String[] args) {
        File dir = new File("src/main/java/t/me/bruhstudio");
        for (File f : dir.listFiles()) {
            if (f.getName().endsWith(".java")) {
                try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                    String line;
                    int ln = 0;
                    while ((line = br.readLine()) != null) {
                        ln++;
                        if (line.contains("-=") || line.contains(".rating -")) {
                            System.out.println(f.getName() + ":" + ln + " " + line.trim());
                        }
                    }
                } catch (Exception e) {}
            }
        }
    }
}
