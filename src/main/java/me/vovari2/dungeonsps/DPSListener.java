package me.vovari2.dungeonsps;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class DPSListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        DPSParty party = DPS.getParty(player.getName());
        if (party == null)
            return;

        party.removePlayer(player);
    }

    @EventHandler
    public void onInventoryToggle(InventoryClickEvent event){
        Player player = (Player)event.getWhoClicked();
        player.sendMessage("Toggle event!");
        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryToggle(InventoryDragEvent event){
        Player player = (Player)event.getWhoClicked();
        player.sendMessage("Click event!");
        event.setCancelled(true);
    }

}
