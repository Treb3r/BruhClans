/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Material
 *  org.bukkit.configuration.ConfigurationSection
 *  org.bukkit.entity.Player
 */
package t.me.bruhstudio;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import t.me.bruhstudio.BruhFunClansPlugin;
import t.me.bruhstudio.Clan;
import t.me.bruhstudio.ClanStore;

public final class ClanService {
    public final BruhFunClansPlugin plugin;
    public final ClanStore store;

    public ClanService(BruhFunClansPlugin plugin, ClanStore store) {
        this.plugin = plugin;
        this.store = store;
    }

    public ClanStore store() {
        return this.store;
    }

    public Clan clan(String player) {
        return this.store.byPlayer(player);
    }

    public Clan clan(Player player) {
        return player == null ? null : this.clan(player.getName());
    }

    public Clan clanByName(String name) {
        return this.store.byName(name);
    }

    public List<Clan> top() {
        ArrayList<Clan> clans = new ArrayList<Clan>();
        for (Clan c : this.store.all()) {
            if (c.topVisible) {
                clans.add(c);
            }
        }
        clans.sort(Comparator.comparingLong((Clan c) -> c.rating).reversed());
        return clans;
    }

    public int getTopPosition(Clan clan) {
        if (clan == null || !clan.topVisible) {
            return -1;
        }
        List<Clan> top = this.top();
        for (int i = 0; i < top.size(); i++) {
            if (top.get(i).name.equalsIgnoreCase(clan.name)) {
                return i + 1;
            }
        }
        return -1;
    }

    public List<Clan> topByMoney() {
        ArrayList<Clan> clans = new ArrayList<Clan>();
        for (Clan c : this.store.all()) {
            if (c.topVisible) {
                clans.add(c);
            }
        }
        clans.sort(Comparator.comparingLong((Clan c) -> c.money).reversed());
        return clans;
    }

    public void announce(Clan clan, String pathOrMessage, String ... replacements) {
        if (clan == null) return;
        boolean isConfigKey = this.plugin.config().get("messages.yml").contains(pathOrMessage);
        for (Clan.Member member : clan.members.values()) {
            if (member == null || member.name == null) continue;
            Player online = Bukkit.getPlayerExact(member.name);
            if (online == null) continue;
            if (isConfigKey) {
                this.plugin.config().send(online, pathOrMessage, replacements);
            } else {
                online.sendMessage(this.plugin.config().color(pathOrMessage));
            }
        }
    }

    public void addExperience(Clan clan, long amount) {
        if (clan != null && amount > 0L) {
            if (amount > Long.MAX_VALUE - clan.rating) {
                clan.rating = Long.MAX_VALUE;
            } else {
                clan.rating += amount;
            }
            while (clan.level < ClanMenus.LEVEL_DEFS.length) {
                ClanMenus.LevelDef nextDef = ClanMenus.getLevelDef(clan.level + 1);
                if (nextDef != null && clan.rating >= nextDef.xp) {
                    int oldLvl = clan.level;
                    clan.level++;
                    clan.moneyLimit = nextDef.treasury;
                    this.plugin.menus().applyLevelPerks(clan, clan.level);
                    this.announce(clan, this.plugin.config().color("&#FF0000[⚔] &#FFFF00Кланы: &#00FF00Уровень &fвашего &#FFFF00Клана &fповышен до &#FFFF00" + clan.level + "-го&f!"));
                    for (String perk : nextDef.perks) {
                        this.announce(clan, this.plugin.config().color("&#FF0000[⚔] &#FFFF00Кланы: &f" + perk.trim()));
                    }
                    Player leader = clan.owner != null ? Bukkit.getPlayer(clan.owner) : null;
                    Bukkit.getPluginManager().callEvent(new t.me.bruhstudio.event.ClanLevelUpEvent(leader, clan, oldLvl, clan.level));
                } else {
                    break;
                }
            }
            this.store.changed();
        }
    }

    public boolean buyLevel(Player player, Clan clan, int targetLevel) {
        if (clan != null && player != null) {
            if (targetLevel <= clan.level) {
                this.plugin.config().send(player, "buy.alreadyPassed", new String[0]);
                return false;
            }
            if (targetLevel != clan.level + 1) {
                return false;
            }
            ClanMenus.LevelDef targetDef = ClanMenus.getLevelDef(targetLevel);
            if (targetDef == null) {
                return false;
            }
            boolean hasXP = clan.rating >= targetDef.xp;
            int price = targetDef.tkCost;
            if (!hasXP && price > 0) {
                if (this.plugin.playerPoints() == null || !this.plugin.playerPoints().has(player.getUniqueId(), price)) {
                    this.plugin.menus().playSound(player, "BLOCK_CHEST_LOCKED");
                    String noPointsMsg = this.plugin.config().text("buy.noPoints");
                    if (noPointsMsg == null || noPointsMsg.trim().isEmpty()) {
                        noPointsMsg = "&#FF0000[✖] Ошибка! &fУ Вас не хватает &#00FF00Токенов&f!\n &fНе беда, их можно купить на &#FFFF00FillaGame.MyDonate.io\n&#FF0000[✖] Ошибка: &fНе удалось списать токены.";
                    }
                    for (String line : noPointsMsg.split("\n")) {
                        player.sendMessage(this.plugin.config().color(line));
                    }
                    return false;
                }
                if (!this.plugin.playerPoints().withdraw(player.getUniqueId(), price)) {
                    this.plugin.menus().playSound(player, "BLOCK_CHEST_LOCKED");
                    String failMsg = this.plugin.config().text("buy.failed");
                    if (failMsg == null || failMsg.trim().isEmpty()) {
                        failMsg = "&#FF0000[✖] Ошибка: &fНе удалось списать токены.";
                    }
                    player.sendMessage(this.plugin.config().color(failMsg));
                    return false;
                }
            }
            int oldLevel = clan.level;
            clan.level = targetLevel;
            if (clan.rating < targetDef.xp) {
                clan.rating = targetDef.xp;
            }
            clan.moneyLimit = targetDef.treasury;
            this.plugin.menus().applyLevelPerks(clan, targetLevel);
            this.store.changed();
            this.store.flush();
            clan.logHistory(player.getName() + " купил " + targetLevel + "-й уровень клана");
            Bukkit.getPluginManager().callEvent(new t.me.bruhstudio.event.ClanLevelUpEvent(player, clan, oldLevel, targetLevel));

            player.sendMessage(this.plugin.config().color("&#00FF00[✔] Успешная покупка!"));
            String msg1 = this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Кланы: &#FFFF00" + player.getName() + " &fкупил &#FFFF00" + targetLevel + "-й &fуровень клана.");
            String msg2 = this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Кланы: &#00FF00Уровень &fвашего &#FFFF00Клана &fповышен!");
            this.announce(clan, msg1);
            this.announce(clan, msg2);
            for (String perk : targetDef.perks) {
                this.announce(clan, this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Кланы: &f" + perk.trim()));
            }
            this.plugin.menus().playSound(player, "ENTITY_PLAYER_LEVELUP");
            return true;
        }
        return false;
    }

    public void upgrade(Clan clan, String instruction) {
        String[] pieces = instruction.trim().split("\\s+", 2);
        if (pieces.length != 0) {
            String arg;
            String action = pieces[0];
            String string = arg = pieces.length > 1 ? pieces[1] : "";
            if (!action.equals("[allowGlow]")) {
                if (action.equals("[allowRgb]")) {
                    if (!clan.colors.contains("rgb")) {
                        clan.colors.add("rgb");
                    }
                } else if (!action.equals("[upgradeColor]") && !action.equals("[addColor]")) {
                    long value;
                    try {
                        value = Long.parseLong(arg);
                    }
                    catch (NumberFormatException var9) {
                        this.plugin.getLogger().warning("Invalid reward: " + instruction);
                        return;
                    }
                    switch (action) {
                        case "[setBank]": {
                            clan.moneyLimit = value;
                            break;
                        }
                        case "[upgradeBank]": {
                            clan.moneyLimit += (long)value;
                            break;
                        }
                        case "[setPlayers]": {
                            clan.memberLimit = (int)value;
                            break;
                        }
                        case "[upgradePlayers]": {
                            clan.memberLimit += (int)value;
                            break;
                        }
                        case "[setSlots]": {
                            clan.slotLimit = (int)value;
                            break;
                        }
                        case "[upgradeSlots]": {
                            clan.slotLimit += (int)value;
                            break;
                        }
                        case "[setHome]": {
                            clan.homeLimit = (int)value;
                            break;
                        }
                        case "[upgradeHome]": {
                            clan.homeLimit += (int)value;
                            break;
                        }
                        case "[setName]": {
                            clan.lengthLimit = value;
                            break;
                        }
                        case "[upgradeName]": {
                            clan.lengthLimit += (long)value;
                            break;
                        }
                        default: {
                            this.plugin.getLogger().warning("Unknown reward: " + instruction);
                            break;
                        }
                    }
                } else {
                    String code = arg.toLowerCase(Locale.ROOT);
                    if (!clan.colors.contains(code)) {
                        clan.colors.add(code);
                    }
                }
            }
        }
    }

    public int blockExperience(String listPath, Material material) {
        for (String entry : this.plugin.config().get("features/exp.yml").getStringList(listPath)) {
            String[] parts = entry.split(":", 2);
            if (parts.length != 2 || !parts[0].equals(material.name())) continue;
            try {
                return Integer.parseInt(parts[1]);
            }
            catch (NumberFormatException var7) {
                return 0;
            }
        }
        return 0;
    }
}

