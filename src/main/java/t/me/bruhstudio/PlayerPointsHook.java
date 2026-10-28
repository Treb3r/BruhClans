/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.plugin.Plugin
 */
package t.me.bruhstudio;

import java.lang.reflect.Method;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import t.me.bruhstudio.BruhFunClansPlugin;

public final class PlayerPointsHook {
    public final BruhFunClansPlugin plugin;
    public Object api;
    public Method lookMethod;
    public Method takeMethod;
    public Method giveMethod;

    public PlayerPointsHook(BruhFunClansPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean setup() {
        Plugin pp = Bukkit.getPluginManager().getPlugin("PlayerPoints");
        if (pp != null && pp.isEnabled()) {
            try {
                this.api = pp.getClass().getMethod("getAPI", new Class[0]).invoke((Object)pp, new Object[0]);
                this.lookMethod = this.api.getClass().getMethod("look", UUID.class);
                this.takeMethod = this.api.getClass().getMethod("take", UUID.class, Integer.TYPE);
                this.giveMethod = this.api.getClass().getMethod("give", UUID.class, Integer.TYPE);
                this.plugin.getLogger().info("PlayerPoints hooked successfully.");
                return true;
            }
            catch (Exception ex) {
                this.plugin.getLogger().severe("Unable to hook PlayerPoints API: " + ex.getMessage());
                return false;
            }
        }
        this.plugin.getLogger().warning("PlayerPoints not found or disabled. Donate level purchases will not be available.");
        return false;
    }

    public boolean isReady() {
        return this.api != null && this.lookMethod != null && this.takeMethod != null;
    }

    public int getPoints(UUID uuid) {
        if (!this.isReady()) {
            return 0;
        }
        try {
            Object result = this.lookMethod.invoke(this.api, uuid);
            return result instanceof Number ? ((Number)result).intValue() : 0;
        }
        catch (Exception var3) {
            return 0;
        }
    }

    public boolean has(UUID uuid, int amount) {
        return this.getPoints(uuid) >= amount;
    }

    public boolean withdraw(UUID uuid, int amount) {
        if (this.isReady() && amount > 0) {
            try {
                Object result = this.takeMethod.invoke(this.api, uuid, amount);
                return !(result instanceof Boolean) || (Boolean)result != false;
            }
            catch (Exception var4) {
                return false;
            }
        }
        return false;
    }

    public boolean deposit(UUID uuid, int amount) {
        if (this.isReady() && amount > 0) {
            try {
                Object result = this.giveMethod.invoke(this.api, uuid, amount);
                return !(result instanceof Boolean) || (Boolean)result != false;
            }
            catch (Exception var4) {
                return false;
            }
        }
        return false;
    }
}

