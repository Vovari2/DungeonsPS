package me.vovari2.dungeonps.objects;

import com.alessiodp.parties.api.interfaces.PartyPlayer;
import me.vovari2.dungeonps.DPS;
import me.vovari2.dungeonps.utils.MenuUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class DPSPlayer {

    private final boolean isLeader;
    private boolean isReady;
    private boolean isChat;

    private boolean isWaitCoolDown;

    private final Player player;
    private final PartyPlayer partyPlayer;
    private Inventory menuSettings;

    public DPSPlayer(Player player, boolean isLeader){
        this.isLeader = isLeader;
        isReady = false;
        isChat = false;

        isWaitCoolDown = DPS.getTaskSeconds().waitCooldownNotice.containsKey(player.getName());

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
    public boolean isWaitCoolDown() {
        return isWaitCoolDown;
    }
    public void setWaitCoolDown(boolean isWaitCoolDown) {
        this.isWaitCoolDown = isWaitCoolDown;
        updateMenuPlayer(DPSParty.get(player.getName()));
    }
    public Inventory getMenuSettings(){
        return menuSettings;
    }
    public void setMenuSettings(Inventory menuSettings){
        this.menuSettings = menuSettings;
    }

    public Player getPlayer() {
        return player;
    }
    public PartyPlayer getPartyPlayer(){
        return partyPlayer;
    }

    public void updateMenuPlayer(DPSParty party){
        if (MenuUtils.isNotOurMenu(MenuUtils.getNameMenu(player.getOpenInventory().getTitle())))
            return;

        if (isLeader)
            MenuUtils.openPartySettingsLeader(party, this);
        else MenuUtils.openPartySettingsPlayer(party, this);
    }
}
