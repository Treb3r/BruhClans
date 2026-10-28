import java.io.*;

public class ListLevelScreenshots {
    public static void main(String[] args) {
        File dir = new File("Уровень");
        File[] files = dir.listFiles((d, n) -> n.endsWith(".jpg"));
        for (int i = 0; i < files.length; i++) {
            System.out.println(i + ": " + files[i].getName());
        }
    }
}
