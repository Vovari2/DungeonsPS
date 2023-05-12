package me.vovari2.dungeonps.listeners;

import me.vovari2.dungeonps.DPS;
import me.vovari2.dungeonps.DPSTaskTicks;
import me.vovari2.dungeonps.objects.*;
import me.vovari2.dungeonps.utils.MenuUtils;
import me.vovari2.dungeonps.utils.TextUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Set;

public class DPSListener implements Listener {
    // Обработка двух событий для выхода игрока из пати: Выход и Кик
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        DPSParty party = DPSParty.get(player.getName());
        if (party == null)
            return;

        DPSPlayer dpsPlayer = party.getPlayer(player.getName());
        if (dpsPlayer == null)
            return;

        party.removePlayer(dpsPlayer, true);
    }
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerKick(PlayerKickEvent event){
        Player player = event.getPlayer();
        DPSParty party = DPSParty.get(player.getName());
        if (party == null)
            return;

        DPSPlayer dpsPlayer = party.getPlayer(player.getName());
        if (dpsPlayer == null)
            return;

        party.removePlayer(dpsPlayer, true);
    }



    // Считывает движения игрока и выполняет команды при входе в один из данжей
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event){
        Player player = event.getPlayer();
        DPSDungeon dungeon = inEnterDungeon(player.getLocation());
        if (dungeon == null)
            return;

        String playerName = player.getName();
        if (DPSDelayFunction.get(playerName, "teleport_start_party") != null)
            return;

        TextUtils.launchExtinction(playerName);
        DPSParty.add(player, dungeon.getName());
        DPSDelayFunction.add(playerName, "teleport_start_party", "extinction");
    }
    // Возвращает данж, если игрок находится в точке его входа
    private DPSDungeon inEnterDungeon(Location playerLocation){
        for (DPSDungeon dungeon : DPS.getDungeons().values())
            if (dungeon.getEnterOut().getWorld() == playerLocation.getWorld() && dungeon.getEnterIn().distance(playerLocation) <= dungeon.getEnterInDistance())
                return dungeon;
        return null;
    }



    @EventHandler
    public void onMenuDrag(InventoryDragEvent event){
        String titleName = MenuUtils.getNameMenu(event.getView());
        if (MenuUtils.isNotOurMenu(titleName))
            return;
        if (titleName == null)
            return;

        // Проверка, чтобы игроки могли раскладывать предметы только в своем инветаре, но не в менюшке
        if (titleName.equals("party_settings") && containsValuesLess54(event.getRawSlots()) || titleName.equals("party_start") && containsValuesLess36(event.getRawSlots()))
            event.setCancelled(true);
    }
    public boolean containsValuesLess36(Set<Integer> set){
        for (Integer value : set)
            if (value < 54)
                return true;
        return false;
    } // Проверка содержится ли в сете чисел числа меньше 36
    public boolean containsValuesLess54(Set<Integer> set){
        for (Integer value : set)
            if (value < 54)
                return true;
        return false;
    } // Проверка содержится ли в сете чисел числа меньше 54


    // Обработка закрытие инвентаря party_start
    @EventHandler
    public void onPlayerClose(InventoryCloseEvent event){
        Player player = (Player) event.getPlayer();
        String titleName = MenuUtils.getNameMenu(event.getView());
        if (titleName == null || MenuUtils.isNotOurMenu(titleName))
            return;

        if (!titleName.equals("party_start"))
            return;

        String playerName = player.getName();
        if (DPSTaskTicks.containsLockableInventory(playerName)){
            DPSTaskTicks.removeLockableInventory(playerName);
            return;
        }
        else DPSTaskTicks.removeLockableInventory(playerName);

        DPSParty party = DPSParty.get(playerName);
        if (party == null)
            return;

        DPSPlayer dpsPlayer = party.getPlayer(playerName);
        if (dpsPlayer == null)
            return;

        party.removePlayer(dpsPlayer, false);
    }
}
