package me.vovari2.dungeonps.objects;

import com.alessiodp.parties.api.interfaces.PartyPlayer;
import me.vovari2.dungeonps.DPS;
import me.vovari2.dungeonps.utils.MenuUtils;
import org.bukkit.entity.Player;

import java.util.Objects;

public class DPSPlayer {

    private boolean isLeader;
    private boolean isReady;
    private boolean isChat;
    private boolean isWaitCoolDown;

    private boolean useOnlyFriends;
    private int partyPlayersPage;

    private final Player player;
    private final PartyPlayer partyPlayer;

    public DPSPlayer(Player player, boolean isLeader){
        this.isLeader = isLeader;
        isReady = false;
        isChat = false;

        isWaitCoolDown = DPS.getTaskTicks().waitCooldownNotice.containsKey(player.getName());

        this.player = player;
        partyPlayer = DPS.getPartiesAPI().getPartyPlayer(player.getUniqueId());
    }
    public boolean isLeader() {
        return isLeader;
    }
    public void setLeader(boolean isLeader){
        this.isLeader = isLeader;
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
        updatePartySettings(DPSParty.get(player.getName()));
    }
    public boolean isWaitCoolDown() {
        return isWaitCoolDown;
    }
    public void setWaitCoolDown(boolean isWaitCoolDown) {
        this.isWaitCoolDown = isWaitCoolDown;
        updatePartySettings(DPSParty.get(player.getName()));
    }
    public boolean isUseOnlyFriends() {
        return useOnlyFriends;
    }
    public void setUseOnlyFriends(boolean useOnlyFriends) {
        this.useOnlyFriends = useOnlyFriends;
        updatePartyPlayers();
    }
    public int getPartyPlayersPage() {
        return partyPlayersPage;
    }
    public void setPartyPlayersPage(int partyPlayersPage) {
        this.partyPlayersPage = partyPlayersPage;
        updatePartyPlayers();
    }

    public Player getPlayer() {
        return player;
    }
    public PartyPlayer getPartyPlayer(){
        return partyPlayer;
    }

    public void updatePartySettings(DPSParty party){
        if (!Objects.equals(MenuUtils.getNameMenu(player.getOpenInventory()), "party_settings"))
            return;

        if (isLeader)
            MenuUtils.openPartySettingsLeader(party, this);
        else MenuUtils.openPartySettingsPlayer(party, this);
    }
    public void updatePartyPlayers(){
        if (!Objects.equals(MenuUtils.getNameMenu(player.getOpenInventory()), "party_players"))
            return;

        MenuUtils.openPartyPlayers(this);
    }
}
