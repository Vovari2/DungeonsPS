package me.vovari2.dungeonsps;

import com.alessiodp.parties.api.interfaces.PartyPlayer;
import me.vovari2.dungeonsps.utils.MenuUtils;
import org.bukkit.entity.Player;

public class DPSPlayer {

    private final boolean isLeader;
    private boolean isReady;
    private boolean isChat;

    private final Player player;
    private final PartyPlayer partyPlayer;

    public DPSPlayer(Player player, boolean isLeader){
        this.isLeader = isLeader;
        isReady = false;
        isChat = false;

        this.player = player;
        partyPlayer = DPS.getPartiesAPI().getPartyPlayer(player.getUniqueId());
    }
    public boolean isLeader() {
        return isLeader;
    }
    public boolean isReady() {
        return isReady;
    }
    public void setReady(boolean isReady) {
        this.isReady = isReady;
        DPSParty party = DPSParty.get(player.getName());
        if (party != null)
            party.updateMenuAllPlayer();
    }
    public boolean isChat() {
        return isChat;
    }
    public void setChat(boolean isChat) {
        this.isChat = isChat;
        player.performCommand("party chat " + (isChat ? "on" : "off"));
        updateMenuPlayer(DPSParty.get(player.getName()));
    }

    public Player getPlayer() {
        return player;
    }
    public PartyPlayer getPartyPlayer(){
        return partyPlayer;
    }

    public void updateMenuPlayer(DPSParty party){
        if (!MenuUtils.isOurMenu(player.getOpenInventory().getTitle()))
            return;

        player.openInventory(isLeader ? MenuUtils.formPartyLeader(party) : null);
    }
}
