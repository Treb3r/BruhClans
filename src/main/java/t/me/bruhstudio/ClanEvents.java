/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Chunk
 *  org.bukkit.block.Block
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.EventPriority
 *  org.bukkit.event.Listener
 *  org.bukkit.event.block.BlockBreakEvent
 *  org.bukkit.event.block.BlockPlaceEvent
 *  org.bukkit.event.entity.EntityDamageByEntityEvent
 *  org.bukkit.event.entity.EntityDeathEvent
 *  org.bukkit.event.player.PlayerJoinEvent
 *  org.bukkit.plugin.Plugin
 */
package t.me.bruhstudio;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import t.me.bruhstudio.BruhFunClansPlugin;
import t.me.bruhstudio.Clan;

public final class ClanEvents
implements Listener {
    public final BruhFunClansPlugin plugin;
    public final Map<String, Long> chunkCooldown = new HashMap<String, Long>();
    public Method protectionCheck;

    public ClanEvents(BruhFunClansPlugin plugin) {
        this.plugin = plugin;
        if (plugin.getServer().getPluginManager().isPluginEnabled("ProtectionStones")) {
            try {
                this.protectionCheck = Class.forName("dev.espi.protectionstones.ProtectionStones").getMethod("isProtectBlock", Block.class);
            }
            catch (ReflectiveOperationException reflectiveOperationException) {
                // empty catch block
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        SkinHelper.cachePlayer(event.getPlayer());
        this.plugin.getServer().getScheduler().runTaskLater((Plugin)this.plugin, () -> this.plugin.glow().refresh(), 2L);
    }

    @EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player damager = null;
        if (event.getDamager() instanceof Player) {
            damager = (Player) event.getDamager();
        } else if (event.getDamager() instanceof org.bukkit.entity.Projectile) {
            org.bukkit.entity.Projectile proj = (org.bukkit.entity.Projectile) event.getDamager();
            if (proj.getShooter() instanceof Player) {
                damager = (Player) proj.getShooter();
            }
        }
        if (damager != null) {
            Clan attacker = this.plugin.service().clan(damager.getName());
            Clan target = this.plugin.service().clan(((Player)event.getEntity()).getName());
            if (attacker != null && attacker == target && !attacker.pvp) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onBreak(BlockBreakEvent event) {
        Clan clan;
        int experience = this.plugin.service().blockExperience("settings.blocksBreak", event.getBlock().getType());
        if (experience > 0 && !this.protectedBlock(event.getBlock()) && (clan = this.plugin.service().clan(event.getPlayer().getName())) != null) {
            Chunk chunk = event.getBlock().getChunk();
            String key = String.valueOf(chunk.getWorld().getUID()) + ":" + chunk.getX() + ":" + chunk.getZ();
            long now = System.currentTimeMillis();
            if (this.chunkCooldown.getOrDefault(key, 0L) <= now) {
                this.chunkCooldown.put(key, now + 3000L);
                if (this.chunkCooldown.size() > 4096) {
                    this.chunkCooldown.entrySet().removeIf(entry -> (Long)entry.getValue() < now);
                }
                this.plugin.service().addExperience(clan, experience);
            }
        }
    }

    public boolean protectedBlock(Block block) {
        if (this.protectionCheck == null) {
            return false;
        }
        try {
            return Boolean.TRUE.equals(this.protectionCheck.invoke(null, block));
        }
        catch (ReflectiveOperationException var3) {
            return false;
        }
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onPlace(BlockPlaceEvent event) {
        Clan clan = this.plugin.service().clan(event.getPlayer().getName());
        if (clan != null) {
            this.plugin.service().addExperience(clan, this.plugin.service().blockExperience("settings.blocksPlace", event.getBlockPlaced().getType()));
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onDeath(EntityDeathEvent event) {
        Clan clan;
        Player killer = event.getEntity().getKiller();
        if (killer != null && (clan = this.plugin.service().clan(killer.getName())) != null) {
            if (event.getEntity() instanceof Player) {
                Clan.Member km = clan.member(killer.getName());
                if (km != null) {
                    km.kills++;
                    this.plugin.service().store().changed();
                }
            }
            int amount = this.plugin.config().get("features/exp.yml").getInt(event.getEntity() instanceof Player ? "settings.playerKill" : "settings.mobKill");
            this.plugin.service().addExperience(clan, amount);
        }
        if (event.getEntity() instanceof Player) {
            Player victim = (Player) event.getEntity();
            Clan victimClan = this.plugin.service().clan(victim.getName());
            if (victimClan != null) {
                Clan.Member vm = victimClan.member(victim.getName());
                if (vm != null) {
                    vm.deaths++;
                    this.plugin.service().store().changed();
                }
            }
        }
    }
}

