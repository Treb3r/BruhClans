import java.io.*;

public class PrintAllRewards {
    public static void main(String[] args) throws Exception {
        File f = new File("C:/Users/uilya/OneDrive/Desktop/Treb3rFuntime/plugins/BruhClans/features/rewards.yml");
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            int lvl = 0;
            while ((line = br.readLine()) != null) {
                if (line.trim().startsWith("level:")) {
                    lvl = Integer.parseInt(line.trim().substring(6).trim());
                    System.out.println("=== LEVEL " + lvl + " ===");
                }
                if (line.contains("Цвет") || line.contains("цветов") || line.contains("addColor") || line.contains("Цвета")) {
                    System.out.println("  " + line.trim());
                }
            }
        }
    }
}
