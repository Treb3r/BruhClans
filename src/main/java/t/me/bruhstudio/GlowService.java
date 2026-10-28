package t.me.bruhstudio;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Pair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public final class GlowService {
    public final BruhFunClansPlugin plugin;
    private PacketAdapter packetListener;

    public GlowService(BruhFunClansPlugin plugin) {
        this.plugin = plugin;
        this.setupProtocolLib();
    }

    private void setupProtocolLib() {
        if (!Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
            return;
        }
        try {
            this.packetListener = new PacketAdapter(this.plugin, ListenerPriority.NORMAL, PacketType.Play.Server.ENTITY_EQUIPMENT) {
                @Override
                public void onPacketSending(PacketEvent event) {
                    if (event.isCancelled()) {
                        return;
                    }
                    Player viewer = event.getPlayer();
                    if (viewer == null) {
                        return;
                    }
                    PacketContainer packet = event.getPacket();
                    int entityId = packet.getIntegers().read(0);
                    if (entityId == viewer.getEntityId()) {
                        return;
                    }

                    Entity targetEntity;
                    try {
                        targetEntity = ProtocolLibrary.getProtocolManager().getEntityFromID(viewer.getWorld(), entityId);
                    } catch (Exception e) {
                        return;
                    }
                    if (!(targetEntity instanceof Player)) {
                        return;
                    }
                    Player target = (Player) targetEntity;

                    Clan targetClan = GlowService.this.plugin.service().clan(target.getName());
                    if (targetClan == null || !targetClan.glow) {
                        return;
                    }

                    Clan viewerClan = GlowService.this.plugin.service().clan(viewer.getName());
                    boolean sameClan = viewerClan != null && viewerClan.name.equalsIgnoreCase(targetClan.name);

                    if (sameClan) {
                        List<Pair<EnumWrappers.ItemSlot, ItemStack>> original = packet.getSlotStackPairLists().read(0);
                        if (original == null) {
                            return;
                        }
                        Clan.Member targetMember = targetClan.member(target.getName());
                        String colorStr = (targetMember != null && targetMember.armorColor != null && !targetMember.armorColor.isEmpty())
                                ? targetMember.armorColor
                                : targetClan.glowColor;
                        ItemStack[] colored = armor(colorStr);
                        List<Pair<EnumWrappers.ItemSlot, ItemStack>> updated = new ArrayList<>();
                        for (Pair<EnumWrappers.ItemSlot, ItemStack> pair : original) {
                            EnumWrappers.ItemSlot slot = pair.getFirst();
                            ItemStack currentItem = pair.getSecond();
                            if (slot == null || currentItem == null || currentItem.getType().isAir()) {
                                updated.add(pair);
                                continue;
                            }
                            switch (slot) {
                                case HEAD:
                                    updated.add(new Pair<>(slot, colored[0] != null ? colored[0] : new ItemStack(Material.AIR)));
                                    break;
                                case CHEST:
                                    updated.add(new Pair<>(slot, colored[1] != null ? colored[1] : new ItemStack(Material.AIR)));
                                    break;
                                case LEGS:
                                    updated.add(new Pair<>(slot, colored[2] != null ? colored[2] : new ItemStack(Material.AIR)));
                                    break;
                                case FEET:
                                    updated.add(new Pair<>(slot, colored[3] != null ? colored[3] : new ItemStack(Material.AIR)));
                                    break;
                                default:
                                    updated.add(pair); // Preserves MAINHAND, OFFHAND, and any other slot!
                                    break;
                            }
                        }
                        packet.getSlotStackPairLists().write(0, updated);
                    }
                }
            };
            ProtocolLibrary.getProtocolManager().addPacketListener(this.packetListener);
        } catch (Throwable t) {
            this.plugin.getLogger().warning("Could not register ProtocolLib armor packet listener: " + t.getMessage());
        }
    }

    public void refresh() {
        if (!Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
            return;
        }
        for (Map.Entry<Clan, List<Player>> entry : this.groups().entrySet()) {
            Clan clan = entry.getKey();
            for (Player target : entry.getValue()) {
                ItemStack[] shownArmor;
                ItemStack[] real = this.realArmor(target);
                if (clan.glow) {
                    Clan.Member targetMember = clan.member(target.getName());
                    String colorStr = (targetMember != null && targetMember.armorColor != null && !targetMember.armorColor.isEmpty())
                            ? targetMember.armorColor
                            : clan.glowColor;
                    ItemStack[] colored = this.armor(colorStr);
                    shownArmor = new ItemStack[4];
                    for (int i = 0; i < 4; i++) {
                        if (real[i] != null && !real[i].getType().isAir()) {
                            shownArmor[i] = colored[i];
                        } else {
                            shownArmor[i] = new ItemStack(Material.AIR);
                        }
                    }
                } else {
                    shownArmor = real;
                }
                for (Player viewer : entry.getValue()) {
                    if (viewer == target) continue;
                    this.send(viewer, target, shownArmor);
                }
            }
        }
    }

    public void clear() {
        if (this.packetListener != null && Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
            try {
                ProtocolLibrary.getProtocolManager().removePacketListener(this.packetListener);
            } catch (Throwable ignored) {}
        }
        for (List<Player> players : this.groups().values()) {
            for (Player target : players) {
                ItemStack[] real = this.realArmor(target);
                for (Player viewer : players) {
                    if (viewer == target) continue;
                    this.send(viewer, target, real);
                }
            }
        }
    }

    public Map<Clan, List<Player>> groups() {
        HashMap<Clan, List<Player>> groups = new HashMap<Clan, List<Player>>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            Clan clan = this.plugin.service().clan(player.getName());
            if (clan == null) continue;
            groups.computeIfAbsent(clan, ignored -> new ArrayList<>()).add(player);
        }
        return groups;
    }

    public ItemStack[] realArmor(Player player) {
        return new ItemStack[]{
            player.getInventory().getHelmet() != null ? player.getInventory().getHelmet() : new ItemStack(Material.AIR),
            player.getInventory().getChestplate() != null ? player.getInventory().getChestplate() : new ItemStack(Material.AIR),
            player.getInventory().getLeggings() != null ? player.getInventory().getLeggings() : new ItemStack(Material.AIR),
            player.getInventory().getBoots() != null ? player.getInventory().getBoots() : new ItemStack(Material.AIR)
        };
    }

    public ItemStack[] armor(Clan clan) {
        return this.armor(clan != null ? clan.glowColor : null);
    }

    public ItemStack[] armor(String colorStr) {
        String[] parts;
        int red = 0;
        int green = 255;
        int blue = 0;
        if (colorStr != null && (parts = colorStr.split(";")).length == 3) {
            try {
                red = Integer.parseInt(parts[0]);
                green = Integer.parseInt(parts[1]);
                blue = Integer.parseInt(parts[2]);
            } catch (NumberFormatException ignored) {}
        }
        Color color = Color.fromRGB(Math.max(0, Math.min(255, red)), Math.max(0, Math.min(255, green)), Math.max(0, Math.min(255, blue)));
        Material[] types = new Material[]{
            Material.LEATHER_HELMET,
            Material.LEATHER_CHESTPLATE,
            Material.LEATHER_LEGGINGS,
            Material.LEATHER_BOOTS
        };
        ItemStack[] result = new ItemStack[4];
        for (int i = 0; i < 4; ++i) {
            ItemStack item = new ItemStack(types[i]);
            LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
            if (meta != null) {
                meta.setColor(color);
                item.setItemMeta(meta);
            }
            result[i] = item;
        }
        return result;
    }

    public void resetPlayer(Player player, Clan clan) {
        if (!Bukkit.getPluginManager().isPluginEnabled("ProtocolLib") || player == null || clan == null) {
            return;
        }
        ItemStack[] targetReal = this.realArmor(player);
        for (Clan.Member member : clan.members.values()) {
            if (member == null || member.name == null) continue;
            Player clanPlayer = Bukkit.getPlayerExact(member.name);
            if (clanPlayer != null && clanPlayer.isOnline() && !clanPlayer.equals(player)) {
                this.send(clanPlayer, player, targetReal);
                this.send(player, clanPlayer, this.realArmor(clanPlayer));
            }
        }
    }

    public void resetClan(Clan clan) {
        if (!Bukkit.getPluginManager().isPluginEnabled("ProtocolLib") || clan == null) {
            return;
        }
        List<Player> onlineMembers = new ArrayList<>();
        for (Clan.Member member : clan.members.values()) {
            if (member == null || member.name == null) continue;
            Player p = Bukkit.getPlayerExact(member.name);
            if (p != null && p.isOnline()) {
                onlineMembers.add(p);
            }
        }
        for (Player target : onlineMembers) {
            ItemStack[] real = this.realArmor(target);
            for (Player viewer : onlineMembers) {
                if (viewer != target) {
                    this.send(viewer, target, real);
                }
            }
        }
    }

    public void send(Player viewer, Player target, ItemStack[] armor) {
        if (!Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
            return;
        }
        try {
            PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_EQUIPMENT);
            packet.getIntegers().write(0, target.getEntityId());
            List<Pair<EnumWrappers.ItemSlot, ItemStack>> pairs = new ArrayList<>();
            pairs.add(new Pair<>(EnumWrappers.ItemSlot.HEAD, armor[0] != null ? armor[0] : new ItemStack(Material.AIR)));
            pairs.add(new Pair<>(EnumWrappers.ItemSlot.CHEST, armor[1] != null ? armor[1] : new ItemStack(Material.AIR)));
            pairs.add(new Pair<>(EnumWrappers.ItemSlot.LEGS, armor[2] != null ? armor[2] : new ItemStack(Material.AIR)));
            pairs.add(new Pair<>(EnumWrappers.ItemSlot.FEET, armor[3] != null ? armor[3] : new ItemStack(Material.AIR)));
            packet.getSlotStackPairLists().write(0, pairs);
            ProtocolLibrary.getProtocolManager().sendServerPacket(viewer, packet);
        } catch (Throwable t) {
            this.plugin.getLogger().warning("Failed to send armor packet: " + t.getMessage());
        }
    }
}
