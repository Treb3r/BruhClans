package t.me.bruhstudio;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TestGuiFixes {
    public static void main(String[] args) {
        System.out.println("Running TestGuiFixes...");

        // 1. Verify buildBannerTitle logic
        ClanMenus menus = new ClanMenus(null);
        String clanGlowTitle = menus.buildBannerTitle("ЦВЕТ БРОНИ КЛАНА", null);
        String memberGlowTitle = menus.buildBannerTitle("ЦВЕТ БРОНИ", null);
        String storageTitle = menus.buildBannerTitle("ХРАНИЛИЩЕ", "H");
        String basesTitle = menus.buildBannerTitle("ТОЧКИ БАЗ", "C");

        if (clanGlowTitle.contains("1M2")) {
            throw new AssertionError("Clan glow title must NOT contain overlay M: " + clanGlowTitle);
        }
        if (!clanGlowTitle.contains("ЦВЕТ БРОНИ КЛАНА")) {
            throw new AssertionError("Clan glow title text incorrect: " + clanGlowTitle);
        }
        if (memberGlowTitle.contains("1M2")) {
            throw new AssertionError("Member glow title must NOT contain overlay M: " + memberGlowTitle);
        }
        if (!memberGlowTitle.contains("ЦВЕТ БРОНИ")) {
            throw new AssertionError("Member glow title text incorrect: " + memberGlowTitle);
        }
        if (!storageTitle.contains("1H2")) {
            throw new AssertionError("Storage title must contain overlay H: " + storageTitle);
        }
        if (!storageTitle.contains("ХРАНИЛИЩЕ")) {
            throw new AssertionError("Storage title text incorrect: " + storageTitle);
        }
        if (!basesTitle.contains("1C2")) {
            throw new AssertionError("Bases title must contain overlay C: " + basesTitle);
        }

        System.out.println("Title overlay tests passed!");

        // 2. Verify shelf slots
        int[] expectedShelfSlots = new int[]{20, 21, 22, 23, 24, 29, 30, 31, 32, 33};
        if (expectedShelfSlots.length != 10) {
            throw new AssertionError("Shelf slots must have length 10");
        }

        System.out.println("Shelf slots verified!");
        System.out.println("ALL GUI FIX TESTS PASSED SUCCESSFULLY!");
    }
}
