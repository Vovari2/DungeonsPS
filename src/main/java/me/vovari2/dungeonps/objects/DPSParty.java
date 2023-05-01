package me.vovari2.dungeonps.objects;

import com.alessiodp.parties.api.interfaces.Party;
import com.alessiodp.parties.api.interfaces.PartyPlayer;
import me.vovari2.dungeonps.DPS;
import me.vovari2.dungeonps.DPSLocale;
import me.vovari2.dungeonps.utils.TextUtils;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DPSParty{
    private Party party;
    private final List<DPSPlayer> players;

    private boolean inDungeon;

    public DPSParty(Party party, Player leader){
        this.party = party;
        players = new ArrayList<>();

        inDungeon = false;
        addPlayer(leader, true);
    }

    public Party getParty(){
        return party;
    }
    public boolean getInDungeon(){
        return inDungeon;
    }
    public void setInDungeon(boolean inDungeon){
        this.inDungeon = inDungeon;
    }

    public List<DPSPlayer> getPlayers(){
        return players;
    }
    public boolean containsPlayer(String playerName){
        for(DPSPlayer player : players)
            if(player.getPlayer().getName().equals(playerName))
                return true;
        return false;
    }
    public boolean allIsReady(){
        for (DPSPlayer player : players)
            if (!player.isReady())
                return false;
        return true;
    }

    public DPSPlayer getPlayer(String playerName){
        for(DPSPlayer player : players)
            if (player.getPlayer().getName().equals(playerName))
                return player;
        return null;
    }
    public void addPlayer(Player player, boolean isLeader){
        if (players.size() + 1 > 4){
            player.sendMessage(DPSLocale.getLocaleComponent("party_is_full"));
            return;
        }

        PartyPlayer partyPlayer = DPS.getPartiesAPI().getPartyPlayer(player.getUniqueId());
        if (partyPlayer == null)
            return;

        party.addMember(partyPlayer);
        players.add(new DPSPlayer(player, isLeader));

        updateMenuAllPlayer();
    }
    public void removePlayer(Player player, boolean isQuit){
        DPSPlayer dpsPlayer = getPlayer(player.getName());
        if (dpsPlayer == null)
            return;

        if (isQuit){
            fullRemovePlayer(dpsPlayer);
            return;
        }

        player.closeInventory();
        TextUtils.launchCommand(DPS.getDPSCommand("extinction").replaceAll("%player%", player.getName()));
        DPSDelayFunction.add(player.getName(), "wait_after_remove_player", 2);
    }

    public void fullRemovePlayer(DPSPlayer dpsPlayer){
        Player player = dpsPlayer.getPlayer();
        if (dpsPlayer.isLeader()){
            if (players.size() > 1){
                players.remove(0);
                DPSPlayer newLeader = players.get(0);
                DPS.getPartiesAPI().createParty(newLeader.getPlayer().getName(), newLeader.getPartyPlayer());
                party = DPS.getPartiesAPI().getParty(newLeader.getPlayer().getName());
                if (party == null)
                    return;
                for(DPSPlayer targetPlayer : players)
                    party.addMember(targetPlayer.getPartyPlayer());
            }
            else {
                party.delete();
                DPS.getParties().remove(player.getName());
            }
        }
        else {
            party.removeMember(dpsPlayer.getPartyPlayer());
            players.removeIf(targetPlayer -> targetPlayer.getPlayer().equals(player));
        }

        if (inDungeon) // ИЗМЕНИТЬ (ДОБАВИТЬ ВКЛЮЧЕНИЕ И ВЫКЛЮЧЕНИЕ ПРАВА НА ИСПОЛЬЗОВАНИЕ КОМАНДЫ /md leave) ЧЕРЕЗ LUCK PERMS
            player.performCommand(DPS.getDPSCommand("leave"));
        player.teleport(DPS.getLocation("enter_dungeon"));
        updateMenuAllPlayer();
    }
    public void enterInDungeons(){
        setInDungeon(true);
        TextUtils.launchCommand(DPS.getDPSCommand("play").replaceAll("%player%", players.get(0).getPlayer().getName()));
    }
    public void updateMenuAllPlayer(){
        for (DPSPlayer dpsPlayer : players)
            dpsPlayer.updateMenuPlayer(this);
    }

    public static DPSParty get(String playerName){
        DPSParty party = DPS.getParties().get(playerName);
        if (party != null)
            return party;

        Iterator<DPSParty> iterator = DPS.getParties().values().iterator();
        while(iterator.hasNext()){
            DPSParty targetParty = iterator.next();
            if (targetParty.containsPlayer(playerName))
                return targetParty;
        }
        return null;
    }
    public static DPSParty add(Player player){
        String playerName = player.getName();
        if (DPSParty.get(playerName) != null)
            return null;

        DPS.getPartiesAPI().createParty(playerName, DPS.getPartiesAPI().getPartyPlayer(player.getUniqueId()));

        DPSParty party = new DPSParty(DPS.getPartiesAPI().getParty(playerName), player);
        DPS.getParties().put(playerName, party);

        return party;
    }
}
