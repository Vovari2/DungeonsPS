package me.vovari2.dungeonps.listeners;

import me.vovari2.dungeonps.DPS;
import me.vovari2.dungeonps.DPSLocale;
import me.vovari2.dungeonps.DPSTaskTicks;
import me.vovari2.dungeonps.objects.DPSDelayFunction;
import me.vovari2.dungeonps.objects.DPSDelayInvitation;
import me.vovari2.dungeonps.objects.DPSParty;
import me.vovari2.dungeonps.objects.DPSPlayer;
import me.vovari2.dungeonps.utils.MenuUtils;
import me.vovari2.dungeonps.utils.SoundUtils;
import me.vovari2.dungeonps.utils.TextUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

public class MenuClick implements Listener {

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
                else return;

                if (!event.getClick().isLeftClick())
                    return;

                DPSParty party = DPSParty.get(playerName);
                if (party == null)
                    return;

                switch(event.getRawSlot()){
                    // Кнопка одиночного режима
                    case 9: case 10: case 11: case 12: case 18: case 19: case 20: case 21: case 27: case 28: case 29: case 30:{
                        TextUtils.launchExtinction(playerName);
                        DPSDelayFunction.add(playerName, "start_dungeon", "extinction");
                        DPSTaskTicks.addLockableInventory(playerName, true);
                        player.closeInventory();
                        SoundUtils.play(player, "transition_click");
                    } return;
                    // Кнопка кооперативного режима
                    case 14: case 15: case 16: case 17: case 23: case 24: case 25: case 26: case 32: case 33: case 34: case 35:{
                        TextUtils.launchExtinction(playerName);
                        DPSDelayFunction.add(playerName, "teleport_settings_party", "extinction");
                        DPSTaskTicks.addLockableInventory(playerName, true);
                        player.closeInventory();
                        SoundUtils.play(player, "transition_click");
                    } return;
                    // Кнопка возвращения назад
                    case 48: case 49: case 50: {
                        DPSTaskTicks.addLockableInventory(playerName, false);
                        player.closeInventory();
                    } return;
                }
            } return;
            case "party_settings": {
                if (event.getRawSlot() < 54)
                    event.setCancelled(true);
                else return;

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
                        SoundUtils.play(player, "menu_close");
                    } break;
                    // Кнопка отправки всем уведомления о приглашении
                    case 6: {
                        if (!event.isLeftClick() || !dpsPlayer.isLeader() || dpsPlayer.isWaitCoolDown())
                            return;

                        DPS.getTaskTicks().waitCooldownNotice.put(playerName, DPS.getTaskTicks().getSecondAfterPeriod(DPS.getPeriod("notice_invitation")));
                        DPSDelayFunction.add(playerName, "wait_cooldown_notice", "notice_invitation");
                        dpsPlayer.setWaitCoolDown(true);
                        dpsPlayer.updatePartySettings(party);
                        Component messageNotice = DPSLocale.replacePlaceHolders("command.party_all_notice", new String[]{"%player%", "%dungeon%"}, new String[]{playerName, DPS.getDungeon(party.getNameDungeon()).getNameTitle()});
                        for (Player targetPlayer : Bukkit.getOnlinePlayers())
                            targetPlayer.sendMessage(messageNotice);
                        SoundUtils.play(player, "button_click");
                    } break;
                    // Кнопка переключения чата пати и публичного чата
                    case 7: {
                        if (!event.isLeftClick())
                            return;

                        dpsPlayer.setChat(!dpsPlayer.isChat());
                        SoundUtils.play(player, "button_click");
                    } break;
                    // Кнопка выхода из пати
                    case 8: {
                        if (!event.isLeftClick())
                            return;

                        party.removePlayer(dpsPlayer, false);
                    } break;
                    // Ячейки с игроками
                    case 21: clickOnPlayerSlots(event, party, dpsPlayer, 1); break;
                    case 23: clickOnPlayerSlots(event, party, dpsPlayer, 2); break;
                    case 25: clickOnPlayerSlots(event, party, dpsPlayer, 3); break;
                    // Кнопка готов, отмена и старт
                    case 48: case 49: case 50: {
                        if (dpsPlayer.isLeader() && party.allIsReady()){
                            TextUtils.launchExtinction(playerName);
                            DPSDelayFunction.add(playerName, "start_dungeon", "extinction");
                            DPSTaskTicks.addLockableInventory(playerName, true);
                            player.closeInventory();
                        }
                        else dpsPlayer.setReady(!dpsPlayer.isReady());
                        SoundUtils.play(player, "transition_click");
                    } break;
                }
            } return;
            case "party_players": {
                if (event.getRawSlot() < 54)
                    event.setCancelled(true);
                else return;

                DPSParty party = DPSParty.get(playerName);
                if (!event.getClick().isLeftClick() || party == null)
                    return;

                DPSPlayer dpsPlayer = party.getPlayer(playerName);
                switch(event.getRawSlot()){
                    case 0: {
                        MenuUtils.openPartySettingsLeader(party, dpsPlayer);
                        SoundUtils.play(player, "transition_click");
                    } break;
                    // Кнопка переключения между всеми игроками и друзьями
                    case 1: {
                        SoundUtils.play(player, "button_click");
                    } break;
                    // Кнопка перехода на предыдущую страницу
                    case 5: case 6: {
                        SoundUtils.play(player, "button_click");
                    } break;
                    // Кнопка перехода на следующую страницу
                    case 7: case 8: {
                        SoundUtils.play(player, "button_click");
                    } break;
                    // Все ячейки с игроками
                    default: {
                        if (event.getRawSlot() < 9 || event.getRawSlot() > 53)
                            return;

                        List<Player> players = MenuUtils.getPlayerOnPage(dpsPlayer.getPartyPlayersPage(), dpsPlayer);
                        if (players.size() < event.getRawSlot() - 8)
                            return;

                        Player targetPlayer = players.get(event.getRawSlot() - 9);
                        DPSDelayInvitation delayInvitation = DPSDelayInvitation.get(playerName, targetPlayer.getName());
                        if (delayInvitation != null) {
                            player.sendMessage(DPSLocale.replacePlaceHolder("command.party_already_invite", "%time%", String.valueOf(DPS.getTaskTicks().getSecondsToTime(delayInvitation.getTime()) / 20)));
                            return;
                        }

                        targetPlayer.sendMessage(DPSLocale.replacePlaceHolders("command.party_invitation", new String[]{"%player%", "%dungeon%"}, new String[]{playerName, DPS.getDungeon(party.getNameDungeon()).getNameTitle()}));
                        MenuUtils.openPartySettingsLeader(party, dpsPlayer);
                        DPSDelayInvitation.add(playerName, targetPlayer.getName(), DPS.getPeriod("send_invite"));
                        SoundUtils.play(player, "button_click");
                    }
                }
            }
        }
    }
    // Отдельный метод для приглашения игрока
    public void clickOnPlayerSlots(InventoryClickEvent event, DPSParty party, DPSPlayer dpsPlayer, int index){
        if (!dpsPlayer.isLeader())
            return;

        if (party.getPlayers().size() < index + 1){
            if (!event.isLeftClick())
                return;

            TextUtils.sendInfoMessage("LeftClickOnPlayerSlots: " + dpsPlayer);
            dpsPlayer.setUseOnlyFriends(false);
            dpsPlayer.setPartyPlayersPage(0);
            MenuUtils.openPartyPlayers(dpsPlayer);
            SoundUtils.play(dpsPlayer.getPlayer(), "button_click");
        }
        else {
            if (!event.isShiftClick())
                return;
            DPSPlayer targetPlayer = party.getPlayers().get(index);
            TextUtils.sendInfoMessage("ShiftClickOnPlayerSlots: " + targetPlayer);
            if (event.isLeftClick())
                party.editLeader(targetPlayer);
            else if (event.isRightClick())
                party.removePlayer(targetPlayer, false);
            SoundUtils.play(dpsPlayer.getPlayer(), "button_click");
        }
    }
}
