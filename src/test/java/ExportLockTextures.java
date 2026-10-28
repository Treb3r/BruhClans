import java.io.*;
import java.util.zip.*;
import java.nio.file.*;

public class ExportLockTextures {
    public static void main(String[] args) throws Exception {
        File zip = new File("C:/Users/uilya/OneDrive/Desktop/ResoursePack.zip");
        File outDir = new File("scratch/lock_textures");
        outDir.mkdirs();
        try (ZipFile zf = new ZipFile(zip)) {
            for (int cmd = 390; cmd <= 405; cmd++) {
                // Find model
                // Let's check entry assets/minecraft/textures/items/<hash>.png
            }
            // Let's look up specific hashes
            String[] hashes = {
                "5d1e208145e3ad2b0e30359a", // 394
                "c71814d8d81f8eab3bec9d67", // 395
                "e8682026407fc700367a8c0c", // 396
                "5bdb0c361ef9ec56f6affe80", // 397
                "30edecb9da2e73312706f651", // 398
                "5b5b014f4302cfe824e44538", // 399
                "c373dc67f0b3052a3bc1110e", // 400
                "b3abaec99a236369eb09e3ac", // 401
                "b56252238ee6ac46f86a8d17", // 402
                "3c2b9faff6c61da412b6aae1"  // 403
            };
            for (int i = 0; i < hashes.length; i++) {
                String h = hashes[i];
                ZipEntry ze = zf.getEntry("assets/minecraft/textures/items/" + h + ".png");
                if (ze != null) {
                    Files.copy(zf.getInputStream(ze), new File(outDir, (394 + i) + "_" + h + ".png").toPath(), StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("Extracted CMD " + (394 + i));
                } else {
                    System.out.println("Not found: " + h);
                }
            }
        }
    }
}
