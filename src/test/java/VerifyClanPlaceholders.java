import java.lang.reflect.Field;
import java.util.Locale;
import sun.misc.Unsafe;
import t.me.bruhstudio.BruhFunClansPlugin;
import t.me.bruhstudio.Clan;
import t.me.bruhstudio.ClanPlaceholders;
import t.me.bruhstudio.ClanService;
import t.me.bruhstudio.ClanStore;
import t.me.bruhstudio.ConfigFiles;

public class VerifyClanPlaceholders {
    public static void main(String[] args) throws Exception {
        System.out.println("Running VerifyClanPlaceholders adversarial test suite...");

        Field f = Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        Unsafe unsafe = (Unsafe) f.get(null);

        BruhFunClansPlugin plugin = (BruhFunClansPlugin) unsafe.allocateInstance(BruhFunClansPlugin.class);
        ConfigFiles config = (ConfigFiles) unsafe.allocateInstance(ConfigFiles.class);
        Field filesField = ConfigFiles.class.getDeclaredField("files");
        filesField.setAccessible(true);
        filesField.set(config, new java.util.HashMap<String, org.bukkit.configuration.file.YamlConfiguration>());
        Field pluginField = ConfigFiles.class.getDeclaredField("plugin");
        pluginField.setAccessible(true);
        pluginField.set(config, plugin);
        ClanStore store = (ClanStore) unsafe.allocateInstance(ClanStore.class);
        Field byPlayerField = ClanStore.class.getDeclaredField("byPlayer");
        byPlayerField.setAccessible(true);
        byPlayerField.set(store, new java.util.LinkedHashMap<String, Clan>());
        Field byNameField = ClanStore.class.getDeclaredField("byName");
        byNameField.setAccessible(true);
        byNameField.set(store, new java.util.LinkedHashMap<String, Clan>());
        ClanService service = new ClanService(plugin, store);

        Field configField = BruhFunClansPlugin.class.getDeclaredField("config");
        configField.setAccessible(true);
        configField.set(plugin, config);

        Field serviceField = BruhFunClansPlugin.class.getDeclaredField("service");
        serviceField.setAccessible(true);
        serviceField.set(plugin, service);

        Field storeField = BruhFunClansPlugin.class.getDeclaredField("store");
        storeField.setAccessible(true);
        storeField.set(plugin, store);

        ClanPlaceholders papi = new ClanPlaceholders(plugin);

        // Test 1: Null player / null parameters
        assertEquals("", papi.onPlaceholderRequest(null, "chat"), "Null player 'chat'");
        assertEquals("", papi.onPlaceholderRequest(null, "name_format"), "Null player 'name_format'");
        assertEquals("", papi.onRequest((org.bukkit.OfflinePlayer) null, "chat"), "Null offline player 'chat'");
        assertEquals("", papi.onPlaceholderRequest(null, null), "Null params");
        assertEquals("", papi.onRequest((org.bukkit.OfflinePlayer) null, null), "Null offline params");

        // Test 2: Player with NO clan
        assertEquals("", papi.resolvePlaceholder("NoClanPlayer", "chat"), "No clan 'chat'");
        assertEquals("", papi.resolvePlaceholder("NoClanPlayer", "name_format"), "No clan 'name_format'");
        assertEquals("", papi.resolvePlaceholder("NoClanPlayer", "name_format_no"), "No clan 'name_format_no'");

        // Test 3: Player WITH clan default uncolored 'jopaa'
        Clan clan1 = new Clan();
        clan1.name = "jopaa";
        clan1.displayName = "jopaa";
        store.byPlayer.put("player1", clan1);

        String chatVal = papi.resolvePlaceholder("player1", "chat");
        assertEquals("§6jopaa ", chatVal, "Uncolored clan 'chat' with gold prefix and trailing space");

        String nameFormatVal = papi.resolvePlaceholder("player1", "name_format");
        assertEquals("§6jopaa ", nameFormatVal, "Uncolored clan 'name_format' with gold prefix and trailing space");

        String noSpaceVal = papi.resolvePlaceholder("player1", "name_format_no");
        assertEquals("§6jopaa", noSpaceVal, "Uncolored clan 'name_format_no' without space");

        // Test 4: Player WITH clan already colored with &b
        Clan clan2 = new Clan();
        clan2.name = "azure";
        clan2.displayName = "&bAzure";
        store.byPlayer.put("player2", clan2);

        String coloredVal = papi.resolvePlaceholder("player2", "chat");
        assertEquals("§bAzure ", coloredVal, "Pre-colored &b clan 'chat'");

        // Test 5: Player WITH clan hex color &#FFD700 or <#FFD700>
        Clan clan3 = new Clan();
        clan3.name = "gold";
        clan3.displayName = "<#FFD700>GoldClan";
        store.byPlayer.put("player3", clan3);

        String hexVal = papi.resolvePlaceholder("player3", "chat");
        assertNotNull(hexVal, "Hex clan");
        assertTrue(hexVal.endsWith("GoldClan "), "Hex clan should end with GoldClan and trailing space");
        assertFalse(hexVal.startsWith("§6"), "Hex clan should not be double-prefixed with §6");

        // Test 6: Verify LeakedChat format strings
        String localTemplate = "&eⓁ %bruhclans_chat%[%vault_prefix%] &f%player_name% ⇨ %msg%";
        
        // When player has no clan:
        String renderedNoClan = localTemplate.replace("%bruhclans_chat%", papi.resolvePlaceholder("NoClanPlayer", "chat"));
        assertEquals("&eⓁ [%vault_prefix%] &f%player_name% ⇨ %msg%", renderedNoClan, "Clean render without double spaces when player has no clan");

        // When player has clan 'jopaa':
        String renderedWithClan = localTemplate.replace("%bruhclans_chat%", papi.resolvePlaceholder("player1", "chat"));
        assertEquals("&eⓁ §6jopaa [%vault_prefix%] &f%player_name% ⇨ %msg%", renderedWithClan, "Clean render with clan 'jopaa'");

        System.out.println("ALL ADVERSARIAL TESTS PASSED SUCCESSFULLY!");
    }

    private static void assertEquals(String expected, String actual, String testName) {
        if (!java.util.Objects.equals(expected, actual)) {
            throw new AssertionError("FAILED [" + testName + "]: expected [" + expected + "] but got [" + actual + "]");
        }
        System.out.println("PASS: " + testName + " => [" + actual + "]");
    }

    private static void assertTrue(boolean cond, String testName) {
        if (!cond) {
            throw new AssertionError("FAILED [" + testName + "]: condition is false");
        }
        System.out.println("PASS: " + testName);
    }

    private static void assertFalse(boolean cond, String testName) {
        if (cond) {
            throw new AssertionError("FAILED [" + testName + "]: condition is true");
        }
        System.out.println("PASS: " + testName);
    }

    private static void assertNotNull(Object obj, String testName) {
        if (obj == null) {
            throw new AssertionError("FAILED [" + testName + "]: object is null");
        }
        System.out.println("PASS: " + testName);
    }
}
