import java.io.File;

public class FindPaperJson {
    public static void main(String[] args) {
        search(new File("."));
        search(new File("C:/Users/uilya/.gemini"));
    }
    static void search(File dir) {
        if (dir == null || !dir.exists()) return;
        File[] files = dir.listFiles();
        if (files == null) return;
        for (File f : files) {
            if (f.isDirectory() && !f.getName().equals(".git")) {
                search(f);
            } else if (f.getName().equals("paper.json") || f.getName().endsWith(".zip")) {
                System.out.println("FOUND: " + f.getAbsolutePath());
            }
        }
    }
}
