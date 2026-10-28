import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import t.me.bruhstudio.Clan;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.lang.reflect.Method;
import java.util.List;

public class VerifyTopToggleAudit {
    public static void main(String[] args) throws Exception {
        System.out.println("=== RUNNING RED-TEAM ADVERSARIAL AUDIT FOR TOP TOGGLE ===");

        File clanMenusFile = new File("src/main/java/t/me/bruhstudio/ClanMenus.java");
        if (!clanMenusFile.exists()) {
            throw new AssertionError("ClanMenus.java not found!");
        }
        String source = Files.readString(clanMenusFile.toPath(), StandardCharsets.UTF_8);

        // 1. Verify slot 7 in main(...) is completely gone
        int mainMethodStart = source.indexOf("public void main(Player player, Clan clan)");
        if (mainMethodStart == -1) throw new AssertionError("main(...) method not found");
        int mainMethodEnd = source.indexOf("public static String gradient", mainMethodStart);
        String mainCode = source.substring(mainMethodStart, mainMethodEnd);

        if (mainCode.contains("setItem(7,") || mainCode.contains("setItem( 7,")) {
            throw new AssertionError("FAILED: slot 7 is still set in main(Player, Clan)!");
        }
        if (mainCode.toLowerCase().contains("hopper")) {
            throw new AssertionError("FAILED: hopper found in main(Player, Clan)!");
        }
        System.out.println("PASS: Slot 7 and hopper are completely absent from main(Player, Clan).");

        // 2. Verify case "main": in onInventoryClick has no handler for slot 7
        int caseMain = source.indexOf("case \"main\":");
        if (caseMain == -1) throw new AssertionError("case \"main\": not found");
        int caseTop = source.indexOf("case \"top\":", caseMain);
        String caseMainCode = source.substring(caseMain, caseTop);

        if (caseMainCode.contains("slot == 7")) {
            throw new AssertionError("FAILED: slot 7 handler still exists under case \"main\":!");
        }
        System.out.println("PASS: No slot 7 handler under case \"main\":.");

        // 3. Verify case "top": in onInventoryClick handles slot 16 and NOT slot 7
        int caseMembers = source.indexOf("case \"members\":", caseTop);
        String caseTopCode = source.substring(caseTop, caseMembers);

        if (caseTopCode.contains("slot == 7")) {
            throw new AssertionError("FAILED: ghost slot 7 handler exists under case \"top\":!");
        }
        if (!caseTopCode.contains("slot == 16")) {
            throw new AssertionError("FAILED: slot 16 handler missing under case \"top\":!");
        }
        if (!caseTopCode.contains("clan.topVisible = !clan.topVisible;")) {
            throw new AssertionError("FAILED: topVisible toggle missing under case \"top\":!");
        }
        if (!caseTopCode.contains("store().changed();") || !caseTopCode.contains("store().flush();")) {
            throw new AssertionError("FAILED: store persistence missing under case \"top\":!");
        }
        if (!caseTopCode.contains("top(player, view.fromMenu);")) {
            throw new AssertionError("FAILED: menu refresh missing under case \"top\":!");
        }
        System.out.println("PASS: case \"top\": correctly and exclusively handles slot 16 with persistence and refresh.");

        // 4. Verify top(...) method places toggle in slot 16
        int topMethodStart = source.indexOf("public void top(Player player, boolean fromMenu)");
        int topMethodEnd = source.indexOf("private ItemStack createTopTrophy", topMethodStart);
        String topCode = source.substring(topMethodStart, topMethodEnd);

        if (!topCode.contains("inventory.setItem(16, this.buildMainTopToggleItem(playerClan));")) {
            throw new AssertionError("FAILED: top(...) does not place toggle in slot 16!");
        }
        if (!topCode.contains("playerClan.owns(player.getName())")) {
            throw new AssertionError("FAILED: top(...) does not check playerClan.owns(...)!");
        }
        System.out.println("PASS: top(...) places toggle in slot 16 only for clan owners.");

        // 5. Verify buildMainTopToggleItem
        int buildToggleStart = source.indexOf("private ItemStack buildMainTopToggleItem(Clan clan)");
        int buildToggleEnd = source.indexOf("private ItemStack buildMainStorageItem", buildToggleStart);
        String toggleCode = source.substring(buildToggleStart, buildToggleEnd);

        if (toggleCode.toLowerCase().contains("material.hopper")) {
            throw new AssertionError("FAILED: Material.HOPPER used in buildMainTopToggleItem!");
        }
        if (!toggleCode.contains("Material.PAPER")) {
            throw new AssertionError("FAILED: Material.PAPER not used in buildMainTopToggleItem!");
        }
        if (!toggleCode.contains("visible ? 414 : 417")) {
            throw new AssertionError("FAILED: CustomModelData visible ? 414 : 417 missing!");
        }
        if (!toggleCode.contains("for (ItemFlag flag : ItemFlag.values())") || !toggleCode.contains("addItemFlags(flag)")) {
            throw new AssertionError("FAILED: Not all ItemFlags are applied!");
        }
        System.out.println("PASS: buildMainTopToggleItem uses Material.PAPER, models 414/417, and all ItemFlags.");

        System.out.println("\n=== ALL ADVERSARIAL RED-TEAM CHECKS PASSED WITH 100% SUCCESS! ===");
    }
}
