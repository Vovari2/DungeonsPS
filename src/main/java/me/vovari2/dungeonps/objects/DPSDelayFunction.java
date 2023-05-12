package me.vovari2.dungeonps.objects;

import me.vovari2.dungeonps.DPS;
import me.vovari2.dungeonps.utils.MenuUtils;
import me.vovari2.dungeonps.utils.TextUtils;
import org.bukkit.entity.Player;

public class DPSDelayFunction {
    private final String playerName;
    private final String functionName;
    private final int time;

    public DPSDelayFunction(String playerName, String functionName, int period){
        this.playerName = playerName;
        this.functionName = functionName;
        this.time = DPS.getTaskTicks().getSecondAfterPeriod(period);
    }
    public int getTime() {
        return time;
    }

    public void launchFunction(){
        DPSParty party = DPSParty.get(playerName);
        if (party == null)
            return;
        DPSPlayer dpsPlayer = party.getPlayer(playerName);
        if (dpsPlayer == null)
            return;

        switch(functionName){
            case "wait_after_remove_player":
                party.fullRemovePlayer(dpsPlayer); return;
            case "teleport_start_party": {
                Player player = dpsPlayer.getPlayer();
                if (player == null)
                    return;
                player.teleport(DPS.getDungeon(party.getNameDungeon()).getPartyStartRoom());
                MenuUtils.openPartyStart(player);
            } return;
            case "teleport_settings_party":{
                Player player = dpsPlayer.getPlayer();
                if (player == null)
                    return;
                player.teleport(DPS.getDungeon(party.getNameDungeon()).getPartySettingsRoom());
                if (dpsPlayer.isLeader())
                    MenuUtils.openPartySettingsLeader(party, dpsPlayer);
                else MenuUtils.openPartySettingsPlayer(party, dpsPlayer);
            } return;
            case "wait_cooldown_notice": {
                dpsPlayer.setWaitCoolDown(false);
                dpsPlayer.updatePartySettings(party);
            } return;
            case "start_dungeon": {
                Player player = dpsPlayer.getPlayer();
                if (player == null)
                    return;
                party.setInDungeon(true);
                TextUtils.launchCommand(DPS.getDPSCommand("play").replaceAll("%player%", party.getPlayers().get(0).getPlayer().getName()));
            }
        }
    }

    public static DPSDelayFunction get(String playerName, String functionName){
        for (DPSDelayFunction function : DPS.getTaskTicks().delayFunctions)
            if(function.playerName.equals(playerName) && function.functionName.equals(functionName))
                return function;
        return null;
    } // Возвращает задержанную функцию по имени игрока и совпадающую имени функции
    public static void add(String playerName, String functionName, int time){
        DPS.getTaskTicks().delayFunctions.add(new DPSDelayFunction(playerName, functionName, time));
    }

    public static void add(String playerName, String functionName, String periodName){
        DPS.getTaskTicks().delayFunctions.add(new DPSDelayFunction(playerName, functionName, DPS.getPeriod(periodName)));
    }
}
