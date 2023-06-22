package me.vovari2.dungeonps.utils;

import me.vovari2.dungeonps.*;
import me.vovari2.dungeonps.objects.DPSParty;
import me.vovari2.dungeonps.objects.DPSPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MenuUtils {
    // Менюшка с выбором одиночного или коорперативного прохождения данжа
    public static void openPartyStart(Player player){
        Inventory inventory = Bukkit.createInventory(player, 54, DPSLocale.getLocaleComponent("menu.party_start.name"));

        DPSTaskTicks.addLockableInventory(player.getName(), false);
        player.openInventory(inventory);

        ItemStack item = DPS.getItem("start_single_player");
        inventory.setItem(9, item);
        inventory.setItem(10, item);
        inventory.setItem(11, item);
        inventory.setItem(12, item);
        inventory.setItem(18, item);
        inventory.setItem(19, item);
        inventory.setItem(20, item);
        inventory.setItem(21, item);
        inventory.setItem(27, item);
        inventory.setItem(28, item);
        inventory.setItem(29, item);
        inventory.setItem(30, item);

        item = DPS.getItem("start_multiplayer_player");
        inventory.setItem(14, item);
        inventory.setItem(15, item);
        inventory.setItem(16, item);
        inventory.setItem(17, item);
        inventory.setItem(23, item);
        inventory.setItem(24, item);
        inventory.setItem(25, item);
        inventory.setItem(26, item);
        inventory.setItem(32, item);
        inventory.setItem(33, item);
        inventory.setItem(34, item);
        inventory.setItem(35, item);

        item = DPS.getItem("start_return_back");
        inventory.setItem(48, item);
        inventory.setItem(49, item);
        inventory.setItem(50, item);
    }



    // Менюшка настроек пати для лидера
    public static void openPartySettingsLeader(DPSParty party, DPSPlayer leader){
        List<DPSPlayer> listPlayers = party.getPlayers();
        Player player = leader.getPlayer();


        // Инициализация переменной инвентаря
        Inventory inventory = Bukkit.createInventory(player, 54, DPSLocale.replacePlaceHolders("menu.party_settings.name_leader",
                new String[]{
                        "%button_ready_1%",
                        "%button_ready_2%",
                        "%frame_ready_2%",
                        "%button_ready_3%",
                        "%frame_ready_3%",
                        "%button_ready_4%",
                        "%frame_ready_4%",
                        "%button_chat%",
                        "%button_func%",
                        "%button_notice%"
                } ,
                new String[]{
                        getButtonReady(listPlayers, 1),
                        getButtonReady(listPlayers, 2),
                        getCellFrame(listPlayers, 2),
                        getButtonReady(listPlayers, 3),
                        getCellFrame(listPlayers, 3),
                        getButtonReady(listPlayers, 4),
                        getCellFrame(listPlayers, 4),
                        leader.isChat() ? DPSLocale.getLocaleString("placeholders.button_chat.use") : DPSLocale.getLocaleString("placeholders.button_chat.not_use"),
                        party.allIsReady() ? DPSLocale.getLocaleString("placeholders.button_func.start") : leader.isReady() ? DPSLocale.getLocaleString("placeholders.button_func.cancel") : DPSLocale.getLocaleString("placeholders.button_func.ready"),
                        leader.isWaitCoolDown() ? DPSLocale.getLocaleString("placeholders.button_notice.not_use") : DPSLocale.getLocaleString("placeholders.button_notice.use")
        }));
        for (int i = 0; i < 4; i++){
            if (listPlayers.size() > i){
                DPSPlayer targetDPSPlayer = listPlayers.get(i);
                Player targetPlayer = targetDPSPlayer.getPlayer();
                inventory.setItem(i * 2 + 19, targetDPSPlayer.isLeader() ? DPS.getItemPH("head_leader_for_leader").getItem(targetPlayer, new String[]{targetPlayer.getName()}, new String[]{}) : DPS.getItemPH("head_player_for_leader").getItem(targetPlayer, new String[]{targetPlayer.getName()}, new String[]{}));
                inventory.setItem(i * 2 + 28, targetDPSPlayer.isReady() ? DPS.getItem("player_ready") : DPS.getItem("player_not_ready"));
            }
            else {
                inventory.setItem(i * 2 + 19, DPS.getItem("party_invite_leader"));
                inventory.setItem(i * 2 + 28, DPS.getItem("player_undefined"));
            }
        }

        DPSTaskTicks.addLockableInventory(player.getName(), true);
        player.openInventory(inventory);

        // Инициализация предметов
        inventory.setItem(0, DPS.getItem("menu_close"));
        setWaitCooldownNotice(leader, inventory);
        inventory.setItem(7, leader.isChat() ? DPS.getItem("open_public_chat") : DPS.getItem("open_party_chat"));
        inventory.setItem(8, DPS.getItem("party_leave"));

        ItemStack funcItem = party.allIsReady() ? DPS.getItem("func_start") : leader.isReady() ? DPS.getItem("func_cancel") : DPS.getItem("func_ready");
        inventory.setItem(48, funcItem);
        inventory.setItem(49, funcItem);
        inventory.setItem(50, funcItem);
    }
    // Менюшка настроек пати для игроков
    public static void openPartySettingsPlayer(DPSParty party, DPSPlayer dpsPlayer){
        List<DPSPlayer> listPlayers = party.getPlayers();
        Player player = dpsPlayer.getPlayer();


        // Инициализация переменной инвентаря
        Inventory inventory = Bukkit.createInventory(player, 54, DPSLocale.replacePlaceHolders("menu.party_settings.name_player",
                new String[]{
                        "%button_ready_1%",
                        "%button_ready_2%",
                        "%frame_ready_2%",
                        "%button_ready_3%",
                        "%frame_ready_3%",
                        "%button_ready_4%",
                        "%frame_ready_4%",
                        "%button_chat%",
                        "%button_func%"
                } ,
                new String[]{
                        getButtonReady(listPlayers, 1),
                        getButtonReady(listPlayers, 2),
                        getCellFrame(listPlayers, 2),
                        getButtonReady(listPlayers, 3),
                        getCellFrame(listPlayers, 3),
                        getButtonReady(listPlayers, 4),
                        getCellFrame(listPlayers, 4),
                        dpsPlayer.isChat() ? DPSLocale.getLocaleString("placeholders.button_chat.use") : DPSLocale.getLocaleString("placeholders.button_chat.not_use"),
                        dpsPlayer.isReady() ? DPSLocale.getLocaleString("placeholders.button_func.cancel") : DPSLocale.getLocaleString("placeholders.button_func.ready")
                }));

        for (int i = 0; i < 4; i++){
            if (listPlayers.size() > i){
                DPSPlayer targetDPSPlayer = listPlayers.get(i);
                Player targetPlayer = targetDPSPlayer.getPlayer();
                inventory.setItem(i * 2 + 19, targetDPSPlayer.isLeader() ? DPS.getItemPH("head_leader_for_player").getItem(targetPlayer, new String[]{targetPlayer.getName()}, new String[]{}) : DPS.getItemPH("head_player_for_player").getItem(targetPlayer, new String[]{targetPlayer.getName()}, new String[]{}));
                inventory.setItem(i * 2 + 28, targetDPSPlayer.isReady() ? DPS.getItem("player_ready") : DPS.getItem("player_not_ready"));
            }
            else {
                inventory.setItem(i * 2 + 19, DPS.getItem("party_invite_player"));
                inventory.setItem(i * 2 + 28, DPS.getItem("player_undefined"));
            }
        }

        DPSTaskTicks.addLockableInventory(player.getName(), true);
        player.openInventory(inventory);

        // Инициализация предметов
        inventory.setItem(0, DPS.getItem("menu_close"));
        inventory.setItem(7, dpsPlayer.isChat() ? DPS.getItem("open_public_chat") : DPS.getItem("open_party_chat"));
        inventory.setItem(8, DPS.getItem("party_leave"));

        ItemStack funcItem = dpsPlayer.isReady() ? DPS.getItem("func_cancel") : DPS.getItem("func_ready");
        inventory.setItem(48, funcItem);
        inventory.setItem(49, funcItem);
        inventory.setItem(50, funcItem);
    }
    // Формирование кнопки готов у игрока
    private static String getButtonReady(List<DPSPlayer> listPlayers, int number){
        return listPlayers.size() >= number && listPlayers.get(number-1).isReady() ? DPSLocale.getLocaleString("placeholders.button_ready_" + number + ".ready") : DPSLocale.getLocaleString("placeholders.button_ready_" + number + ".not_ready");
    }
    private static String getCellFrame(List<DPSPlayer> listPlayers, int number){
        return listPlayers.size() >= number ? DPSLocale.getLocaleString("placeholders.frame_ready_" + number + ".use") : DPSLocale.getLocaleString("placeholders.frame_ready_" + number + ".not_use");
    }
    // Формирование кнопки уведомить всех
    public static void setWaitCooldownNotice(DPSPlayer dpsPlayer, Inventory inventory){
        if (inventory == null)
            return;
        if (dpsPlayer.isWaitCoolDown()){
            int waitSeconds = DPS.getTaskTicks().getSecondsToTime(DPS.getTaskTicks().waitCooldownNotice.get(dpsPlayer.getPlayer().getName()));
            inventory.setItem(6, DPS.getItemPH("party_all_invitation_not_use").getItem(new String[]{}, new String[]{waitSeconds / 1200 + ":" + (waitSeconds / 20 % 60 < 10 ? "0" : "") + (waitSeconds / 20 % 60)}));
        }
        else inventory.setItem(6, DPS.getItem("party_all_invitation_use"));
    }



    // Менюшка настроек пати для игроков
    public static void openPartyPlayers(DPSPlayer dpsPlayer){
        List<Player> players = getPlayerOnPage(dpsPlayer.getPartyPlayersPage(), dpsPlayer);
        Player player = dpsPlayer.getPlayer();

        List<Player> prevPage = getPlayerOnPage(dpsPlayer.getPartyPlayersPage() - 1, dpsPlayer),
                nextPage = getPlayerOnPage(dpsPlayer.getPartyPlayersPage() + 1, dpsPlayer);

        // Инициализация переменной инвентаря
        Inventory inventory = Bukkit.createInventory(player, 54, DPSLocale.replacePlaceHolders("menu.party_players.name",
                new String[]{
                        "%party_menu_name%",
                        "%prev_page%",
                        "%next_page%"
                } ,
                new String[]{
                        dpsPlayer.isUseOnlyFriends() ? DPSLocale.getLocaleString("placeholders.party_players_name.friends") : DPSLocale.getLocaleString("placeholders.party_players_name.players"),
                        prevPage.size() == 0 ? DPSLocale.getLocaleString("placeholders.prev_page.not_use") : DPSLocale.getLocaleString("placeholders.prev_page.use"),
                        nextPage.size() == 0 ? DPSLocale.getLocaleString("placeholders.next_page.not_use") : DPSLocale.getLocaleString("placeholders.next_page.use")
                }));

        for (int i = 0; i < players.size(); i++){
            Player targetPlayer = players.get(i);
            inventory.setItem(i + 9, DPS.getItemPH("head_player_in_party_players").getItem(targetPlayer, new String[]{targetPlayer.getName()}, new String[]{}));
        }

        DPSTaskTicks.addLockableInventory(player.getName(), true);
        player.openInventory(inventory);

        inventory.setItem(0, DPS.getItem("return_back"));
        int size = players.size();
        inventory.setItem(1, dpsPlayer.isUseOnlyFriends() ? DPS.getItemPH("players_pages").getItem(new String[]{}, new String[]{String.valueOf(size)}) : DPS.getItemPH("friends_pages").getItem(new String[]{}, new String[]{String.valueOf(size)}));
        ItemStack item = prevPage.size() == 0 ? DPS.getItem("prev_page_off") : DPS.getItem("prev_page_on");
        inventory.setItem(5, item);
        inventory.setItem(6, item);
        item = nextPage.size() == 0 ? DPS.getItem("next_page_off") : DPS.getItem("next_page_on");
        inventory.setItem(7, item);
        inventory.setItem(8, item);
    }
    public static List<Player> getPlayerOnPage(int page, DPSPlayer dpsPlayer){
        List<Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers()),
                players = new ArrayList<>();

        int size = onlinePlayers.size();

        if (page < 0 || size < page * 45 + 1)
            return players;

        int max = (page + 1) * 45;
        for (int i = page * 45; i < max && i < size; i++)
            players.add(onlinePlayers.get(i));
        return players;
    }





    // Проверка для определения, какая меню открыта
    public static String getNameMenu(InventoryView view){
        String name = view.getTitle();
        int startIndex = name.lastIndexOf("{"),
                endIndex = name.lastIndexOf("}");

        return startIndex == -1 || endIndex == -1 ? null : name.substring(startIndex + 1, endIndex);
    }
    public static boolean isNotOurMenu(String titleMenu){
        return !DPS.getNameMenus().contains(titleMenu);
    }
}
