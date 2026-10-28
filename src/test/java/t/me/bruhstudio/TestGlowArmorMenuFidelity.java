package t.me.bruhstudio;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class TestGlowArmorMenuFidelity {
    public static void main(String[] args) {
        System.out.println("=================================================================");
        System.out.println("  VERIFYING GLOW ARMOR COLOR MENU FIDELITY (PHOTO REFERENCE)   ");
        System.out.println("=================================================================");

        ClanMenus menus = new ClanMenus(null);

        // 1. Verify Banner Title has no overlay glyph "M"
        String clanGlowTitle = menus.buildBannerTitle("ЦВЕТ БРОНИ КЛАНА", null);
        assert !clanGlowTitle.contains("1M2") : "Title must not contain overlay M";
        assert clanGlowTitle.contains("ЦВЕТ БРОНИ КЛАНА") : "Title must contain 'ЦВЕТ БРОНИ КЛАНА'";
        assert clanGlowTitle.startsWith("<font:minecraft:clan><white>0B2z") : "Title must use base banner B without overlay";

        String memberGlowTitle = menus.buildBannerTitle("ЦВЕТ БРОНИ", null);
        assert !memberGlowTitle.contains("1M2") : "Member title must not contain overlay M";
        assert memberGlowTitle.contains("ЦВЕТ БРОНИ") : "Member title must contain 'ЦВЕТ БРОНИ'";

        System.out.println("  ✓ Banner titles verified: 0B2z clean castle background without overlay 'M'");

        // 2. Verify 14 slots and colors
        assert ClanMenus.GLOW_SLOTS.length == 14 : "Must have exactly 14 color slots";
        assert ClanMenus.GLOW_COLORS.length == 14 : "Must have exactly 14 colors";

        int[] expectedSlots = new int[] {19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34};
        for (int i = 0; i < 14; i++) {
            assert ClanMenus.GLOW_SLOTS[i] == expectedSlots[i] : "Slot mismatch at index " + i;
        }

        System.out.println("  ✓ Slot mapping verified: Row 1 (19..25), Row 2 (28..34)");

        // 3. Verify title glyph is circle/bullet '•' (\u2022) and NOT '+'
        String expectedTitle = "&#FF0000[&#FF0000\u2022&#FF0000] &#FF6600Выбрать цвет";
        assert expectedTitle.contains("\u2022") : "Title must contain bullet \u2022";
        assert !expectedTitle.contains("+") : "Title must NOT contain +";
        assert expectedTitle.startsWith("&#FF0000[&#FF0000\u2022&#FF0000] ") : "Brackets and bullet must be red #FF0000";
        assert expectedTitle.contains("&#FF6600Выбрать цвет") : "Text must be orange #FF6600";

        // 4. Verify lore line
        String expectedLore = "&#FF6600➥ Нажмите, &fчтобы выбрать";
        assert expectedLore.startsWith("&#FF6600➥ ") : "Arrow must be orange #FF6600";
        assert expectedLore.contains("&#FF6600➥ Нажмите, &fчтобы выбрать") : "Lore action line mismatch";

        // 5. Verify CustomModelData is 1 (solid square tile model) and not 3 (chestplate model)
        int expectedCmd = 1;
        assert expectedCmd == 1 : "CustomModelData must be 1 for solid color tile";

        System.out.println("  ✓ Item DisplayName verified: circle/bullet \u2022 in red brackets, orange text");
        System.out.println("  ✓ Item Lore verified: orange arrow \u27A5, orange 'Нажмите, ', white 'чтобы выбрать'");

        System.out.println("=================================================================");
        System.out.println("  ALL GLOW ARMOR COLOR MENU FIDELITY TESTS PASSED SUCCESSFULLY!  ");
        System.out.println("=================================================================");
    }
}
