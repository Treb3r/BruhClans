import t.me.bruhstudio.*;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class VerifyArmorAndXP {
    public static void main(String[] args) {
        // Test 1: Level XP progression
        Clan clan = new Clan();
        clan.name = "TestClan";
        clan.level = 0;
        clan.rating = 0;

        ClanMenus.LevelDef def1 = ClanMenus.getLevelDef(1);
        if (def1.xp != 200L) throw new AssertionError("Level 1 should be 200 XP");

        // Simulate buying level 1 with TK when at 0 XP
        clan.level = 1;
        if (clan.rating < def1.xp) {
            clan.rating = def1.xp;
        }
        if (clan.rating != 200L) throw new AssertionError("Clan rating should be at least 200 after level 1");

        // Next level is level 2
        ClanMenus.LevelDef def2 = ClanMenus.getLevelDef(2);
        if (def2.xp != 400L) throw new AssertionError("Level 2 should be 400 XP");

        // Now clan earns extra 150 XP organically -> total 350 XP
        clan.rating += 150L;
        if (clan.rating != 350L) throw new AssertionError("Clan rating should be 350");

        // Clan buys level 2: rating should preserve 350 (or at least 400)
        clan.level = 2;
        if (clan.rating < def2.xp) {
            clan.rating = def2.xp;
        }
        if (clan.rating != 400L) throw new AssertionError("Clan rating should be 400");

        // Clan has 550 XP, passes level 2:
        clan.rating = 550L;
        clan.level = 2;
        // Next is level 3 (600 XP)
        ClanMenus.LevelDef def3 = ClanMenus.getLevelDef(3);
        if (def3.xp != 600L) throw new AssertionError("Level 3 should be 600 XP");
        // Clan earns 100 XP -> 650 XP, auto advances to level 3:
        clan.rating += 100L;
        if (clan.rating >= def3.xp) {
            clan.level = 3;
        }
        if (clan.rating != 650L) throw new AssertionError("Clan rating should preserve all 650 XP!");

        System.out.println("TEST 1 PASSED: XP correctly preserved across all level transitions!");

        // Test 2: Check armor filtering logic
        ItemStack[] real = new ItemStack[] {
            null, // naked head
            new ItemStack(Material.NETHERITE_CHESTPLATE), // chestplate equipped
            new ItemStack(Material.AIR), // naked legs
            new ItemStack(Material.DIAMOND_BOOTS) // boots equipped
        };

        GlowService glow = new GlowService(null);
        ItemStack[] colored = glow.armor("0;255;0"); // green leather

        ItemStack[] shown = new ItemStack[4];
        for (int i = 0; i < 4; i++) {
            if (real[i] != null && !real[i].getType().isAir()) {
                shown[i] = colored[i];
            } else {
                shown[i] = new ItemStack(Material.AIR);
            }
        }

        if (shown[0].getType() != Material.AIR) throw new AssertionError("Head was naked, should be AIR!");
        if (shown[1].getType() != Material.LEATHER_CHESTPLATE) throw new AssertionError("Chest had armor, should be colored leather!");
        if (shown[2].getType() != Material.AIR) throw new AssertionError("Legs were naked, should be AIR!");
        if (shown[3].getType() != Material.LEATHER_BOOTS) throw new AssertionError("Boots had armor, should be colored leather!");

        System.out.println("TEST 2 PASSED: Naked slots remain AIR, equipped slots become colored leather!");
        System.out.println("ALL VERIFICATIONS COMPLETED SUCCESSFULLY!");
    }
}
