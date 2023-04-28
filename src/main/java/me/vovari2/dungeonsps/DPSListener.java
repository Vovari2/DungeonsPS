package me.vovari2.dungeonsps;

import me.vovari2.dungeonsps.utils.MenuUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Set;

public class DPSListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        DPSParty party = DPSParty.get(player.getName());
        if (party == null)
            return;

        party.removePlayer(player, true);
    }

    @EventHandler
    public void onMenuDrag(InventoryDragEvent event){
        if (!MenuUtils.isOurMenu(event.getView().getTitle()))
            return;

        if (containsValuesLess54(event.getRawSlots()))
            event.setCancelled(true);
    }
    public boolean containsValuesLess54(Set<Integer> set){
        for (Integer value : set)
            if (value < 54)
                return true;
        return false;
    }

    @EventHandler
    public void onMenuClick(InventoryClickEvent event){
        Player player = (Player)event.getWhoClicked();
        if (!MenuUtils.isOurMenu(event.getView().getTitle()))
            return;

        if (event.getRawSlot() < 54)
            event.setCancelled(true);

        String playerName = player.getName();
        DPSParty party = DPSParty.get(playerName);
        if (!event.getClick().isLeftClick() || party == null)
            return;

        DPSPlayer dpsPlayer = party.getPlayer(playerName);
        switch(event.getRawSlot()){
            case 0: player.closeInventory(); return;
            case 6: {
                if (!dpsPlayer.isLeader() || DPSTaskSeconds.waitNotices.containsKey(playerName))
                    return;

                DPSTaskSeconds.waitNotices.put(playerName, DPSTaskSeconds.getSecondAfterPeriod(300));
                dpsPlayer.updateMenuPlayer(party);
                for (Player targetPlayer : Bukkit.getOnlinePlayers())
                    targetPlayer.sendMessage(DPSLocale.replacePlaceHolder("command.party_all_notice", "%player%", playerName));
            } return;
            case 7: dpsPlayer.setChat(!dpsPlayer.isChat()); return;
            case 8: party.removePlayer(player, false); return;
            case 48: case 49: case 50: {
                if (dpsPlayer.isLeader() && party.allIsReady())
                    party.enterInDungeons();
                else dpsPlayer.setReady(!dpsPlayer.isReady());
            }
        }
    }

}
