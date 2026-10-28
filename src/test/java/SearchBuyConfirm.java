import java.io.*;

public class SearchBuyConfirm {
    public static void main(String[] args) throws Exception {
        File f = new File("src/main/java/t/me/bruhstudio/ClanMenus.java");
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            int ln = 0;
            while ((line = br.readLine()) != null) {
                ln++;
                if (line.contains("buyConfirm")) {
                    System.out.println(ln + ": " + line.trim());
                }
            }
        }
    }
}
