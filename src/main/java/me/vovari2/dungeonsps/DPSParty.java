package me.vovari2.dungeonsps;

import com.alessiodp.parties.api.interfaces.Party;
import com.alessiodp.parties.api.interfaces.PartyPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
    public boolean allIsReady(){
        for (DPSPlayer player : players)
            if (!player.isReady())
                return false;
        return true;
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
    }
    public void removePlayer(Player player){
        PartyPlayer partyPlayer = DPS.getPartiesAPI().getPartyPlayer(player.getUniqueId());
        if (partyPlayer == null)
            return;

        if (party.getLeader() == player.getUniqueId()){
            DPSPlayer newLeader = players.get(1);
            party.delete();

            DPS.getPartiesAPI().createParty(newLeader.getPlayer().getName(), newLeader.getPartyPlayer());
            party = DPS.getPartiesAPI().getParty(newLeader.getPlayer().getName());
            if (party == null)
                return;

            players.remove(0);
            for(DPSPlayer targetPlayer : players)
                party.addMember(targetPlayer.getPartyPlayer());

            if (inDungeon)
                player.performCommand("md leave");
            player.teleport(DPS.getLocation("enter_dungeon"));
            return;
        }

        party.removeMember(partyPlayer);
        players.removeIf(targetPlayer -> targetPlayer.getPlayer().equals(player));
        player.teleport(DPS.getLocation("enter_dungeon"));
    }
    public void updateMenuPlayers(){
        for (UUID id : party.getMembers()){
            Player player = Bukkit.getPlayer(id);
            if (player == null)
                continue;
            player.getOpenInventory().getPlayer();
        }
    }
}
