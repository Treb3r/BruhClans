import t.me.bruhstudio.*;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class VerifyLevelMenuAndColors {
    public static void main(String[] args) {
        ConfigFiles cf = new ConfigFiles(null);
        String testInput = "&#00FF00+ &fЦвета названия: &#AAAAAA&&7 &#555555&&8";
        String colored = cf.color(testInput);
        System.out.println("Input: " + testInput);
        System.out.println("Output: " + colored);
        if (!colored.contains("&7") || !colored.contains("&8")) {
            throw new AssertionError("Literal &7 or &8 was lost in color translation!");
        }
        System.out.println("PASS: literal &7 and &8 preserved!");

        ClanMenus menus = new ClanMenus(null);
        String title0 = menus.buildLevelBannerTitle(0);
        String title1 = menus.buildLevelBannerTitle(1);
        String title2 = menus.buildLevelBannerTitle(2);
        System.out.println("Title 0: " + title0);
        System.out.println("Title 1: " + title1);
        System.out.println("Title 2: " + title2);

        if (!title0.contains("0B1D2z") || !title1.contains("0B1X2z") || !title2.contains("0B1Y2z")) {
            throw new AssertionError("Banner titles do not contain castle background glyph 0B1!");
        }
        System.out.println("PASS: Castle background glyph 0B1 is present in all titles!");

        for (int i = 1; i <= 39; i++) {
            ClanMenus.LevelDef def = ClanMenus.getLevelDef(i);
            if (def == null) throw new AssertionError("Missing LevelDef for level " + i);
            for (String perk : def.perks) {
                if (perk.contains("Цвета названия")) {
                    String perkColored = cf.color(perk);
                    if (!perkColored.contains("&")) {
                        throw new AssertionError("Color code perk stripped literal '&': " + perk);
                    }
                    System.out.println("Level " + i + " perk: " + perkColored);
                }
            }
        }
        System.out.println("ALL CHECKS PASSED PERFECTLY!");
    }
}
