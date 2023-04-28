package me.vovari2.dungeonsps;

import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class DPSTaskSeconds extends BukkitRunnable {

    private DPS plugin;
    DPSTaskSeconds(DPS plugin){
        this.plugin = plugin;
        waitNotices = new HashMap<>();
        waitBeforeRemove = new HashMap<>();
    }
    public static HashMap<String, Integer> waitNotices;
    public static HashMap<String, Integer> waitBeforeRemove;

    public static int seconds;

    @Override
    public void run() {
        seconds++;
        if (seconds > 3599)
            seconds = 0;

        for (Map.Entry<String, Integer> entry : waitNotices.entrySet()){
            String playerName = entry.getKey();
            if (entry.getValue() == seconds)
                waitNotices.remove(playerName);

            DPSParty party = DPSParty.get(playerName);
            if (party == null)
                continue;

            party.getPlayer(playerName).updateMenuPlayer(party);
        }
        for (Map.Entry<String, Integer> entry : waitBeforeRemove.entrySet()){
            if (entry.getValue() == seconds){
                String playerName = entry.getKey();
                waitNotices.remove(playerName);

                DPSParty party = DPSParty.get(playerName);
                if (party == null)
                    continue;

                party.fullRemovePlayer(party.getPlayer(playerName));
            }
        }
    }
    public static int getSecondAfterPeriod(int period){
        int time = seconds + period;
        return time > 3599 ? time - 3600 : time;
    } // Число, в какую секунду нужно вызвать событие из промежутка 0 - 3599
    public static int getSecondsToTime(int time){
        return time < seconds ? 3600 - seconds + time : time - seconds;
    } // Количество секунд, оставшееся до вызова события
}
