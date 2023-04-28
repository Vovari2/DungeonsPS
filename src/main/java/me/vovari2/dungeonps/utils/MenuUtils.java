package me.vovari2.dungeonps.utils;

import me.vovari2.dungeonps.*;
import me.vovari2.dungeonps.objects.DPSParty;
import me.vovari2.dungeonps.objects.DPSPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

public class MenuUtils {
    public static Inventory formPartyLeader(DPSParty party){
        List<DPSPlayer> listPlayers = party.getPlayers();
        DPSPlayer leader = listPlayers.get(0);
        Player player = leader.getPlayer();
        String playerName = player.getName();


        // Инициализация переменной инвентаря
        Inventory inventory = Bukkit.createInventory(player, 54, DPSLocale.replacePlaceHolders("menu.party_settings.name_leader",
                new String[]{
                        "%button_ready_1%",
                        "%button_ready_2%",
                        "%button_ready_3%",
                        "%button_ready_4%",
                        "%button_chat%",
                        "%button_func%",
                        "%button_notice%"
                } ,
                new String[]{
                        getButtonReady(listPlayers, 1),
                        getButtonReady(listPlayers, 2),
                        getButtonReady(listPlayers, 3),
                        getButtonReady(listPlayers, 4),
                        leader.isChat() ? DPSLocale.getLocaleString("placeholders.button_chat.use") : DPSLocale.getLocaleString("placeholders.button_chat.not_use"),
                        party.allIsReady() ? DPSLocale.getLocaleString("placeholders.button_func.start") : leader.isReady() ? DPSLocale.getLocaleString("placeholders.button_func.cancel") : DPSLocale.getLocaleString("placeholders.button_func.ready"),
                        leader.isWaitCoolDown() ? DPSLocale.getLocaleString("placeholders.button_notice.not_use") : DPSLocale.getLocaleString("placeholders.button_notice.use")
        }));

        // Инициализация предметов
        inventory.setItem(0, DPS.getItem("menu_close"));

        if (leader.isWaitCoolDown()){
            ItemStack item = new ItemStack(Material.FEATHER);
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setCustomModelData(10004);
            itemMeta.displayName(DPSLocale.getLocaleComponent("button.party_all_invitation_cooldown_name"));
            int waitSeconds = DPS.getTaskSeconds().getSecondsToTime(DPS.getTaskSeconds().delayFunctions.get(playerName).getTime());
            itemMeta.lore(DPSLocale.replacePlaceHolderList("button.party_all_invitation_cooldown_lore", "%time%", waitSeconds / 60 + ":" + (waitSeconds % 60 < 10 ? "0" : "") + waitSeconds % 60));
            item.setItemMeta(itemMeta);
            inventory.setItem(6, item);
        }
        else inventory.setItem(6, DPS.getItem("party_all_invitation"));
        inventory.setItem(7, leader.isChat() ? DPS.getItem("open_public_chat") : DPS.getItem("open_party_chat"));
        inventory.setItem(8, DPS.getItem("party_leave"));
        for (int i = 0; i < 4; i++)
            if (listPlayers.size() > i){
                DPSPlayer targetDPSPlayer = listPlayers.get(i);
                Player targetPlayer = targetDPSPlayer.getPlayer();
                ItemStack item = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
                skullMeta.setOwningPlayer(targetPlayer);
                skullMeta.displayName(targetDPSPlayer.isLeader() ? DPSLocale.replacePlaceHolder("button.party_name_leader", "%player%", targetPlayer.getName()) : DPSLocale.replacePlaceHolder("button.party_name_player", "%player%", targetPlayer.getName()));
                skullMeta.lore(targetDPSPlayer.isLeader() ? DPSLocale.getLocaleListComponent("button.party_lore_player_for_leader") : DPSLocale.getLocaleListComponent("button.party_lore_player_for_player"));
                item.setItemMeta(skullMeta);
                inventory.setItem(i * 2 + 19, item);

                inventory.setItem(i * 2 + 28, targetDPSPlayer.isReady() ? DPS.getItem("player_ready") : DPS.getItem("player_not_ready"));
            }
            else {
                inventory.setItem(i * 2 + 19, DPS.getItem("party_invite"));
                inventory.setItem(i * 2 + 28,  DPS.getItem("player_undefined"));
            }
        ItemStack funcItem = party.allIsReady() ? DPS.getItem("func_start") : leader.isReady() ? DPS.getItem("func_cancel") : DPS.getItem("func_ready");
        inventory.setItem(48, funcItem);
        inventory.setItem(49, funcItem);
        inventory.setItem(50, funcItem);
        return inventory;
    }
    private static String getButtonReady(List<DPSPlayer> listPlayers, int number){
        return listPlayers.size() >= number && listPlayers.get(number-1).isReady() ? DPSLocale.getLocaleString("placeholders.button_ready_" + number + ".ready") : DPSLocale.getLocaleString("placeholders.button_ready_" + number + ".not_ready");
    }

    // Проверка для определения, какая меню открыта
    public static String getNameMenu(String name){
        int startIndex = name.lastIndexOf("{"),
                endIndex = name.lastIndexOf("}");

        return startIndex == -1 || endIndex == -1 ? null : name.substring(startIndex + 1, endIndex);
    }
    public static boolean isOurMenu(String titleMenu){
        return !DPS.getNameMenus().contains(getNameMenu(titleMenu));
    }
}
