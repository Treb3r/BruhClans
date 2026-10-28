/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.configuration.file.YamlConfiguration
 *  org.bukkit.inventory.ItemStack
 */
package t.me.bruhstudio;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import t.me.bruhstudio.BruhFunClansPlugin;
import t.me.bruhstudio.Clan;

public final class ClanStore {
    public final BruhFunClansPlugin plugin;
    public final Map<String, Clan> byName = new LinkedHashMap<String, Clan>();
    public final Map<String, Clan> byPlayer = new LinkedHashMap<String, Clan>();
    public Connection connection;
    public boolean dirty;

    public ClanStore(BruhFunClansPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        this.connect();
        YamlConfiguration yaml = new YamlConfiguration();
        try (PreparedStatement query = this.connection.prepareStatement("SELECT PAYLOAD FROM BRUHCLANS_V5 WHERE ID = 1");
             ResultSet result = query.executeQuery();){
            if (result.next()) {
                yaml.loadFromString(result.getString(1));
            }
        }
        catch (Exception ex) {
            throw new IllegalStateException("Could not load clan data from database", ex);
        }
        ConfigurationSection root = yaml.getConfigurationSection("clans");
        if (root != null) {
            for (String id : root.getKeys(false)) {
                ConfigurationSection storage;
                ConfigurationSection homes;
                ConfigurationSection section = root.getConfigurationSection(id);
                if (section == null) continue;
                Clan clan = new Clan();
                clan.id = section.getInt("id", 0);
                clan.name = section.getString("name", id);
                clan.displayName = section.getString("displayName", clan.name);
                clan.owner = section.getString("owner", "");
                clan.money = section.getLong("money");
                clan.moneyLimit = section.getLong("moneyLimit");
                clan.rating = section.getLong("rating");
                clan.lengthLimit = section.getLong("lengthLimit");
                clan.level = section.getInt("level", 0);
                clan.memberLimit = section.getInt("memberLimit", 5);
                clan.slotLimit = section.getInt("slotLimit", 2);
                clan.homeLimit = section.getInt("homeLimit", 1);
                if (clan.homeLimit < 1) {
                    clan.homeLimit = 1;
                }
                clan.pvp = section.getBoolean("pvp");
                clan.glow = section.getBoolean("glow");
                clan.glowColor = section.getString("glowColor");
                clan.topVisible = section.getBoolean("topVisible", true);
                clan.colors.addAll(section.getStringList("colors"));
                List<String> hist = section.getStringList("history");
                if (hist != null && !hist.isEmpty()) {
                    clan.history.addAll(hist);
                }
                ConfigurationSection members = section.getConfigurationSection("members");
                if (members != null) {
                    for (Object key : members.getKeys(false)) {
                        ConfigurationSection value = members.getConfigurationSection((String)key);
                        if (value == null) continue;
                        Clan.Member member = new Clan.Member(value.getString("name", (String)key), value.getString("prefix", ""));
                        member.rights.clear();
                        member.joinedAt = value.getLong("joinedAt", System.currentTimeMillis());
                        member.kills = value.getInt("kills", 0);
                        member.deaths = value.getInt("deaths", 0);
                        member.invested = value.getLong("invested", 0L);
                        member.withdrawn = value.getLong("withdrawn", 0L);
                        member.armorColor = value.getString("armorColor", null);
                        for (String right : value.getStringList("rights")) {
                            try {
                                member.rights.add(Clan.Right.valueOf(right));
                            }
                            catch (IllegalArgumentException var17) {
                                this.plugin.getLogger().warning("Unknown clan right: " + right);
                            }
                        }
                        clan.add(member);
                    }
                }
                if (clan.member(clan.owner) == null) {
                    Clan.Member owner = new Clan.Member(clan.owner, this.plugin.config().text("command.ownerPrefix"));
                    owner.rights.add(Clan.Right.ALL);
                    clan.add(owner);
                }
                if ((homes = section.getConfigurationSection("homes")) != null) {
                    for (String key2 : homes.getKeys(false)) {
                        Object value = homes.get(key2);
                        if (!(value instanceof Location)) continue;
                        clan.homes.put(key2, (Location)value);
                    }
                }
                if ((storage = section.getConfigurationSection("storage")) != null) {
                    for (String key : storage.getKeys(false)) {
                        Object value = storage.get(key);
                        if (!(value instanceof ItemStack)) continue;
                        try {
                            clan.storage.put(Integer.parseInt(key), (ItemStack)value);
                        }
                        catch (NumberFormatException numberFormatException) {}
                    }
                }
                ConfigurationSection rentedSec = section.getConfigurationSection("rentedStorage");
                if (rentedSec != null) {
                    for (String key : rentedSec.getKeys(false)) {
                        try {
                            int rSlot = Integer.parseInt(key);
                            long rExp = rentedSec.getLong(key);
                            clan.rentedStorage.put(rSlot, rExp);
                        }
                        catch (NumberFormatException ignored) {}
                    }
                }
                this.add(clan);
            }
            this.dirty = false;
        }
    }

    public void connect() {
        String databaseFile = this.plugin.config().get("settings.yml").getString("database.file", "database.db");
        String url = "jdbc:sqlite:" + new File(this.plugin.getDataFolder(), databaseFile).getAbsolutePath();
        try {
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection(url);
            try (Statement statement = this.connection.createStatement();){
                statement.execute("CREATE TABLE IF NOT EXISTS BRUHCLANS_V5 (ID INTEGER PRIMARY KEY, PAYLOAD TEXT NOT NULL)");
            }
        }
        catch (ClassNotFoundException | SQLException ex) {
            throw new IllegalStateException("Could not connect to SQLite database", ex);
        }
    }

    public void close() {
        if (this.connection != null) {
            try {
                this.connection.close();
            }
            catch (SQLException ex) {
                this.plugin.getLogger().log(Level.WARNING, "Could not close database", ex);
            }
        }
    }

    public Clan byName(String name) {
        return this.byName.get(name.toLowerCase(Locale.ROOT));
    }

    public Clan byPlayer(String player) {
        return this.byPlayer.get(player.toLowerCase(Locale.ROOT));
    }

    public Collection<Clan> all() {
        return Collections.unmodifiableCollection(this.byName.values());
    }

    public void add(Clan clan) {
        this.byName.put(clan.name.toLowerCase(Locale.ROOT), clan);
        for (Clan.Member member : clan.members.values()) {
            this.byPlayer.put(member.name.toLowerCase(Locale.ROOT), clan);
        }
        this.dirty = true;
    }

    public void join(Clan clan, Clan.Member member) {
        clan.add(member);
        this.byPlayer.put(member.name.toLowerCase(Locale.ROOT), clan);
        this.dirty = true;
    }

    public void leave(Clan clan, String player) {
        clan.members.remove(player.toLowerCase(Locale.ROOT));
        this.byPlayer.remove(player.toLowerCase(Locale.ROOT));
        this.dirty = true;
    }

    public void remove(Clan clan) {
        this.byName.remove(clan.name.toLowerCase(Locale.ROOT));
        for (Clan.Member member : clan.members.values()) {
            this.byPlayer.remove(member.name.toLowerCase(Locale.ROOT));
        }
        this.dirty = true;
    }

    public void changed() {
        this.dirty = true;
    }

    public void flush() {
        if (this.dirty) {
            YamlConfiguration yaml = new YamlConfiguration();
            for (Clan clan : this.byName.values()) {
                String path = "clans." + clan.name.toLowerCase(Locale.ROOT);
                yaml.set(path + ".id", (Object)clan.getId());
                yaml.set(path + ".name", (Object)clan.name);
                yaml.set(path + ".displayName", (Object)clan.displayName);
                yaml.set(path + ".owner", (Object)clan.owner);
                yaml.set(path + ".money", (Object)clan.money);
                yaml.set(path + ".moneyLimit", (Object)clan.moneyLimit);
                yaml.set(path + ".rating", (Object)clan.rating);
                yaml.set(path + ".lengthLimit", (Object)clan.lengthLimit);
                yaml.set(path + ".level", (Object)clan.level);
                yaml.set(path + ".memberLimit", (Object)clan.memberLimit);
                yaml.set(path + ".slotLimit", (Object)clan.slotLimit);
                yaml.set(path + ".homeLimit", (Object)clan.homeLimit);
                yaml.set(path + ".pvp", (Object)clan.pvp);
                yaml.set(path + ".glow", (Object)clan.glow);
                yaml.set(path + ".glowColor", (Object)clan.glowColor);
                yaml.set(path + ".topVisible", (Object)clan.topVisible);
                yaml.set(path + ".colors", new ArrayList<String>(clan.colors));
                yaml.set(path + ".history", new ArrayList<String>(clan.history));
                for (Clan.Member member : clan.members.values()) {
                    String m = path + ".members." + member.name.toLowerCase(Locale.ROOT);
                    yaml.set(m + ".name", (Object)member.name);
                    yaml.set(m + ".prefix", (Object)member.prefix);
                    yaml.set(m + ".joinedAt", (Object)member.joinedAt);
                    yaml.set(m + ".kills", (Object)member.kills);
                    yaml.set(m + ".deaths", (Object)member.deaths);
                    yaml.set(m + ".invested", (Object)member.invested);
                    yaml.set(m + ".withdrawn", (Object)member.withdrawn);
                    if (member.armorColor != null) {
                        yaml.set(m + ".armorColor", (Object)member.armorColor);
                    }
                    ArrayList<String> rights = new ArrayList<String>();
                    for (Clan.Right right : member.rights) {
                        rights.add(right.name());
                    }
                    yaml.set(m + ".rights", rights);
                }
                for (Map.Entry entry : clan.homes.entrySet()) {
                    yaml.set(path + ".homes." + (String)entry.getKey(), entry.getValue());
                }
                for (Map.Entry entry : clan.storage.entrySet()) {
                    yaml.set(path + ".storage." + String.valueOf(entry.getKey()), entry.getValue());
                }
                for (Map.Entry<Integer, Long> entry : clan.rentedStorage.entrySet()) {
                    yaml.set(path + ".rentedStorage." + entry.getKey(), entry.getValue());
                }
            }
            try (PreparedStatement save = this.connection.prepareStatement("INSERT INTO BRUHCLANS_V5 (ID, PAYLOAD) VALUES (1, ?) ON CONFLICT(ID) DO UPDATE SET PAYLOAD = excluded.PAYLOAD");){
                save.setString(1, yaml.saveToString());
                save.executeUpdate();
                this.dirty = false;
            }
            catch (SQLException ex) {
                this.plugin.getLogger().log(Level.SEVERE, "Could not save clans", ex);
            }
        }
    }
}

