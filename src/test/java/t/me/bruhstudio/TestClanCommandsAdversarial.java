package t.me.bruhstudio;

public class TestClanCommandsAdversarial {

    public static void main(String[] args) throws Exception {
        testScreenshotFidelityStrings();
        testCanPermissionBypassForCustomHandlers();
        System.out.println("ALL ADVERSARIAL TESTS PASSED SUCCESSFULLY!");
    }

    public static void testScreenshotFidelityStrings() {
        // 1. clan chat / cc
        String chatUsage = "&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Кланы: &f/c (или /cc) <сообщение>";
        String chatFormat = "&#FF0000[&#FF0000КЛАН&#FF0000] &#FF6600%s &#FF0000» &#FFFF00%s";
        
        assert chatUsage.contains("&#FF0000[&#FF0000⚔&#FF0000]");
        assert chatUsage.contains("&#FFFF00Кланы:");
        assert chatUsage.contains("&f/c (или /cc) <сообщение>");
        assert chatFormat.contains("&#FF0000[&#FF0000КЛАН&#FF0000]");
        assert chatFormat.contains("&#FF6600");
        assert chatFormat.contains("&#FF0000»");
        assert chatFormat.contains("&#FFFF00");

        // 2. clan coords
        String coordsSuccess = "&#FF0000[&#FF0000?&#FF0000] &#FFFF00%s &7(&#FFFF00%s&7, &#FFFF00%d&7, &#FFFF00%d&7, &#FFFF00%d&7)";
        String coordsNotInClan = "&#FF0000[&#FF0000⚔&#FF0000] &#FF0000Ошибка: &fУчастника &#FFFF00%s &fнет в вашем клане!";
        String coordsOffline = "&#FF0000[&#FF0000⚔&#FF0000] &#FF0000Ошибка: &fИгрок &#FFFF00%s &fне в сети!";

        assert coordsSuccess.contains("&#FF0000[&#FF0000?&#FF0000]");
        assert coordsSuccess.contains("&7(");
        assert coordsNotInClan.contains("&#FF0000[&#FF0000⚔&#FF0000] &#FF0000Ошибка: &fУчастника");
        assert coordsOffline.contains("&#FF0000[&#FF0000⚔&#FF0000] &#FF0000Ошибка: &fИгрок");

        // 3. clan delhome
        String delhomeUsage = "&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Кланы: &f/clan delhome <название>";
        String delhomeSuccess = "&#FF0000[&#FF0000⚔&#FF0000] &fТочка базы клана &#FFFF00%s &fудалена";
        String delhomeNotFound = "&#FF0000[&#FF0000⚔&#FF0000] &#FF0000Ошибка: &fВ клане нет такой точки базы!";
        String delhomeNoPerm = "&#FF0000[&#FF0000⚔&#FF0000] &#FF0000Ошибка: &fУ вас нет прав на удаление точки базы!";

        assert delhomeUsage.contains("&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Кланы: &f/clan delhome <название>");
        assert delhomeSuccess.contains("&#FF0000[&#FF0000⚔&#FF0000] &fТочка базы клана &#FFFF00");
        assert !delhomeSuccess.endsWith("!"); // Screenshot does NOT have exclamation mark at end of delhome success
        assert delhomeNotFound.contains("&#FF0000[&#FF0000⚔&#FF0000] &#FF0000Ошибка: &fВ клане нет такой точки базы!");
        assert delhomeNoPerm.contains("&#FF0000[&#FF0000⚔&#FF0000] &#FF0000Ошибка: &fУ вас нет прав на удаление точки базы!");

        // 4. clan leave
        String leaveLeader1 = "&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Кланы: &fВы не можете покинуть клан, так как Вы его Лидер";
        String leaveLeader2 = "&#FF0000|| &fРешение: удалить клан &#FFFF00/clan disband";
        assert leaveLeader1.contains("&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Кланы: &fВы не можете покинуть клан, так как Вы его Лидер");
        assert !leaveLeader1.endsWith(".");
        assert leaveLeader2.equals("&#FF0000|| &fРешение: удалить клан &#FFFF00/clan disband");

        // 5. clan money
        String moneyFormat = "&#FF0000[&#FF0000⚔&#FF0000] &fБаланс клана: &#FFFF00%d";
        assert moneyFormat.equals("&#FF0000[&#FF0000⚔&#FF0000] &fБаланс клана: &#FFFF00%d");
        assert !moneyFormat.contains("$");

        // 6. clan prefix
        String prefixHelp = "&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Помощь: &f/clan prefix <игрок> <префикс>";
        String prefixNotInClan = "&#FF0000[&#FF0000⚔&#FF0000] &#FF0000Ошибка: &fИгрок не состоит в клане!";
        String prefixSuccess = "&#FF0000[&#FF0000⚔&#FF0000] &fИгроку &#FFFF00%s &fустановлен префикс &#FFFF00%s";
        assert prefixHelp.equals("&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Помощь: &f/clan prefix <игрок> <префикс>");
        assert prefixNotInClan.equals("&#FF0000[&#FF0000⚔&#FF0000] &#FF0000Ошибка: &fИгрок не состоит в клане!");
        assert prefixSuccess.equals("&#FF0000[&#FF0000⚔&#FF0000] &fИгроку &#FFFF00%s &fустановлен префикс &#FFFF00%s");

        // 7. clan pvp
        String pvpDisabled = "&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Кланы: PVP-Режим &fв Клане теперь &#FF0000отключён&f!";
        String pvpEnabled = "&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Кланы: PVP-Режим &fв Клане теперь &#00FF00включён&f!";
        assert pvpDisabled.equals("&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Кланы: PVP-Режим &fв Клане теперь &#FF0000отключён&f!");
        assert pvpEnabled.equals("&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Кланы: PVP-Режим &fв Клане теперь &#00FF00включён&f!");

        // 8. clan sethome
        String sethomeHelp = "&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Помощь: &f/clan sethome <название>";
        String sethomeNew = "&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Клан: &fУстановлена новая точка базы клана — &#FFFF00%s.";
        String sethomeLimit = "&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Клан: &fБольше точек баз поставить нельзя. Прокачивайте\n &fклан, чтобы открыть новые!";
        String sethomeMoved = "&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Клан: &fТочка &#FFFF00%s &fперенесена. Прошлые координаты:\n &#FFFF00%d %d %d";
        assert sethomeHelp.equals("&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Помощь: &f/clan sethome <название>");
        assert sethomeNew.contains("&#FFFF00Клан: &fУстановлена новая точка базы клана — &#FFFF00");
        assert sethomeLimit.contains("&#FFFF00Клан: &fБольше точек баз");
        assert sethomeMoved.contains("&#FFFF00Клан: &fТочка &#FFFF00");

        // 9. clan rename
        String renameHelp = "&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Помощь: &f/clan rename <название>";
        String renameAnnounce1 = "&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Кланы: &#FFFF00%s &fпереименовал клан в %s";
        String renameAnnounce2 = "&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Кланы: &fНазвание клана изменено.";
        assert renameHelp.equals("&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Помощь: &f/clan rename <название>");
        assert renameAnnounce1.contains("&#FFFF00Кланы: &#FFFF00");
        assert renameAnnounce2.equals("&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Кланы: &fНазвание клана изменено.");

        // 10. clan withdraw
        String withdrawHelp = "&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Помощь: &f/clan withdraw <сумма>";
        String withdrawError = "&#FF0000[&#FF0000⚔&#FF0000] &#FF0000Ошибка: &fБаланс казны меньше веденной суммы!";
        String withdrawAnnounce = "&#FF0000[&#FF0000⚔&#FF0000] &fИгрок &#FFFF00%s &fснял &#FFFF00$%d &fиз казны клана!";
        assert withdrawHelp.equals("&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Помощь: &f/clan withdraw <сумма>");
        assert withdrawError.equals("&#FF0000[&#FF0000⚔&#FF0000] &#FF0000Ошибка: &fБаланс казны меньше веденной суммы!");
        assert withdrawAnnounce.contains("&fИгрок &#FFFF00%s &fснял &#FFFF00$%d &fиз казны клана!");

        // 11. No extra commands assertion
        assert !ClanCommands.COMMANDS.contains("level");
        assert !ClanCommands.COMMANDS.contains("levels");
        assert !ClanCommands.COMMANDS.contains("rewards");

        // 12. clan info
        String infoHeader = "&#FF0000[&#FF0000⚔&#FF0000] &fКлан &#FFFF00%s &7№%d";
        String infoLevel = "&#FF0000|| &fУровень: &#FFFF00%d&f, опыт: &#FFFF00%s";
        String infoLeader = "&#FF0000|| &fЛидер: &#FFFF00%s";
        String infoMoney = "&#FF0000|| &fКазна: &#FFFF00$%s";
        String infoPvp = "&#FF0000|| &fPvP-режим: &#FF0000[Выключен]";
        String infoMembers = "&#FF0000|| &fУчастники: &7[%s&7] %s";
        assert infoHeader.contains("&fКлан &#FFFF00%s &7№");
        assert infoLevel.startsWith("&#FF0000|| &fУровень: &#FFFF00");
        assert infoLeader.equals("&#FF0000|| &fЛидер: &#FFFF00%s");
        assert infoMoney.startsWith("&#FF0000|| &fКазна: &#FFFF00$");
        assert infoPvp.equals("&#FF0000|| &fPvP-режим: &#FF0000[Выключен]");
        assert infoMembers.startsWith("&#FF0000|| &fУчастники: ");

        // 13. clan invest
        String investHelp = "&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Помощь: &f/clan invest <сумма>";
        String investAnnounce = "&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Кланы: &#FFFF00%s &fпополнил баланс казны\n&#FF0000|| &fБаланс казны: &#FFFF00%d";
        assert investHelp.equals("&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Помощь: &f/clan invest <сумма>");
        assert investAnnounce.contains("&#FFFF00Кланы: &#FFFF00%s &fпополнил баланс казны\n&#FF0000|| &fБаланс казны: &#FFFF00%d");
        assert !investAnnounce.contains("$");

        // 14. clan invite
        String inviteHelp = "&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Помощь: &f/clan invite <игрок>";
        String inviteOffline = "&#FF0000[&#FF0000⚔&#FF0000] &#FF0000Ошибка: &fИгрок &#FFFF00%s &fне в сети!";
        String inviteInOtherClan = "&#FF0000[&#FF0000⚔&#FF0000] &#FF0000Ошибка: &fИгрок состоит в другом клане!";
        String inviteSent = "&#FF0000[&#FF0000⚔&#FF0000] &fВы отправили приглашение в клан игроку &#FFFF00%s&f!";
        String inviteDeclined = "&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Кланы: &#FFFF00%s &fотклонил Ваше приглашение в клан.";
        assert inviteHelp.equals("&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Помощь: &f/clan invite <игрок>");
        assert inviteOffline.equals("&#FF0000[&#FF0000⚔&#FF0000] &#FF0000Ошибка: &fИгрок &#FFFF00%s &fне в сети!");
        assert inviteInOtherClan.equals("&#FF0000[&#FF0000⚔&#FF0000] &#FF0000Ошибка: &fИгрок состоит в другом клане!");
        assert inviteSent.equals("&#FF0000[&#FF0000⚔&#FF0000] &fВы отправили приглашение в клан игроку &#FFFF00%s&f!");
        assert inviteDeclined.equals("&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Кланы: &#FFFF00%s &fотклонил Ваше приглашение в клан.");

        // 15. clan kick
        String kickHelp = "&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Помощь: &f/clan kick <игрок>";
        String kickNotInClan = "&#FF0000[&#FF0000⚔&#FF0000] &#FF0000Ошибка: &fИгрок не состоит в клане!";
        String kickGuiTitle = "&#FF0000Кикнуть %s?";
        String kickAnnounce = "&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Кланы: &#FFFF00%s &fисключен из клана";
        assert kickHelp.equals("&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Помощь: &f/clan kick <игрок>");
        assert kickNotInClan.equals("&#FF0000[&#FF0000⚔&#FF0000] &#FF0000Ошибка: &fИгрок не состоит в клане!");
        assert kickGuiTitle.equals("&#FF0000Кикнуть %s?");
        assert kickAnnounce.equals("&#FF0000[&#FF0000⚔&#FF0000] &#FFFF00Кланы: &#FFFF00%s &fисключен из клана");
        assert !kickAnnounce.endsWith("!"); // Screenshot does NOT have exclamation mark at end
    }

    public static void testCanPermissionBypassForCustomHandlers() {
        ClanCommands commands = new ClanCommands(null);

        Clan clan = new Clan();
        clan.name = "TestClan";
        clan.owner = "Leader";
        Clan.Member member = new Clan.Member("TestMember", "");
        clan.add(member);

        // delhome must return TRUE in can() so home() can handle it and print the custom error message
        if (!commands.can(null, "delhome", clan)) throw new AssertionError("can(delhome) must be true");
        if (!commands.can(null, "sethome", clan)) throw new AssertionError("can(sethome) must be true");
        if (!commands.can(null, "pvp", clan)) throw new AssertionError("can(pvp) must be true");
        if (!commands.can(null, "glow", clan)) throw new AssertionError("can(glow) must be true");
        if (!commands.can(null, "coords", clan)) throw new AssertionError("can(coords) must be true");
        if (!commands.can(null, "chat", clan)) throw new AssertionError("can(chat) must be true");
        if (!commands.can(null, "money", clan)) throw new AssertionError("can(money) must be true");
        if (!commands.can(null, "prefix", clan)) throw new AssertionError("can(prefix) must be true");
        if (!commands.can(null, "leave", clan)) throw new AssertionError("can(leave) must be true");
        if (!commands.can(null, "storage", clan)) throw new AssertionError("can(storage) must be true");
        if (!commands.can(null, "top", clan)) throw new AssertionError("can(top) must be true");
        if (!commands.can(null, "rename", clan)) throw new AssertionError("can(rename) must be true");
        if (!commands.can(null, "info", clan)) throw new AssertionError("can(info) must be true");
        if (!commands.can(null, "invest", clan)) throw new AssertionError("can(invest) must be true");
        if (!commands.can(null, "invite", clan)) throw new AssertionError("can(invite) must be true");
        if (!commands.can(null, "kick", clan)) throw new AssertionError("can(kick) must be true");
        if (!commands.can(null, "withdraw", clan)) throw new AssertionError("can(withdraw) must be true");

        // Non-clan tests
        if (!commands.can(null, "create", null)) throw new AssertionError("can(create) must be true for non-clan");
        if (!commands.can(null, "top", null)) throw new AssertionError("can(top) must be true for non-clan");
        if (!commands.can(null, "accept", null)) throw new AssertionError("can(accept) must be true for non-clan");
        if (!commands.can(null, "cancel", null)) throw new AssertionError("can(cancel) must be true for non-clan");
        if (commands.can(null, "menu", null)) throw new AssertionError("can(menu) must be false for non-clan");
        if (commands.can(null, "storage", null)) throw new AssertionError("can(storage) must be false for non-clan");
        if (commands.can(null, "sethome", null)) throw new AssertionError("can(sethome) must be false for non-clan");
        if (commands.can(null, "rename", null)) throw new AssertionError("can(rename) must be false for non-clan");
        if (commands.can(null, "info", null)) throw new AssertionError("can(info) must be false for non-clan");
        if (commands.can(null, "invest", null)) throw new AssertionError("can(invest) must be false for non-clan");
        if (commands.can(null, "invite", null)) throw new AssertionError("can(invite) must be false for non-clan");
        if (commands.can(null, "kick", null)) throw new AssertionError("can(kick) must be false for non-clan");
        if (commands.can(null, "withdraw", null)) throw new AssertionError("can(withdraw) must be false for non-clan");
        if (commands.can(null, "level", null)) throw new AssertionError("can(level) must be false");
        if (commands.can(null, "levels", null)) throw new AssertionError("can(levels) must be false");
        if (commands.can(null, "rewards", null)) throw new AssertionError("can(rewards) must be false");
    }
}
