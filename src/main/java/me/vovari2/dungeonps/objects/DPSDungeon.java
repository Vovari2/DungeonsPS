package me.vovari2.dungeonps.objects;


import org.bukkit.Location;

public class DPSDungeon {
    private final String name;
    private final String nameTitle;
    private final int enterInDistance;
    private final Location enterIn;
    private final Location enterOut;
    private final Location partyStartRoom;
    private final Location partySettingsRoom;

    public DPSDungeon(String name, String nameTitle, int enterInDistance, Location enterIn, Location enterOut, Location partyStartRoom, Location partySettingsRoom){
        this.name = name;
        this.nameTitle = nameTitle;
        this.enterInDistance = enterInDistance;
        this.enterIn = enterIn;
        this.enterOut = enterOut;
        this.partyStartRoom = partyStartRoom;
        this.partySettingsRoom = partySettingsRoom;
    }

    public String getName(){
        return name;
    }
    public String getNameTitle(){
        return nameTitle;
    }
    public int getEnterInDistance(){
        return enterInDistance;
    }
    public Location getEnterIn(){
        return enterIn;
    }
    public Location getEnterOut(){
        return enterOut;
    }
    public Location getPartyStartRoom(){
        return partyStartRoom;
    }
    public Location getPartySettingsRoom(){
        return partySettingsRoom;
    }
}
