package me.vovari2.dungeonps;

import me.vovari2.dungeonps.objects.*;
import me.vovari2.dungeonps.utils.MenuUtils;
import me.vovari2.dungeonps.utils.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
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
        DPSDelayFunction.add(playerName, "teleport_start_party", 2);
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

    @EventHandler
    public void onMenuClick(InventoryClickEvent event){
        String titleName = MenuUtils.getNameMenu(event.getView());
        if (titleName == null || MenuUtils.isNotOurMenu(titleName))
            return;

        if(event.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY))
            event.setCancelled(true);

        Player player = (Player)event.getWhoClicked();
        String playerName = player.getName();

        switch(titleName){
            case "party_start": {
                if (event.getRawSlot() < 36)
                    event.setCancelled(true);

                if (!event.getClick().isLeftClick())
                    return;

                DPSParty party = DPSParty.get(playerName);
                if (party == null)
                    return;

                switch(event.getRawSlot()){
                    // Кнопка одиночного режима
                    case 1: case 2: case 3: case 10: case 11: case 12: {
                        TextUtils.launchExtinction(playerName);
                        DPSDelayFunction.add(playerName, "start_dungeon", 2);
                        DPSTaskSeconds.addLockableInventory(playerName, true);
                        player.closeInventory();
                    } return;
                    // Кнопка кооперативного режима
                    case 5: case 6: case 7: case 14: case 15: case 16: {
                        TextUtils.launchExtinction(playerName);
                        DPSDelayFunction.add(playerName, "teleport_settings_party", 2);
                        DPSTaskSeconds.addLockableInventory(playerName, true);
                        player.closeInventory();
                    } return;
                    // Кнопка возвращения назад
                    case 30: case 31: case 32: {
                        player.closeInventory();
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
                    // Кнопка закрытия менюшки
                    case 0: {
                        if (!event.isLeftClick())
                            return;

                        player.closeInventory();
                    } break;
                    // Кнопка отправки всем уведомления о приглашении
                    case 6: {
                        if (!event.isLeftClick() || !dpsPlayer.isLeader() || dpsPlayer.isWaitCoolDown())
                            return;

                        DPS.getTaskSeconds().waitCooldownNotice.put(playerName, DPS.getTaskSeconds().getSecondAfterPeriod(30));
                        DPSDelayFunction.add(playerName, "wait_cooldown_notice", 30);
                        dpsPlayer.setWaitCoolDown(true);
                        dpsPlayer.updatePartySettings(party);
                        for (Player targetPlayer : Bukkit.getOnlinePlayers())
                            targetPlayer.sendMessage(DPSLocale.replacePlaceHolder("command.party_all_notice", "%player%", playerName));
                    } break;
                    // Кнопка переключения чата пати и публичного чата
                    case 7: {
                        if (!event.isLeftClick())
                            return;

                        dpsPlayer.setChat(!dpsPlayer.isChat());
                    } break;
                    // Кнопка выхода из пати
                    case 8: {
                        if (!event.isLeftClick())
                            return;

                        party.removePlayer(dpsPlayer, false);
                    } break;
                    // Ячейки с игроками
                    case 30: clickOnPlayerSlots(event, party, dpsPlayer, 1); break;
                    case 32: clickOnPlayerSlots(event, party, dpsPlayer, 2); break;
                    case 34: clickOnPlayerSlots(event, party, dpsPlayer, 3); break;
                    // Кнопка готов, отмена и старт
                    case 48: case 49: case 50: {
                        if (dpsPlayer.isLeader() && party.allIsReady()){
                            TextUtils.launchExtinction(playerName);
                            DPSDelayFunction.add(playerName, "start_dungeon", 2);
                            DPSTaskSeconds.addLockableInventory(playerName, true);
                            player.closeInventory();
                        }
                        else dpsPlayer.setReady(!dpsPlayer.isReady());
                    } break;
                }
            } return;
            case "party_players": {
                if (event.getRawSlot() < 54)
                    event.setCancelled(true);
                
                DPSParty party = DPSParty.get(playerName);
                if (!event.getClick().isLeftClick() || party == null)
                    return;

                DPSPlayer dpsPlayer = party.getPlayer(playerName);
                switch(event.getRawSlot()){
                    case 0: {
                        MenuUtils.openPartySettingsLeader(party, dpsPlayer);
                    } break;
                    case 1: // Будет кнопка переключения между всеми игроками и друзьями
                        break;

                    case 2: case 3: case 4: return;
                    // Кнопка перехода на предыдущую страницу
                    case 5: case 6: {

                    } break;
                    // Кнопка перехода на следующую страницу
                    case 7: case 8: {

                    } break;
                    // Все ячейки с игроками
                    default:
                }
            }
        }
    }

    public void clickOnPlayerSlots(InventoryClickEvent event, DPSParty party, DPSPlayer dpsPlayer, int index){
        if (!dpsPlayer.isLeader())
            return;

        if (party.getPlayers().size() < index + 1){
            if (!event.isLeftClick())
                return;

            MenuUtils.openPartyPlayers(dpsPlayer);
        }
        else {
            if (!event.isShiftClick())
                return;
            DPSPlayer targetPlayer = party.getPlayers().get(index);
            if (event.isLeftClick())
                party.editLeader(targetPlayer);
            else if (event.isRightClick())
                party.removePlayer(targetPlayer, false);
        }
    }

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
        if (DPSTaskSeconds.containsLockableInventory(playerName)){
            DPSTaskSeconds.removeLockableInventory(playerName);
            return;
        }
        else DPSTaskSeconds.removeLockableInventory(playerName);

        DPSParty party = DPSParty.get(playerName);
        if (party == null)
            return;

        DPSPlayer dpsPlayer = party.getPlayer(playerName);
        if (dpsPlayer == null)
            return;

        party.removePlayer(dpsPlayer, false);
    }
}
