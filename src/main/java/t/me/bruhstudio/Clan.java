/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Location
 *  org.bukkit.inventory.ItemStack
 */
package t.me.bruhstudio;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public final class Clan {
    public int id = 0;
    public String name;
    public String displayName;
    public String owner;
    public long money;
    public long moneyLimit;
    public long rating;
    public long lengthLimit;
    public int level = 0;
    public int memberLimit;
    public int slotLimit = 2;
    public int homeLimit = 1;
    public boolean pvp;
    public boolean glow;
    public String glowColor;
    public boolean topVisible = true;
    public final Map<String, Member> members = new LinkedHashMap<String, Member>();
    public final Map<String, Location> homes = new LinkedHashMap<String, Location>();
    public final Map<Integer, ItemStack> storage = new LinkedHashMap<Integer, ItemStack>();
    public final Map<Integer, Long> rentedStorage = new LinkedHashMap<Integer, Long>();
    public final List<String> colors = new ArrayList<String>();

    public boolean isStorageSlotRented(int slot) {
        Long exp = this.rentedStorage.get(slot);
        return exp != null && exp > System.currentTimeMillis();
    }

    public int getId() {
        if (this.id > 0) return this.id;
        if (this.name == null) return 1000000;
        int hash = Math.abs(this.name.toLowerCase(Locale.ROOT).hashCode() % 900000);
        return 1000000 + hash;
    }
    public final List<String> history = new ArrayList<String>();

    public void logHistory(String eventText) {
        String date = java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").format(java.time.LocalDateTime.now());
        this.history.add(0, date + ": " + eventText);
        while (this.history.size() > 100) {
            this.history.remove(this.history.size() - 1);
        }
    }

    public void removeMember(String player) {
        if (player != null) {
            this.members.remove(player.toLowerCase(Locale.ROOT));
        }
    }

    public Member member(String player) {
        return this.members.get(player.toLowerCase(Locale.ROOT));
    }

    public void add(Member member) {
        this.members.put(member.name.toLowerCase(Locale.ROOT), member);
    }

    public boolean owns(String player) {
        return player != null && this.owner != null && this.owner.equalsIgnoreCase(player);
    }

    public boolean permits(String player, Right right) {
        if (player != null && this.owns(player)) {
            return true;
        }
        Member member = this.member(player);
        return member != null && member.has(right);
    }

    public String getName() {
        return this.name;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getOwner() {
        return this.owner;
    }

    public Map<String, Member> getMembers() {
        return this.members;
    }

    public int getLevel() {
        return this.level;
    }

    public long getMoney() {
        return this.money;
    }

    public long getRating() {
        return this.rating;
    }

    public static final class Member {
        public String name;
        public String prefix;
        public long joinedAt = System.currentTimeMillis();
        public int kills = 0;
        public int deaths = 0;
        public long invested = 0L;
        public long withdrawn = 0L;
        public String armorColor;
        public final Set<Right> rights = EnumSet.noneOf(Right.class);

        public Member(String name, String prefix) {
            this.name = name;
            this.prefix = prefix;
            this.rights.add(Right.SHOW_MONEY);
            this.rights.add(Right.INVEST_MONEY);
        }

        public boolean has(Right right) {
            return this.rights.contains((Object)Right.ALL) || this.rights.contains((Object)right);
        }

        public String getName() {
            return this.name;
        }

        public String getPrefix() {
            return this.prefix;
        }

        public Set<Right> getRights() {
            return this.rights;
        }
    }

    public static enum Right {
        SHOW_MONEY,
        WITHDRAW_MONEY,
        INVEST_MONEY,
        INVITE_PLAYERS,
        KICK_PLAYERS,
        PVP_TOGGLE,
        CLAN_RENAME,
        CLAN_SETHOME,
        CLAN_DELHOME,
        CLAN_GLOW,
        USE_STORAGE,
        EDIT_PERMISSIONS,
        CHANGE_PREFIX,
        TAKE_REWARDS,
        ALL;
    }
}

