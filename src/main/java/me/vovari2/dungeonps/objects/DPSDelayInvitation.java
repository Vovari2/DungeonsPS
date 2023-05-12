package me.vovari2.dungeonps.objects;

import me.vovari2.dungeonps.DPS;

public class DPSDelayInvitation {
    private final String playerNameFrom;
    private final String playerNameTo;
    private final int time;

    public DPSDelayInvitation(String playerNameFrom, String playerNameTo, int period){
        this.playerNameFrom = playerNameFrom;
        this.playerNameTo = playerNameTo;
        this.time = DPS.getTaskTicks().getSecondAfterPeriod(period);
    }

    public int getTime() {
        return time;
    }

    public static DPSDelayInvitation get(String playerNameFrom, String playerNameTo){
        for (DPSDelayInvitation function : DPS.getTaskTicks().delayInvitations)
            if(function.playerNameFrom.equals(playerNameFrom) && function.playerNameTo.equals(playerNameTo))
                return function;
        return null;
    } // Возвращает задержанную функцию по имени игрока и совпадающую имени функции
    public static void add(String playerNameFrom, String playerNameTo, int period){
        DPS.getTaskTicks().delayInvitations.add(new DPSDelayInvitation(playerNameFrom, playerNameTo, period));
    }
}
