package t.me.bruhstudio;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class SkinHelper {
    private static final Map<String, PlayerProfile> CACHE = new ConcurrentHashMap<>();
    private static final Set<String> PENDING = ConcurrentHashMap.newKeySet();
    private static File storageFile;
    private static YamlConfiguration storage;
    private static BruhFunClansPlugin plugin;

    public static void init(BruhFunClansPlugin pl) {
        plugin = pl;
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
        storageFile = new File(plugin.getDataFolder(), "skins_cache.yml");
        if (storageFile.exists()) {
            storage = YamlConfiguration.loadConfiguration(storageFile);
            for (String key : storage.getKeys(false)) {
                String val = storage.getString(key + ".value");
                String sig = storage.getString(key + ".sig");
                String uuidStr = storage.getString(key + ".uuid");
                if (val != null && !val.isEmpty()) {
                    UUID u = UUID.randomUUID();
                    if (uuidStr != null && !uuidStr.isEmpty()) {
                        try {
                            u = UUID.fromString(uuidStr);
                        } catch (IllegalArgumentException ignored) {}
                    }
                    PlayerProfile p = Bukkit.createProfile(u, key);
                    p.setProperty(new ProfileProperty("textures", val, sig));
                    CACHE.put(key.toLowerCase(Locale.ROOT), p);
                }
            }
        } else {
            storage = new YamlConfiguration();
        }

        // Seed with verified skins for specific known players if not yet present
        seedSkin("v_potoke_treb3r", UUID.fromString("3ca00591-0c6d-4357-b839-9fea66388480"),
                "eyJ0aW1lc3RhbXAiOjE3NjM3NDAyMDYzMjksInByb2ZpbGVJZCI6IjNjYTAwNTkxMGM2ZDQzNTdiODM5OWZlYTY2Mzg4NDgwIiwicHJvZmlsZU5hbWUiOiJ2X3BvdG9rZV90cmViM3IiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzQ3ZmYzZDcwMzI4ZmE0YTBlZjQ1ODdmNzlhMGE1ZDI2NTJjZTFjYWE0YjNlZGI4NmViOGRlMTA4ZDJiZjlhNTIifX19", null);
        seedSkin("Rekmodde3", UUID.fromString("267ff0d8-bacb-3cc7-9608-721ee10a09f1"),
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDY3MGM1MzQ0MzFlNzdlMjAxYjE3YjYyMDkzZmIxZDA2MzcxN2RmM2I4ZGYwZmM0OTcyMzM3NzdkMTk1YzZmMyJ9fX0=", null);
        seedSkin("Rekmodde2", UUID.fromString("4929505e-c851-38ed-835e-934035e245f1"),
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjg2YTFmODE4ZWI4NTI4N2YzZGQ2YzMyMTA0MGY3YjljNmQzNzUzMzgxYTdkYjgwNWI2M2JjMjZjYjBmNGZmMyJ9fX0=", null);
    }

    private static void seedSkin(String name, UUID uuid, String value, String sig) {
        String lower = name.toLowerCase(Locale.ROOT);
        if (!CACHE.containsKey(lower)) {
            PlayerProfile p = Bukkit.createProfile(uuid, name);
            p.setProperty(new ProfileProperty("textures", value, sig));
            CACHE.put(lower, p);
            saveProfile(name, p);
        }
    }

    public static void cachePlayer(Player player) {
        if (player == null) return;
        PlayerProfile profile = player.getPlayerProfile();
        if (profile != null && profile.hasTextures()) {
            CACHE.put(player.getName().toLowerCase(Locale.ROOT), profile);
            saveProfile(player.getName(), profile);
        }
    }

    public static void applySkin(SkullMeta meta, String playerName) {
        if (meta == null || playerName == null || playerName.isEmpty()) return;
        String lower = playerName.toLowerCase(Locale.ROOT);

        Player online = Bukkit.getPlayerExact(playerName);
        if (online != null) {
            PlayerProfile p = online.getPlayerProfile();
            if (p != null && p.hasTextures()) {
                CACHE.put(lower, p);
                meta.setPlayerProfile(p);
                saveProfile(playerName, p);
                return;
            }
        }

        PlayerProfile cached = CACHE.get(lower);
        if (cached != null) {
            meta.setPlayerProfile(cached);
            return;
        }

        // Try offline player fallback initially, and trigger async Mojang lookup
        meta.setOwningPlayer(Bukkit.getOfflinePlayer(playerName));

        if (plugin != null && plugin.isEnabled() && PENDING.add(lower)) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                try {
                    PlayerProfile profile = Bukkit.createProfile(playerName);
                    if (profile.complete(true, true) && profile.hasTextures()) {
                        CACHE.put(lower, profile);
                        saveProfile(playerName, profile);
                    }
                } catch (Exception ignored) {
                } finally {
                    PENDING.remove(lower);
                }
            });
        }
    }

    private static synchronized void saveProfile(String playerName, PlayerProfile profile) {
        if (storage == null || storageFile == null) return;
        for (ProfileProperty prop : profile.getProperties()) {
            if ("textures".equals(prop.getName())) {
                storage.set(playerName + ".value", prop.getValue());
                storage.set(playerName + ".sig", prop.getSignature());
                storage.set(playerName + ".uuid", profile.getId() != null ? profile.getId().toString() : "");
                try {
                    storage.save(storageFile);
                } catch (Exception ignored) {}
                break;
            }
        }
    }
}
