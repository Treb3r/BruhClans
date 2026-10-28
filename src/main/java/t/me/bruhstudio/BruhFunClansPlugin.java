/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.milkbowl.vault.economy.Economy
 *  org.bukkit.Bukkit
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.TabCompleter
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Listener
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.RegisteredServiceProvider
 *  org.bukkit.plugin.java.JavaPlugin
 *  org.bukkit.scheduler.BukkitScheduler
 */
package t.me.bruhstudio;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import t.me.bruhstudio.Clan;
import t.me.bruhstudio.ClanCommands;
import t.me.bruhstudio.ClanEvents;
import t.me.bruhstudio.ClanMenus;
import t.me.bruhstudio.ClanPlaceholders;
import t.me.bruhstudio.ClanService;
import t.me.bruhstudio.ClanStore;
import t.me.bruhstudio.ConfigFiles;
import t.me.bruhstudio.GlowService;
import t.me.bruhstudio.PlayerPointsHook;

public final class BruhFunClansPlugin
extends JavaPlugin {
    public static BruhFunClansPlugin instance;
    public ConfigFiles config;
    public ClanStore store;
    public ClanService service;
    public ClanMenus menus;
    public GlowService glow;
    public Economy economy;
    public PlayerPointsHook playerPoints;
    public BruhClansAdminCommands adminCommands;
    public ClanCommands clanCommands;

    public static BruhFunClansPlugin getInstance() {
        return instance;
    }

    public void onEnable() {
        instance = this;
        this.config = new ConfigFiles(this);
        this.config.load();
        RegisteredServiceProvider provider = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (provider != null && provider.getProvider() != null) {
            this.economy = (Economy)provider.getProvider();
            this.playerPoints = new PlayerPointsHook(this);
            this.playerPoints.setup();
            this.store = new ClanStore(this);
            this.store.load();
            this.service = new ClanService(this, this.store);
            this.glow = new GlowService(this);
            SkinHelper.init(this);
            this.menus = new ClanMenus(this);
            this.clanCommands = new ClanCommands(this);
            ClanCommands commands = this.clanCommands;
            Objects.requireNonNull(this.getCommand("clan"), "clan command").setExecutor((CommandExecutor)commands);
            this.getCommand("clan").setTabCompleter((TabCompleter)commands);
            if (this.getCommand("cc") != null) {
                this.getCommand("cc").setExecutor((CommandExecutor)commands);
            }
            this.adminCommands = new BruhClansAdminCommands(this);
            if (this.getCommand("bruhclans") != null) {
                this.getCommand("bruhclans").setExecutor((CommandExecutor)this.adminCommands);
                this.getCommand("bruhclans").setTabCompleter((TabCompleter)this.adminCommands);
            }
            Bukkit.getPluginManager().registerEvents((Listener)new ClanEvents(this), (Plugin)this);
            Bukkit.getPluginManager().registerEvents((Listener)this.menus, (Plugin)this);
            new ClanPlaceholders(this).register();
            BukkitScheduler var10000 = Bukkit.getScheduler();
            ClanStore var10002 = this.store;
            Objects.requireNonNull(var10002);
            var10000.runTaskTimer((Plugin)this, var10002::flush, 600L, 600L);
            var10000 = Bukkit.getScheduler();
            GlowService var4 = this.glow;
            Objects.requireNonNull(var4);
            var10000.runTaskTimer((Plugin)this, var4::refresh, 100L, 100L);
        } else {
            this.getLogger().severe("Vault economy provider is unavailable");
            Bukkit.getPluginManager().disablePlugin((Plugin)this);
        }
    }

    public void onDisable() {
        if (this.menus != null) {
            this.menus.saveOpenStorage();
        }
        if (this.store != null) {
            this.store.flush();
            this.store.close();
        }
        if (this.glow != null) {
            this.glow.clear();
        }
        instance = null;
    }

    public ConfigFiles config() {
        return this.config;
    }

    public ClanStore store() {
        return this.store;
    }

    public ClanService service() {
        return this.service;
    }

    public ClanMenus menus() {
        return this.menus;
    }

    public GlowService glow() {
        return this.glow;
    }

    public Economy economy() {
        return this.economy;
    }

    public PlayerPointsHook playerPoints() {
        return this.playerPoints;
    }

    public BruhClansAdminCommands adminCommands() {
        return this.adminCommands;
    }

    public ClanCommands clanCommands() {
        return this.clanCommands;
    }

    public String getPlayerClanName(String playerName) {
        if (playerName != null && this.service != null) {
            Clan clan = this.service.clan(playerName);
            return clan != null ? clan.name : null;
        }
        return null;
    }

    public String getPlayerClanName(Player player) {
        return player != null ? this.getPlayerClanName(player.getName()) : null;
    }

    public String getPlayerClanDisplayName(String playerName) {
        if (playerName != null && this.service != null) {
            Clan clan = this.service.clan(playerName);
            return clan != null ? clan.displayName : null;
        }
        return null;
    }

    public String getPlayerClanDisplayName(Player player) {
        return player != null ? this.getPlayerClanDisplayName(player.getName()) : null;
    }

    public boolean areClanmates(String firstPlayer, String secondPlayer) {
        if (firstPlayer != null && secondPlayer != null && this.service != null) {
            if (firstPlayer.equalsIgnoreCase(secondPlayer)) {
                return true;
            }
            Clan firstClan = this.service.clan(firstPlayer);
            if (firstClan == null) {
                return false;
            }
            Clan secondClan = this.service.clan(secondPlayer);
            return firstClan == secondClan;
        }
        return false;
    }

    public boolean areClanmates(Player firstPlayer, Player secondPlayer) {
        if (firstPlayer != null && secondPlayer != null) {
            return firstPlayer.getUniqueId().equals(secondPlayer.getUniqueId()) ? true : this.areClanmates(firstPlayer.getName(), secondPlayer.getName());
        }
        return false;
    }

    public List<String> getClanMemberNames(String clanName) {
        ArrayList<String> list = new ArrayList<String>();
        if (clanName != null && this.store != null) {
            Clan clan = this.store.byName(clanName);
            if (clan == null) {
                for (Clan c : this.store.all()) {
                    if (c.displayName == null || !c.displayName.equalsIgnoreCase(clanName)) continue;
                    clan = c;
                    break;
                }
            }
            if (clan != null) {
                for (Clan.Member member : clan.members.values()) {
                    if (member.name == null || member.name.isEmpty() || list.contains(member.name)) continue;
                    list.add(member.name);
                }
            }
            return list;
        }
        return list;
    }

    public Clan getClanByPlayer(String playerName) {
        return this.service != null && playerName != null ? this.service.clan(playerName) : null;
    }

    public Clan getClanByPlayer(Player player) {
        return player != null ? this.getClanByPlayer(player.getName()) : null;
    }

    public Clan getClanByName(String clanName) {
        if (this.store != null && clanName != null) {
            Clan clan = this.store.byName(clanName);
            if (clan == null) {
                for (Clan c : this.store.all()) {
                    if (c.displayName == null || !c.displayName.equalsIgnoreCase(clanName)) continue;
                    return c;
                }
            }
            return clan;
        }
        return null;
    }
}

