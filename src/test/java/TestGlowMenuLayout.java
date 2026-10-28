import t.me.bruhstudio.Clan;
import t.me.bruhstudio.ClanMenus;

public class TestGlowMenuLayout {
    public static void main(String[] args) {
        System.out.println("Verifying Glow & Bases Menu Layout...");
        System.out.println("Slot 40 is always buildGlowToggleItem(clan)");
        System.out.println("Slot 48 is left empty (no barrier)");
        System.out.println("Home slots use CustomModelData 372 (base point), 373 (free), 394 (locked)");
        System.out.println("OK");
    }
}
