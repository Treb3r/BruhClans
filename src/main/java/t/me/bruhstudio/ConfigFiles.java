/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.md_5.bungee.api.ChatColor
 *  org.bukkit.ChatColor
 *  org.bukkit.configuration.file.YamlConfiguration
 *  org.bukkit.entity.Player
 */
package t.me.bruhstudio;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import t.me.bruhstudio.BruhFunClansPlugin;

public final class ConfigFiles {
    public static final Pattern HEX_PATTERN_1 = Pattern.compile("&#([A-Fa-f0-9]{6})");
    public static final Pattern HEX_PATTERN_2 = Pattern.compile("<#([A-Fa-f0-9]{6})>");
    public final BruhFunClansPlugin plugin;
    public final Map<String, YamlConfiguration> files = new HashMap<String, YamlConfiguration>();

    public ConfigFiles(BruhFunClansPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        if (!this.plugin.getDataFolder().exists()) {
            this.plugin.getDataFolder().mkdirs();
        }
        for (String name : new String[]{"settings.yml", "messages.yml", "menus.yml", "features/exp.yml", "features/glow.yml", "features/rewards.yml", "features/seller.yml"}) {
            File file = new File(this.plugin.getDataFolder(), name);
            if (!file.exists()) {
                File parent = file.getParentFile();
                if (parent != null && !parent.exists()) {
                    parent.mkdirs();
                }
                this.plugin.saveResource(name, false);
            }
            this.files.put(name, YamlConfiguration.loadConfiguration((File)file));
        }
    }

    public YamlConfiguration get(String name) {
        return this.files.get(name);
    }

    public String getSetting(String path, String def) {
        YamlConfiguration yaml = this.get("settings.yml");
        return yaml != null ? yaml.getString(path, def) : def;
    }

    public String text(String path) {
        return this.color(this.get("messages.yml").getString(path, ""));
    }

    public String color(String value) {
        if (value != null && !value.isEmpty()) {
            Matcher matcher2 = HEX_PATTERN_2.matcher(value);
            StringBuffer sb2 = new StringBuffer();
            while (matcher2.find()) {
                matcher2.appendReplacement(sb2, net.md_5.bungee.api.ChatColor.of((String)("#" + matcher2.group(1))).toString());
            }
            matcher2.appendTail(sb2);
            value = sb2.toString();
            Matcher matcher1 = HEX_PATTERN_1.matcher(value);
            StringBuffer sb1 = new StringBuffer();
            while (matcher1.find()) {
                matcher1.appendReplacement(sb1, net.md_5.bungee.api.ChatColor.of((String)("#" + matcher1.group(1))).toString());
            }
            matcher1.appendTail(sb1);
            value = sb1.toString();

            String red = net.md_5.bungee.api.ChatColor.of("#FF0000").toString();
            String yellow = net.md_5.bungee.api.ChatColor.of("#FFFF00").toString();
            String green = net.md_5.bungee.api.ChatColor.of("#00FF00").toString();

            String placeholder = "\uFFF0";
            value = value.replace("&&", placeholder);

            value = value.replace("&c", red).replace("§c", red);
            value = value.replace("&e", yellow).replace("§e", yellow);
            value = value.replace("&a", green).replace("§a", green);

            String translated = ChatColor.translateAlternateColorCodes((char)'&', (String)value);
            return translated.replace(placeholder, "&");
        }
        return "";
    }

    public List<String> color(List<String> list) {
        if (list == null) {
            return new ArrayList<String>();
        }
        ArrayList<String> result = new ArrayList<String>(list.size());
        for (String line : list) {
            result.add(this.color(line));
        }
        return result;
    }

    public void sendText(Player player, String value, String ... replacements) {
        Object result = this.text("lang.prefix") + this.color(value);
        int i = 0;
        while (i + 1 < replacements.length) {
            result = ((String)result).replace(replacements[i], replacements[i + 1]);
            i += 2;
        }
        player.sendMessage((String)result);
    }

    public void send(Player player, String path, String ... replacements) {
        this.sendText(player, this.text(path), replacements);
    }
}

