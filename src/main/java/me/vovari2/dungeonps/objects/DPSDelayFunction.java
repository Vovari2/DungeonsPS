package me.vovari2.dungeonps.objects;

import me.vovari2.dungeonps.DPS;

public class DPSDelayFunction {
    private final String playerName;
    private final String functionName;
    private final int time;

    public DPSDelayFunction(String playerName, String functionName, int period){
        this.playerName = playerName;
        this.functionName = functionName;
        this.time = DPS.getTaskSeconds().getSecondAfterPeriod(period);
    }

    public boolean equalsTime(){
        return this.time == DPS.getTaskSeconds().seconds;
    }
    public boolean equals(String playerName, String functionName){
        return this.playerName.equals(playerName) && this.functionName.equals(functionName);
    }

    public void launchFunction(){
        DPSParty party = DPSParty.get(playerName);
        if (party == null)
            return;
        DPSPlayer dpsPlayer = party.getPlayer(playerName);

        switch(functionName){
            case "wait_cooldown_notice": {
                dpsPlayer.setWaitCoolDown(false);
                dpsPlayer.updateMenuPlayer(party);
            } return;
            case "wait_after_remove_player":
                party.fullRemovePlayer(dpsPlayer);
        }
    }

    public static DPSDelayFunction get(String playerName, String functionName){
        for (DPSDelayFunction function : DPS.getTaskSeconds().delayFunctions)
            if(function.equals(playerName, functionName))
                return function;
        return null;
    }
    public static void add(String playerName, String functionName, int time){
        DPS.getTaskSeconds().delayFunctions.add(new DPSDelayFunction(playerName, functionName, time));
    }
}
