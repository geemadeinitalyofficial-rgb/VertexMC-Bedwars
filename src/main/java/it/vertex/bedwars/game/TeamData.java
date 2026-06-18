package it.vertex.bedwars.game;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TeamData {

    private final BedwarsTeam team;
    private final List<UUID> members = new ArrayList<>();
    private boolean bedAlive = true;
    private Location spawnLocation;
    private Location bedLocation;
    private int kills = 0;

    // Upgrade levels (0 = base)
    private int sharpnessLevel = 0;
    private int protectionLevel = 0;
    private int hasteLevel = 0;
    private int miningFatigueLevel = 0;
    private boolean healPool = false;
    private int forgeLevel = 0; // 0=none, 1=iron, 2=golden, 3=emerald, 4=molten

    public TeamData(BedwarsTeam team) {
        this.team = team;
    }

    public BedwarsTeam getTeam() { return team; }
    public List<UUID> getMembers() { return members; }
    public boolean hasBed() { return bedAlive; }
    public void destroyBed() { this.bedAlive = false; }
    public Location getSpawnLocation() { return spawnLocation; }
    public void setSpawnLocation(Location loc) { this.spawnLocation = loc; }
    public Location getBedLocation() { return bedLocation; }
    public void setBedLocation(Location loc) { this.bedLocation = loc; }
    public int getKills() { return kills; }
    public void addKill() { this.kills++; }

    public boolean isEliminated() {
        return !bedAlive && members.isEmpty();
    }

    public void addMember(Player player) { members.add(player.getUniqueId()); }
    public void removeMember(Player player) { members.remove(player.getUniqueId()); }
    public boolean isMember(Player player) { return members.contains(player.getUniqueId()); }

    // Upgrades
    public int getSharpnessLevel() { return sharpnessLevel; }
    public void setSharpnessLevel(int l) { this.sharpnessLevel = l; }
    public int getProtectionLevel() { return protectionLevel; }
    public void setProtectionLevel(int l) { this.protectionLevel = l; }
    public int getHasteLevel() { return hasteLevel; }
    public void setHasteLevel(int l) { this.hasteLevel = l; }
    public int getMiningFatigueLevel() { return miningFatigueLevel; }
    public void setMiningFatigueLevel(int l) { this.miningFatigueLevel = l; }
    public boolean hasHealPool() { return healPool; }
    public void setHealPool(boolean h) { this.healPool = h; }
    public int getForgeLevel() { return forgeLevel; }
    public void setForgeLevel(int l) { this.forgeLevel = l; }
}
