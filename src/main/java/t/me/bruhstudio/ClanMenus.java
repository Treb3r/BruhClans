package t.me.bruhstudio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;

public final class ClanMenus implements Listener {
    public final BruhFunClansPlugin plugin;
    public final Map<String, Inventory> sharedStorage = new HashMap<String, Inventory>();
    public final Map<UUID, Integer> selectedRight = new HashMap<UUID, Integer>();
    public final Map<UUID, Long> clickCooldown = new HashMap<UUID, Long>();
    public final Map<UUID, Long> glowColorCooldown = new HashMap<UUID, Long>();
    public final Map<UUID, Long> glowToggleCooldown = new HashMap<UUID, Long>();

    public ClanMenus(BruhFunClansPlugin plugin) {
        this.plugin = plugin;
    }

    public void saveOpenStorage() {
        for (Inventory inventory : this.sharedStorage.values()) {
            Clan clan;
            if (!(inventory.getHolder() instanceof View) || (clan = this.plugin.service().store().byName(((View)inventory.getHolder()).clan)) == null) continue;
            this.saveStorage(clan, inventory);
        }
    }

    public String placeholders(Player player, String value) {
        return PlaceholderAPI.setPlaceholders(player, value);
    }

    public Inventory inventory(View view, int size, String title) {
        if (title != null && (title.startsWith("<font:") || title.contains("<font:"))) {
            try {
                String cleanTitle = title.replace("\u00a7f", "").replace("\u00a7r", "").replace("\u00a7", "");
                net.kyori.adventure.text.Component comp = net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize(cleanTitle);
                view.inventory = Bukkit.createInventory((InventoryHolder)view, size, comp);
                return view.inventory;
            } catch (Throwable t) {
                this.plugin.getLogger().warning("Failed to deserialize inventory title: " + title + " - " + t.getMessage());
            }
        }
        view.inventory = Bukkit.createInventory((InventoryHolder)view, size, this.plugin.config().color(title));
        return view.inventory;
    }

    public String replace(Player player, Clan clan, String value) {
        String output = this.plugin.config().color(value);
        output = output.replace("<name>", clan.displayName != null ? clan.displayName : clan.name)
            .replace("<xp>", Long.toString(clan.rating))
            .replace("<size>", Integer.toString(clan.members.size()))
            .replace("<max>", Integer.toString(clan.memberLimit))
            .replace("<bank_balance>", String.format("%,d", clan.money))
            .replace("<bank_limit>", String.format("%,d", clan.moneyLimit))
            .replace("<storage_limit>", Integer.toString(clan.slotLimit))
            .replace("<max_length>", Long.toString(clan.lengthLimit))
            .replace("<level>", Integer.toString(clan.level))
            .replace("<pvp>", this.plugin.config().color(clan.pvp ? "&#55FF55Да" : "&#FF5555Нет"));
        return this.placeholders(player, output);
    }

    private Color parseRgb(String colorStr, Color fallback) {
        if (colorStr == null || colorStr.isEmpty()) return fallback;
        String[] parts = colorStr.split(";");
        if (parts.length != 3) return fallback;
        try {
            int r = Math.max(0, Math.min(255, Integer.parseInt(parts[0])));
            int g = Math.max(0, Math.min(255, Integer.parseInt(parts[1])));
            int b = Math.max(0, Math.min(255, Integer.parseInt(parts[2])));
            return Color.fromRGB(r, g, b);
        } catch (Exception e) {
            return fallback;
        }
    }

    public ItemStack item(Player player, Clan clan, ConfigurationSection section) {
        ItemStack item;
        ItemMeta meta;
        if (section == null) {
            return new ItemStack(Material.BARRIER);
        }
        Material material = Material.matchMaterial(section.getString("material", "BARRIER"));
        if (material == null) {
            material = Material.BARRIER;
        }
        if ((meta = (item = new ItemStack(material, Math.max(1, section.getInt("amount", 1)))).getItemMeta()) != null) {
            List<String> flags;
            if (section.contains("name")) {
                meta.setDisplayName(this.replace(player, clan, section.getString("name", "")));
            }
            if (section.contains("lore")) {
                ArrayList<String> lore = new ArrayList<String>();
                for (String line : section.getStringList("lore")) {
                    lore.add(this.replace(player, clan, line));
                }
                meta.setLore(lore);
            }
            if ((flags = section.getStringList("flags")).isEmpty()) {
                flags = section.getStringList("item_flags");
            }
            for (String flag : flags) {
                try {
                    meta.addItemFlags(ItemFlag.valueOf(flag));
                } catch (IllegalArgumentException ignored) {}
            }
            if (meta instanceof LeatherArmorMeta) {
                ((LeatherArmorMeta)meta).setColor(this.parseRgb(clan.glowColor, Color.fromRGB(0, 255, 0)));
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    public ItemStack item(Player player, Clan clan, String key) {
        return this.item(player, clan, this.plugin.config().get("menus.yml").getConfigurationSection("items." + key));
    }

    public ItemStack createItem(Material material, int amount, String name, List<String> lore, ItemFlag ... flags) {
        ItemStack item = new ItemStack(material == null ? Material.BARRIER : material, Math.max(1, amount));
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            if (name != null) {
                meta.setDisplayName(this.plugin.config().color(name));
            }
            if (lore != null) {
                ArrayList<String> colored = new ArrayList<String>(lore.size());
                for (String line : lore) {
                    colored.add(this.plugin.config().color(line));
                }
                meta.setLore(colored);
            }
            if (flags != null) {
                for (ItemFlag flag : flags) {
                    if (flag == null) continue;
                    meta.addItemFlags(flag);
                }
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    public void playSound(Player player, String soundName) {
        try {
            Sound sound = Sound.valueOf(soundName);
            player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
        } catch (Exception ignored) {}
    }

    public List<String> buildTopLore(Clan clan, boolean includeHeaderAndRank) {
        ArrayList<String> lore = new ArrayList<String>();
        List<Clan> top = this.plugin.service().top();
        if (includeHeaderAndRank) {
            int rank = 1;
            for (int i = 0; i < top.size(); ++i) {
                if (!top.get(i).name.equalsIgnoreCase(clan.name)) continue;
                rank = i + 1;
                break;
            }
            lore.add(this.plugin.config().color("&#FFFFFFВы на&#FFFF55 " + rank + " месте&#FFFFFF в топе"));
            lore.add("");
            lore.add(this.plugin.config().color("&#FF5555[⚔]&#FFFFFF ТОП по Уровню &#FF5555Кланов&#FFFFFF:"));
        }
        String[] symbols = new String[]{"①", "②", "③", "④", "⑤", "⑥", "⑦", "⑧", "⑨", "⑩"};
        String[] colors = new String[]{"&#55FF55", "&#FFFF55", "&#FFAA00", "&#FF5555", "&#FF5555", "&#FF5555", "&#FF5555", "&#FF5555", "&#FF5555", "&#FF5555"};
        int max = Math.min(10, top.size());
        for (int i = 0; i < max; ++i) {
            Clan c = top.get(i);
            String sym = symbols[i];
            String col = colors[i];
            lore.add(this.plugin.config().color(col + " " + sym + " " + col + "[" + c.displayName + col + "]&#FFFFFF, лидер:&#FF5555 " + c.owner + "&#FFFFFF,&#5555FF " + c.rating + " XP"));
        }
        return lore;
    }

    public void top(Player player) {
        this.top(player, false);
    }

    public void top(Player player, boolean fromMenu) {
        Clan playerClan = this.plugin.service().clan(player.getName());
        View view = new View("top", playerClan, 0, fromMenu);
        String titleTemplate = this.plugin.config().get("menus.yml").getString("gui.top.title", "<font:minecraft:clan><white>0B1K2z600<font:minecraft:title>ТОП КЛАНОВ");
        Inventory inventory = this.inventory(view, 54, titleTemplate);

        List<Clan> allClans = this.plugin.service().topByMoney();
        int[] slots = new int[]{13, 21, 22, 23, 29, 30, 31, 32, 33};

        for (int i = 0; i < Math.min(slots.length, allClans.size()); ++i) {
            int slot = slots[i];
            int rank = i + 1;
            Clan c = allClans.get(i);
            ItemStack item;
            if (rank == 1) {
                item = this.createTopTrophy(c, rank, 388, 1);
            } else if (rank == 2) {
                item = this.createTopTrophy(c, rank, 392, 2);
            } else if (rank == 3) {
                item = this.createTopTrophy(c, rank, 389, 3);
            } else {
                item = this.createTopHead(c, rank);
            }
            inventory.setItem(slot, item);
        }

        if (playerClan != null && playerClan.owns(player.getName())) {
            inventory.setItem(16, this.buildMainTopToggleItem(playerClan));
        }

        inventory.setItem(40, this.buildDoorItem());
        player.openInventory(inventory);
    }

    private ItemStack createTopTrophy(Clan clan, int rank, int customModelData, int amount) {
        ItemStack item = new ItemStack(Material.PAPER, Math.max(1, amount));
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setCustomModelData(customModelData);
            String clanName = clan.displayName != null ? clan.displayName : clan.name;
            String starBracket;
            if (rank == 1) {
                starBracket = "&#FFFF00[★] &#FFFF00" + clanName;
            } else if (rank == 2) {
                starBracket = "&f[★] &#FFAA00" + clanName;
            } else {
                starBracket = "&#FFAA00[★] &#FFAA00" + clanName;
            }
            meta.setDisplayName(this.plugin.config().color(starBracket));

            List<String> lore = new ArrayList<String>();
            lore.add(this.plugin.config().color("&#FF8000❖ &fМесто: &#FFFF00" + rank));
            lore.add(this.plugin.config().color("&#FF8000★ &fЛидер: &#FFFF00" + clan.owner));
            lore.add(this.plugin.config().color("&#FF8000★ &fУровень: &#FFFF00" + clan.level));
            lore.add(this.plugin.config().color("&#FF8000🌐 &fКазна: &#FFFF00$" + String.format(Locale.US, "%,d", clan.money)));
            lore.add(this.plugin.config().color("&#FF8000⚕ &fУчастники: &#FFFF00" + clan.members.size()));

            meta.setLore(lore);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createTopHead(Clan clan, int rank) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD, Math.max(1, rank));
        SkullMeta meta = (SkullMeta)item.getItemMeta();
        if (meta != null) {
            meta.setCustomModelData(3);
            SkinHelper.applySkin(meta, clan.owner);
            String clanName = clan.displayName != null ? clan.displayName : clan.name;
            String starBracket = "&#FF0000[★] &#FFAA00" + clanName;
            meta.setDisplayName(this.plugin.config().color(starBracket));

            List<String> lore = new ArrayList<String>();
            lore.add(this.plugin.config().color("&#FF8000❖ &fМесто: &#FFFF00" + rank));
            lore.add(this.plugin.config().color("&#FF8000★ &fЛидер: &#FFFF00" + clan.owner));
            lore.add(this.plugin.config().color("&#FF8000★ &fУровень: &#FFFF00" + clan.level));
            lore.add(this.plugin.config().color("&#FF8000🌐 &fКазна: &#FFFF00$" + String.format(Locale.US, "%,d", clan.money)));
            lore.add(this.plugin.config().color("&#FF8000⚕ &fУчастники: &#FFFF00" + clan.members.size()));

            meta.setLore(lore);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
        }
        return item;
    }

    public void main(Player player, Clan clan) {
        if (clan != null) {
            View view = new View("main", clan, 0);
            String title = this.buildMainMenuTitle(clan.displayName != null && !clan.displayName.isEmpty() ? clan.displayName : clan.name);
            Inventory inventory = this.inventory(view, 54, title);

            inventory.setItem(13, this.buildMainInfoItem(player, clan));
            inventory.setItem(20, this.buildMainMembersItem());
            inventory.setItem(21, this.buildMainLevelItem());
            inventory.setItem(22, this.buildMainTopItem(clan));
            inventory.setItem(23, this.buildMainBasesItem());
            inventory.setItem(24, this.buildMainErrandsItem());
            if (clan.permits(player.getName(), Clan.Right.CLAN_GLOW)) {
                inventory.setItem(30, this.buildMainArmorColorItem(clan));
            }
            inventory.setItem(31, this.buildMainTrialsItem());
            if (clan.permits(player.getName(), Clan.Right.USE_STORAGE)) {
                inventory.setItem(32, this.buildMainStorageItem());
            }

            player.openInventory(inventory);
        }
    }

    public static String gradient(String text, int r1, int g1, int b1, int r2, int g2, int b2) {
        if (text == null || text.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        int len = text.length();
        for (int i = 0; i < len; i++) {
            char c = text.charAt(i);
            if (c == ' ') {
                sb.append(' ');
                continue;
            }
            double ratio = len > 1 ? (double)i / (double)(len - 1) : 0.0;
            int r = (int)Math.round(r1 + (r2 - r1) * ratio);
            int g = (int)Math.round(g1 + (g2 - g1) * ratio);
            int b = (int)Math.round(b1 + (b2 - b1) * ratio);
            sb.append(String.format(Locale.ROOT, "&#%02X%02X%02X%c", r, g, b, c));
        }
        return sb.toString();
    }

    private static final int[] BASE_SHIFTS = new int[]{99, 101, 104, 107, 110};
    private static final char[] BASE_CHARS = new char[]{'3', '4', '5', '6', '7'};

    public String buildBannerTitle(String text, String overlayGlyph) {
        String cleanName = ChatColor.stripColor(text != null ? text : "").toUpperCase(Locale.ROOT);
        cleanName = net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().escapeTags(cleanName);
        int textWidth = 0;
        for (char c : cleanName.toCharArray()) {
            if (c == ' ') textWidth += 4;
            else if (c == ':' || c == '.' || c == ',') textWidth += 2;
            else if (c == 'T' || c == 'Т' || c == 'I' || c == '1') textWidth += 5;
            else if (c == 'И' || c == 'Й' || c == 'Л' || c == 'Ц' || c == 'Ъ' || c == 'G' || c == 'N') textWidth += 7;
            else if (c == 'Д' || c == 'М' || c == 'M' || c == 'W') textWidth += 8;
            else if (c == 'Ж' || c == 'Ф' || c == 'Ш' || c == 'Ы' || c == 'Ю') textWidth += 9;
            else if (c == 'Щ') textWidth += 10;
            else textWidth += 6;
        }

        int targetShift = (int)Math.round((overlayGlyph != null ? 100.5 : 94.5) + (textWidth / 2.0));
        int bestDiff = Integer.MAX_VALUE;
        String bestSeq = "600";
        for (int i = 0; i < BASE_SHIFTS.length; i++) {
            int base = BASE_SHIFTS[i];
            char ch = BASE_CHARS[i];
            for (int z = 0; z <= 6; z++) {
                int total = base + (z * 9);
                int diff = Math.abs(total - targetShift);
                if (diff < bestDiff) {
                    bestDiff = diff;
                    StringBuilder sb = new StringBuilder().append(ch);
                    for (int k = 0; k < z; k++) sb.append('0');
                    bestSeq = sb.toString();
                }
            }
        }
        if (overlayGlyph != null && !overlayGlyph.isEmpty()) {
            if (cleanName.isEmpty()) {
                return "<font:minecraft:clan><white>0B1" + overlayGlyph + "2z";
            }
            return "<font:minecraft:clan><white>0B1" + overlayGlyph + "2z" + bestSeq + "<font:minecraft:title><white>" + cleanName;
        }
        if (cleanName.isEmpty()) {
            return "<font:minecraft:clan><white>0B2z";
        }
        return "<font:minecraft:clan><white>0B2z" + bestSeq + "<font:minecraft:title><white>" + cleanName;
    }

    public String buildMainMenuTitle(String clanName) {
        String cleanName = ChatColor.stripColor(clanName != null ? clanName : "").toUpperCase(Locale.ROOT);
        return this.buildBannerTitle("КЛАН: " + cleanName, null);
    }

    private ItemStack buildMainTopToggleItem(Clan clan) {
        ItemStack toggle = new ItemStack(Material.PAPER, 1);
        ItemMeta toggleMeta = toggle.getItemMeta();
        if (toggleMeta != null) {
            boolean visible = clan.topVisible;
            toggleMeta.setCustomModelData(visible ? 414 : 417);
            toggleMeta.setDisplayName(this.plugin.config().color("&#FF0000[&#FF0000☼&#FF0000] &#FF6600Показ клана в топе"));
            List<String> lore = new ArrayList<String>();
            if (visible) {
                lore.add(this.plugin.config().color("&#FF6600➥ &#FF6600Нажмите, &fчтобы скрыть клан"));
                lore.add(this.plugin.config().color("&#FF6600❖ &fСтатус: &#00FF00[Виден]"));
            } else {
                lore.add(this.plugin.config().color("&#FF6600➥ &#FF6600Нажмите, &fчтобы показать клан"));
                lore.add(this.plugin.config().color("&#FF6600❖ &fСтатус: &#FF0000[Скрыт]"));
            }
            lore.add("");
            lore.add(this.plugin.config().color("&fСкрытого клана нет в топе"));
            lore.add(this.plugin.config().color("&fни у кого, места остальных"));
            lore.add(this.plugin.config().color("&fсдвигаются вверх."));
            toggleMeta.setLore(lore);
            for (ItemFlag flag : ItemFlag.values()) {
                toggleMeta.addItemFlags(flag);
            }
            toggle.setItemMeta(toggleMeta);
        }
        return toggle;
    }

    private ItemStack buildDoorItem() {
        ItemStack back = new ItemStack(Material.PAPER, 1);
        ItemMeta backMeta = back.getItemMeta();
        if (backMeta != null) {
            backMeta.setCustomModelData(371);
            backMeta.setDisplayName(this.plugin.config().color("&#FF0000[&#FF0000\u27f2&#FF0000] &#FF6600Вернуться..."));
            backMeta.setLore(Collections.emptyList());
            for (ItemFlag flag : ItemFlag.values()) {
                backMeta.addItemFlags(flag);
            }
            back.setItemMeta(backMeta);
        }
        return back;
    }

    private ItemStack buildSwitchItem(String title, boolean active) {
        ItemStack item = new ItemStack(Material.PAPER, 1);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setCustomModelData(active ? 414 : 417);
            meta.setDisplayName(this.plugin.config().color("&#FF0000[&#FF0000+&#FF0000] &#FF6600" + title));
            List<String> lore = new ArrayList<String>();
            lore.add(this.plugin.config().color("&#FF6600➥ &#FF6600Нажмите, &fчтобы переключить"));
            lore.add(this.plugin.config().color("&#FF6600❖ &fСтатус: " + (active ? "&#00FF00[Включено]" : "&#FF0000[Выключено]")));
            meta.setLore(lore);
            for (ItemFlag flag : ItemFlag.values()) {
                meta.addItemFlags(flag);
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack buildArrowItem(boolean next, int page, int maxPages) {
        ItemStack item = new ItemStack(Material.PAPER, 1);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setCustomModelData(next ? 415 : 416);
            meta.setDisplayName(this.plugin.config().color("&#FF0000➥ &#FF6600" + (next ? "Вперёд" : "Назад")));
            List<String> lore = new ArrayList<String>();
            lore.add(this.plugin.config().color("&#FF6600Страница &#FFFF00" + page + "/" + maxPages));
            meta.setLore(lore);
            for (ItemFlag flag : ItemFlag.values()) {
                meta.addItemFlags(flag);
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    public static String getRightDisplayName(Clan.Right r) {
        if (r == null) return null;
        switch (r) {
            case INVITE_PLAYERS: return "Приглашать";
            case KICK_PLAYERS: return "Кикать";
            case USE_STORAGE: return "Хранилище";
            case EDIT_PERMISSIONS: return "Менять права";
            case CHANGE_PREFIX: return "Менять префикс";
            case SHOW_MONEY: return "Просматривать баланс";
            case INVEST_MONEY: return "Вкладывать деньги";
            case WITHDRAW_MONEY: return "Снимать деньги";
            case PVP_TOGGLE: return "Менять режим пвп";
            case TAKE_REWARDS: return "Забирать награды";
            case CLAN_SETHOME: return "Ставить точки баз";
            case CLAN_DELHOME: return "Удалять точки баз";
            case CLAN_RENAME: return "Менять название";
            case CLAN_GLOW: return "Цвет брони";
            case ALL: return "Все права";
            default: return r.name();
        }
    }

    private ItemStack buildMainInfoItem(Player player, Clan clan) {
        ItemStack item = new ItemStack(Material.PAPER, 1);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setCustomModelData(375);
            meta.setDisplayName(this.plugin.config().color("&#FF0000[&#FF0000ℹ&#FF0000] " + gradient("Информация", 0xFF, 0x33, 0x00, 0xFF, 0xAA, 0x00)));
            List<String> lore = new ArrayList<String>();
            lore.add(this.plugin.config().color("&#FF6600★ &fУровень &#FFFF00" + clan.level));

            long needXP = 200L;
            LevelDef nextDef = getLevelDef(clan.level + 1);
            if (nextDef != null) {
                needXP = nextDef.xp;
            } else if (clan.level > 0) {
                LevelDef curDef = getLevelDef(clan.level);
                if (curDef != null) needXP = curDef.xp;
            }
            double ratio = needXP > 0 ? Math.min(1.0, Math.max(0.0, (double)clan.rating / (double)needXP)) : 1.0;
            int filled = (int)Math.round(ratio * 10.0);
            StringBuilder bar = new StringBuilder("  ");
            if (filled > 0) {
                bar.append("&#FFFF00");
                for (int i = 0; i < filled; i++) bar.append("█");
            }
            if (filled < 10) {
                bar.append("&8");
                for (int i = filled; i < 10; i++) bar.append("█");
            }
            lore.add(this.plugin.config().color(bar.toString()));
            lore.add(this.plugin.config().color("                &#FFFF00" + clan.rating + "&#FF5500/&#FF8800" + needXP));

            lore.add(this.plugin.config().color("&#00FF00🌐 &fКазна: &#00FF00$" + String.format(Locale.US, "%,d", clan.money)));
            lore.add(this.plugin.config().color("&#FF6600$ &fЛимит казны: &#FFFF00$" + String.format(Locale.US, "%,d", clan.moneyLimit)));
            lore.add("");
            lore.add(this.plugin.config().color("&#FF6600⚑ &fНазвание: &#FFFF00" + (clan.displayName != null && !clan.displayName.isEmpty() ? clan.displayName : clan.name)));
            lore.add(this.plugin.config().color("&#FF6600★ &fЛидер: &#FFFF00" + clan.owner));
            lore.add(this.plugin.config().color("&#FF6600⚕ &fУчастники: &#FFFF00" + clan.members.size() + "&#FF5500/&#FF8800" + clan.memberLimit));
            lore.add(this.plugin.config().color("&#FF6600⚔ &fPvP-режим: " + (clan.pvp ? "&#00FF00[Включен]" : "&#FF0000[Выключен]")));
            int totalStorageUnlocked = Math.min(23, clan.slotLimit);
            for (int s = 39; s <= 43; s++) {
                if (clan.isStorageSlotRented(s)) totalStorageUnlocked++;
            }
            lore.add(this.plugin.config().color("&#FF6600❖ &fХранилище: &fоткрыто &#FFFF00" + totalStorageUnlocked + "&#FF5500/&#FF880028"));

            meta.setLore(lore);
            for (ItemFlag flag : ItemFlag.values()) {
                meta.addItemFlags(flag);
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack buildMainMembersItem() {
        ItemStack item = new ItemStack(Material.PAPER, 1);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setCustomModelData(379);
            meta.setDisplayName(this.plugin.config().color("&#FF0000[&#FF0000●&#FF0000] &#FF6600Участники"));
            List<String> lore = new ArrayList<String>();
            lore.add(this.plugin.config().color("&#FF6600➥ &#FF6600Нажмите, &fчтобы открыть"));
            meta.setLore(lore);
            for (ItemFlag flag : ItemFlag.values()) {
                meta.addItemFlags(flag);
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack buildMainLevelItem() {
        ItemStack item = new ItemStack(Material.PAPER, 1);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setCustomModelData(376);
            meta.setDisplayName(this.plugin.config().color("&#00FF00[&#00FF00★&#00FF00] " + gradient("Уровень", 0x00, 0xFF, 0x00, 0x00, 0xFF, 0xFF)));
            List<String> lore = new ArrayList<String>();
            lore.add(this.plugin.config().color("&#00FF00➥ &#00FF00Нажмите, &fчтобы открыть"));
            meta.setLore(lore);
            for (ItemFlag flag : ItemFlag.values()) {
                meta.addItemFlags(flag);
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack buildMainTopItem(Clan clan) {
        ItemStack item = new ItemStack(Material.PAPER, 1);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setCustomModelData(388);
            meta.setDisplayName(this.plugin.config().color("&#FFFF00[&#FFFF00★&#FFFF00] &#FF6600Топ кланов"));
            List<String> lore = new ArrayList<String>();
            lore.add(this.plugin.config().color("&#FFFF00➥ &#FFFF00Нажмите, &fчтобы открыть"));
            if (!clan.topVisible) {
                lore.add("");
                lore.add(this.plugin.config().color("&#FF6600❖ &fКлан скрыт из топа"));
            } else {
                int pos = this.plugin.service().getTopPosition(clan);
                if (pos > 0) {
                    lore.add(this.plugin.config().color("&#FF6600❖ &fВаш клан на &#FFFF00" + pos + " &fместе"));
                }
            }
            meta.setLore(lore);
            for (ItemFlag flag : ItemFlag.values()) {
                meta.addItemFlags(flag);
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack buildMainBasesItem() {
        ItemStack item = new ItemStack(Material.PAPER, 1);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setCustomModelData(372);
            meta.setDisplayName(this.plugin.config().color("&#FF0000[&#FF0000⚑&#FF0000] " + gradient("Точки баз", 0xFF, 0x33, 0x00, 0xFF, 0xCC, 0x00)));
            List<String> lore = new ArrayList<String>();
            lore.add(this.plugin.config().color("&#FF6600➥ &#FF6600Нажмите, &fчтобы открыть"));
            meta.setLore(lore);
            for (ItemFlag flag : ItemFlag.values()) {
                meta.addItemFlags(flag);
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack buildMainErrandsItem() {
        ItemStack item = new ItemStack(Material.PAPER, 1);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setCustomModelData(385);
            meta.setDisplayName(this.plugin.config().color("&#FF0000[&#FF0000★&#FF0000] " + gradient("Поручения", 0xFF, 0x33, 0x00, 0xFF, 0xCC, 0x00)));
            List<String> lore = new ArrayList<String>();
            lore.add(this.plugin.config().color("&#FF6600➥ &#FF6600Нажмите, &fчтобы открыть"));
            meta.setLore(lore);
            for (ItemFlag flag : ItemFlag.values()) {
                meta.addItemFlags(flag);
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack buildMainArmorColorItem(Clan clan) {
        ItemStack item = new ItemStack(Material.LEATHER_HORSE_ARMOR, 1);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setCustomModelData(3);
            if (meta instanceof LeatherArmorMeta) {
                ((LeatherArmorMeta)meta).setColor(this.parseRgb(clan.glowColor, Color.fromRGB(0, 255, 0)));
            }
            meta.setDisplayName(this.plugin.config().color("&#A87BFC[&#A87BFC★&#A87BFC] &#FF8FFFЦвет брони"));
            List<String> lore = new ArrayList<String>();
            if (clan.level >= 3 || clan.glow) {
                lore.add(this.plugin.config().color("&#FF6600➥ Нажмите, &fчтобы открыть"));
                lore.add("");
                lore.add(this.plugin.config().color("&fСокланы видят броню"));
                lore.add(this.plugin.config().color("&fдруг друга цветной."));
            } else {
                lore.add(this.plugin.config().color("&fСокланы видят броню"));
                lore.add(this.plugin.config().color("&fдруг друга цветной."));
                lore.add("");
                lore.add(this.plugin.config().color("&#FF6600❖ &fСтатус: &#FF0000[Не открыто]"));
                lore.add(this.plugin.config().color("&fПрокачайте клан, чтобы открыть."));
            }
            meta.setLore(lore);
            for (ItemFlag flag : ItemFlag.values()) {
                meta.addItemFlags(flag);
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack buildMainTrialsItem() {
        ItemStack item = new ItemStack(Material.PAPER, 1);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setCustomModelData(374);
            meta.setDisplayName(this.plugin.config().color("&#FF0000[&#FF0000★&#FF0000] " + gradient("Испытания", 0xFF, 0x33, 0x00, 0xFF, 0xCC, 0x00)));
            List<String> lore = new ArrayList<String>();
            lore.add(this.plugin.config().color("&#FF6600➥ &#FF6600Нажмите, &fчтобы открыть"));
            meta.setLore(lore);
            for (ItemFlag flag : ItemFlag.values()) {
                meta.addItemFlags(flag);
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack buildMainStorageItem() {
        ItemStack item = new ItemStack(Material.PAPER, 1);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setCustomModelData(384);
            meta.setDisplayName(this.plugin.config().color("&#FF0000[&#FF0000❖&#FF0000] " + gradient("Хранилище", 0xFF, 0x33, 0x00, 0xFF, 0xCC, 0x00)));
            List<String> lore = new ArrayList<String>();
            lore.add(this.plugin.config().color("&#FF6600➥ &#FF6600Нажмите, &fчтобы открыть"));
            meta.setLore(lore);
            for (ItemFlag flag : ItemFlag.values()) {
                meta.addItemFlags(flag);
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    public static String formatWorld(String worldName) {
        if (worldName == null || worldName.equalsIgnoreCase("world")) return "Обычный мир";
        if (worldName.equalsIgnoreCase("world_nether")) return "Незер";
        if (worldName.equalsIgnoreCase("world_the_end")) return "Энд";
        if (worldName.equalsIgnoreCase("game")) return "Спавн";
        return worldName;
    }

    public static String formatPlayTime(long joinedAt) {
        long diff = Math.max(0, System.currentTimeMillis() - joinedAt);
        long minutes = diff / (1000 * 60);
        long hours = minutes / 60;
        long days = hours / 24;
        if (days > 0) {
            return days + "д " + (hours % 24) + "ч";
        }
        if (hours > 0) {
            return hours + "ч " + (minutes % 60) + "м";
        }
        return minutes + "м";
    }

    public static final class LevelDef {
        public final int level;
        public final long xp;
        public final long treasury;
        public final int tkCost;
        public final String[] perks;

        public LevelDef(int level, long xp, long treasury, int tkCost, String... perks) {
            this.level = level;
            this.xp = xp;
            this.treasury = treasury;
            this.tkCost = tkCost;
            this.perks = perks;
        }
    }

    public static final LevelDef[] LEVEL_DEFS = new LevelDef[]{
        new LevelDef(1, 200L, 2500000L, 990,
            "&#00FF00+ &fЛимит казны: &#FFFF00$2,500,000",
            "&#00FF00+ &f1 ячейка хранилища"),
        new LevelDef(2, 400L, 5000000L, 1490,
            "&#00FF00+ &fЛимит казны: &#FFFF00$5,000,000",
            "&#00FF00+ &f1 точка базы",
            "&#00FF00+ &f1 знак в названии"),
        new LevelDef(3, 600L, 10000000L, 2490,
            "&#00FF00+ &fЛимит казны: &#FFFF00$10,000,000",
            "&#00FF00+ &f1 ячейка хранилища",
            "&#00FF00+ &fОкраска брони клана"),
        new LevelDef(4, 800L, 20000000L, 4900,
            "&#00FF00+ &fЛимит казны: &#FFFF00$20,000,000",
            "&#00FF00+ &f1 ячейка хранилища",
            "&#00FF00+ &fЦвета названия: &#AAAAAA&&7 &#555555&&8"),
        new LevelDef(5, 1000L, 35000000L, 7900,
            "&#00FF00+ &fЛимит казны: &#FFFF00$35,000,000",
            "&#00FF00+ &f1 точка базы",
            "&#00FF00+ &f1 ячейка хранилища"),
        new LevelDef(6, 1200L, 50000000L, 9900,
            "&#00FF00+ &fЛимит казны: &#FFFF00$50,000,000",
            "&#00FF00+ &f1 ячейка хранилища",
            "&#00FF00+ &fОпыт &#FFFF00x1,5 &fна сутки"),
        new LevelDef(7, 1400L, 75000000L, 12900,
            "&#00FF00+ &fЛимит казны: &#FFFF00$75,000,000",
            "&#00FF00+ &f1 ячейка хранилища",
            "&#00FF00+ &fЦвета названия: &#5555FF&&9 &#0000AA&&1"),
        new LevelDef(8, 1600L, 100000000L, 14900,
            "&#00FF00+ &fЛимит казны: &#FFFF00$100,000,000",
            "&#00FF00+ &f1 ячейка хранилища",
            "&#00FF00+ &f1 знак в названии"),
        new LevelDef(9, 1800L, 150000000L, 17900,
            "&#00FF00+ &fЛимит казны: &#FFFF00$150,000,000",
            "&#00FF00+ &f1 точка базы"),
        new LevelDef(10, 2000L, 250000000L, 21900,
            "&#00FF00+ &fЛимит казны: &#FFFF00$250,000,000",
            "&#00FF00+ &f1 ячейка хранилища",
            "&#00FF00+ &fЦвета названия: &#55FFFF&&b &#00AAAA&&3",
            "&#00FF00+ &fОпыт &#FFFF00x1,5 &fна сутки"),
        new LevelDef(11, 2400L, 350000000L, 24900,
            "&#00FF00+ &fЛимит казны: &#FFFF00$350,000,000",
            "&#00FF00+ &f1 ячейка хранилища"),
        new LevelDef(12, 2800L, 50000000L, 27900,
            "&#00FF00+ &fЛимит казны: &#FFFF00$500,000,000",
            "&#00FF00+ &f1 знак в названии"),
        new LevelDef(13, 3200L, 750000000L, 29900,
            "&#00FF00+ &fЛимит казны: &#FFFF00$750,000,000",
            "&#00FF00+ &f1 ячейка хранилища",
            "&#00FF00+ &fЦвета названия: &#FF55FF&&d &#AA00AA&&5"),
        new LevelDef(14, 3600L, 1000000000L, 30900,
            "&#00FF00+ &fЛимит казны: &#FFFF00$1,000,000,000",
            "&#00FF00+ &f1 точка базы",
            "&#00FF00+ &f1 ячейка хранилища"),
        new LevelDef(15, 4000L, 1500000000L, 31900,
            "&#00FF00+ &fЛимит казны: &#FFFF00$1,500,000,000",
            "&#00FF00+ &fОпыт &#FFFF00x1,5 &fна сутки"),
        new LevelDef(16, 4400L, 2000000000L, 32900,
            "&#00FF00+ &fЛимит казны: &#FFFF00$2,000,000,000",
            "&#00FF00+ &f1 ячейка хранилища",
            "&#00FF00+ &f1 знак в названии"),
        new LevelDef(17, 4800L, 2500000000L, 33900,
            "&#00FF00+ &fЛимит казны: &#FFFF00$2,500,000,000"),
        new LevelDef(18, 5200L, 3000000000L, 34900,
            "&#00FF00+ &fЛимит казны: &#FFFF00$3,000,000,000",
            "&#00FF00+ &f1 ячейка хранилища",
            "&#00FF00+ &fЦвета названия: &#00FF00&&a &#00AA00&&2"),
        new LevelDef(19, 5600L, 4000000000L, 35900,
            "&#00FF00+ &fЛимит казны: &#FFFF00$4,000,000,000",
            "&#00FF00+ &f1 точка базы",
            "&#00FF00+ &f1 ячейка хранилища"),
        new LevelDef(20, 6000L, 5000000000L, 36900,
            "&#00FF00+ &fЛимит казны: &#FFFF00$5,000,000,000",
            "&#00FF00+ &fОпыт &#FFFF00x2 &fна сутки"),
        new LevelDef(21, 6800L, 6000000000L, 37900,
            "&#00FF00+ &fЛимит казны: &#FFFF00$6,000,000,000",
            "&#00FF00+ &f1 ячейка хранилища",
            "&#00FF00+ &f1 знак в названии"),
        new LevelDef(22, 7600L, 7500000000L, 38900,
            "&#00FF00+ &fЛимит казны: &#FFFF00$7,500,000,000",
            "&#00FF00+ &f1 точка базы"),
        new LevelDef(23, 8400L, 9000000000L, 39900,
            "&#00FF00+ &fЛимит казны: &#FFFF00$9,000,000,000",
            "&#00FF00+ &f1 ячейка хранилища",
            "&#00FF00+ &fЦвета названия: &#FFFF00&&e &#FFAA00&&6"),
        new LevelDef(24, 9200L, 10500000000L, 40900,
            "&#00FF00+ &fЛимит казны: &#FFFF00$10,500,000,000",
            "&#00FF00+ &f1 точка базы",
            "&#00FF00+ &f1 ячейка хранилища"),
        new LevelDef(25, 10000L, 12000000000L, 41900,
            "&#00FF00+ &fЛимит казны: &#FFFF00$12,000,000,000",
            "&#00FF00+ &fОпыт &#FFFF00x1,5 &fна сутки"),
        new LevelDef(26, 10800L, 12000000000L, 42900,
            "&#00FF00+ &fЛимит казны: &#FFFF00$12,000,000,000",
            "&#00FF00+ &f1 ячейка хранилища",
            "&#00FF00+ &f1 знак в названии"),
        new LevelDef(27, 11600L, 14000000000L, 43900,
            "&#00FF00+ &fЛимит казны: &#FFFF00$14,000,000,000"),
        new LevelDef(28, 12400L, 16000000000L, 44900,
            "&#00FF00+ &fЛимит казны: &#FFFF00$16,000,000,000",
            "&#00FF00+ &f1 ячейка хранилища",
            "&#00FF00+ &fЦвета названия: &#FF0000&&c &#AA0000&&4"),
        new LevelDef(29, 13200L, 18000000000L, 45900,
            "&#00FF00+ &fЛимит казны: &#FFFF00$18,000,000,000",
            "&#00FF00+ &f1 точка базы",
            "&#00FF00+ &f1 ячейка хранилища"),
        new LevelDef(30, 14000L, 20000000000L, 46900,
            "&#00FF00+ &fЛимит казны: &#FFFF00$20,000,000,000",
            "&#00FF00+ &fОпыт &#FFFF00x2 &fна сутки"),
        new LevelDef(31, 15200L, 25000000000L, 47900,
            "&#00FF00+ &fЛимит казны: &#FFFF00$25,000,000,000",
            "&#00FF00+ &f1 знак в названии"),
        new LevelDef(32, 16400L, 30000000000L, 48900,
            "&#00FF00+ &fЛимит казны: &#FFFF00$30,000,000,000",
            "&#00FF00+ &fОпыт &#FFFF00x1,5 &fна 12 часов"),
        new LevelDef(33, 17600L, 35000000000L, 49900,
            "&#00FF00+ &fЛимит казны: &#FFFF00$35,000,000,000",
            "&#00FF00+ &fЦвета названия: &#000000&&0 &#FFFFFF&&f"),
        new LevelDef(34, 18800L, 40000000000L, 50900,
            "&#00FF00+ &fЛимит казны: &#FFFF00$40,000,000,000",
            "&#00FF00+ &f1 точка базы",
            "&#00FF00+ &fОпыт &#FFFF00x1,5 &fна 12 часов"),
        new LevelDef(35, 20000L, 50000000000L, 51900,
            "&#00FF00+ &fЛимит казны: &#FFFF00$50,000,000,000",
            "&#00FF00+ &fЛюбой цвет названия",
            "&#00FF00+ &fОпыт &#FFFF00x2 &fна сутки"),
        new LevelDef(36, 21200L, 60000000000L, 52900,
            "&#00FF00+ &fЛимит казны: &#FFFF00$60,000,000,000",
            "&#00FF00+ &fОпыт &#FFFF00x1,5 &fна 12 часов"),
        new LevelDef(37, 22400L, 70000000000L, 53900,
            "&#00FF00+ &fЛимит казны: &#FFFF00$70,000,000,000",
            "&#00FF00+ &fОпыт &#FFFF00x1,5 &fна 12 часов"),
        new LevelDef(38, 23600L, 80000000000L, 54900,
            "&#00FF00+ &fЛимит казны: &#FFFF00$80,000,000,000",
            "&#00FF00+ &f1 точка базы",
            "&#00FF00+ &fОпыт &#FFFF00x1,5 &fна 12 часов"),
        new LevelDef(39, 24800L, 100000000000L, 55900,
            "&#00FF00+ &fЛимит казны: &#FFFF00$100,000,000,000")
    };

    public static LevelDef getLevelDef(int level) {
        if (level >= 1 && level <= LEVEL_DEFS.length) {
            return LEVEL_DEFS[level - 1];
        }
        return null;
    }

    private static String getChatColorHex(String code) {
        switch (code) {
            case "0": return "&#000000";
            case "1": return "&#0000AA";
            case "2": return "&#00AA00";
            case "3": return "&#00AAAA";
            case "4": return "&#AA0000";
            case "5": return "&#AA00AA";
            case "6": return "&#FFAA00";
            case "7": return "&#AAAAAA";
            case "8": return "&#555555";
            case "9": return "&#5555FF";
            case "a": return "&#00FF00";
            case "b": return "&#55FFFF";
            case "c": return "&#FF0000";
            case "d": return "&#FF55FF";
            case "e": return "&#FFFF00";
            case "f": return "&#FFFFFF";
            default: return "&#FFFFFF";
        }
    }

    public static final int[] LEVEL_MENU_SLOTS = new int[]{9, 10, 19, 28, 29, 30, 31, 32, 33, 34, 25, 16, 17};

    public String buildLevelBannerTitle(int page) {
        String frameGlyph = page == 0 ? "D" : (page == 1 ? "X" : "Y");
        String cleanName = "УРОВЕНЬ КЛАНА";
        int textWidth = 78;
        int targetShift = (int)Math.round(100.5 + (textWidth / 2.0));
        int bestDiff = Integer.MAX_VALUE;
        String bestSeq = "600";
        for (int i = 0; i < BASE_SHIFTS.length; i++) {
            int base = BASE_SHIFTS[i];
            char ch = BASE_CHARS[i];
            for (int z = 0; z <= 6; z++) {
                int total = base + (z * 9);
                int diff = Math.abs(total - targetShift);
                if (diff < bestDiff) {
                    bestDiff = diff;
                    StringBuilder sb = new StringBuilder().append(ch);
                    for (int k = 0; k < z; k++) sb.append('0');
                    bestSeq = sb.toString();
                }
            }
        }
        return "<font:minecraft:clan><white>0B1" + frameGlyph + "2z" + bestSeq + "<font:minecraft:title><white>" + cleanName;
    }

    private ItemStack buildLevelArrowItem(boolean next, int currentPage, int totalPages) {
        ItemStack item = new ItemStack(Material.PAPER, 1);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setCustomModelData(next ? 415 : 416);
            meta.setDisplayName(this.plugin.config().color("&#FF0000" + (next ? "➜" : "⬅") + " &#FF6600" + (next ? "Следующие уровни" : "Предыдущие уровни")));
            List<String> lore = new ArrayList<String>();
            lore.add(this.plugin.config().color("&#FF6600Страница &#FFFF00" + (currentPage + 1) + "/" + totalPages));
            meta.setLore(lore);
            for (ItemFlag flag : ItemFlag.values()) {
                meta.addItemFlags(flag);
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    public void applyLevelPerks(Clan clan, int targetLevel) {
        if (clan == null || targetLevel < 1 || targetLevel > LEVEL_DEFS.length) return;
        LevelDef def = LEVEL_DEFS[targetLevel - 1];
        clan.moneyLimit = def.treasury;
        if (targetLevel >= 3) {
            clan.glow = true;
        }
        int extraSlots = 0;
        int extraBases = 0;
        int extraChars = 0;
        for (int i = 1; i <= targetLevel; i++) {
            LevelDef d = LEVEL_DEFS[i - 1];
            for (String p : d.perks) {
                if (p.contains("ячейка хранилища")) extraSlots++;
                if (p.contains("точка базы")) extraBases++;
                if (p.contains("знак в названии")) extraChars++;
                if (p.contains("&7") && !clan.colors.contains("&7")) clan.colors.add("&7");
                if (p.contains("&8") && !clan.colors.contains("&8")) clan.colors.add("&8");
                if (p.contains("&9") && !clan.colors.contains("&9")) clan.colors.add("&9");
                if (p.contains("&1") && !clan.colors.contains("&1")) clan.colors.add("&1");
                if (p.contains("&b") && !clan.colors.contains("&b")) clan.colors.add("&b");
                if (p.contains("&3") && !clan.colors.contains("&3")) clan.colors.add("&3");
                if (p.contains("&d") && !clan.colors.contains("&d")) clan.colors.add("&d");
                if (p.contains("&5") && !clan.colors.contains("&5")) clan.colors.add("&5");
                if (p.contains("&a") && !clan.colors.contains("&a")) clan.colors.add("&a");
                if (p.contains("&2") && !clan.colors.contains("&2")) clan.colors.add("&2");
                if (p.contains("&e") && !clan.colors.contains("&e")) clan.colors.add("&e");
                if (p.contains("&6") && !clan.colors.contains("&6")) clan.colors.add("&6");
                if (p.contains("&c") && !clan.colors.contains("&c")) clan.colors.add("&c");
                if (p.contains("&4") && !clan.colors.contains("&4")) clan.colors.add("&4");
                if (p.contains("&0") && !clan.colors.contains("&0")) clan.colors.add("&0");
                if (p.contains("&f") && !clan.colors.contains("&f")) clan.colors.add("&f");
                if (p.contains("Любой цвет")) {
                    for (String code : new String[]{"&0", "&1", "&2", "&3", "&4", "&5", "&6", "&7", "&8", "&9", "&a", "&b", "&c", "&d", "&e", "&f"}) {
                        if (!clan.colors.contains(code)) clan.colors.add(code);
                    }
                }
            }
        }
        clan.slotLimit = Math.max(clan.slotLimit, 2 + extraSlots);
        clan.homeLimit = Math.max(clan.homeLimit, 1 + extraBases);
        clan.lengthLimit = Math.max(clan.lengthLimit, 12 + extraChars);
    }

    public void levels(Player player, Clan clan) {
        this.levels(player, clan, 0);
    }

    public void levels(Player player, Clan clan, int page) {
        if (clan != null) {
            if (page < 0) page = 0;
            if (page > 2) page = 2;
            View view = new View("levels", clan, page);
            String title = this.buildLevelBannerTitle(page);
            Inventory inventory = this.inventory(view, 54, title);

            ItemStack shield = new ItemStack(Material.PAPER, 1);
            ItemMeta sMeta = shield.getItemMeta();
            if (sMeta != null) {
                sMeta.setCustomModelData(376);
                sMeta.setDisplayName(this.plugin.config().color("&#FF0000[&#FF0000★&#FF0000] &#FF6600Уровень клана"));
                List<String> lore = new ArrayList<String>();
                lore.add(this.plugin.config().color("&#FF6600★ &fУровень &#FFFF00" + clan.level));
                long nextXP = 200L;
                LevelDef nextDef = getLevelDef(clan.level + 1);
                if (nextDef != null) {
                    nextXP = nextDef.xp;
                } else if (clan.level > 0) {
                    LevelDef curDef = getLevelDef(clan.level);
                    if (curDef != null) nextXP = curDef.xp;
                }
                double ratio = nextXP > 0 ? Math.min(1.0, Math.max(0.0, (double)clan.rating / (double)nextXP)) : 1.0;
                int filled = (int)Math.round(ratio * 10.0);
                StringBuilder bar = new StringBuilder();
                bar.append("&#00FF00");
                for (int i = 0; i < filled; i++) bar.append("■");
                bar.append("&#555555");
                for (int i = filled; i < 10; i++) bar.append("■");
                lore.add(this.plugin.config().color(bar.toString()));
                lore.add(this.plugin.config().color("          &#FFFF00" + clan.rating + "&#FF6600/&#FF6600" + nextXP));
                long curTreasury = clan.moneyLimit > 0 ? clan.moneyLimit : 250000L;
                lore.add(this.plugin.config().color("&#FF6600🪙 &fЛимит казны: &#FFFF00$" + String.format(Locale.US, "%,d", curTreasury)));

                int extraSlots = 0;
                int extraBases = 0;
                int extraChars = 0;
                for (int i = 1; i <= clan.level; i++) {
                    LevelDef d = getLevelDef(i);
                    if (d != null) {
                        for (String p : d.perks) {
                            if (p.contains("ячейка хранилища")) extraSlots++;
                            if (p.contains("точка базы")) extraBases++;
                            if (p.contains("знак в названии")) extraChars++;
                        }
                    }
                }
                if (clan.level > 0) {
                    lore.add("");
                    lore.add(this.plugin.config().color("&#FF6600★ &fБусты клана:"));
                    if (extraSlots > 0) lore.add(this.plugin.config().color("  &#FF6600• &fЯчейки хранилища: &#FFFF00+" + extraSlots));
                    if (extraBases > 0) lore.add(this.plugin.config().color("  &#FF6600• &fТочки баз: &#FFFF00+" + extraBases));
                    if (extraChars > 0) lore.add(this.plugin.config().color("  &#FF6600• &fЗнаки в названии: &#FFFF00+" + extraChars));
                    if (clan.level >= 3 || clan.glow) lore.add(this.plugin.config().color("  &#FF6600• &fОкраска брони клана: &#00FF00[Открыто]"));
                    if (!clan.colors.isEmpty()) {
                        StringBuilder colSb = new StringBuilder();
                        for (String c : clan.colors) {
                            String code = c.replace("&", "").replace("§", "").toLowerCase(Locale.ROOT);
                            String hex = getChatColorHex(code);
                            colSb.append(hex).append("&&").append(code).append(" ");
                        }
                        lore.add(this.plugin.config().color("  &#FF6600• &fЦвета названия: " + colSb.toString().trim()));
                    }
                }
                sMeta.setLore(lore);
                for (ItemFlag flag : ItemFlag.values()) sMeta.addItemFlags(flag);
                shield.setItemMeta(sMeta);
            }
            inventory.setItem(13, shield);

            for (int i = 0; i < LEVEL_MENU_SLOTS.length; i++) {
                int lvl = page * 13 + i + 1;
                if (lvl > 39) break;
                int slot = LEVEL_MENU_SLOTS[i];
                view.slotLevel.put(slot, lvl);

                LevelDef def = getLevelDef(lvl);
                if (def == null) continue;

                ItemStack bottle = new ItemStack(Material.PAPER, 1);
                ItemMeta bMeta = bottle.getItemMeta();
                if (bMeta != null) {
                    if (lvl <= clan.level) {
                        bMeta.setCustomModelData(1);
                        bMeta.setDisplayName(this.plugin.config().color("&#FF0000[" + lvl + "] &#FF6600Уровень"));
                        List<String> lore = new ArrayList<String>();
                        lore.add(this.plugin.config().color("&#00FF00Уровень пройден!"));
                        for (String perk : def.perks) {
                            lore.add(this.plugin.config().color(perk));
                        }
                        bMeta.setLore(lore);
                    } else if (lvl == clan.level + 1) {
                        bMeta.setCustomModelData(395);
                        bMeta.setDisplayName(this.plugin.config().color("&#FF0000[" + lvl + "] &#FF6600Уровень"));
                        List<String> lore = new ArrayList<String>();
                        lore.add(this.plugin.config().color("&#FF6600❖ &fОпыт: &#FFFF00" + clan.rating + "&#FF6600/&#FF6600" + String.format(Locale.US, "%,d", def.xp)));
                        for (String perk : def.perks) {
                            lore.add(this.plugin.config().color(perk));
                        }
                        lore.add("");
                        lore.add(this.plugin.config().color("&#00FF00🌐 &fПройти быстрее: &#00FF00" + def.tkCost + " ТК"));
                        lore.add(this.plugin.config().color("&#FF0000➥ &#FF6600Нажмите, &fчтобы купить уровень"));
                        bMeta.setLore(lore);
                    } else {
                        bMeta.setCustomModelData(394);
                        bMeta.setDisplayName(this.plugin.config().color("&#FF0000[" + lvl + "] &#FF6600Уровень"));
                        List<String> lore = new ArrayList<String>();
                        lore.add(this.plugin.config().color("&#FF6600❖ &fНужно опыта: &#FFFF00" + String.format(Locale.US, "%,d", def.xp)));
                        for (String perk : def.perks) {
                            lore.add(this.plugin.config().color(perk));
                        }
                        lore.add("");
                        lore.add(this.plugin.config().color("&#00FF00🌐 &fПройти быстрее: &#00FF00" + def.tkCost + " ТК"));
                        lore.add(this.plugin.config().color("&#FF0000➥ &#FF6600Нажмите, &fчтобы купить уровень"));
                        bMeta.setLore(lore);
                    }
                    for (ItemFlag flag : ItemFlag.values()) bMeta.addItemFlags(flag);
                    bottle.setItemMeta(bMeta);
                }
                inventory.setItem(slot, bottle);
            }

            if (page > 0) {
                inventory.setItem(46, this.buildLevelArrowItem(false, page, 3));
            }
            inventory.setItem(49, this.buildDoorItem());
            if (page < 2) {
                inventory.setItem(52, this.buildLevelArrowItem(true, page, 3));
            }
            player.openInventory(inventory);
        }
    }

    public void rewards(Player player, Clan clan, int page) {
        this.levels(player, clan, page);
    }

    public void buyConfirm(Player player, Clan clan, int targetLevel) {
        if (clan != null) {
            View view = new View("buyConfirm", clan, 0);
            view.targetLevel = targetLevel;
            Inventory inventory = this.inventory(view, 27, this.plugin.config().color("&#FF0000Купить уровень &#FFFF00" + targetLevel + "?"));

            ItemStack confirm = new ItemStack(Material.LIME_STAINED_GLASS_PANE, 1);
            ItemMeta cMeta = confirm.getItemMeta();
            if (cMeta != null) {
                cMeta.setDisplayName(this.plugin.config().color("&#00FF00Подтвердить"));
                confirm.setItemMeta(cMeta);
            }
            for (int slot : new int[]{0, 1, 2, 9, 10, 11, 18, 19, 20}) {
                inventory.setItem(slot, confirm);
            }

            ItemStack gray = new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1);
            ItemMeta gMeta = gray.getItemMeta();
            if (gMeta != null) {
                gMeta.setDisplayName(this.plugin.config().color(" "));
                gray.setItemMeta(gMeta);
            }
            for (int slot : new int[]{3, 4, 5, 12, 13, 14, 21, 22, 23}) {
                inventory.setItem(slot, gray);
            }

            ItemStack cancel = new ItemStack(Material.RED_STAINED_GLASS_PANE, 1);
            ItemMeta canMeta = cancel.getItemMeta();
            if (canMeta != null) {
                canMeta.setDisplayName(this.plugin.config().color("&#FF0000Отмена"));
                cancel.setItemMeta(canMeta);
            }
            for (int slot : new int[]{6, 7, 8, 15, 16, 17, 24, 25, 26}) {
                inventory.setItem(slot, cancel);
            }

            player.openInventory(inventory);
        }
    }

    public void bases(Player player, Clan clan) {
        if (clan != null) {
            View view = new View("bases", clan, 0);
            String title = this.buildBannerTitle("ТОЧКИ БАЗ", null);
            Inventory inventory = this.inventory(view, 54, title);

            int[] shelfSlots = new int[]{20, 21, 22, 23, 24, 29, 30, 31, 32, 33};
            List<Map.Entry<String, Location>> homesList = new ArrayList<>(clan.homes.entrySet());

            for (int i = 0; i < shelfSlots.length; i++) {
                int slot = shelfSlots[i];
                if (i < homesList.size()) {
                    Map.Entry<String, Location> entry = homesList.get(i);
                    String homeName = entry.getKey();
                    Location loc = entry.getValue();
                    view.slotKeys.put(slot, homeName);

                    ItemStack item = new ItemStack(Material.PAPER, 1);
                    ItemMeta meta = item.getItemMeta();
                    if (meta != null) {
                        meta.setCustomModelData(372);
                        meta.setDisplayName(this.plugin.config().color("&#FF0000[&#FF0000⚑&#FF0000] &#FF6600" + homeName));
                        List<String> lore = new ArrayList<String>();
                        lore.add(this.plugin.config().color("&#FF6600➥ &#FF6600Нажмите, &fчтобы телепортироваться"));
                        lore.add("");
                        String serverName = this.plugin.config().getSetting("server.name",
                            this.plugin.config().getSetting("server-name", "Анархия-304"));
                        lore.add(this.plugin.config().color("&#FF6600★ &fАнархия: &#FFFF00" + serverName));
                        String worldName = (loc != null && loc.getWorld() != null) ? loc.getWorld().getName() : "world";
                        lore.add(this.plugin.config().color("&#FF6600• &fМир: &#FFFF00" + formatWorld(worldName)));
                        int bx = loc != null ? loc.getBlockX() : 0;
                        int by = loc != null ? loc.getBlockY() : 0;
                        int bz = loc != null ? loc.getBlockZ() : 0;
                        lore.add(this.plugin.config().color("&#FF6600❖ &fКоординаты: &#FFFF00" + bx + " " + by + " " + bz));
                        meta.setLore(lore);
                        for (ItemFlag flag : ItemFlag.values()) meta.addItemFlags(flag);
                        item.setItemMeta(meta);
                    }
                    inventory.setItem(slot, item);
                } else if (i < Math.max(1, clan.homeLimit)) {
                    ItemStack freeItem = new ItemStack(Material.PAPER, 1);
                    ItemMeta meta = freeItem.getItemMeta();
                    if (meta != null) {
                        meta.setCustomModelData(1);
                        meta.setDisplayName(this.plugin.config().color("&#00FF00[&#00FF00⚑&#00FF00] &#FF6600Свободно"));
                        List<String> lore = new ArrayList<String>();
                        lore.add(this.plugin.config().color("&fПоставить точку:"));
                        lore.add(this.plugin.config().color("&#FFFF00/clan sethome <название>"));
                        meta.setLore(lore);
                        for (ItemFlag flag : ItemFlag.values()) meta.addItemFlags(flag);
                        freeItem.setItemMeta(meta);
                    }
                    inventory.setItem(slot, freeItem);
                } else {
                    ItemStack lockItem = new ItemStack(Material.PAPER, 1);
                    ItemMeta meta = lockItem.getItemMeta();
                    if (meta != null) {
                        meta.setCustomModelData(394);
                        meta.setDisplayName(this.plugin.config().color("&#FF0000[&#FF0000⚑&#FF0000] &#FF6600Закрыто"));
                        List<String> lore = new ArrayList<String>();
                        lore.add(this.plugin.config().color("&fПрокачайте клан, чтобы открыть."));
                        meta.setLore(lore);
                        for (ItemFlag flag : ItemFlag.values()) meta.addItemFlags(flag);
                        lockItem.setItemMeta(meta);
                    }
                    inventory.setItem(slot, lockItem);
                }
            }

            inventory.setItem(49, this.buildDoorItem());
            player.openInventory(inventory);
        }
    }

    public void errands(Player player, Clan clan) {
        if (clan != null) {
            View view = new View("errands", clan, 0);
            String title = this.buildBannerTitle("ЗАДАНИЯ КЛАНА", "C");
            Inventory inventory = this.inventory(view, 54, title);

            ItemStack info = new ItemStack(Material.PAPER, 1);
            ItemMeta iMeta = info.getItemMeta();
            if (iMeta != null) {
                iMeta.setCustomModelData(375);
                iMeta.setDisplayName(this.plugin.config().color("&#FF0000[&#FF0000ℹ&#FF0000] &#FF6600Что такое поручения"));
                List<String> lore = new ArrayList<String>();
                lore.add(this.plugin.config().color("&fЗадания клана на неделю."));
                lore.add(this.plugin.config().color("&fКаждый участник выполняет"));
                lore.add(this.plugin.config().color("&fих самостоятельно, прогресс"));
                lore.add(this.plugin.config().color("&fу каждого свой."));
                lore.add("");
                lore.add(this.plugin.config().color("&#FF6600❖ &fНаграды за выполнение:"));
                lore.add(this.plugin.config().color("  &f• &fопыт — клану"));
                lore.add(this.plugin.config().color("  &f• &fлут — тому, кто выполнил"));
                lore.add("");
                lore.add(this.plugin.config().color("&#FF6600❖ &fНовые — каждую неделю."));
                iMeta.setLore(lore);
                for (ItemFlag flag : ItemFlag.values()) iMeta.addItemFlags(flag);
                info.setItemMeta(iMeta);
            }
            inventory.setItem(13, info);

            int[] taskSlots = new int[]{20, 21, 22, 23, 24};
            String[] taskNames = new String[]{"Бахча", "Алмазная лихорадка", "Посейдон", "Изгнание", "Месть"};
            String[] taskDescs = new String[]{
                "Сломай 64 арбузов инструментом с Удачей",
                "Добудь алмазной руды киркой с Удачей",
                "Убей 24 существа трезубцем",
                "Убей 32 ифрита",
                "Убей 5 игроков не из клана"
            };

            for (int i = 0; i < taskSlots.length; i++) {
                ItemStack task = new ItemStack(Material.PAPER, 1);
                ItemMeta tMeta = task.getItemMeta();
                if (tMeta != null) {
                    tMeta.setCustomModelData(385);
                    tMeta.setDisplayName(this.plugin.config().color("&#FF0000[&#FF0000★&#FF0000] &#FF6600" + taskNames[i]));
                    List<String> lore = new ArrayList<String>();
                    lore.add(this.plugin.config().color("&f" + taskDescs[i]));
                    lore.add("");
                    lore.add(this.plugin.config().color("&#FF6600❖ &fПрогресс: &#FFFF000&#FF5500/&#FF8800" + (i == 0 ? 64 : (i == 1 ? 32 : (i == 2 ? 24 : (i == 3 ? 32 : 5))))));
                    lore.add(this.plugin.config().color("&#FF6600❖ &fОпыт клану: &#00FF00+200"));
                    tMeta.setLore(lore);
                    for (ItemFlag flag : ItemFlag.values()) tMeta.addItemFlags(flag);
                    task.setItemMeta(tMeta);
                }
                inventory.setItem(taskSlots[i], task);
            }

            inventory.setItem(49, this.buildDoorItem());
            player.openInventory(inventory);
        }
    }

    public void trials(Player player, Clan clan) {
        if (clan != null) {
            View view = new View("trials", clan, 0);
            String title = this.buildBannerTitle("ИСПЫТАНИЯ", "C");
            Inventory inventory = this.inventory(view, 54, title);

            ItemStack info = new ItemStack(Material.PAPER, 1);
            ItemMeta iMeta = info.getItemMeta();
            if (iMeta != null) {
                iMeta.setCustomModelData(375);
                iMeta.setDisplayName(this.plugin.config().color("&#FF0000[&#FF0000ℹ&#FF0000] &#FF6600Что такое испытания"));
                List<String> lore = new ArrayList<String>();
                lore.add(this.plugin.config().color("&fОбщие задания всего клана."));
                lore.add(this.plugin.config().color("&fПрогресс один на всех: в него"));
                lore.add(this.plugin.config().color("&fидут действия каждого участника."));
                lore.add("");
                lore.add(this.plugin.config().color("&#FF6600❖ &fНаграды:"));
                lore.add(this.plugin.config().color("  &f• &fопыт — клану"));
                lore.add(this.plugin.config().color("  &f• &fлут — тому, кто завершил"));
                lore.add(this.plugin.config().color("  &f• &fчарджи — в склад"));
                lore.add("");
                lore.add(this.plugin.config().color("&#FF6600❖ &fВыполненное сменится новым"));
                lore.add(this.plugin.config().color("&fчерез 24 ч."));
                iMeta.setLore(lore);
                for (ItemFlag flag : ItemFlag.values()) iMeta.addItemFlags(flag);
                info.setItemMeta(iMeta);
            }
            inventory.setItem(13, info);

            int[] trialSlots = new int[]{20, 21, 22, 23, 24};
            String[] trialNames = new String[]{"Удобрение", "Каменоломня", "Тотемы смерти", "Клановая война", "Зачистка"};
            String[] trialDescs = new String[]{
                "Удобрите кланом растения костной мукой 300 раз",
                "Добудьте кланом 500 камня или сланца киркой с Удачей",
                "Выбейте кланом 8 тотемов бессмертия у игроков",
                "Убейте кланом 20 игроков не из клана",
                "Убейте кланом 100 мобов в Незере"
            };

            for (int i = 0; i < trialSlots.length; i++) {
                ItemStack trial = new ItemStack(Material.PAPER, 1);
                ItemMeta tMeta = trial.getItemMeta();
                if (tMeta != null) {
                    tMeta.setCustomModelData(374);
                    tMeta.setDisplayName(this.plugin.config().color("&#FF0000[&#FF0000★&#FF0000] &#FF6600" + trialNames[i]));
                    List<String> lore = new ArrayList<String>();
                    lore.add(this.plugin.config().color("&f" + trialDescs[i]));
                    lore.add("");
                    lore.add(this.plugin.config().color("&#FF6600❖ &fПрогресс: &#FFFF000&#FF5500/&#FF8800" + (i == 0 ? 300 : (i == 1 ? 500 : (i == 2 ? 8 : (i == 3 ? 20 : 100))))));
                    lore.add(this.plugin.config().color("&#FF6600❖ &fОпыт клану: &#00FF00+500"));
                    tMeta.setLore(lore);
                    for (ItemFlag flag : ItemFlag.values()) tMeta.addItemFlags(flag);
                    trial.setItemMeta(tMeta);
                }
                inventory.setItem(trialSlots[i], trial);
            }

            inventory.setItem(49, this.buildDoorItem());
            player.openInventory(inventory);
        }
    }

    public void seller(Player player, Clan clan) {
        if (clan != null) {
            View view = new View("seller", clan, 0);
            YamlConfiguration yaml = this.plugin.config().get("features/seller.yml");
            Inventory inventory = this.inventory(view, 27, this.plugin.config().color(yaml.getString("title", "Клановый скупщик")));
            ItemStack panel = this.item(player, clan, "panel");
            for (int slot : new int[]{0, 1, 2, 3, 5, 6, 7, 8, 9, 10, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26}) {
                inventory.setItem(slot, panel);
            }
            inventory.setItem(4, this.item(player, clan, "back"));
            ConfigurationSection entries = yaml.getConfigurationSection("items");
            if (entries != null) {
                int slot = 11;
                for (String key : entries.getKeys(false)) {
                    if (slot > 15) continue;
                    ConfigurationSection sec = entries.getConfigurationSection(key);
                    inventory.setItem(slot++, this.item(player, clan, sec));
                    view.keys.add(key);
                }
            }
            player.openInventory(inventory);
        }
    }

    public static final int[] STORAGE_SLOTS = new int[]{
        10, 11, 12, 13, 14, 15, 16,
        19, 20, 21, 22, 23, 24, 25,
        28, 29, 30, 31, 32, 33, 34,
        37, 38, 39, 40, 41, 42, 43
    };

    public boolean isPlaceholder(ItemStack item) {
        if (item == null || item.getType() != Material.PAPER || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasCustomModelData()) return false;
        int cmd = meta.getCustomModelData();
        return cmd == 394 || cmd == 378 || cmd == 402 || cmd == 140 || cmd == 371;
    }

    public boolean isStorageSlotUnlocked(Clan clan, int slot) {
        if (clan == null) return false;
        int idx = this.storageIndex(slot);
        if (idx < 0 || idx >= 28) return false;
        if (idx < 23) {
            return idx < clan.slotLimit;
        } else {
            return clan.isStorageSlotRented(slot) || idx < clan.slotLimit;
        }
    }

    public void refreshStorageSlot(Clan clan, Inventory inventory, int slot) {
        if (clan == null || inventory == null) return;
        int idx = this.storageIndex(slot);
        if (idx < 0 || idx >= 28) return;
        if (this.isStorageSlotUnlocked(clan, slot)) {
            ItemStack currentInInv = inventory.getItem(slot);
            if (currentInInv != null && currentInInv.getType() != Material.AIR && !this.isPlaceholder(currentInInv)) {
                return;
            }
            ItemStack item = clan.storage.get(slot);
            if (item != null && item.getType() != Material.AIR && !this.isPlaceholder(item)) {
                inventory.setItem(slot, item.clone());
            } else {
                inventory.setItem(slot, null);
            }
        } else if (idx >= 23) {
            ItemStack rentItem = new ItemStack(Material.PAPER, 1);
            ItemMeta meta = rentItem.getItemMeta();
            if (meta != null) {
                meta.setCustomModelData(378);
                meta.setDisplayName(this.plugin.config().color("&#FF0000[&#FF0000⛃&#FF0000] &#FF6600Аренда ячейки"));
                List<String> lore = new ArrayList<String>();
                lore.add(this.plugin.config().color("&#FF6600➥ Нажмите, &fчтобы арендовать"));
                lore.add("");
                lore.add(this.plugin.config().color("&#00FF00$ &fЦена: &#00FF00490 ТК"));
                lore.add(this.plugin.config().color("&#FF6600• &fСрок: &#FFFF001000 ч"));
                meta.setLore(lore);
                for (ItemFlag flag : ItemFlag.values()) meta.addItemFlags(flag);
                rentItem.setItemMeta(meta);
            }
            inventory.setItem(slot, rentItem);
        } else {
            ItemStack lockItem = new ItemStack(Material.PAPER, 1);
            ItemMeta meta = lockItem.getItemMeta();
            if (meta != null) {
                meta.setCustomModelData(394);
                meta.setDisplayName(this.plugin.config().color("&#FF0000Ячейка закрыта &7— прокачайте клан"));
                for (ItemFlag flag : ItemFlag.values()) meta.addItemFlags(flag);
                lockItem.setItemMeta(meta);
            }
            inventory.setItem(slot, lockItem);
        }
    }

    public void storage(Player player, Clan clan) {
        if (!clan.permits(player.getName(), Clan.Right.USE_STORAGE)) {
            this.plugin.config().send(player, "lang.noPermission", new String[0]);
        } else {
            Inventory inventory = this.sharedStorage.get(clan.name.toLowerCase(Locale.ROOT));
            if (inventory == null) {
                View view = new View("storage", clan, 0);
                String title = this.buildBannerTitle("ХРАНИЛИЩЕ", "H");
                inventory = this.inventory(view, 54, title);
                this.sharedStorage.put(clan.name.toLowerCase(Locale.ROOT), inventory);
            }
            for (int slot : STORAGE_SLOTS) {
                this.refreshStorageSlot(clan, inventory, slot);
            }
            inventory.setItem(49, this.buildDoorItem());
            player.openInventory(inventory);
        }
    }

    public int storageIndex(int slot) {
        int y = slot / 9;
        int x = slot % 9;
        if (y >= 1 && y <= 4 && x >= 1 && x <= 7) {
            return (y - 1) * 7 + (x - 1);
        }
        return -1;
    }

    public void saveStorage(Clan clan, Inventory inventory) {
        if (clan == null || inventory == null) return;
        for (int slot : STORAGE_SLOTS) {
            if (this.isStorageSlotUnlocked(clan, slot)) {
                ItemStack item = inventory.getItem(slot);
                if (item != null && item.getType() != Material.AIR && !this.isPlaceholder(item)) {
                    clan.storage.put(slot, item.clone());
                } else {
                    clan.storage.remove(slot);
                }
            }
        }
        this.plugin.service().store().changed();
    }

    public void confirmStorageRent(Player player, Clan clan, int slot) {
        if (clan != null) {
            View view = new View("storageRentConfirm", clan, 0);
            view.targetSlot = slot;
            Inventory inventory = this.inventory(view, 27, this.plugin.config().color("&#FF0000Арендовать ячейку хранилища?"));

            ItemStack confirm = new ItemStack(Material.LIME_STAINED_GLASS_PANE, 1);
            ItemMeta cMeta = confirm.getItemMeta();
            if (cMeta != null) {
                cMeta.setDisplayName(this.plugin.config().color("&#00FF00Подтвердить"));
                confirm.setItemMeta(cMeta);
            }
            for (int s : new int[]{0, 1, 2, 9, 10, 11, 18, 19, 20}) {
                inventory.setItem(s, confirm);
            }

            ItemStack gray = new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1);
            ItemMeta gMeta = gray.getItemMeta();
            if (gMeta != null) {
                gMeta.setDisplayName(this.plugin.config().color("&7 "));
                gray.setItemMeta(gMeta);
            }
            for (int s : new int[]{3, 4, 5, 12, 13, 14, 21, 22, 23}) {
                inventory.setItem(s, gray);
            }

            ItemStack cancel = new ItemStack(Material.RED_STAINED_GLASS_PANE, 1);
            ItemMeta canMeta = cancel.getItemMeta();
            if (canMeta != null) {
                canMeta.setDisplayName(this.plugin.config().color("&#FF0000Отмена"));
                cancel.setItemMeta(canMeta);
            }
            for (int s : new int[]{6, 7, 8, 15, 16, 17, 24, 25, 26}) {
                inventory.setItem(s, cancel);
            }

            player.openInventory(inventory);
        }
    }

    private static final String[] RIGHTS_PAGE_TEXT_GLYPHS = new String[]{"T", "U", "V", "W"};
    private static final Clan.Right[][] RIGHTS_PAGES = new Clan.Right[][]{
        {Clan.Right.INVITE_PLAYERS, Clan.Right.KICK_PLAYERS, Clan.Right.USE_STORAGE, Clan.Right.EDIT_PERMISSIONS},
        {Clan.Right.CHANGE_PREFIX, Clan.Right.SHOW_MONEY, Clan.Right.INVEST_MONEY, Clan.Right.WITHDRAW_MONEY},
        {Clan.Right.PVP_TOGGLE, Clan.Right.TAKE_REWARDS, Clan.Right.CLAN_SETHOME, Clan.Right.CLAN_DELHOME},
        {Clan.Right.CLAN_RENAME}
    };
    private static final int[] SWITCH_SLOTS = new int[]{16, 25, 34, 43};
    private static final int[] MEMBER_SHELF_SLOTS = new int[]{20, 21, 22, 23, 24, 29, 30, 31, 32, 33};

    public void members(Player player, Clan clan, int page) {
        if (clan != null) {
            int maxPages = (int)Math.ceil((double)clan.members.size() / (double)MEMBER_SHELF_SLOTS.length);
            if (maxPages < 1) maxPages = 1;
            if (page >= maxPages) page = maxPages - 1;
            if (page < 0) page = 0;

            View view = new View("members", clan, page);
            String title = this.buildBannerTitle("УЧАСТНИКИ КЛАНА", "C");
            Inventory inventory = this.inventory(view, 54, title);

            if (clan.owner != null && clan.member(clan.owner) == null) {
                Clan.Member ownerMember = new Clan.Member(clan.owner, this.plugin.config().text("command.ownerPrefix"));
                clan.add(ownerMember);
            }

            ArrayList<Clan.Member> members = new ArrayList<Clan.Member>(clan.members.values());
            members.removeIf(m -> m == null || m.name == null);
            members.sort((a, b) -> {
                boolean aOwner = clan.owns(a.name);
                boolean bOwner = clan.owns(b.name);
                if (aOwner != bOwner) {
                    return aOwner ? -1 : 1;
                }
                return a.name.compareToIgnoreCase(b.name);
            });

            int start = page * MEMBER_SHELF_SLOTS.length;
            for (int index = start; index < Math.min(start + MEMBER_SHELF_SLOTS.length, members.size()); ++index) {
                Clan.Member member = members.get(index);
                ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
                SkullMeta meta = (SkullMeta)head.getItemMeta();
                if (meta != null) {
                    meta.setCustomModelData(3);
                    SkinHelper.applySkin(meta, member.name);
                    boolean isOwner = clan.owns(member.name);
                    String roleTag = isOwner ? "&#FF0000[Лидер] " : "&#FF0000[Участник] ";
                    meta.setDisplayName(this.plugin.config().color(roleTag + "&#FF6600" + member.name));
                    List<String> lore = new ArrayList<String>();
                    lore.add(this.plugin.config().color("&#FF6600➥ Нажмите, &fчтобы открыть"));
                    if (isOwner) {
                        lore.add("");
                        lore.add(this.plugin.config().color("&#FF6600★ &fВладелец клана"));
                    }
                    meta.setLore(lore);
                    head.setItemMeta(meta);
                }
                int slot = MEMBER_SHELF_SLOTS[index - start];
                inventory.setItem(slot, head);
                view.slotKeys.put(slot, member.name);
            }

            if (page > 0) {
                inventory.setItem(46, this.buildArrowItem(false, page + 1, maxPages));
            }
            inventory.setItem(49, this.buildDoorItem());
            if (start + MEMBER_SHELF_SLOTS.length < members.size()) {
                inventory.setItem(52, this.buildArrowItem(true, page + 1, maxPages));
            }

            player.openInventory(inventory);
        }
    }

    public void memberProfile(Player player, Clan clan, Clan.Member target) {
        if (clan != null) {
            if (target == null && clan.owner != null) {
                target = clan.member(clan.owner);
                if (target == null) {
                    target = new Clan.Member(clan.owner, this.plugin.config().text("command.ownerPrefix"));
                    clan.add(target);
                }
            }
            if (target == null) {
                return;
            }
            View view = new View("member_profile", clan, 0);
            view.targetMember = target.name;
            String title = this.buildBannerTitle(target.name.toUpperCase(Locale.ROOT), "F");
            Inventory inventory = this.inventory(view, 54, title);

            ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
            SkullMeta meta = (SkullMeta)head.getItemMeta();
            if (meta != null) {
                meta.setCustomModelData(3);
                SkinHelper.applySkin(meta, target.name);
                boolean isOwner = clan.owns(target.name);
                String roleTag = isOwner ? "&#FF0000[Лидер] " : "&#FF0000[Участник] ";
                meta.setDisplayName(this.plugin.config().color(roleTag + "&#FF6600" + target.name));
                List<String> lore = new ArrayList<String>();
                lore.add(this.plugin.config().color("&#FF6600⌛ &fНаиграно в клане: &#FFFF00" + formatPlayTime(target.joinedAt)));
                lore.add(this.plugin.config().color("&#FF0000⚔ &fУбийств: &#FF0000" + target.kills));
                lore.add(this.plugin.config().color("&#FFAA00☠ &fСмертей: &#FFFF00" + target.deaths));
                lore.add(this.plugin.config().color("&#00FF00🌐 &fВложил: &#00FF00" + target.invested));
                lore.add(this.plugin.config().color("&#FF6600$ &fСнял: &#FFFF00" + target.withdrawn));
                lore.add("");
                lore.add(this.plugin.config().color("&#FF6600★ &fПрава:"));
                if (isOwner) {
                    lore.add(this.plugin.config().color("  &#00FF00• &fВсе права"));
                } else if (target.rights.isEmpty()) {
                    lore.add(this.plugin.config().color("  &#FF0000• &fНет прав"));
                } else {
                    for (Clan.Right r : target.rights) {
                        String name = getRightDisplayName(r);
                        if (name != null) {
                            lore.add(this.plugin.config().color("  &#FF6600• &f" + name));
                        }
                    }
                }
                meta.setLore(lore);
                head.setItemMeta(meta);
            }
            inventory.setItem(22, head);

            if (!clan.owns(target.name)) {
                ItemStack settings = new ItemStack(Material.PAPER, 1);
                ItemMeta sMeta = settings.getItemMeta();
                if (sMeta != null) {
                    sMeta.setCustomModelData(382);
                    sMeta.setDisplayName(this.plugin.config().color("&#FF0000[&#FF0000☼&#FF0000] &#FF6600Разрешения"));
                    List<String> sLore = new ArrayList<String>();
                    sLore.add(this.plugin.config().color("&#FF6600➥ &#FF6600Нажмите, &fчтобы настроить"));
                    sMeta.setLore(sLore);
                    for (ItemFlag flag : ItemFlag.values()) sMeta.addItemFlags(flag);
                    settings.setItemMeta(sMeta);
                }
                inventory.setItem(30, settings);
            }

            ItemStack armor = new ItemStack(Material.LEATHER_HORSE_ARMOR, 1);
            ItemMeta aMeta = armor.getItemMeta();
            if (aMeta != null) {
                aMeta.setCustomModelData(3);
                if (aMeta instanceof LeatherArmorMeta) {
                    String memberColor = (target.armorColor != null && !target.armorColor.isEmpty()) ? target.armorColor : clan.glowColor;
                    ((LeatherArmorMeta)aMeta).setColor(this.parseRgb(memberColor, Color.fromRGB(0, 255, 0)));
                }
                aMeta.setDisplayName(this.plugin.config().color("&#A87BFC[&#A87BFC★&#A87BFC] &#FF8FFFЦвет брони"));
                List<String> aLore = new ArrayList<String>();
                if (clan.level >= 3 || clan.glow) {
                    aLore.add(this.plugin.config().color("&#FF6600➥ Нажмите, &fчтобы открыть"));
                    aLore.add("");
                    aLore.add(this.plugin.config().color("&fСокланы видят броню"));
                    aLore.add(this.plugin.config().color("&fдруг друга цветной."));
                } else {
                    aLore.add(this.plugin.config().color("&fСокланы видят броню"));
                    aLore.add(this.plugin.config().color("&fдруг друга цветной."));
                    aLore.add("");
                    aLore.add(this.plugin.config().color("&#FF6600❖ &fСтатус: &#FF0000[Не открыто]"));
                    aLore.add(this.plugin.config().color("&fПрокачайте клан, чтобы открыть."));
                }
                aMeta.setLore(aLore);
                for (ItemFlag flag : ItemFlag.values()) aMeta.addItemFlags(flag);
                armor.setItemMeta(aMeta);
            }
            inventory.setItem(31, armor);

            if (!clan.owns(target.name) && !target.name.equalsIgnoreCase(player.getName())) {
                ItemStack kick = new ItemStack(Material.PAPER, 1);
                ItemMeta kMeta = kick.getItemMeta();
                if (kMeta != null) {
                    kMeta.setCustomModelData(381);
                    kMeta.setDisplayName(this.plugin.config().color("&#FF0000[Исключить]"));
                    List<String> kLore = new ArrayList<String>();
                    kLore.add(this.plugin.config().color("&#FF6600➥ &#FF6600Нажмите, &fчтобы исключить из клана"));
                    kMeta.setLore(kLore);
                    for (ItemFlag flag : ItemFlag.values()) kMeta.addItemFlags(flag);
                    kick.setItemMeta(kMeta);
                }
                inventory.setItem(32, kick);
            }

            inventory.setItem(49, this.buildDoorItem());
            player.openInventory(inventory);
        }
    }

    public void kickConfirm(Player player, Clan clan, Clan.Member target) {
        if (clan != null && target != null) {
            if (clan.owns(target.name) || target.name.equalsIgnoreCase(player.getName())) {
                return;
            }
            View view = new View("kickConfirm", clan, 0);
            view.targetMember = target.name;
            Inventory inventory = this.inventory(view, 27, this.plugin.config().color("&#FF0000Кикнуть " + target.name + "?"));

            ItemStack confirm = new ItemStack(Material.LIME_STAINED_GLASS_PANE, 1);
            ItemMeta cMeta = confirm.getItemMeta();
            if (cMeta != null) {
                cMeta.setDisplayName(this.plugin.config().color("&#00FF00Подтвердить"));
                confirm.setItemMeta(cMeta);
            }
            for (int slot : new int[]{0, 1, 2, 9, 10, 11, 18, 19, 20}) {
                inventory.setItem(slot, confirm);
            }

            ItemStack gray = new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1);
            ItemMeta gMeta = gray.getItemMeta();
            if (gMeta != null) {
                gMeta.setDisplayName(this.plugin.config().color(" "));
                gray.setItemMeta(gMeta);
            }
            for (int slot : new int[]{3, 4, 5, 12, 13, 14, 21, 22, 23}) {
                inventory.setItem(slot, gray);
            }

            ItemStack cancel = new ItemStack(Material.RED_STAINED_GLASS_PANE, 1);
            ItemMeta canMeta = cancel.getItemMeta();
            if (canMeta != null) {
                canMeta.setDisplayName(this.plugin.config().color("&#FF0000Отмена"));
                cancel.setItemMeta(canMeta);
            }
            for (int slot : new int[]{6, 7, 8, 15, 16, 17, 24, 25, 26}) {
                inventory.setItem(slot, cancel);
            }

            player.openInventory(inventory);
        }
    }

    public void memberRights(Player player, Clan clan, Clan.Member target, int page) {
        if (clan != null && target != null && !clan.owns(target.name)) {
            if (page < 0) page = 0;
            if (page > 3) page = 3;
            View view = new View("member_rights", clan, page);
            view.targetMember = target.name;

            String title = "<font:minecraft:clan><white>0B1P1" + RIGHTS_PAGE_TEXT_GLYPHS[page] + "2z10S";
            Inventory inventory = this.inventory(view, 54, title);

            Clan.Right[] pageRights = RIGHTS_PAGES[page];
            for (int i = 0; i < pageRights.length; i++) {
                Clan.Right right = pageRights[i];
                boolean active = target.has(right);
                String displayName = getRightDisplayName(right);
                inventory.setItem(SWITCH_SLOTS[i], this.buildSwitchItem(displayName, active));
            }

            if (page > 0) {
                inventory.setItem(46, this.buildArrowItem(false, page + 1, 4));
            }
            inventory.setItem(49, this.buildDoorItem());
            if (page < 3) {
                inventory.setItem(52, this.buildArrowItem(true, page + 1, 4));
            }

            player.openInventory(inventory);
        }
    }

    public void glow(Player player, Clan clan) {
        this.glow(player, clan, null);
    }

    public static final int[][] GLOW_COLORS = new int[][] {
        {255, 0, 0},     // 19: Red
        {255, 120, 0},   // 20: Orange
        {255, 255, 0},   // 21: Yellow
        {128, 255, 0},   // 22: Light Green
        {0, 255, 0},     // 23: Lime/Green
        {0, 255, 255},   // 24: Cyan
        {30, 144, 255},  // 25: Light Blue
        {0, 0, 255},     // 28: Dark Blue
        {100, 0, 255},   // 29: Purple
        {200, 0, 255},   // 30: Magenta
        {255, 255, 255}, // 31: White
        {170, 170, 170}, // 32: Light Gray
        {70, 70, 70},    // 33: Gray
        {1, 1, 1}        // 34: Black
    };
    public static final int[] GLOW_SLOTS = new int[] {19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34};

    public void glow(Player player, Clan clan, Clan.Member target) {
        View view = new View("glow", clan, 0);
        if (target != null) {
            view.targetMember = target.name;
        }
        String titleText = target == null ? "ЦВЕТ БРОНИ КЛАНА" : "ЦВЕТ БРОНИ";
        Inventory inventory = this.inventory(view, 54, this.buildBannerTitle(titleText, null));
        
        inventory.setItem(49, this.buildDoorItem());
        if (target != null) {
            inventory.setItem(39, this.buildResetMemberColorItem(clan, target));
        } else {
            inventory.setItem(39, this.buildGlowToggleItem(clan));
        }

        String activeColor = null;
        if (target != null) {
            activeColor = target.armorColor;
        } else if (clan.glowColor != null && !clan.glowColor.isEmpty()) {
            activeColor = clan.glowColor;
        } else {
            activeColor = "0;255;0";
        }

        for (int i = 0; i < GLOW_SLOTS.length; i++) {
            inventory.setItem(GLOW_SLOTS[i], this.buildGlowColorItem(activeColor, GLOW_COLORS[i][0], GLOW_COLORS[i][1], GLOW_COLORS[i][2]));
        }

        player.openInventory(inventory);
    }

    private ItemStack buildResetMemberColorItem(Clan clan, Clan.Member target) {
        ItemStack item = new ItemStack(Material.PAPER, 1);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setCustomModelData(417);
            meta.setDisplayName(this.plugin.config().color("&#FF0000[&#FF0000✖&#FF0000] &#FF6600Сбросить цвет"));
            List<String> lore = new ArrayList<String>();
            lore.add(this.plugin.config().color("&#FF6600➥ Нажмите, &fчтобы сбросить"));
            lore.add(this.plugin.config().color("&#FF6600❖ &fЦвет станет как у клана"));
            meta.setLore(lore);
            for (ItemFlag flag : ItemFlag.values()) {
                meta.addItemFlags(flag);
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack buildGlowToggleItem(Clan clan) {
        ItemStack item = new ItemStack(Material.PAPER, 1);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setCustomModelData(clan.glow ? 414 : 417);
            meta.setDisplayName(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FF6600Окраска"));
            List<String> lore = new ArrayList<String>();
            lore.add(this.plugin.config().color("&#FF6600➥ &#FF6600Нажмите, &fчтобы переключить"));
            lore.add(this.plugin.config().color("&#FF6600❖ &fСтатус: " + (clan.glow ? "&#00FF00[Включена]" : "&#FF0000[Выключена]")));
            meta.setLore(lore);
            for (ItemFlag flag : ItemFlag.values()) {
                meta.addItemFlags(flag);
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack buildGlowColorItem(String activeColor, int r, int g, int b) {
        ItemStack item = new ItemStack(Material.LEATHER_HORSE_ARMOR, 1);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setCustomModelData(1);
            if (meta instanceof LeatherArmorMeta) {
                ((LeatherArmorMeta)meta).setColor(Color.fromRGB(r, g, b));
            }
            meta.setDisplayName(this.plugin.config().color("&#FF0000[&#FF0000\u2022&#FF0000] &#FF6600Выбрать цвет"));
            List<String> lore = new ArrayList<String>();
            boolean isSelected = false;
            if (activeColor != null && !activeColor.isEmpty()) {
                String[] parts = activeColor.split(";");
                if (parts.length == 3) {
                    try {
                        int curR = Integer.parseInt(parts[0]);
                        int curG = Integer.parseInt(parts[1]);
                        int curB = Integer.parseInt(parts[2]);
                        isSelected = (curR == r && curG == g && curB == b);
                    } catch (NumberFormatException ignored) {}
                }
            }

            if (isSelected) {
                lore.add(this.plugin.config().color("&#FF6600❖ &fСтатус: &#00FF00[Выбран]"));
            } else {
                lore.add(this.plugin.config().color("&#FF6600➥ Нажмите, &fчтобы выбрать"));
            }
            meta.setLore(lore);
            for (ItemFlag flag : ItemFlag.values()) {
                meta.addItemFlags(flag);
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player)event.getWhoClicked();
        Inventory top = event.getView().getTopInventory();
        Inventory bottom = event.getView().getBottomInventory();
        int slot = event.getRawSlot();
        if (top != null && top.getHolder() instanceof View) {
            View view = (View)top.getHolder();
            Clan clan = this.plugin.service().clanByName(view.clan);
            if (clan == null && !view.kind.equals("top")) {
                event.setCancelled(true);
                return;
            }
            if (view.kind.equals("storage")) {
                if (!clan.permits(player.getName(), Clan.Right.USE_STORAGE)) {
                    event.setCancelled(true);
                    player.closeInventory();
                    player.sendMessage(this.plugin.config().color("&#FF0000[✖] Ошибка: &fУ вас нет прав на использование хранилища!"));
                    return;
                }
                if (event.getAction() == InventoryAction.COLLECT_TO_CURSOR || event.getClick() == ClickType.DOUBLE_CLICK) {
                    event.setCancelled(true);
                    return;
                }
                if (event.getClickedInventory() == bottom) {
                    if (event.isShiftClick()) {
                        ItemStack clickedItem = event.getCurrentItem();
                        if (clickedItem != null && clickedItem.getType() != Material.AIR) {
                            int targetSlot = -1;
                            int maxStack = clickedItem.getMaxStackSize();
                            for (int s : STORAGE_SLOTS) {
                                if (this.isStorageSlotUnlocked(clan, s)) {
                                    ItemStack inSlot = top.getItem(s);
                                    if (inSlot != null && inSlot.isSimilar(clickedItem) && inSlot.getAmount() < maxStack) {
                                        targetSlot = s;
                                        break;
                                    }
                                }
                            }
                            if (targetSlot == -1) {
                                for (int s : STORAGE_SLOTS) {
                                    if (this.isStorageSlotUnlocked(clan, s)) {
                                        ItemStack inSlot = top.getItem(s);
                                        if (inSlot == null || inSlot.getType() == Material.AIR) {
                                            targetSlot = s;
                                            break;
                                        }
                                    }
                                }
                            }
                            if (targetSlot != -1) {
                                ItemStack inSlot = top.getItem(targetSlot);
                                if (inSlot == null || inSlot.getType() == Material.AIR) {
                                    top.setItem(targetSlot, clickedItem.clone());
                                    bottom.setItem(event.getSlot(), null);
                                } else {
                                    int space = maxStack - inSlot.getAmount();
                                    int move = Math.min(space, clickedItem.getAmount());
                                    inSlot.setAmount(inSlot.getAmount() + move);
                                    clickedItem.setAmount(clickedItem.getAmount() - move);
                                    if (clickedItem.getAmount() <= 0) {
                                        bottom.setItem(event.getSlot(), null);
                                    } else {
                                        bottom.setItem(event.getSlot(), clickedItem);
                                    }
                                }
                                player.updateInventory();
                                Bukkit.getScheduler().runTask((Plugin)this.plugin, () -> {
                                    this.saveStorage(clan, top);
                                });
                            }
                        }
                        event.setCancelled(true);
                        return;
                    }
                    return;
                } else {
                    if (slot == 49) {
                        event.setCancelled(true);
                        this.playSound(player, "UI_BUTTON_CLICK");
                        this.main(player, clan);
                        return;
                    }
                    int idx = this.storageIndex(slot);
                    if (idx >= 0 && idx < 28) {
                        if (this.isStorageSlotUnlocked(clan, slot)) {
                            Bukkit.getScheduler().runTask((Plugin)this.plugin, () -> {
                                this.saveStorage(clan, top);
                            });
                            return;
                        } else if (idx >= 23) {
                            event.setCancelled(true);
                            this.playSound(player, "UI_BUTTON_CLICK");
                            this.confirmStorageRent(player, clan, slot);
                            return;
                        } else {
                            event.setCancelled(true);
                            this.playSound(player, "BLOCK_CHEST_LOCKED");
                            return;
                        }
                    }
                    event.setCancelled(true);
                }
                return;
            } else {
                event.setCancelled(true);
                if (slot < 0 || slot >= top.getSize()) return;

                switch (view.kind) {
                    case "main": {
                        if (slot == 20) {
                            this.playSound(player, "UI_BUTTON_CLICK");
                            this.members(player, clan, 0);
                            return;
                        }
                        if (slot == 21) {
                            this.playSound(player, "UI_BUTTON_CLICK");
                            this.levels(player, clan);
                            return;
                        }
                        if (slot == 22) {
                            this.playSound(player, "UI_BUTTON_CLICK");
                            this.top(player, true);
                            return;
                        }
                        if (slot == 23) {
                            this.playSound(player, "UI_BUTTON_CLICK");
                            this.bases(player, clan);
                            return;
                        }
                        if (slot == 24) {
                            this.playSound(player, "UI_BUTTON_CLICK");
                            this.errands(player, clan);
                            return;
                        }
                        if (slot == 30) {
                            if (clan.level < 3 && !clan.glow) {
                                this.playSound(player, "BLOCK_CHEST_LOCKED");
                                return;
                            }
                            if (clan.permits(player.getName(), Clan.Right.CLAN_GLOW)) {
                                this.playSound(player, "UI_BUTTON_CLICK");
                                this.glow(player, clan);
                            } else {
                                this.plugin.config().send(player, "lang.noPermission");
                                this.playSound(player, "BLOCK_CHEST_LOCKED");
                            }
                            return;
                        }
                        if (slot == 31) {
                            this.playSound(player, "UI_BUTTON_CLICK");
                            this.trials(player, clan);
                            return;
                        }
                        if (slot == 32) {
                            if (clan.permits(player.getName(), Clan.Right.USE_STORAGE)) {
                                this.playSound(player, "UI_BUTTON_CLICK");
                                this.storage(player, clan);
                            }
                            return;
                        }
                        return;
                    }
                    case "top": {
                        if (slot == 16 && clan != null && clan.owns(player.getName())) {
                            clan.topVisible = !clan.topVisible;
                            this.plugin.service().store().changed();
                            this.plugin.service().store().flush();
                            this.playSound(player, "UI_BUTTON_CLICK");
                            this.top(player, view.fromMenu);
                            return;
                        }
                        if (slot == 40) {
                            this.playSound(player, "UI_BUTTON_CLICK");
                            if (view.fromMenu && clan != null) {
                                this.main(player, clan);
                            } else {
                                player.closeInventory();
                            }
                            return;
                        }
                        return;
                    }
                    case "members": {
                        if (slot == 49) {
                            this.playSound(player, "UI_BUTTON_CLICK");
                            this.main(player, clan);
                            return;
                        }
                        if (slot == 46 && view.page > 0) {
                            this.playSound(player, "UI_BUTTON_CLICK");
                            this.members(player, clan, view.page - 1);
                            return;
                        }
                        int maxPages = (int)Math.ceil((double)clan.members.size() / (double)MEMBER_SHELF_SLOTS.length);
                        if (slot == 52 && view.page + 1 < maxPages) {
                            this.playSound(player, "UI_BUTTON_CLICK");
                            this.members(player, clan, view.page + 1);
                            return;
                        }
                        if (view.slotKeys.containsKey(slot)) {
                            String targetName = view.slotKeys.get(slot);
                            Clan.Member target = clan.member(targetName);
                            if (target != null) {
                                this.playSound(player, "UI_BUTTON_CLICK");
                                this.memberProfile(player, clan, target);
                            } else {
                                this.members(player, clan, view.page);
                            }
                            return;
                        }
                        return;
                    }
                    case "member_profile": {
                        Clan.Member target = clan.member(view.targetMember);
                        if (slot == 49) {
                            this.playSound(player, "UI_BUTTON_CLICK");
                            this.members(player, clan, 0);
                            return;
                        }
                        if (target == null) {
                            this.members(player, clan, 0);
                            return;
                        }
                        if (slot == 30) {
                            if (clan.owns(target.name)) {
                                this.playSound(player, "BLOCK_CHEST_LOCKED");
                                return;
                            }
                            this.playSound(player, "UI_BUTTON_CLICK");
                            this.memberRights(player, clan, target, 0);
                            return;
                        }
                        if (slot == 31) {
                            if (clan.level < 3 && !clan.glow) {
                                player.sendMessage(this.plugin.config().color("&#FF0000[✖] Ошибка: &fОкраска брони доступна с 3-го уровня клана!"));
                                this.playSound(player, "BLOCK_CHEST_LOCKED");
                                return;
                            }
                            boolean canConfigure = clan.permits(player.getName(), Clan.Right.CLAN_GLOW) || target.name.equalsIgnoreCase(player.getName()) || clan.owns(player.getName());
                            if (canConfigure) {
                                this.playSound(player, "UI_BUTTON_CLICK");
                                this.glow(player, clan, target);
                            } else {
                                this.plugin.config().send(player, "lang.noPermission");
                                this.playSound(player, "BLOCK_CHEST_LOCKED");
                            }
                            return;
                        }
                        if (slot == 32) {
                            if (clan.owns(target.name)) {
                                this.playSound(player, "BLOCK_CHEST_LOCKED");
                                return;
                            }
                            if (target.name.equalsIgnoreCase(player.getName())) {
                                player.sendMessage(this.plugin.config().color("&#FF0000[✖] Ошибка: &fВы не можете исключить самого себя!"));
                                this.playSound(player, "BLOCK_CHEST_LOCKED");
                                return;
                            }
                            if (clan.permits(player.getName(), Clan.Right.KICK_PLAYERS)) {
                                this.playSound(player, "UI_BUTTON_CLICK");
                                this.kickConfirm(player, clan, target);
                            } else {
                                this.plugin.config().send(player, "lang.noPermission");
                                this.playSound(player, "BLOCK_CHEST_LOCKED");
                            }
                            return;
                        }
                        return;
                    }
                    case "kickConfirm": {
                        Clan.Member target = clan.member(view.targetMember);
                        if (target == null) {
                            this.members(player, clan, 0);
                            return;
                        }
                        if (clan.owns(target.name) || target.name.equalsIgnoreCase(player.getName())) {
                            player.closeInventory();
                            return;
                        }
                        if (slot == 0 || slot == 1 || slot == 2 || slot == 9 || slot == 10 || slot == 11 || slot == 18 || slot == 19 || slot == 20) {
                            if (!clan.permits(player.getName(), Clan.Right.KICK_PLAYERS)) {
                                this.plugin.config().send(player, "lang.noPermission");
                                player.closeInventory();
                                return;
                            }
                            Player targetPl = Bukkit.getPlayerExact(target.name);
                            clan.removeMember(target.name);
                            this.plugin.service().store().leave(clan, target.name);
                            if (targetPl != null && targetPl.isOnline()) {
                                this.plugin.glow().resetPlayer(targetPl, clan);
                            }
                            this.plugin.service().store().changed();
                            this.plugin.service().store().flush();
                            this.plugin.glow().refresh();
                            clan.logHistory(player.getName() + " исключил из клана " + target.name);

                            String kickMsg = this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Кланы: &#FFFF00" + target.name + " &fисключен из клана");
                            this.plugin.service().announce(clan, kickMsg);
                            if (targetPl != null) {
                                targetPl.sendMessage(kickMsg);
                                targetPl.closeInventory();
                            }
                            this.playSound(player, "ENTITY_EXPERIENCE_ORB_PICKUP");
                            player.closeInventory();
                            return;
                        }
                        if (slot == 6 || slot == 7 || slot == 8 || slot == 15 || slot == 16 || slot == 17 || slot == 24 || slot == 25 || slot == 26) {
                            this.playSound(player, "UI_BUTTON_CLICK");
                            this.memberProfile(player, clan, target);
                            return;
                        }
                        return;
                    }
                    case "member_rights": {
                        Clan.Member target = clan.member(view.targetMember);
                        if (slot == 49) {
                            this.playSound(player, "UI_BUTTON_CLICK");
                            if (target != null) {
                                this.memberProfile(player, clan, target);
                            } else {
                                this.members(player, clan, 0);
                            }
                            return;
                        }
                        if (target == null || clan.owns(target.name)) {
                            this.members(player, clan, 0);
                            return;
                        }
                        if (slot == 46 && view.page > 0) {
                            this.playSound(player, "UI_BUTTON_CLICK");
                            this.memberRights(player, clan, target, view.page - 1);
                            return;
                        }
                        if (slot == 52 && view.page < 3) {
                            this.playSound(player, "UI_BUTTON_CLICK");
                            this.memberRights(player, clan, target, view.page + 1);
                            return;
                        }
                        Clan.Right[] pageRights = RIGHTS_PAGES[view.page];
                        for (int i = 0; i < pageRights.length; i++) {
                            if (slot == SWITCH_SLOTS[i]) {
                                if (!clan.permits(player.getName(), Clan.Right.EDIT_PERMISSIONS)) {
                                    this.plugin.config().send(player, "lang.noPermission");
                                    this.playSound(player, "BLOCK_CHEST_LOCKED");
                                    return;
                                }
                                Clan.Right r = pageRights[i];
                                if (target.rights.contains(r)) {
                                    target.rights.remove(r);
                                } else {
                                    target.rights.add(r);
                                }
                                this.plugin.service().store().changed();
                                this.playSound(player, "UI_BUTTON_CLICK");
                                this.memberRights(player, clan, target, view.page);
                                return;
                            }
                        }
                        return;
                    }
                    case "bases": {
                        if (slot == 49) {
                            this.playSound(player, "UI_BUTTON_CLICK");
                            this.main(player, clan);
                            return;
                        }
                        if (view.slotKeys.containsKey(slot)) {
                            String homeName = view.slotKeys.get(slot);
                            Location loc = clan.homes.get(homeName);
                            if (loc != null && loc.getWorld() != null) {
                                player.teleport(loc);
                                this.playSound(player, "ENTITY_ENDERMAN_TELEPORT");
                                player.closeInventory();
                            } else {
                                player.sendMessage(this.plugin.config().color("&#FF0000[✖] Ошибка: &fМир этой точки базы недоступен."));
                                this.playSound(player, "BLOCK_CHEST_LOCKED");
                            }
                            return;
                        }
                        int[] shelfSlots = new int[]{20, 21, 22, 23, 24, 29, 30, 31, 32, 33};
                        for (int i = 0; i < shelfSlots.length; i++) {
                            if (slot == shelfSlots[i]) {
                                if (i < Math.max(1, clan.homeLimit)) {
                                    player.sendMessage(this.plugin.config().color("&#FF0000[⚔] &#FFFF00Помощь: &f/clan sethome <название>"));
                                    this.playSound(player, "UI_BUTTON_CLICK");
                                } else {
                                    this.playSound(player, "BLOCK_CHEST_LOCKED");
                                }
                                return;
                            }
                        }
                        return;
                    }
                    case "levels": {
                        if (slot == 49) {
                            this.playSound(player, "UI_BUTTON_CLICK");
                            this.main(player, clan);
                            return;
                        }
                        if (slot == 46 && view.page > 0) {
                            this.playSound(player, "UI_BUTTON_CLICK");
                            this.levels(player, clan, view.page - 1);
                            return;
                        }
                        if (slot == 52 && view.page < 2) {
                            this.playSound(player, "UI_BUTTON_CLICK");
                            this.levels(player, clan, view.page + 1);
                            return;
                        }
                        if (view.slotLevel.containsKey(slot)) {
                            int targetLevel = view.slotLevel.get(slot);
                            if (targetLevel <= clan.level) {
                                this.playSound(player, "BLOCK_CHEST_LOCKED");
                                return;
                            }
                            if (targetLevel > clan.level + 1) {
                                player.sendMessage(this.plugin.config().color("&#FF0000[✖] Ошибка: &fСначала необходимо приобрести предыдущие уровни!"));
                                this.playSound(player, "BLOCK_CHEST_LOCKED");
                                return;
                            }
                            if (!clan.owns(player.getName()) && !clan.permits(player.getName(), Clan.Right.WITHDRAW_MONEY) && !clan.permits(player.getName(), Clan.Right.TAKE_REWARDS)) {
                                player.sendMessage(this.plugin.config().color("&#FF0000[✖] Ошибка: &fУ вас нет прав на прокачку клана!"));
                                this.playSound(player, "BLOCK_CHEST_LOCKED");
                                return;
                            }
                            this.playSound(player, "UI_BUTTON_CLICK");
                            this.buyConfirm(player, clan, targetLevel);
                            return;
                        }
                        return;
                    }
                    case "buyConfirm": {
                        int targetLevel = view.targetLevel;
                        if (slot == 0 || slot == 1 || slot == 2 || slot == 9 || slot == 10 || slot == 11 || slot == 18 || slot == 19 || slot == 20) {
                            if (!clan.owns(player.getName()) && !clan.permits(player.getName(), Clan.Right.WITHDRAW_MONEY) && !clan.permits(player.getName(), Clan.Right.TAKE_REWARDS)) {
                                player.sendMessage(this.plugin.config().color("&#FF0000[✖] Ошибка: &fУ вас нет прав на прокачку клана!"));
                                player.closeInventory();
                                return;
                            }
                            if (targetLevel != clan.level + 1) {
                                player.sendMessage(this.plugin.config().color("&#FF0000[✖] Ошибка: &fУровень клана уже изменился!"));
                                player.closeInventory();
                                return;
                            }
                            LevelDef def = getLevelDef(targetLevel);
                            if (def == null) {
                                player.closeInventory();
                                return;
                            }
                            boolean hasXP = clan.rating >= def.xp;
                            if (!hasXP) {
                                if (this.plugin.playerPoints() == null || !this.plugin.playerPoints().has(player.getUniqueId(), def.tkCost)) {
                                    this.playSound(player, "BLOCK_CHEST_LOCKED");
                                    String noPointsMsg = this.plugin.config().text("buy.noPoints");
                                    if (noPointsMsg == null || noPointsMsg.trim().isEmpty()) {
                                        noPointsMsg = "&#FF0000[✖] Ошибка! &fУ Вас не хватает &#00FF00Токенов&f!\n &fНе беда, их можно купить на &#FFFF00FillaGame.MyDonate.io\n&#FF0000[✖] Ошибка: &fНе удалось списать токены.";
                                    }
                                    for (String line : noPointsMsg.split("\n")) {
                                        player.sendMessage(this.plugin.config().color(line));
                                    }
                                    player.closeInventory();
                                    return;
                                }
                                if (!this.plugin.playerPoints().withdraw(player.getUniqueId(), def.tkCost)) {
                                    this.playSound(player, "BLOCK_CHEST_LOCKED");
                                    String failMsg = this.plugin.config().text("buy.failed");
                                    if (failMsg == null || failMsg.trim().isEmpty()) {
                                        failMsg = "&#FF0000[✖] Ошибка: &fНе удалось списать токены.";
                                    }
                                    player.sendMessage(this.plugin.config().color(failMsg));
                                    player.closeInventory();
                                    return;
                                }
                            }
                            int oldLevel = clan.level;
                            clan.level = targetLevel;
                            if (clan.rating < def.xp) {
                                clan.rating = def.xp;
                            }
                            clan.moneyLimit = def.treasury;
                            applyLevelPerks(clan, targetLevel);
                            this.plugin.service().store().changed();
                            this.plugin.service().store().flush();
                            clan.logHistory(player.getName() + " купил " + targetLevel + "-й уровень клана");
                            Bukkit.getPluginManager().callEvent(new t.me.bruhstudio.event.ClanLevelUpEvent(player, clan, oldLevel, targetLevel));

                            player.sendMessage(this.plugin.config().color("&#00FF00[✔] Успешная покупка!"));
                            String msg1 = this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Кланы: &#FFFF00" + player.getName() + " &fкупил &#FFFF00" + targetLevel + "-й &fуровень клана.");
                            String msg2 = this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Кланы: &#00FF00Уровень &fвашего &#FFFF00Клана &fповышен!");
                            this.plugin.service().announce(clan, msg1);
                            this.plugin.service().announce(clan, msg2);
                            for (String perk : def.perks) {
                                this.plugin.service().announce(clan, this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Кланы: &f" + perk.trim()));
                            }

                            this.playSound(player, "ENTITY_PLAYER_LEVELUP");
                            this.levels(player, clan, (targetLevel - 1) / 13);
                            return;
                        }
                        if (slot == 6 || slot == 7 || slot == 8 || slot == 15 || slot == 16 || slot == 17 || slot == 24 || slot == 25 || slot == 26) {
                            this.playSound(player, "UI_BUTTON_CLICK");
                            this.levels(player, clan, (targetLevel - 1) / 13);
                            return;
                        }
                        return;
                    }
                    case "storageRentConfirm": {
                        if (slot == 0 || slot == 1 || slot == 2 || slot == 9 || slot == 10 || slot == 11 || slot == 18 || slot == 19 || slot == 20) {
                            int targetSlot = view.targetSlot;
                            if (targetSlot < 39 || targetSlot > 43) {
                                player.closeInventory();
                                return;
                            }
                            if (!clan.permits(player.getName(), Clan.Right.USE_STORAGE)) {
                                player.sendMessage(this.plugin.config().color("&#FF0000[✖] Ошибка: &fУ вас нет прав на аренду ячейки!"));
                                player.closeInventory();
                                return;
                            }
                            int cost = 490;
                            if (this.plugin.playerPoints() == null || !this.plugin.playerPoints().has(player.getUniqueId(), cost)) {
                                this.playSound(player, "BLOCK_CHEST_LOCKED");
                                String noPointsMsg = this.plugin.config().text("buy.noPoints");
                                if (noPointsMsg == null || noPointsMsg.trim().isEmpty()) {
                                    noPointsMsg = "&#FF0000[✖] Ошибка! &fУ Вас не хватает &#00FF00Токенов&f!\n &fНе беда, их можно купить на &#FFFF00FillaGame.MyDonate.io\n&#FF0000[✖] Ошибка: &fНе удалось списать токены.";
                                }
                                for (String line : noPointsMsg.split("\n")) {
                                    player.sendMessage(this.plugin.config().color(line));
                                }
                                player.closeInventory();
                                return;
                            }
                            if (!this.plugin.playerPoints().withdraw(player.getUniqueId(), cost)) {
                                this.playSound(player, "BLOCK_CHEST_LOCKED");
                                String failMsg = this.plugin.config().text("buy.failed");
                                if (failMsg == null || failMsg.trim().isEmpty()) {
                                    failMsg = "&#FF0000[✖] Ошибка: &fНе удалось списать токены.";
                                }
                                player.sendMessage(this.plugin.config().color(failMsg));
                                player.closeInventory();
                                return;
                            }
                            long durationMillis = 1000L * 3600L * 1000L;
                            long currentExp = clan.rentedStorage.getOrDefault(targetSlot, 0L);
                            long baseTime = Math.max(currentExp, System.currentTimeMillis());
                            clan.rentedStorage.put(targetSlot, baseTime + durationMillis);
                            this.plugin.service().store().changed();
                            this.plugin.service().store().flush();
                            clan.logHistory(player.getName() + " арендовал ячейку хранилища на 1000 ч.");

                            this.playSound(player, "ENTITY_PLAYER_LEVELUP");
                            player.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Кланы: &fЯчейка хранилища арендована на &#FFFF001000 &fч."));

                            Inventory openInv = this.sharedStorage.get(clan.name.toLowerCase(Locale.ROOT));
                            if (openInv != null) {
                                this.refreshStorageSlot(clan, openInv, targetSlot);
                            }
                            this.storage(player, clan);
                            return;
                        }
                        if (slot == 6 || slot == 7 || slot == 8 || slot == 15 || slot == 16 || slot == 17 || slot == 24 || slot == 25 || slot == 26) {
                            this.playSound(player, "UI_BUTTON_CLICK");
                            this.storage(player, clan);
                            return;
                        }
                        return;
                    }
                    case "errands": {
                        if (slot == 49) {
                            this.playSound(player, "UI_BUTTON_CLICK");
                            this.main(player, clan);
                            return;
                        }
                        return;
                    }
                    case "trials": {
                        if (slot == 49) {
                            this.playSound(player, "UI_BUTTON_CLICK");
                            this.main(player, clan);
                            return;
                        }
                        return;
                    }
                    case "seller": {
                        if (slot == 4) {
                            this.main(player, clan);
                            return;
                        }
                        return;
                    }
                    case "glow": {
                        Clan.Member target = view.targetMember != null ? clan.member(view.targetMember) : null;
                        if (view.targetMember != null && target == null) {
                            player.sendMessage(this.plugin.config().color("&#FF0000[✖] Ошибка: &fУчастника больше нет в клане!"));
                            this.members(player, clan, 0);
                            return;
                        }
                        if (slot == 49) {
                            this.playSound(player, "UI_BUTTON_CLICK");
                            if (target != null) {
                                this.memberProfile(player, clan, target);
                            } else {
                                this.main(player, clan);
                            }
                            return;
                        }
                        if (slot == 39) {
                            if (clan.level < 3 && !clan.glow) {
                                player.sendMessage(this.plugin.config().color("&#FF0000[✖] Ошибка: &fОкраска брони доступна с 3-го уровня клана!"));
                                this.playSound(player, "BLOCK_CHEST_LOCKED");
                                return;
                            }
                            boolean canChange = clan.permits(player.getName(), Clan.Right.CLAN_GLOW) || clan.owns(player.getName());
                            if (!canChange && target != null && target.name.equalsIgnoreCase(player.getName())) {
                                canChange = true;
                            }
                            if (!canChange) {
                                player.sendMessage(this.plugin.config().color("&#FF0000[✖] Ошибка: &fУ вас нет прав на изменение цвета брони!"));
                                this.playSound(player, "BLOCK_CHEST_LOCKED");
                                return;
                            }

                            if (target != null) {
                                Long until = this.glowColorCooldown.get(player.getUniqueId());
                                if (until != null && until > System.currentTimeMillis()) {
                                    player.sendMessage(this.plugin.config().color("&#FF0000[✖] Ошибка: &fПодождите перед сбросом цвета!"));
                                    return;
                                }
                                this.glowColorCooldown.put(player.getUniqueId(), System.currentTimeMillis() + 1000L);
                                if (target.armorColor != null) {
                                    target.armorColor = null;
                                    clan.logHistory(player.getName() + " сбросил цвет брони для " + target.name);
                                    this.plugin.service().store().changed();
                                    this.plugin.service().store().flush();
                                    this.plugin.glow().refresh();
                                }
                                this.playSound(player, "UI_BUTTON_CLICK");
                                this.glow(player, clan, target);
                                return;
                            }

                            Long until = this.glowToggleCooldown.get(player.getUniqueId());
                            if (until != null && until > System.currentTimeMillis()) {
                                player.sendMessage(this.plugin.config().color("&#FF0000[✖] Ошибка: &fПодождите перед переключением!"));
                                return;
                            }
                            this.glowToggleCooldown.put(player.getUniqueId(), System.currentTimeMillis() + 1000L);
                            clan.glow = !clan.glow;
                            this.plugin.service().store().changed();
                            this.plugin.service().store().flush();
                            this.plugin.glow().refresh();
                            clan.logHistory(player.getName() + (clan.glow ? " включил окраску брони" : " выключил окраску брони"));
                            this.playSound(player, "UI_BUTTON_CLICK");
                            this.glow(player, clan, target);
                            return;
                        }

                        for (int i = 0; i < GLOW_SLOTS.length; i++) {
                            if (slot == GLOW_SLOTS[i]) {
                                if (clan.level < 3 && !clan.glow) {
                                    player.sendMessage(this.plugin.config().color("&#FF0000[✖] Ошибка: &fОкраска брони доступна с 3-го уровня клана!"));
                                    this.playSound(player, "BLOCK_CHEST_LOCKED");
                                    return;
                                }
                                boolean canChange = clan.permits(player.getName(), Clan.Right.CLAN_GLOW) || clan.owns(player.getName());
                                if (!canChange && target != null && target.name.equalsIgnoreCase(player.getName())) {
                                    canChange = (clan.level >= 3 || clan.glow);
                                }
                                if (!canChange) {
                                    player.sendMessage(this.plugin.config().color("&#FF0000[✖] Ошибка: &fУ вас нет прав на изменение цвета брони!"));
                                    this.playSound(player, "BLOCK_CHEST_LOCKED");
                                    return;
                                }
                                Long until = this.glowColorCooldown.get(player.getUniqueId());
                                if (until != null && until > System.currentTimeMillis()) {
                                    player.sendMessage(this.plugin.config().color("&#FF0000[✖] Ошибка: &fПодождите перед сменой цвета!"));
                                    return;
                                }
                                this.glowColorCooldown.put(player.getUniqueId(), System.currentTimeMillis() + 1000L);
                                String colorStr = GLOW_COLORS[i][0] + ";" + GLOW_COLORS[i][1] + ";" + GLOW_COLORS[i][2];
                                String currentColor = (target != null ? target.armorColor : clan.glowColor);
                                if (colorStr.equalsIgnoreCase(currentColor)) {
                                    this.playSound(player, "UI_BUTTON_CLICK");
                                    return;
                                }
                                if (target != null) {
                                    target.armorColor = colorStr;
                                    clan.logHistory(player.getName() + " изменил цвет брони для " + target.name);
                                } else {
                                    clan.glowColor = colorStr;
                                    clan.logHistory(player.getName() + " изменил цвет брони");
                                }
                                this.plugin.service().store().changed();
                                this.plugin.service().store().flush();
                                this.plugin.glow().refresh();
                                this.playSound(player, "UI_BUTTON_CLICK");
                                this.glow(player, clan, target);
                                return;
                            }
                        }
                        return;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        Inventory top = event.getView().getTopInventory();
        if (top != null && top.getHolder() instanceof View) {
            View view = (View)top.getHolder();
            Clan clan = this.plugin.service().clanByName(view.clan);
            if (clan != null && view.kind.equals("storage")) {
                if (!clan.permits(event.getWhoClicked().getName(), Clan.Right.USE_STORAGE)) {
                    event.setCancelled(true);
                    event.getWhoClicked().closeInventory();
                    return;
                }
                for (int slot : event.getRawSlots()) {
                    if (slot < top.getSize() && !this.isStorageSlotUnlocked(clan, slot)) {
                        event.setCancelled(true);
                        return;
                    }
                }
                Bukkit.getScheduler().runTask((Plugin)this.plugin, () -> {
                    this.saveStorage(clan, top);
                });
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        if (inventory.getHolder() instanceof View) {
            View view = (View)inventory.getHolder();
            Clan clan = this.plugin.service().clanByName(view.clan);
            if (clan != null && view.kind.equals("storage")) {
                this.saveStorage(clan, inventory);
                Bukkit.getScheduler().runTask((Plugin)this.plugin, () -> {
                    if (inventory.getViewers().isEmpty()) {
                        this.sharedStorage.remove(view.clan.toLowerCase(Locale.ROOT), inventory);
                        this.plugin.service().store().flush();
                    }
                });
            }
        }
    }

    public static final class View implements InventoryHolder {
        public final String kind;
        public final String clan;
        public final int page;
        public boolean fromMenu;
        public int targetLevel;
        public int targetSlot;
        public String targetMember;
        public final List<String> keys = new ArrayList<String>();
        public final Map<Integer, String> slotKeys = new HashMap<Integer, String>();
        public final Map<Integer, Integer> slotLevel = new HashMap<Integer, Integer>();
        public Inventory inventory;

        public View(String kind, Clan clan, int page) {
            this(kind, clan, page, false);
        }

        public View(String kind, Clan clan, int page, boolean fromMenu) {
            this.kind = kind;
            this.clan = clan != null ? clan.name : "";
            this.page = page;
            this.fromMenu = fromMenu;
        }

        public Inventory getInventory() {
            return this.inventory;
        }
    }
}
