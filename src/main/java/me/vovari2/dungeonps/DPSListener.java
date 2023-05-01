package me.vovari2.dungeonps;

import me.vovari2.dungeonps.objects.DPSDelayFunction;
import me.vovari2.dungeonps.objects.DPSDungeon;
import me.vovari2.dungeonps.objects.DPSParty;
import me.vovari2.dungeonps.objects.DPSPlayer;
import me.vovari2.dungeonps.utils.MenuUtils;
import me.vovari2.dungeonps.utils.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerMoveEvent;
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
    public void onPlayerMove(PlayerMoveEvent event){
        Player player = event.getPlayer();
        String playerName = player.getName();
        DPSDungeon dungeon = inEnterDungeon(player.getLocation());
        if (dungeon == null)
            return;

        if (DPSDelayFunction.get(playerName, "teleport_start_party") != null)
            return;

        TextUtils.launchCommand(DPS.getDPSCommand("extinction").replaceAll("%player%", player.getName()));
        DPSParty.add(player, dungeon.getName());
        DPSDelayFunction.add(playerName, "teleport_start_party", 2);
    }

    private DPSDungeon inEnterDungeon(Location playerLocation){
        for (DPSDungeon dungeon : DPS.getDungeons().values())
            if (dungeon.getEnterOut().getWorld() == playerLocation.getWorld() && dungeon.getEnterIn().distance(playerLocation) <= dungeon.getEnterInDistance())
                return dungeon;
        return null;
    }

    @EventHandler
    public void onMenuDrag(InventoryDragEvent event){
        String titleName = MenuUtils.getNameMenu(event.getView().getTitle());
        if (MenuUtils.isNotOurMenu(titleName))
            return;
        if (titleName == null)
            return;

        if (titleName.equals("party_settings") && containsValuesLess54(event.getRawSlots()) || titleName.equals("party_start") && containsValuesLess36(event.getRawSlots()))
            event.setCancelled(true);
    }
    public boolean containsValuesLess36(Set<Integer> set){
        for (Integer value : set)
            if (value < 54)
                return true;
        return false;
    }
    public boolean containsValuesLess54(Set<Integer> set){
        for (Integer value : set)
            if (value < 54)
                return true;
        return false;
    }

    @EventHandler
    public void onMenuClick(InventoryClickEvent event){
        String titleName = MenuUtils.getNameMenu(event.getView().getTitle());
        if (titleName == null || MenuUtils.isNotOurMenu(titleName))
            return;

        Player player = (Player)event.getWhoClicked();
        String playerName = player.getName();

        switch(titleName){
            case "party_start": {
                if (event.getRawSlot() < 36)
                    event.setCancelled(true);

                if (!event.getClick().isLeftClick())
                    return;

                switch(event.getRawSlot()){
                    case 1: case 2: case 3: case 10: case 11: case 12: {
                        DPSParty party = DPSParty.get(playerName);
                        if (party == null)
                            return;
                        player.closeInventory();
                        TextUtils.launchCommand(DPS.getDPSCommand("extinction").replaceAll("%player%", player.getName()));
                        DPSDelayFunction.add(playerName, "start_dungeon", 2);
                    } return;
                    case 5: case 6: case 7: case 14: case 15: case 16: {
                        DPSParty party = DPSParty.get(playerName);
                        if (party == null)
                            return;
                        player.closeInventory();
                        TextUtils.launchCommand(DPS.getDPSCommand("extinction").replaceAll("%player%", player.getName()));
                        DPSDelayFunction.add(playerName, "teleport_settings_party", 2);
                    } return;
                    case 30: case 31: case 32: {
                        player.closeInventory();
                        TextUtils.launchCommand(DPS.getDPSCommand("extinction").replaceAll("%player%", player.getName()));
                        DPSDelayFunction.add(playerName, "wait_after_remove_player", 2);
                    } return;
                }
            } return;
            case "party_settings": {
                if (event.getRawSlot() < 54)
                    event.setCancelled(true);

                DPSParty party = DPSParty.get(playerName);
                if (!event.getClick().isLeftClick() || party == null)
                    return;

                DPSPlayer dpsPlayer = party.getPlayer(playerName);
                switch(event.getRawSlot()){
                    case 0: player.closeInventory(); break;
                    case 6: {
                        if (!dpsPlayer.isLeader() || dpsPlayer.isWaitCoolDown())
                            return;

                        DPS.getTaskSeconds().waitCooldownNotice.put(playerName, DPS.getTaskSeconds().getSecondAfterPeriod(300));
                        DPSDelayFunction.add(playerName, "wait_cooldown_notice", 300);
                        dpsPlayer.setWaitCoolDown(true);
                        dpsPlayer.updateMenuPlayer(party);
                        for (Player targetPlayer : Bukkit.getOnlinePlayers())
                            targetPlayer.sendMessage(DPSLocale.replacePlaceHolder("command.party_all_notice", "%player%", playerName));
                    } break;
                    case 7: dpsPlayer.setChat(!dpsPlayer.isChat()); break;
                    case 8: party.removePlayer(player, false); break;
                    case 48: case 49: case 50: {
                        if (dpsPlayer.isLeader() && party.allIsReady())
                            party.enterInDungeons();
                        else dpsPlayer.setReady(!dpsPlayer.isReady());
                    } break;
                }
            }
        }
    }

//    @EventHandler
//    public void onPlayerClose(InventoryCloseEvent event){
//        Player player = (Player) event.getPlayer();
//        String titleName = MenuUtils.getNameMenu(event.getView().getTitle());
//        if (titleName == null || MenuUtils.isNotOurMenu(titleName))
//            return;
//
//        if (!titleName.equals("party_start"))
//            return;
//
//        MenuUtils.openPartyStart(player);
//    }

}
