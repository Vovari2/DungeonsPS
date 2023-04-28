package me.vovari2.dungeonps.objects;

import me.vovari2.dungeonps.DPS;

public class DPSDelayFunction {
    private final String functionName;
    private final int time;
    private final boolean afterPeriod;

    public DPSDelayFunction(String functionName, int period, boolean afterPeriod){
        this.functionName = functionName;
        this.time = DPS.getTaskSeconds().getSecondAfterPeriod(period);
        this.afterPeriod = afterPeriod;
    }

    public String getFunctionName() {
        return functionName;
    }
    public int getTime(){
        return time;
    }
    public boolean equalsTime(){
        return this.time == DPS.getTaskSeconds().seconds;
    }
    public boolean isAfterPeriod(){
        return afterPeriod;
    }

    public static void launchFunction(String playerName, String functionName){
        DPSParty party = DPSParty.get(playerName);
        if (party == null)
            return;
        DPSPlayer dpsPlayer = party.getPlayer(playerName);

        switch(functionName){
            case "wait_cooldown_notice": dpsPlayer.setWaitCoolDown(false); return;
            case "wait_after_remove_player": party.fullRemovePlayer(dpsPlayer); return;
        }
    }
}
