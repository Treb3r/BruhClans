import java.io.*;
import java.util.*;

public class RunVerifyWithClasspath {
    public static void main(String[] args) throws Exception {
        List<String> cp = new ArrayList<>();
        cp.add("target/classes");
        cp.add("target/test-classes");

        File libDir = new File("test_server/libraries");
        if (libDir.exists()) {
            addJars(libDir, cp);
        }
        File testServer = new File("test_server");
        for (File f : testServer.listFiles()) {
            if (f.getName().endsWith(".jar")) {
                cp.add(f.getPath());
            }
        }
        String fullCp = String.join(File.pathSeparator, cp);
        ProcessBuilder pb = new ProcessBuilder("java", "-cp", fullCp, "VerifyArmorAndXP");
        pb.redirectErrorStream(true);
        Process p = pb.start();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        }
        int code = p.waitFor();
        System.out.println("Exit code: " + code);
        if (code != 0) System.exit(code);
    }

    static void addJars(File dir, List<String> cp) {
        File[] files = dir.listFiles();
        if (files == null) return;
        for (File f : files) {
            if (f.isDirectory()) addJars(f, cp);
            else if (f.getName().endsWith(".jar")) cp.add(f.getPath());
        }
    }
}
