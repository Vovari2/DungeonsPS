package me.vovari2.dungeonsps.utils;

import me.vovari2.dungeonsps.DPS;
import me.vovari2.dungeonsps.DPSLocale;
import me.vovari2.dungeonsps.DPSParty;
import me.vovari2.dungeonsps.DPSPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

public class MenuUtils {
    public static Inventory formPartyLeader(DPSParty party){
        List<DPSPlayer> listPlayers = party.getPlayers();
        DPSPlayer leader = listPlayers.get(0);
        Player player = listPlayers.get(0).getPlayer();

        // Инициализация переменной инвентаря
        Inventory inventory = Bukkit.createInventory(player, 54, DPSLocale.replacePlaceHolders("menu.party_settings.name_leader",
                new String[]{
                        "%button_ready_1%",
                        "%button_ready_2%",
                        "%button_ready_3%",
                        "%button_ready_4%",
                        "%button_chat%",
                        "%button_func%"
                } ,
                new String[]{
                        getButtonReady(listPlayers, 1),
                        getButtonReady(listPlayers, 2),
                        getButtonReady(listPlayers, 3),
                        getButtonReady(listPlayers, 4),
                        leader.isChat() ? DPSLocale.getLocaleString("placeholders.button_chat.use") : DPSLocale.getLocaleString("placeholders.button_chat.not_use"),
                        party.allIsReady() ? DPSLocale.getLocaleString("placeholders.button_func.start") : leader.isReady() ? DPSLocale.getLocaleString("placeholders.button_func.cancel") : DPSLocale.getLocaleString("placeholders.button_func.ready")
        }));

        // Инициализация предметов
        inventory.setItem(0, DPS.getItem("menu_close"));
        inventory.setItem(6, DPS.getItem("party_all_invitation"));
        inventory.setItem(7, leader.isChat() ? DPS.getItem("open_public_chat") : DPS.getItem("open_party_chat"));
        inventory.setItem(8, DPS.getItem("party_leave"));
        for (int i = 0; i < 4; i++)
            if (listPlayers.size() > i){
                DPSPlayer targetPlayer = listPlayers.get(i);
                ItemStack item = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
                skullMeta.setOwningPlayer(targetPlayer.getPlayer());
                skullMeta.displayName(targetPlayer.isLeader() ? DPSLocale.replacePlaceHolder("button.party_player_for_leader", "%player%", targetPlayer.getPlayer().getName()) : DPSLocale.replacePlaceHolder("button.party_player_for_player", "%player%", targetPlayer.getPlayer().getName()));
                if (!targetPlayer.isLeader())
                    skullMeta.lore(DPSLocale.getLocaleListComponent("button.party_player_for_leader_lore"));
                item.setItemMeta(skullMeta);
                inventory.setItem(i * 2 + 19, item);

                inventory.setItem(i * 2 + 28, targetPlayer.isReady() ? DPS.getItem("player_ready") : DPS.getItem("player_not_ready"));
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
}
