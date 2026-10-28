package t.me.bruhstudio;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public final class BruhClansAdminCommands implements CommandExecutor, TabCompleter {
    public static final String ADMIN_PERMISSION = "bruhclans.admin";
    private static final List<String> SUBCOMMANDS = Collections.unmodifiableList(Arrays.asList(
        "addexp", "setexp", "setlevel", "addmoney", "setmoney",
        "givepoints", "info", "disband", "setleader", "reset", "reload", "help"
    ));

    private final BruhFunClansPlugin plugin;

    public BruhClansAdminCommands(BruhFunClansPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean hasPermission(CommandSender sender) {
        return sender.isOp() || sender.hasPermission(ADMIN_PERMISSION) || !(sender instanceof Player);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!this.hasPermission(sender)) {
            sender.sendMessage("Unknown command. Type \"/help\" for help.");
            return true;
        }

        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            this.sendHelp(sender, label);
            return true;
        }

        String sub = args[0].toLowerCase(Locale.ROOT);
        switch (sub) {
            case "addexp": {
                this.handleAddExp(sender, args);
                break;
            }
            case "setexp": {
                this.handleSetExp(sender, args);
                break;
            }
            case "setlevel": {
                this.handleSetLevel(sender, args);
                break;
            }
            case "addmoney": {
                this.handleAddMoney(sender, args);
                break;
            }
            case "setmoney": {
                this.handleSetMoney(sender, args);
                break;
            }
            case "givepoints": {
                this.handleGivePoints(sender, args);
                break;
            }
            case "info": {
                this.handleInfo(sender, args);
                break;
            }
            case "disband": {
                this.handleDisband(sender, args);
                break;
            }
            case "setleader": {
                this.handleSetLeader(sender, args);
                break;
            }
            case "reset": {
                this.handleReset(sender, args);
                break;
            }
            case "reload": {
                this.handleReload(sender);
                break;
            }
            default: {
                sender.sendMessage(this.plugin.config().color("&#FF0000[✖] Ошибка: &fНеизвестная подкоманда! Используйте &#FFFF00/" + label + " help"));
                break;
            }
        }
        return true;
    }

    private void sendHelp(CommandSender sender, String label) {
        sender.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Панель администратора BruhClans:"));
        sender.sendMessage(this.plugin.config().color(" &#FF6600/" + label + " addexp <клан|игрок> <кол-во> &#FF0000— &fдобавить опыт клану"));
        sender.sendMessage(this.plugin.config().color(" &#FF6600/" + label + " setexp <клан|игрок> <кол-во> &#FF0000— &fустановить опыт клану"));
        sender.sendMessage(this.plugin.config().color(" &#FF6600/" + label + " setlevel <клан|игрок> <0-39> &#FF0000— &fустановить уровень клана"));
        sender.sendMessage(this.plugin.config().color(" &#FF6600/" + label + " addmoney <клан|игрок> <кол-во> &#FF0000— &fдобавить деньги в казну"));
        sender.sendMessage(this.plugin.config().color(" &#FF6600/" + label + " setmoney <клан|игрок> <кол-во> &#FF0000— &fустановить баланс казны"));
        sender.sendMessage(this.plugin.config().color(" &#FF6600/" + label + " givepoints <игрок> <кол-во> &#FF0000— &fвыдать ТК (PlayerPoints)"));
        sender.sendMessage(this.plugin.config().color(" &#FF6600/" + label + " info <клан|игрок> &#FF0000— &fдебаг-информация о клане"));
        sender.sendMessage(this.plugin.config().color(" &#FF6600/" + label + " setleader <клан> <игрок> &#FF0000— &fсменить лидера клана"));
        sender.sendMessage(this.plugin.config().color(" &#FF6600/" + label + " disband <клан> &#FF0000— &fпринудительно распустить клан"));
        sender.sendMessage(this.plugin.config().color(" &#FF6600/" + label + " reset <клан> &#FF0000— &fсбросить уровень и опыт в 0"));
        sender.sendMessage(this.plugin.config().color(" &#FF6600/" + label + " reload &#FF0000— &fперезагрузить конфигурации"));
    }

    private Clan findClan(String input) {
        if (input == null || input.isEmpty()) return null;
        Clan clan = this.plugin.service().clanByName(input);
        if (clan != null) return clan;
        clan = this.plugin.service().clan(input);
        if (clan != null) return clan;
        for (Clan c : this.plugin.service().store().all()) {
            if (c.name.equalsIgnoreCase(input) || (c.displayName != null && c.displayName.equalsIgnoreCase(input))) {
                return c;
            }
        }
        return null;
    }

    private void handleAddExp(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(this.plugin.config().color("&#FF0000[✖] Помощь: &f/bruhclans addexp <клан|игрок> <количество>"));
            return;
        }
        Clan clan = this.findClan(args[1]);
        if (clan == null) {
            sender.sendMessage(this.plugin.config().color("&#FF0000[✖] Ошибка: &fКлан &#FFFF00" + args[1] + " &fне найден!"));
            return;
        }
        long amount;
        try {
            amount = Long.parseLong(args[2]);
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            sender.sendMessage(this.plugin.config().color("&#FF0000[✖] Ошибка: &fКоличество должно быть положительным числом!"));
            return;
        }

        this.plugin.service().addExperience(clan, amount);
        this.plugin.service().store().changed();
        this.plugin.service().store().flush();
        clan.logHistory("Администратор добавил " + amount + " опыта клану");

        sender.sendMessage(this.plugin.config().color("&#00FF00[✔] &fОпыт клана &#FFFF00" + clan.name + " &fувеличен на &#00FF00+" + amount + " &f(Итого: &#FFFF00" + clan.rating + "&f)."));
        this.plugin.service().announce(clan, this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Кланы: &#00FF00Администратор начислил клану &#FFFF00" + amount + " &fопыта!"));
    }

    private void handleSetExp(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(this.plugin.config().color("&#FF0000[✖] Помощь: &f/bruhclans setexp <клан|игрок> <количество>"));
            return;
        }
        Clan clan = this.findClan(args[1]);
        if (clan == null) {
            sender.sendMessage(this.plugin.config().color("&#FF0000[✖] Ошибка: &fКлан &#FFFF00" + args[1] + " &fне найден!"));
            return;
        }
        long amount;
        try {
            amount = Long.parseLong(args[2]);
            if (amount < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            sender.sendMessage(this.plugin.config().color("&#FF0000[✖] Ошибка: &fОпыт должен быть неотрицательным числом!"));
            return;
        }

        clan.rating = amount;
        this.plugin.service().store().changed();
        this.plugin.service().store().flush();
        clan.logHistory("Администратор установил опыт клана на " + amount);

        sender.sendMessage(this.plugin.config().color("&#00FF00[✔] &fОпыт клана &#FFFF00" + clan.name + " &fустановлен на &#FFFF00" + clan.rating + "&f."));
    }

    private void handleSetLevel(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(this.plugin.config().color("&#FF0000[✖] Помощь: &f/bruhclans setlevel <клан|игрок> <0-39>"));
            return;
        }
        Clan clan = this.findClan(args[1]);
        if (clan == null) {
            sender.sendMessage(this.plugin.config().color("&#FF0000[✖] Ошибка: &fКлан &#FFFF00" + args[1] + " &fне найден!"));
            return;
        }
        int level;
        try {
            level = Integer.parseInt(args[2]);
            if (level < 0 || level > 39) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            sender.sendMessage(this.plugin.config().color("&#FF0000[✖] Ошибка: &fУровень должен быть от 0 до 39!"));
            return;
        }

        clan.level = level;
        if (level == 0) {
            clan.moneyLimit = 100000L;
            clan.slotLimit = 2;
            clan.homeLimit = 2;
            clan.lengthLimit = 12;
            clan.glow = false;
            clan.colors.clear();
        } else {
            ClanMenus.LevelDef def = ClanMenus.getLevelDef(level);
            if (def != null && clan.rating < def.xp) {
                clan.rating = def.xp;
            }
            this.plugin.menus().applyLevelPerks(clan, level);
        }
        this.plugin.service().store().changed();
        this.plugin.service().store().flush();
        clan.logHistory("Администратор установил уровень клана на " + level);

        sender.sendMessage(this.plugin.config().color("&#00FF00[✔] &fУровень клана &#FFFF00" + clan.name + " &fустановлен на &#FFFF00" + clan.level + " &f(Казна: &#00FF00$" + String.format(Locale.US, "%,d", clan.moneyLimit) + "&f, слотов: &#FFFF00" + clan.slotLimit + "&f, баз: &#FFFF00" + clan.homeLimit + "&f)."));
        this.plugin.service().announce(clan, this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Кланы: &#00FF00Уровень клана установлен администратором на &#FFFF00" + level + "-й!"));
    }

    private void handleAddMoney(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(this.plugin.config().color("&#FF0000[✖] Помощь: &f/bruhclans addmoney <клан|игрок> <сумма>"));
            return;
        }
        Clan clan = this.findClan(args[1]);
        if (clan == null) {
            sender.sendMessage(this.plugin.config().color("&#FF0000[✖] Ошибка: &fКлан &#FFFF00" + args[1] + " &fне найден!"));
            return;
        }
        long amount;
        try {
            amount = Long.parseLong(args[2]);
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            sender.sendMessage(this.plugin.config().color("&#FF0000[✖] Ошибка: &fСумма должна быть положительным числом!"));
            return;
        }

        long limit = clan.moneyLimit > 0 ? clan.moneyLimit : 100000L;
        clan.money = Math.min(limit, clan.money + amount);
        this.plugin.service().store().changed();
        this.plugin.service().store().flush();
        clan.logHistory("Администратор пополнил казну на $" + amount);

        sender.sendMessage(this.plugin.config().color("&#00FF00[✔] &fКазна клана &#FFFF00" + clan.name + " &fпополнена на &#00FF00$" + String.format(Locale.US, "%,d", amount) + " &f(Итого: &#00FF00$" + String.format(Locale.US, "%,d", clan.money) + "&f)."));
    }

    private void handleSetMoney(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(this.plugin.config().color("&#FF0000[✖] Помощь: &f/bruhclans setmoney <клан|игрок> <сумма>"));
            return;
        }
        Clan clan = this.findClan(args[1]);
        if (clan == null) {
            sender.sendMessage(this.plugin.config().color("&#FF0000[✖] Ошибка: &fКлан &#FFFF00" + args[1] + " &fне найден!"));
            return;
        }
        long amount;
        try {
            amount = Long.parseLong(args[2]);
            if (amount < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            sender.sendMessage(this.plugin.config().color("&#FF0000[✖] Ошибка: &fСумма должна быть неотрицательным числом!"));
            return;
        }

        clan.money = amount;
        this.plugin.service().store().changed();
        this.plugin.service().store().flush();
        clan.logHistory("Администратор установил казну на $" + amount);

        sender.sendMessage(this.plugin.config().color("&#00FF00[✔] &fКазна клана &#FFFF00" + clan.name + " &fустановлена на &#00FF00$" + String.format(Locale.US, "%,d", clan.money) + "&f."));
    }

    private void handleGivePoints(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(this.plugin.config().color("&#FF0000[✖] Помощь: &f/bruhclans givepoints <игрок> <количество>"));
            return;
        }
        if (this.plugin.playerPoints() == null || !this.plugin.playerPoints().isReady()) {
            sender.sendMessage(this.plugin.config().color("&#FF0000[✖] Ошибка: &fПлагин PlayerPoints не подключен или недоступен!"));
            return;
        }
        String targetName = args[1];
        Player targetPl = Bukkit.getPlayerExact(targetName);
        UUID targetUuid = targetPl != null ? targetPl.getUniqueId() : null;
        if (targetUuid == null) {
            try {
                targetUuid = Bukkit.getOfflinePlayer(targetName).getUniqueId();
            } catch (Exception ignored) {}
        }
        if (targetUuid == null) {
            sender.sendMessage(this.plugin.config().color("&#FF0000[✖] Ошибка: &fИгрок &#FFFF00" + targetName + " &fне найден!"));
            return;
        }
        int amount;
        try {
            amount = Integer.parseInt(args[2]);
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            sender.sendMessage(this.plugin.config().color("&#FF0000[✖] Ошибка: &fКоличество должно быть положительным целым числом!"));
            return;
        }

        boolean ok = this.plugin.playerPoints().deposit(targetUuid, amount);
        if (ok) {
            int current = this.plugin.playerPoints().getPoints(targetUuid);
            sender.sendMessage(this.plugin.config().color("&#00FF00[✔] &fИгроку &#FFFF00" + targetName + " &fвыдано &#00FF00" + amount + " ТК &f(Баланс: &#FFFF00" + current + " ТК&f)."));
            if (targetPl != null && targetPl.isOnline()) {
                targetPl.sendMessage(this.plugin.config().color("&#00FF00[✔] &#FFFF00Вам начислено &#00FF00" + amount + " ТК&f!"));
            }
        } else {
            sender.sendMessage(this.plugin.config().color("&#FF0000[✖] Ошибка: &fНе удалось начислить ТК через PlayerPoints API!"));
        }
    }

    private void handleInfo(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(this.plugin.config().color("&#FF0000[✖] Помощь: &f/bruhclans info <клан|игрок>"));
            return;
        }
        Clan clan = this.findClan(args[1]);
        if (clan == null) {
            sender.sendMessage(this.plugin.config().color("&#FF0000[✖] Ошибка: &fКлан &#FFFF00" + args[1] + " &fне найден!"));
            return;
        }

        sender.sendMessage(this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00--- Информация о клане " + clan.name + " ---"));
        sender.sendMessage(this.plugin.config().color(" &#FF6600• Системное имя: &#FFFF00" + clan.name));
        sender.sendMessage(this.plugin.config().color(" &#FF6600• Отображаемое имя: &#FFFF00" + (clan.displayName != null ? clan.displayName : clan.name)));
        sender.sendMessage(this.plugin.config().color(" &#FF6600• Владелец: &#FFFF00" + clan.owner));
        sender.sendMessage(this.plugin.config().color(" &#FF6600• Уровень: &#FFFF00" + clan.level + "/39 &7(Опыт: &#FFFF00" + clan.rating + "&7)"));
        sender.sendMessage(this.plugin.config().color(" &#FF6600• Казна: &#00FF00$" + String.format(Locale.US, "%,d", clan.money) + " &7/ &#00FF00$" + String.format(Locale.US, "%,d", clan.moneyLimit)));
        sender.sendMessage(this.plugin.config().color(" &#FF6600• Участники: &#FFFF00" + clan.members.size() + "/" + clan.memberLimit));
        int rentedActive = 0;
        for (int s = 39; s <= 43; s++) {
            if (clan.isStorageSlotRented(s)) rentedActive++;
        }
        sender.sendMessage(this.plugin.config().color(" &#FF6600• Слоты склада: &#FFFF00" + clan.slotLimit + " &7(арендовано: &#FFFF00" + rentedActive + "&7, предметов: " + clan.storage.size() + ")"));
        sender.sendMessage(this.plugin.config().color(" &#FF6600• Точки баз: &#FFFF00" + clan.homes.size() + "/" + clan.homeLimit + " &7(" + String.join(", ", clan.homes.keySet()) + ")"));
        sender.sendMessage(this.plugin.config().color(" &#FF6600• PvP-режим: " + (clan.pvp ? "&#00FF00Включен" : "&#FF0000Выключен") + " &7| Окраска брони: " + (clan.glow ? "&#00FF00Включена (" + clan.glowColor + ")" : "&#FF0000Выключена")));
        sender.sendMessage(this.plugin.config().color(" &#FF6600• Цвета названия: &#FFFF00" + (clan.colors.isEmpty() ? "&7нет" : String.join(" ", clan.colors))));
    }

    private void handleSetLeader(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(this.plugin.config().color("&#FF0000[✖] Помощь: &f/bruhclans setleader <клан> <новый_лидер>"));
            return;
        }
        Clan clan = this.findClan(args[1]);
        if (clan == null) {
            sender.sendMessage(this.plugin.config().color("&#FF0000[✖] Ошибка: &fКлан &#FFFF00" + args[1] + " &fне найден!"));
            return;
        }
        String newLeaderName = args[2];
        Clan.Member member = clan.member(newLeaderName);
        if (member == null) {
            sender.sendMessage(this.plugin.config().color("&#FF0000[✖] Ошибка: &fИгрок &#FFFF00" + newLeaderName + " &fне состоит в клане &#FFFF00" + clan.name + "&f!"));
            return;
        }

        String oldOwner = clan.owner;
        Clan.Member oldOwnerMember = clan.member(oldOwner);
        if (oldOwnerMember != null) {
            oldOwnerMember.rights.clear();
            oldOwnerMember.rights.add(Clan.Right.SHOW_MONEY);
            oldOwnerMember.rights.add(Clan.Right.INVEST_MONEY);
        }

        clan.owner = member.name;
        member.rights.clear();
        member.rights.add(Clan.Right.ALL);

        this.plugin.service().store().changed();
        this.plugin.service().store().flush();
        clan.logHistory("Администратор сменил лидера с " + oldOwner + " на " + member.name);

        sender.sendMessage(this.plugin.config().color("&#00FF00[✔] &fЛидер клана &#FFFF00" + clan.name + " &fуспешно изменен на &#FFFF00" + member.name + "&f."));
        this.plugin.service().announce(clan, this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Кланы: &#00FF00Новым лидером клана назначен &#FFFF00" + member.name + "&f!"));
    }

    private void handleDisband(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(this.plugin.config().color("&#FF0000[✖] Помощь: &f/bruhclans disband <клан>"));
            return;
        }
        Clan clan = this.findClan(args[1]);
        if (clan == null) {
            sender.sendMessage(this.plugin.config().color("&#FF0000[✖] Ошибка: &fКлан &#FFFF00" + args[1] + " &fне найден!"));
            return;
        }

        String clanName = clan.name;
        this.plugin.service().announce(clan, this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Кланы: &#FF0000Клан был принудительно распущен администратором!"));
        for (Clan.Member m : clan.members.values()) {
            Player p = Bukkit.getPlayerExact(m.name);
            if (p != null && p.isOnline()) {
                p.closeInventory();
            }
        }
        if (this.plugin.clanCommands() != null) {
            for (java.util.Map<String, ClanCommands.Invitation> invites : this.plugin.clanCommands().invitations.values()) {
                invites.remove(clan.name.toLowerCase(Locale.ROOT));
            }
        }
        this.plugin.glow().resetClan(clan);
        this.plugin.service().store().remove(clan);
        this.plugin.service().store().changed();
        this.plugin.service().store().flush();

        sender.sendMessage(this.plugin.config().color("&#00FF00[✔] &fКлан &#FFFF00" + clanName + " &fуспешно распущен."));
    }

    private void handleReset(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(this.plugin.config().color("&#FF0000[✖] Помощь: &f/bruhclans reset <клан>"));
            return;
        }
        Clan clan = this.findClan(args[1]);
        if (clan == null) {
            sender.sendMessage(this.plugin.config().color("&#FF0000[✖] Ошибка: &fКлан &#FFFF00" + args[1] + " &fне найден!"));
            return;
        }

        clan.level = 0;
        clan.rating = 0;
        clan.moneyLimit = 100000L;
        clan.slotLimit = 2;
        clan.homeLimit = 2;
        clan.lengthLimit = 12;
        clan.glow = false;
        clan.colors.clear();
        this.plugin.service().store().changed();
        this.plugin.service().store().flush();
        clan.logHistory("Администратор сбросил прогресс клана");

        sender.sendMessage(this.plugin.config().color("&#00FF00[✔] &fПрогресс клана &#FFFF00" + clan.name + " &fсброшен (уровень 0, опыт 0)."));
        this.plugin.service().announce(clan, this.plugin.config().color("&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Кланы: &#FF0000Прогресс клана был сброшен администратором."));
    }

    private void handleReload(CommandSender sender) {
        this.plugin.config().load();
        sender.sendMessage(this.plugin.config().color("&#00FF00[✔] &fКонфигурация плагина BruhClans успешно перезагружена!"));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!this.hasPermission(sender)) {
            return Collections.emptyList();
        }

        if (args.length == 1) {
            ArrayList<String> matches = new ArrayList<>();
            String prefix = args[0].toLowerCase(Locale.ROOT);
            for (String sub : SUBCOMMANDS) {
                if (sub.startsWith(prefix)) matches.add(sub);
            }
            return matches;
        }

        if (args.length == 2) {
            String sub = args[0].toLowerCase(Locale.ROOT);
            ArrayList<String> matches = new ArrayList<>();
            String prefix = args[1].toLowerCase(Locale.ROOT);

            if (sub.equals("givepoints")) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p.getName().toLowerCase(Locale.ROOT).startsWith(prefix)) {
                        matches.add(p.getName());
                    }
                }
                return matches;
            }

            for (Clan c : this.plugin.service().store().all()) {
                if (c.name.toLowerCase(Locale.ROOT).startsWith(prefix)) {
                    matches.add(c.name);
                }
            }
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().toLowerCase(Locale.ROOT).startsWith(prefix) && !matches.contains(p.getName())) {
                    matches.add(p.getName());
                }
            }
            return matches;
        }

        if (args.length == 3) {
            String sub = args[0].toLowerCase(Locale.ROOT);
            String prefix = args[2].toLowerCase(Locale.ROOT);
            ArrayList<String> matches = new ArrayList<>();

            if (sub.equals("setlevel")) {
                for (String lvl : Arrays.asList("0", "1", "2", "3", "5", "10", "20", "30", "39")) {
                    if (lvl.startsWith(prefix)) matches.add(lvl);
                }
                return matches;
            }
            if (sub.equals("addexp") || sub.equals("setexp")) {
                for (String exp : Arrays.asList("100", "500", "1000", "2500", "5000", "10000", "25000")) {
                    if (exp.startsWith(prefix)) matches.add(exp);
                }
                return matches;
            }
            if (sub.equals("addmoney") || sub.equals("setmoney")) {
                for (String money : Arrays.asList("10000", "50000", "100000", "1000000", "10000000")) {
                    if (money.startsWith(prefix)) matches.add(money);
                }
                return matches;
            }
            if (sub.equals("givepoints")) {
                for (String pts : Arrays.asList("100", "500", "1000", "5000", "10000")) {
                    if (pts.startsWith(prefix)) matches.add(pts);
                }
                return matches;
            }
            if (sub.equals("setleader")) {
                Clan clan = this.findClan(args[1]);
                if (clan != null) {
                    for (String memberName : clan.members.keySet()) {
                        if (memberName.toLowerCase(Locale.ROOT).startsWith(prefix)) {
                            matches.add(memberName);
                        }
                    }
                }
                return matches;
            }
        }

        return Collections.emptyList();
    }
}
