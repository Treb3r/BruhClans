/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.clip.placeholderapi.expansion.PlaceholderExpansion
 *  org.bukkit.entity.Player
 */
package t.me.bruhstudio;

import java.util.List;
import java.util.Locale;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import t.me.bruhstudio.BruhFunClansPlugin;
import t.me.bruhstudio.Clan;

public final class ClanPlaceholders
extends PlaceholderExpansion {
    public final BruhFunClansPlugin plugin;

    public ClanPlaceholders(BruhFunClansPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "bruhclans";
    }

    @Override
    public String getAuthor() {
        return "Power_bruh";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, String parameters) {
        if (parameters == null) {
            return "";
        }
        if (player instanceof Player) {
            return onPlaceholderRequest((Player) player, parameters);
        }
        String playerName = player == null ? null : player.getName();
        return resolvePlaceholder(playerName, parameters.toLowerCase(Locale.ROOT));
    }

    @Override
    public String onPlaceholderRequest(Player player, String parameters) {
        if (parameters == null) {
            return "";
        }
        String playerName = player == null ? null : player.getName();
        return resolvePlaceholder(playerName, parameters.toLowerCase(Locale.ROOT));
    }

    public String resolvePlaceholder(String playerName, String key) {
        if (this.plugin == null) {
            return "";
        }
        Clan clan = null;
        if (playerName != null && this.plugin.service() != null) {
            clan = this.plugin.service().clan(playerName);
        }

        if (key.equals("chat") || key.equals("name_chat") || key.equals("chat_format") || key.equals("name_format") || key.equals("tag") || key.equals("prefix")) {
            if (clan == null) {
                return "";
            }
            String name = (clan.displayName != null && !clan.displayName.trim().isEmpty()) ? clan.displayName : clan.name;
            if (name == null || name.trim().isEmpty()) {
                return "";
            }
            String colored = (name.startsWith("&") || name.startsWith("§") || name.startsWith("#") || name.startsWith("<#")) ? name : "&6" + name;
            return (this.plugin.config() != null ? this.plugin.config().color(colored) : colored) + " ";
        }

        if (key.equals("name_format_no") || key.equals("chat_no_space") || key.equals("name_chat_no_space") || key.equals("tag_no_space")) {
            if (clan == null) {
                return "";
            }
            String name = (clan.displayName != null && !clan.displayName.trim().isEmpty()) ? clan.displayName : clan.name;
            if (name == null || name.trim().isEmpty()) {
                return "";
            }
            String colored = (name.startsWith("&") || name.startsWith("§") || name.startsWith("#") || name.startsWith("<#")) ? name : "&6" + name;
            return this.plugin.config() != null ? this.plugin.config().color(colored) : colored;
        }

        if (key.equals("name")) {
            if (clan == null) {
                return this.plugin.config() != null ? this.plugin.config().text("lang.placeholder") : "";
            }
            return (clan.displayName != null && !clan.displayName.isEmpty()) ? clan.displayName : (clan.name != null ? clan.name : "");
        }

        String absent = this.plugin.config() != null ? this.plugin.config().text("lang.absent") : "";

        if (!key.equals("timeupdate")) {
            if (key.equals("topbalance_you")) {
                if (clan == null || this.plugin.service() == null) {
                    return null;
                }
                List<Clan> sorted = this.plugin.service().top();
                return sorted != null ? Integer.toString(sorted.indexOf(clan) + 1) : null;
            }
            String[] categories = new String[]{"topbalance_clan_", "topbalance_player_", "topbalance_size_"};
            for (String category : categories) {
                int rank;
                if (!key.startsWith(category)) continue;
                try {
                    rank = Integer.parseInt(key.substring(category.length())) - 1;
                }
                catch (NumberFormatException var14) {
                    return absent;
                }
                if (this.plugin.service() == null) {
                    return absent;
                }
                List<Clan> sorted = this.plugin.service().top();
                if (sorted != null && rank >= 0 && rank < sorted.size()) {
                    Clan entry = sorted.get(rank);
                    if (entry == null) {
                        return absent;
                    }
                    if (category.equals("topbalance_clan_")) {
                        return (entry.displayName != null && !entry.displayName.isEmpty()) ? entry.displayName : (entry.name != null ? entry.name : absent);
                    }
                    if (category.equals("topbalance_player_")) {
                        return entry.owner != null ? entry.owner : absent;
                    }
                    return Long.toString(entry.rating);
                }
                return absent;
            }
            return null;
        }

        long seconds = 30L - System.currentTimeMillis() / 1000L % 30L;
        String form = seconds % 10L == 1L && seconds % 100L != 11L ? "one" : (seconds % 10L < 2L || seconds % 10L > 4L || seconds % 100L >= 12L && seconds % 100L <= 14L ? "many" : "few");
        String timeText = this.plugin.config() != null ? this.plugin.config().text("timeForms.seconds." + form) : "сек";
        return seconds + " " + timeText;
    }
}


