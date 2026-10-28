package t.me.bruhstudio;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public final class ClanCommands implements CommandExecutor, TabCompleter {
    public static final Pattern NAME = Pattern.compile("[a-zA-Zа-яА-ЯёЁ0-9]+");
    public static final List<String> COMMANDS = Collections.unmodifiableList(Arrays.asList(
        "create", "top", "cancel", "accept", "menu", "info", "errands", "history", "leave",
        "chat", "coords", "money", "invest", "withdraw", "pvp", "storage", "invite", "kick",
        "prefix", "rename", "glow", "home", "sethome", "delhome", "disband"
    ));
    public final BruhFunClansPlugin plugin;
    public final Map<UUID, Long> cooldown = new HashMap<UUID, Long>();
    public final Map<UUID, Map<String, Invitation>> invitations = new HashMap<UUID, Map<String, Invitation>>();

    public ClanCommands(BruhFunClansPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player)sender;
        Clan clan = this.plugin.service().clan(player.getName());

        if (label.equalsIgnoreCase("c") && args.length > 0 && COMMANDS.contains(args[0].toLowerCase(Locale.ROOT))) {
            // If it's a known clan command like /c menu, /c storage, /c delhome, /c top, etc., let it fall through to regular processing
        } else if (label.equalsIgnoreCase("cc") || label.equalsIgnoreCase("c")) {
            if (clan == null) {
                this.helpNonClan(player);
                return true;
            }
            if (args.length == 0) {
                player.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Кланы: &f/c (или /cc) <сообщение>"));
                return true;
            }
            long now = System.currentTimeMillis();
            Long until = this.cooldown.get(player.getUniqueId());
            if (until != null && until > now) {
                player.sendMessage(this.plugin.config().color("&#FF0000[✖] Не так быстро! &fПодожди секунду."));
                return true;
            }
            this.cooldown.put(player.getUniqueId(), now + 1000L);
            if (this.cooldown.size() > 2048) {
                this.cooldown.entrySet().removeIf(e -> e.getValue() < now);
            }
            String message = String.join(" ", args);
            String formatted = this.plugin.config().color("&#FF0000[&#FF0000КЛАН&#FF0000] &#FF6600" + player.getName() + " &#FF0000» &#FFFF00" + message);
            this.plugin.service().announce(clan, formatted);
            return true;
        }

        if (args.length == 0) {
            if (clan == null) {
                this.helpNonClan(player);
            } else {
                this.helpInClan(player);
            }
            return true;
        }

        String name = args[0].toLowerCase(Locale.ROOT);
        if ((name.equals("admin") || name.equals("debug")) && this.plugin.adminCommands() != null && this.plugin.adminCommands().hasPermission(player)) {
            String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
            return this.plugin.adminCommands().onCommand(sender, command, "clan " + name, subArgs);
        }
        if (name.equals("help") || !COMMANDS.contains(name)) {
            if (clan == null) {
                this.helpNonClan(player);
            } else {
                this.helpInClan(player);
            }
            return true;
        }

        long now = System.currentTimeMillis();
        Long until = this.cooldown.get(player.getUniqueId());
        if (until != null && until > now) {
            player.sendMessage(this.plugin.config().color("&#FF0000[✖] Не так быстро! &fПодожди секунду."));
            return true;
        }
        this.cooldown.put(player.getUniqueId(), now + 1000L);
        if (this.cooldown.size() > 2048) {
            this.cooldown.entrySet().removeIf(e -> e.getValue() < now);
        }

        if (clan == null) {
            if (!name.equals("create") && !name.equals("accept") && !name.equals("cancel") && !name.equals("top")) {
                this.helpNonClan(player);
                return true;
            }
        }

        if (!this.can(player, name, clan)) {
            if (clan == null) {
                this.helpNonClan(player);
            } else {
                this.send(player, "lang.noPermission", new String[0]);
            }
            return true;
        }

        String[] values = Arrays.copyOfRange(args, 1, args.length);
        switch (name) {
            case "create": {
                this.create(player, values);
                break;
            }
            case "cancel": {
                this.cancel(player, values);
                break;
            }
            case "accept": {
                this.accept(player, values);
                break;
            }
            case "menu": {
                if (clan == null) {
                    this.helpNonClan(player);
                    break;
                }
                this.plugin.menus().main(player, clan);
                break;
            }
            case "top": {
                this.plugin.menus().top(player, false);
                break;
            }
            case "leave": {
                this.leave(player, clan, values);
                break;
            }
            case "money": {
                if (!clan.permits(player.getName(), Clan.Right.SHOW_MONEY)) {
                    player.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FF0000Ошибка: &fУ вас нет прав на просмотр баланса!"));
                    break;
                }
                player.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &fБаланс клана: &#FFFF00" + clan.money));
                break;
            }
            case "invest": {
                this.transfer(player, clan, values, true);
                break;
            }
            case "withdraw": {
                this.transfer(player, clan, values, false);
                break;
            }
            case "pvp": {
                if (!clan.permits(player.getName(), Clan.Right.PVP_TOGGLE)) {
                    player.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FF0000Ошибка: &fУ вас нет прав на переключение PvP!"));
                    break;
                }
                clan.pvp = !clan.pvp;
                this.plugin.service().store().changed();
                this.plugin.service().store().flush();
                clan.logHistory(player.getName() + (clan.pvp ? " включил PvP" : " выключил PvP"));
                this.plugin.service().announce(clan, this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Кланы: PVP-Режим &fв Клане теперь " + (clan.pvp ? "&#00FF00включён&f!" : "&#FF0000отключён&f!")));
                break;
            }
            case "storage": {
                if (!clan.permits(player.getName(), Clan.Right.USE_STORAGE)) {
                    player.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FF0000Ошибка: &fУ вас нет прав на использование хранилища!"));
                    break;
                }
                this.plugin.menus().storage(player, clan);
                break;
            }
            case "home": {
                this.home(player, clan, values, 0);
                break;
            }
            case "sethome": {
                this.home(player, clan, values, 1);
                break;
            }
            case "delhome": {
                this.home(player, clan, values, 2);
                break;
            }
            case "invite": {
                this.invite(player, clan, values);
                break;
            }
            case "prefix": {
                this.prefix(player, clan, values);
                break;
            }
            case "kick": {
                this.kick(player, clan, values);
                break;
            }
            case "rename": {
                this.rename(player, clan, values);
                break;
            }
            case "disband": {
                this.disband(player, clan, values);
                break;
            }
            case "glow": {
                if (clan.level < 3 && !clan.glow) {
                    player.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FF0000Ошибка: &fОкраска брони доступна с 3-го уровня клана!"));
                    break;
                }
                if (!clan.permits(player.getName(), Clan.Right.CLAN_GLOW)) {
                    player.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FF0000Ошибка: &fУ вас нет прав на изменение цвета брони!"));
                    break;
                }
                clan.glow = !clan.glow;
                this.plugin.service().store().changed();
                this.plugin.service().store().flush();
                this.plugin.glow().refresh();
                clan.logHistory(player.getName() + (clan.glow ? " включил окраску брони" : " выключил окраску брони"));
                this.plugin.service().announce(clan, this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Кланы: Индикация союзников &fклана теперь " + (clan.glow ? "&#00FF00включена" : "&#FF0000отключена") + "&f!"));
                break;
            }
            case "coords": {
                this.coords(player, clan, values);
                break;
            }
            case "chat": {
                if (values.length == 0) {
                    player.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Кланы: &f/c (или /cc) <сообщение>"));
                    break;
                }
                String msg = String.join(" ", values);
                String formatted = this.plugin.config().color("&#FF0000[&#FF0000КЛАН&#FF0000] &#FF6600" + player.getName() + " &#FF0000» &#FFFF00" + msg);
                this.plugin.service().announce(clan, formatted);
                break;
            }
            case "errands": {
                this.plugin.menus().errands(player, clan);
                break;
            }
            case "history": {
                this.history(player, clan, values);
                break;
            }
            case "info": {
                this.info(player, clan, values);
                break;
            }
        }
        return true;
    }

    public boolean can(Player player, String command, Clan clan) {
        if (clan == null) {
            return command.equals("create") || command.equals("accept") || command.equals("cancel") || command.equals("top") || command.equals("help");
        }
        if (command.equals("create")) {
            return false;
        }
        if (command.equals("accept") || command.equals("cancel") || command.equals("help") || command.equals("top")
            || command.equals("menu") || command.equals("info") || command.equals("errands")
            || command.equals("leave") || command.equals("coords") || command.equals("chat")
            || command.equals("history") || command.equals("home") || command.equals("storage")
            || command.equals("sethome") || command.equals("delhome") || command.equals("pvp") || command.equals("glow")
            || command.equals("invest") || command.equals("withdraw") || command.equals("invite") || command.equals("kick")
            || command.equals("rename") || command.equals("prefix") || command.equals("disband") || command.equals("money")) {
            return true;
        }

        Clan.Right right;
        switch (command) {
            case "money": {
                right = Clan.Right.SHOW_MONEY;
                break;
            }
            case "invest": {
                right = Clan.Right.INVEST_MONEY;
                break;
            }
            case "withdraw": {
                right = Clan.Right.WITHDRAW_MONEY;
                break;
            }
            case "pvp": {
                right = Clan.Right.PVP_TOGGLE;
                break;
            }
            case "storage": {
                right = Clan.Right.USE_STORAGE;
                break;
            }
            case "sethome": {
                right = Clan.Right.CLAN_SETHOME;
                break;
            }
            case "delhome": {
                right = Clan.Right.CLAN_DELHOME;
                break;
            }
            case "invite": {
                right = Clan.Right.INVITE_PLAYERS;
                break;
            }
            case "kick": {
                right = Clan.Right.KICK_PLAYERS;
                break;
            }
            case "rename": {
                right = Clan.Right.CLAN_RENAME;
                break;
            }
            case "glow": {
                right = Clan.Right.CLAN_GLOW;
                break;
            }
            case "prefix": {
                right = Clan.Right.CHANGE_PREFIX;
                break;
            }
            case "disband": {
                right = Clan.Right.ALL;
                break;
            }
            default: {
                return true;
            }
        }
        return clan.permits(player.getName(), right);
    }

    public void help(Player player) {
        Clan clan = this.plugin.service().clan(player.getName());
        if (clan == null) {
            this.helpNonClan(player);
        } else {
            this.helpInClan(player);
        }
    }

    public void helpNonClan(Player player) {
        long price = this.plugin.config().get("features/clan.yml").getLong("clan.create.price", 10000L);
        player.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &fПомощь по &#FFFF00Кланам&f:"));
        player.sendMessage(this.plugin.config().color("&#FF0000/clan create <название> &f- создать клан (&#FFFF00$" + price + "&f)"));
        player.sendMessage(this.plugin.config().color("&#FF0000/clan top &f- топ кланов по казне"));
    }

    public void helpInClan(Player player) {
        Clan clan = this.plugin.service().clan(player.getName());
        player.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &fПомощь по &#FFFF00Кланам&f:"));
        player.sendMessage(this.plugin.config().color("&#FF0000/clan menu &f- информация о клане"));
        player.sendMessage(this.plugin.config().color("&#FF0000/clan info &f- сведения о клане в чат"));
        player.sendMessage(this.plugin.config().color("&#FF0000/clan top &f- топ кланов по казне"));
        player.sendMessage(this.plugin.config().color("&#FF0000/clan errands &f- поручения клана"));
        player.sendMessage(this.plugin.config().color("&#FF0000/clan history [страница] &f- история клана"));
        player.sendMessage(this.plugin.config().color("&#FF0000/clan leave &f- покинуть клан"));
        player.sendMessage(this.plugin.config().color("&#FF0000/clan chat <текст> &f- клановый чат (/cc)"));
        player.sendMessage(this.plugin.config().color("&#FF0000/clan coords [ник] &f- координаты сокланов"));
        if (clan == null || clan.permits(player.getName(), Clan.Right.SHOW_MONEY)) {
            player.sendMessage(this.plugin.config().color("&#FF0000/clan money &f- посмотреть клановый баланс"));
        }
        if (clan == null || clan.permits(player.getName(), Clan.Right.INVEST_MONEY)) {
            player.sendMessage(this.plugin.config().color("&#FF0000/clan invest <сумма> &f- вложить деньги"));
        }
        if (clan == null || clan.permits(player.getName(), Clan.Right.WITHDRAW_MONEY)) {
            player.sendMessage(this.plugin.config().color("&#FF0000/clan withdraw <сумма> &f- снять деньги"));
        }
        if (clan == null || clan.permits(player.getName(), Clan.Right.PVP_TOGGLE)) {
            player.sendMessage(this.plugin.config().color("&#FF0000/clan pvp &f- переключение PVP-режима"));
        }
        if (clan == null || clan.permits(player.getName(), Clan.Right.USE_STORAGE)) {
            player.sendMessage(this.plugin.config().color("&#FF0000/clan storage &f- хранилище клана"));
        }
        if (clan == null || clan.permits(player.getName(), Clan.Right.INVITE_PLAYERS)) {
            player.sendMessage(this.plugin.config().color("&#FF0000/clan invite <игрок> &f- пригласить игрока в клан"));
        }
        if (clan == null || clan.permits(player.getName(), Clan.Right.KICK_PLAYERS)) {
            player.sendMessage(this.plugin.config().color("&#FF0000/clan kick <игрок> &f- кикнуть игрока из клана"));
        }
        if (clan == null || clan.permits(player.getName(), Clan.Right.CHANGE_PREFIX)) {
            player.sendMessage(this.plugin.config().color("&#FF0000/clan prefix <игрок> <префикс> &f- изменить префикс"));
        }
        if (clan == null || clan.permits(player.getName(), Clan.Right.CLAN_RENAME)) {
            player.sendMessage(this.plugin.config().color("&#FF0000/clan rename <название> &f- переименовать клан"));
        }
        if (clan == null || clan.permits(player.getName(), Clan.Right.CLAN_GLOW)) {
            player.sendMessage(this.plugin.config().color("&#FF0000/clan glow &f- окраска брони сокланов"));
        }
        if (clan == null || clan.permits(player.getName(), Clan.Right.CLAN_SETHOME)) {
            player.sendMessage(this.plugin.config().color("&#FF0000/clan sethome <название> &f- установить точку базы"));
        }
        if (clan == null || clan.permits(player.getName(), Clan.Right.CLAN_DELHOME)) {
            player.sendMessage(this.plugin.config().color("&#FF0000/clan delhome <название> &f- удалить точку базы"));
        }
        if (clan == null || clan.owns(player.getName())) {
            player.sendMessage(this.plugin.config().color("&#FF0000/clan disband &f- расформировать клан"));
        }
    }

    public void coords(Player player, Clan clan, String[] values) {
        if (values.length == 0) {
            String coordsText = this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00" + player.getName() + " &7(&#FFFF00" + player.getWorld().getName() + "&7, &#FFFF00" + player.getLocation().getBlockX() + "&7, &#FFFF00" + player.getLocation().getBlockY() + "&7, &#FFFF00" + player.getLocation().getBlockZ() + "&7)");
            this.plugin.service().announce(clan, coordsText);
        } else {
            String targetName = values[0];
            Clan.Member targetMember = clan.member(targetName);
            if (targetMember == null) {
                player.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FF0000Ошибка: &fУчастника &#FFFF00" + targetName + " &fнет в вашем клане!"));
                return;
            }
            Player targetPl = Bukkit.getPlayerExact(targetName);
            if (targetPl == null && targetMember.name != null) {
                targetPl = Bukkit.getPlayerExact(targetMember.name);
            }
            if (targetPl == null && targetMember.name != null) {
                targetPl = Bukkit.getPlayer(targetMember.name);
            }
            if (targetPl == null) {
                player.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FF0000Ошибка: &fИгрок &#FFFF00" + targetName + " &fне в сети!"));
                return;
            }
            player.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00" + targetPl.getName() + " &7(&#FFFF00" + targetPl.getWorld().getName() + "&7, &#FFFF00" + targetPl.getLocation().getBlockX() + "&7, &#FFFF00" + targetPl.getLocation().getBlockY() + "&7, &#FFFF00" + targetPl.getLocation().getBlockZ() + "&7)"));
        }
    }

    public void history(Player player, Clan clan, String[] values) {
        int page = 1;
        if (values.length > 0) {
            try {
                page = Integer.parseInt(values[0]);
            } catch (NumberFormatException ignored) {}
        }
        if (page < 1) page = 1;

        player.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00История клана&f:"));
        List<String> entries = new ArrayList<>(clan.history);
        if (entries.isEmpty()) {
            player.sendMessage(this.plugin.config().color("&7История пуста"));
            return;
        }

        int pageSize = 5;
        int maxPages = (int)Math.ceil((double)entries.size() / (double)pageSize);
        if (maxPages < 1) maxPages = 1;
        if (page > maxPages) page = maxPages;

        int start = (page - 1) * pageSize;
        for (int i = start; i < Math.min(start + pageSize, entries.size()); i++) {
            String entry = entries.get(i);
            int colon = entry.indexOf(": ");
            if (colon > 0) {
                String date = entry.substring(0, colon);
                String rest = entry.substring(colon + 2);
                int firstSpace = rest.indexOf(' ');
                if (firstSpace > 0) {
                    String firstWord = rest.substring(0, firstSpace);
                    String action = rest.substring(firstSpace + 1);
                    int lastSpace = action.lastIndexOf(' ');
                    if (lastSpace > 0 && (action.startsWith("создал точку базы ") || action.startsWith("установил точку базы ") || action.startsWith("удалил точку базы ") || action.startsWith("перенес точку базы "))) {
                        String actPrefix = action.substring(0, lastSpace);
                        String target = action.substring(lastSpace + 1);
                        player.sendMessage(this.plugin.config().color("&#FF6600● &#FFFF00" + date + "&f: &#FFFF00" + firstWord + " &f" + actPrefix + " &#FFFF00" + target));
                    } else {
                        player.sendMessage(this.plugin.config().color("&#FF6600● &#FFFF00" + date + "&f: &#FFFF00" + firstWord + " &f" + action));
                    }
                } else {
                    player.sendMessage(this.plugin.config().color("&#FF6600● &#FFFF00" + date + "&f: &f" + rest));
                }
            } else {
                player.sendMessage(this.plugin.config().color("&#FF6600● &f" + entry));
            }
        }
        BaseComponent[] prevComp = TextComponent.fromLegacyText(this.plugin.config().color("&#FF6600<<< "));
        if (page > 1) {
            ClickEvent click = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clan history " + (page - 1));
            for (BaseComponent c : prevComp) {
                c.setClickEvent(click);
            }
        }
        BaseComponent[] textComp = TextComponent.fromLegacyText(this.plugin.config().color("&fСтраница &#FFFF00" + page + " &fиз &#FFFF00" + maxPages + " "));
        BaseComponent[] nextComp = TextComponent.fromLegacyText(this.plugin.config().color("&#FF6600>>>"));
        if (page < maxPages) {
            ClickEvent click = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clan history " + (page + 1));
            for (BaseComponent c : nextComp) {
                c.setClickEvent(click);
            }
        }
        BaseComponent[] pagination = new BaseComponent[prevComp.length + textComp.length + nextComp.length];
        System.arraycopy(prevComp, 0, pagination, 0, prevComp.length);
        System.arraycopy(textComp, 0, pagination, prevComp.length, textComp.length);
        System.arraycopy(nextComp, 0, pagination, prevComp.length + textComp.length, nextComp.length);
        player.spigot().sendMessage(pagination);
    }

    public void info(Player player, Clan clan, String[] values) {
        if (values != null && values.length > 0) {
            Clan targetClan = this.plugin.service().clanByName(values[0]);
            if (targetClan == null) {
                player.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FF0000Ошибка: &fКлан &#FFFF00" + values[0] + " &fне найден!"));
                return;
            }
            clan = targetClan;
        }
        if (clan == null) {
            this.helpNonClan(player);
            return;
        }
        this.info(player, clan);
    }

    public void info(Player player, Clan clan) {
        String clanTitle = (clan.displayName != null && !clan.displayName.isEmpty()) ? clan.displayName : clan.name;
        player.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &fКлан &#FFFF00" + clanTitle + " &7№" + clan.getId()));
        player.sendMessage(this.plugin.config().color("&#FF0000|| &fУровень: &#FFFF00" + clan.level + "&f, опыт: &#FFFF00" + String.format(Locale.US, "%,d", clan.rating)));
        player.sendMessage(this.plugin.config().color("&#FF0000|| &fЛидер: &#FFFF00" + clan.owner));
        player.sendMessage(this.plugin.config().color("&#FF0000|| &fКазна: &#FFFF00$" + String.format(Locale.US, "%,d", clan.money)));
        player.sendMessage(this.plugin.config().color("&#FF0000|| &fPvP-режим: " + (clan.pvp ? "&#00FF00[Включен]" : "&#FF0000[Выключен]")));

        StringBuilder mb = new StringBuilder("&#FF0000|| &fУчастники: ");
        int mi = 0;
        for (Clan.Member m : clan.members.values()) {
            if (mi++ > 0) mb.append("&f, ");
            boolean isO = clan.owns(m.name);
            boolean online = Bukkit.getPlayerExact(m.name) != null;
            String role = (m.prefix != null && !m.prefix.isEmpty()) ? m.prefix : (isO ? "Лидер" : "Участник");
            mb.append("&7[").append(role).append("&7] ");
            mb.append(online ? "&f" : "&7").append(m.name);
        }
        player.sendMessage(this.plugin.config().color(mb.toString()));
    }

    public void disband(Player player, Clan clan, String[] values) {
        if (!clan.owns(player.getName())) {
            player.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FF0000Ошибка: &fЭто доступно только лидеру!"));
            return;
        }
        if (values.length > 0) {
            if (values[0].equalsIgnoreCase("confirm")) {
                for (Clan.Member m : clan.members.values()) {
                    Player p = Bukkit.getPlayerExact(m.name);
                    if (p != null && p.isOnline()) {
                        p.closeInventory();
                    }
                }
                for (Map<String, Invitation> invites : this.invitations.values()) {
                    invites.remove(clan.name.toLowerCase(Locale.ROOT));
                }
                this.plugin.glow().resetClan(clan);
                this.plugin.service().store().remove(clan);
                this.plugin.service().store().changed();
                this.plugin.service().store().flush();
                player.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Кланы: &fВы успешно удалили свой клан!"));
                return;
            }
            if (values[0].equalsIgnoreCase("cancel")) {
                player.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Кланы: &fВы отменили удаление клана!"));
                return;
            }
        }
        player.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Кланы: &fВнимание! Вы уверены, что хотите удалить клан?"));
        BaseComponent[] yesComp = TextComponent.fromLegacyText(this.plugin.config().color("&#00FF00[ДА, удалить клан]"));
        ClickEvent yesClick = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clan disband confirm");
        for (BaseComponent c : yesComp) {
            c.setClickEvent(yesClick);
        }
        BaseComponent[] noComp = TextComponent.fromLegacyText(this.plugin.config().color(" &#FF0000[НЕТ, не удалять]"));
        ClickEvent noClick = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clan disband cancel");
        for (BaseComponent c : noComp) {
            c.setClickEvent(noClick);
        }
        BaseComponent[] combined = new BaseComponent[yesComp.length + noComp.length];
        System.arraycopy(yesComp, 0, combined, 0, yesComp.length);
        System.arraycopy(noComp, 0, combined, yesComp.length, noComp.length);
        player.spigot().sendMessage(combined);
    }

    public void leave(Player player, Clan clan, String[] values) {
        if (clan == null) {
            this.helpNonClan(player);
            return;
        }
        if (clan.owns(player.getName())) {
            player.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Кланы: &fВы не можете покинуть клан, так как Вы его Лидер"));
            player.sendMessage(this.plugin.config().color("&#FF0000|| &fРешение: удалить клан &#FFFF00/clan disband"));
            return;
        }
        if (values.length > 0) {
            if (values[0].equalsIgnoreCase("confirm")) {
                player.closeInventory();
                clan.removeMember(player.getName());
                this.plugin.service().store().leave(clan, player.getName());
                this.plugin.glow().resetPlayer(player, clan);
                this.plugin.service().store().changed();
                this.plugin.service().store().flush();
                this.plugin.glow().refresh();
                clan.logHistory(player.getName() + " покинул клан");
                player.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Кланы: &fВы вышли из клана!"));
                this.plugin.service().announce(clan, this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Кланы: &fИгрок &#FFFF00" + player.getName() + " &fпокинул клан!"));
                return;
            }
            if (values[0].equalsIgnoreCase("cancel")) {
                player.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Кланы: &fВы отменили выход из клана!"));
                return;
            }
        }
        player.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Кланы: &fВнимание! Вы уверены, что хотите покинуть клан?"));
        BaseComponent[] yesComp = TextComponent.fromLegacyText(this.plugin.config().color("&#00FF00[ДА, Выйти из клана]"));
        ClickEvent yesClick = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clan leave confirm");
        for (BaseComponent c : yesComp) {
            c.setClickEvent(yesClick);
        }
        BaseComponent[] noComp = TextComponent.fromLegacyText(this.plugin.config().color(" &#FF0000[НЕТ, Не выходить]"));
        ClickEvent noClick = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clan leave cancel");
        for (BaseComponent c : noComp) {
            c.setClickEvent(noClick);
        }
        BaseComponent[] combined = new BaseComponent[yesComp.length + noComp.length];
        System.arraycopy(yesComp, 0, combined, 0, yesComp.length);
        System.arraycopy(noComp, 0, combined, yesComp.length, noComp.length);
        player.spigot().sendMessage(combined);
    }

    public void home(Player player, Clan clan, String[] values, int action) {
        if (action == 1 && !clan.permits(player.getName(), Clan.Right.CLAN_SETHOME)) {
            player.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FF0000Ошибка: &fУ вас нет прав на установку точки базы!"));
            return;
        }
        if (action == 2 && !clan.permits(player.getName(), Clan.Right.CLAN_DELHOME)) {
            player.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FF0000Ошибка: &fУ вас нет прав на удаление точки базы!"));
            return;
        }
        if (action == 0) { // home
            if (values.length == 0) {
                if (clan.homes.isEmpty()) {
                    player.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FF0000Ошибка: &fВ клане нет такой точки базы!"));
                    return;
                }
                Location loc = clan.homes.values().iterator().next();
                player.teleport(loc);
                return;
            }
            String name = values[0];
            Location loc = clan.homes.get(name);
            if (loc == null) {
                for (Map.Entry<String, Location> entry : clan.homes.entrySet()) {
                    if (entry.getKey().equalsIgnoreCase(name)) {
                        loc = entry.getValue();
                        break;
                    }
                }
            }
            if (loc == null) {
                player.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FF0000Ошибка: &fВ клане нет такой точки базы!"));
                return;
            }
            player.teleport(loc);
            return;
        }
        if (action == 1) { // sethome
            if (values.length == 0) {
                player.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Помощь: &f/clan sethome <название>"));
                return;
            }
            String name = values[0];
            if (!name.matches("^[a-zA-Zа-яА-ЯёЁ0-9_-]+$")) {
                player.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FF0000Ошибка: &fНедопустимое название точки базы!"));
                return;
            }
            String matchedKey = null;
            if (clan.homes.containsKey(name)) {
                matchedKey = name;
            } else {
                for (String k : clan.homes.keySet()) {
                    if (k.equalsIgnoreCase(name)) {
                        matchedKey = k;
                        break;
                    }
                }
            }
            if (matchedKey != null) {
                Location prev = clan.homes.get(matchedKey);
                clan.homes.put(matchedKey, player.getLocation());
                this.plugin.service().store().changed();
                this.plugin.service().store().flush();
                clan.logHistory(player.getName() + " перенес точку базы " + matchedKey);
                this.plugin.service().announce(clan, this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Клан: &fТочка &#FFFF00" + matchedKey + " &fперенесена. Прошлые координаты:\n &#FFFF00" + prev.getBlockX() + " " + prev.getBlockY() + " " + prev.getBlockZ()));
            } else {
                if (clan.homes.size() >= clan.homeLimit) {
                    player.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Клан: &fБольше точек баз поставить нельзя. Прокачивайте\n &fклан, чтобы открыть новые!"));
                    return;
                }
                clan.homes.put(name, player.getLocation());
                this.plugin.service().store().changed();
                this.plugin.service().store().flush();
                clan.logHistory(player.getName() + " создал точку базы " + name);
                this.plugin.service().announce(clan, this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Клан: &fУстановлена новая точка базы клана — &#FFFF00" + name + "."));
            }
            return;
        }
        if (action == 2) { // delhome
            if (values.length == 0) {
                player.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Кланы: &f/clan delhome <название>"));
                return;
            }
            String name = values[0];
            if (!name.matches("^[a-zA-Zа-яА-ЯёЁ0-9_-]+$")) {
                player.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FF0000Ошибка: &fВ клане нет такой точки базы!"));
                return;
            }
            String matchedKey = null;
            if (clan.homes.containsKey(name)) {
                matchedKey = name;
            } else {
                for (String k : clan.homes.keySet()) {
                    if (k.equalsIgnoreCase(name)) {
                        matchedKey = k;
                        break;
                    }
                }
            }
            if (matchedKey == null) {
                player.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FF0000Ошибка: &fВ клане нет такой точки базы!"));
                return;
            }
            clan.homes.remove(matchedKey);
            this.plugin.service().store().changed();
            this.plugin.service().store().flush();
            clan.logHistory(player.getName() + " удалил точку базы " + matchedKey);
            this.plugin.service().announce(clan, this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &fТочка базы клана &#FFFF00" + matchedKey + " &fудалена"));
        }
    }

    public void transfer(Player player, Clan clan, String[] values, boolean deposit) {
        if (!deposit && !clan.permits(player.getName(), Clan.Right.WITHDRAW_MONEY)) {
            player.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FF0000Ошибка: &fУ вас нет прав на снятие денег из казны!"));
            return;
        }
        if (deposit && !clan.permits(player.getName(), Clan.Right.INVEST_MONEY)) {
            player.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FF0000Ошибка: &fУ вас нет прав на пополнение казны!"));
            return;
        }
        if (values.length == 0) {
            player.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Помощь: &f/clan " + (deposit ? "invest" : "withdraw") + " <сумма>"));
            return;
        }
        long amount;
        try {
            amount = Long.parseLong(values[0]);
        } catch (NumberFormatException e) {
            return;
        }
        if (amount <= 0) return;

        if (deposit) {
            if (this.plugin.economy.getBalance(player) < (double)amount) {
                player.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FF0000Ошибка: &fУ вас недостаточно средств!"));
                return;
            }
            if (clan.money + amount > clan.moneyLimit) {
                player.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FF0000Ошибка: &fПревышен лимит казны клана!"));
                return;
            }
            this.plugin.economy.withdrawPlayer(player, amount);
            clan.money += amount;
            Clan.Member m = clan.member(player.getName());
            if (m != null) {
                m.invested += amount;
            }
            this.plugin.service().store().changed();
            this.plugin.service().store().flush();
            clan.logHistory(player.getName() + " пополнил баланс казны на $" + amount);
            this.plugin.service().announce(clan, this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Кланы: &#FFFF00" + player.getName() + " &fпополнил баланс казны\n&#FF0000|| &fБаланс казны: &#FFFF00" + clan.money));
        } else {
            if (clan.money < amount) {
                player.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FF0000Ошибка: &fБаланс казны меньше веденной суммы!"));
                return;
            }
            clan.money -= amount;
            Clan.Member m = clan.member(player.getName());
            if (m != null) {
                m.withdrawn += amount;
            }
            this.plugin.economy.depositPlayer(player, amount);
            this.plugin.service().store().changed();
            this.plugin.service().store().flush();
            clan.logHistory(player.getName() + " снял $" + amount + " из казны");
            this.plugin.service().announce(clan, this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &fИгрок &#FFFF00" + player.getName() + " &fснял &#FFFF00$" + amount + " &fиз казны клана!"));
        }
    }

    public void invite(Player player, Clan clan, String[] values) {
        if (!clan.permits(player.getName(), Clan.Right.INVITE_PLAYERS)) {
            player.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FF0000Ошибка: &fУ вас нет прав на приглашение игроков!"));
            return;
        }
        if (values.length == 0) {
            player.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Помощь: &f/clan invite <игрок>"));
            return;
        }
        String targetName = values[0];
        Player targetPl = Bukkit.getPlayerExact(targetName);
        if (targetPl == null) {
            player.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FF0000Ошибка: &fИгрок &#FFFF00" + targetName + " &fне в сети!"));
            return;
        }
        if (this.plugin.service().clan(targetPl.getName()) != null) {
            player.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FF0000Ошибка: &fИгрок состоит в другом клане!"));
            return;
        }
        if (clan.members.size() >= clan.memberLimit) {
            player.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FF0000Ошибка: &fДостигнут лимит участников клана!"));
            return;
        }

        Map<String, Invitation> playerInvites = this.invitations.computeIfAbsent(targetPl.getUniqueId(), k -> new HashMap<>());
        playerInvites.put(clan.name.toLowerCase(Locale.ROOT), new Invitation(clan, player.getName(), System.currentTimeMillis() + 60000L));
        if (this.invitations.size() > 512) {
            long current = System.currentTimeMillis();
            this.invitations.values().forEach(m -> m.entrySet().removeIf(e -> e.getValue().expiresAt < current));
            this.invitations.entrySet().removeIf(e -> e.getValue().isEmpty());
        }

        player.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &fВы отправили приглашение в клан игроку &#FFFF00" + targetPl.getName() + "&f!"));

        targetPl.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Кланы: &fИгрок &#FFFF00" + player.getName() + " &fприглашает вас в клан &#FFFF00" + (clan.displayName != null && !clan.displayName.isEmpty() ? clan.displayName : clan.name)));
        BaseComponent[] acceptComp = TextComponent.fromLegacyText(this.plugin.config().color("&#00FF00[Принять]"));
        ClickEvent acceptClick = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clan accept " + clan.name);
        for (BaseComponent c : acceptComp) {
            c.setClickEvent(acceptClick);
        }
        BaseComponent[] declineComp = TextComponent.fromLegacyText(this.plugin.config().color(" &#FF0000[Отклонить]"));
        ClickEvent declineClick = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/clan cancel " + clan.name);
        for (BaseComponent c : declineComp) {
            c.setClickEvent(declineClick);
        }
        BaseComponent[] combined = new BaseComponent[acceptComp.length + declineComp.length];
        System.arraycopy(acceptComp, 0, combined, 0, acceptComp.length);
        System.arraycopy(declineComp, 0, combined, acceptComp.length, declineComp.length);
        targetPl.spigot().sendMessage(combined);
    }

    public void kick(Player player, Clan clan, String[] values) {
        if (!clan.permits(player.getName(), Clan.Right.KICK_PLAYERS)) {
            player.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FF0000Ошибка: &fУ вас нет прав на исключение игроков!"));
            return;
        }
        if (values.length == 0) {
            player.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Помощь: &f/clan kick <игрок>"));
            return;
        }
        String targetName = values[0];
        Clan.Member target = clan.member(targetName);
        if (target == null) {
            player.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FF0000Ошибка: &fИгрок не состоит в клане!"));
            return;
        }
        if (clan.owns(target.name)) {
            player.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FF0000Ошибка: &fНельзя исключить лидера клана!"));
            return;
        }
        this.plugin.menus().kickConfirm(player, clan, target);
    }

    public void prefix(Player player, Clan clan, String[] values) {
        if (!clan.permits(player.getName(), Clan.Right.CHANGE_PREFIX)) {
            player.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FF0000Ошибка: &fУ вас нет прав на изменение префиксов!"));
            return;
        }
        if (values.length < 2) {
            player.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Помощь: &f/clan prefix <игрок> <префикс>"));
            return;
        }
        String targetName = values[0];
        Clan.Member target = clan.member(targetName);
        if (target == null) {
            player.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FF0000Ошибка: &fИгрок не состоит в клане!"));
            return;
        }
        String prefix = String.join(" ", Arrays.copyOfRange(values, 1, values.length));
        target.prefix = prefix;
        this.plugin.service().store().changed();
        this.plugin.service().store().flush();
        clan.logHistory(player.getName() + " установил префикс " + prefix + " игроку " + target.name);
        this.plugin.service().announce(clan, this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &fИгроку &#FFFF00" + target.name + " &fустановлен префикс &#FFFF00" + prefix));
    }

    public void rename(Player player, Clan clan, String[] values) {
        if (!clan.permits(player.getName(), Clan.Right.CLAN_RENAME)) {
            player.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FF0000Ошибка: &fУ вас нет прав на переименование клана!"));
            return;
        }
        if (values.length == 0) {
            player.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Помощь: &f/clan rename <название>"));
            return;
        }
        String newName = values[0];
        clan.displayName = newName;
        this.plugin.service().store().changed();
        this.plugin.service().store().flush();
        clan.logHistory(player.getName() + " переименовал клан в " + newName);
        this.plugin.service().announce(clan, this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Кланы: &#FFFF00" + player.getName() + " &fпереименовал клан в " + newName));
        this.plugin.service().announce(clan, this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Кланы: &fНазвание клана изменено."));
    }

    public void create(Player player, String[] values) {
        if (this.plugin.service().clan(player.getName()) != null) {
            player.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FF0000Ошибка: &fВы уже состоите в клане!"));
            return;
        }
        if (values.length == 0) {
            player.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Помощь: &f/clan create <название>"));
            return;
        }
        String name = values[0];
        if (name.length() < 3 || name.length() > 16 || !NAME.matcher(name).matches()) {
            player.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FF0000Ошибка: &fНазвание клана должно быть от 3 до 16 символов!"));
            return;
        }
        if (this.plugin.service().clanByName(name) != null) {
            player.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FF0000Ошибка: &fКлан с таким названием уже существует!"));
            return;
        }
        long price = this.plugin.config().get("features/clan.yml").getLong("clan.create.price", 10000L);
        if (this.plugin.economy.getBalance(player) < (double)price) {
            player.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FF0000Ошибка: &fДля создания клана необходимо $" + price + "!"));
            return;
        }
        this.plugin.economy.withdrawPlayer(player, price);

        Clan clan = new Clan();
        clan.name = name;
        clan.displayName = name;
        clan.owner = player.getName();
        clan.moneyLimit = 100000L;
        clan.memberLimit = 5;
        clan.homeLimit = 2;
        clan.level = 0;
        clan.rating = 0;

        Clan.Member ownerMember = new Clan.Member(player.getName(), "");
        ownerMember.rights.add(Clan.Right.ALL);
        clan.add(ownerMember);
        clan.logHistory("Клан создан!");

        this.plugin.service().store().add(clan);
        this.plugin.service().store().flush();

        player.sendMessage(this.plugin.config().color("&#00FF00[✔] &fВы успешно создали клан &#FFFF00" + name + "&f!"));
    }

    public void accept(Player player, String[] values) {
        if (this.plugin.service().clan(player.getName()) != null) {
            player.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FF0000Ошибка: &fВы уже состоите в клане!"));
            return;
        }
        Map<String, Invitation> playerInvites = this.invitations.get(player.getUniqueId());
        if (playerInvites == null || playerInvites.isEmpty()) {
            player.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FF0000Ошибка: &fУ вас нет активных приглашений в клан!"));
            return;
        }
        String clanKey = values.length > 0 ? values[0].toLowerCase(Locale.ROOT) : playerInvites.keySet().iterator().next();
        Invitation inv = playerInvites.remove(clanKey);
        if (inv == null || inv.expiresAt < System.currentTimeMillis()) {
            player.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FF0000Ошибка: &fПриглашение недействительно или истекло!"));
            return;
        }
        Clan clan = inv.clan;
        if (this.plugin.service().clanByName(clan.name) == null) {
            player.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FF0000Ошибка: &fЭтот клан больше не существует!"));
            return;
        }
        if (clan.members.size() >= clan.memberLimit) {
            player.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FF0000Ошибка: &fВ клане превышен лимит участников!"));
            return;
        }

        Clan.Member member = new Clan.Member(player.getName(), "");
        this.plugin.service().store().join(clan, member);
        this.plugin.service().store().flush();
        this.plugin.glow().refresh();

        clan.logHistory(player.getName() + " вступил в клан (принял " + inv.inviter + ")");
        player.sendMessage(this.plugin.config().color("&#00FF00[✔] &fВы успешно вступили в клан &#FFFF00" + (clan.displayName != null ? clan.displayName : clan.name) + "&f!"));
        this.plugin.service().announce(clan, this.plugin.config().color("&#00FF00[✔] &fИгрок &#FFFF00" + player.getName() + " &fуспешно вступил в клан!"));
    }

    public void cancel(Player player, String[] values) {
        Map<String, Invitation> playerInvites = this.invitations.get(player.getUniqueId());
        if (playerInvites == null || playerInvites.isEmpty()) {
            player.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FF0000Ошибка: &fУ вас нет активных приглашений в клан!"));
            return;
        }
        String clanKey = values.length > 0 ? values[0].toLowerCase(Locale.ROOT) : playerInvites.keySet().iterator().next();
        Invitation inv = playerInvites.remove(clanKey);
        if (inv != null) {
            player.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Кланы: &fВы отклонили приглашение в клан!"));
            Player inviter = Bukkit.getPlayerExact(inv.inviter);
            if (inviter != null) {
                inviter.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Кланы: &#FFFF00" + player.getName() + " &fотклонил Ваше приглашение в клан."));
            }
        }
    }

    public void send(Player player, String key, String ... replacements) {
        this.plugin.config().send(player, key, replacements);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (alias.equalsIgnoreCase("cc")) {
            return Collections.emptyList();
        }
        boolean isAdmin = this.plugin.adminCommands() != null && this.plugin.adminCommands().hasPermission(sender);
        if (args.length == 1) {
            ArrayList<String> matches = new ArrayList<>();
            String prefix = args[0].toLowerCase(Locale.ROOT);
            if (sender instanceof Player) {
                Player player = (Player)sender;
                Clan clan = this.plugin.service().clan(player.getName());
                if (clan == null) {
                    if ("create".startsWith(prefix)) matches.add("create");
                    if ("top".startsWith(prefix)) matches.add("top");
                    Map<String, Invitation> playerInvites = this.invitations.get(player.getUniqueId());
                    if (playerInvites != null && !playerInvites.isEmpty()) {
                        if ("accept".startsWith(prefix)) matches.add("accept");
                        if ("cancel".startsWith(prefix)) matches.add("cancel");
                    }
                } else {
                    for (String c : COMMANDS) {
                        if (c.equals("create") || c.equals("accept") || c.equals("cancel")) {
                            continue;
                        }
                        if (c.startsWith(prefix)) matches.add(c);
                    }
                }
            } else {
                for (String c : COMMANDS) {
                    if (c.startsWith(prefix)) matches.add(c);
                }
            }
            if (isAdmin) {
                if ("admin".startsWith(prefix)) matches.add("admin");
                if ("debug".startsWith(prefix)) matches.add("debug");
            }
            return matches;
        }
        if (isAdmin && args.length >= 2 && (args[0].equalsIgnoreCase("admin") || args[0].equalsIgnoreCase("debug"))) {
            return this.plugin.adminCommands().onTabComplete(sender, command, alias, Arrays.copyOfRange(args, 1, args.length));
        }
        if (sender instanceof Player && args.length == 2) {
            Player player = (Player)sender;
            Clan clan = this.plugin.service().clan(player.getName());
            if (clan != null) {
                String sub = args[0].toLowerCase(Locale.ROOT);
                String prefix = args[1].toLowerCase(Locale.ROOT);
                ArrayList<String> matches = new ArrayList<>();
                if (sub.equals("delhome") || sub.equals("home")) {
                    for (String h : clan.homes.keySet()) {
                        if (h.toLowerCase(Locale.ROOT).startsWith(prefix)) matches.add(h);
                    }
                    return matches;
                }
                if (sub.equals("coords") || sub.equals("kick") || sub.equals("prefix")) {
                    for (String m : clan.members.keySet()) {
                        if (m.toLowerCase(Locale.ROOT).startsWith(prefix)) matches.add(m);
                    }
                    return matches;
                }
                if (sub.equals("invite")) {
                    for (Player online : Bukkit.getOnlinePlayers()) {
                        if (!clan.members.containsKey(online.getName()) && online.getName().toLowerCase(Locale.ROOT).startsWith(prefix)) {
                            matches.add(online.getName());
                        }
                    }
                    return matches;
                }
            }
        }
        return Collections.emptyList();
    }

    public static final class Invitation {
        public final Clan clan;
        public final String inviter;
        public final long expiresAt;

        public Invitation(Clan clan, String inviter, long expiresAt) {
            this.clan = clan;
            this.inviter = inviter;
            this.expiresAt = expiresAt;
        }
    }
}
