package mtpets.mtpets;

import mtpets.mtpets.inventories.PetsGUI;
import mtpets.mtpets.managers.StorageManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;

public class PetListener implements Listener {
    private MTPets main;
    private StorageManager storage;

    public PetListener (MTPets main) {
        this.main = main;
        this.storage = main.storage;
    }

    @EventHandler
    public void onAnger(EntityTargetEvent e) {
        Entity entity = e.getEntity();
        if (storage.getSpawnedPets().containsValue(entity.getUniqueId()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onBreed (EntityBreedEvent e) {
        Entity entity = e.getEntity();
        if (storage.getSpawnedPets().containsValue(entity.getUniqueId()))
            e.setCancelled(true);
    }
    @EventHandler
    public void onEntityInteract (PlayerInteractEntityEvent e) {
        Entity entity = e.getRightClicked();
        if (storage.getSpawnedPets().containsValue(entity.getUniqueId()))
            e.setCancelled(true);
    }


    @EventHandler
    public void onInvClick(InventoryClickEvent e) throws IOException {
        if (e.getCurrentItem()!=null && !e.getSlotType().equals(InventoryType.SlotType.OUTSIDE)) {
            Player p = (Player) e.getWhoClicked();
            Inventory inv = e.getInventory();
            ItemStack i = e.getCurrentItem();

            if (i != null && i.getType() != null && e.getView().getTitle().contains(ChatColor.translateAlternateColorCodes('&',main.getConfigString("GUI.title").substring(0,5)))) {
                int page;
                if (inv.getItem(45)!=null)
                    page = Integer.parseInt(inv.getItem(45).getItemMeta().getLocalizedName());
                else
                    page = 1;

                if (i.getItemMeta()!=null && i.getItemMeta().getLocalizedName()!=null) {
                    String[] values = i.getItemMeta().getLocalizedName().split("\\|\\|");
                    if (storage.doesEntityExist(values[0])) {
                        if (storage.hasSpawnedEntity(p)) {
                            storage.killPet(p);
                        }
                        storage.addSpawnedPet(p,EntityType.valueOf(values[0]),values[1]);
                        p.closeInventory();
                        main.sendMessage(p,main.getConfigString("messages.petspawned").replace("%name%",values[1]));
                    }
                }
                if (i.getItemMeta()!=null && i.getItemMeta().getLocalizedName().contains("despawnpet"))
                    if (storage.hasSpawnedEntity(p)) {
                        storage.killPet(p);
                        storage.removeEntityToSpawn(p);
                        storage.saveData();
                        main.sendMessage(p,main.getConfigString("messages.petdespawned"));
                        p.closeInventory();
                    } else
                        main.sendMessage(p,main.getConfigString("messages.nodespawn"));

                if (e.getSlot()==45 && i.getType().equals(Material.ARROW)) {
                    new PetsGUI(p,page-1,main,storage);
                } else if (e.getSlot()==53 && i.getType().equals(Material.ARROW)){
                    new PetsGUI(p,page+1,main,storage);
                }

                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPortalTeleport(EntityPortalEvent e) {
        Entity entity = e.getEntity();
        if (storage.getSpawnedPets().containsValue(entity.getUniqueId()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onEntityDmg(EntityDamageEvent e) {
        Entity entity = e.getEntity();
        if (storage.getSpawnedPets().containsValue(entity.getUniqueId()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) throws IOException {
        Player p = e.getPlayer();
        if (storage.hasEntityToSpawn(p)) {
            storage.spawnPet(p,storage.getEntityToSpawn(p));
        }
        if (main.getDataFile().getConfigurationSection("players")==null){
            main.getDataFile().set("players."+p.getUniqueId()+".spawned", null);
            main.getDataFile().save(main.getFile());
        }

        else if (main.getDataFile().getConfigurationSection("players").getKeys(true)==null) {
            main.getDataFile().set("players." + p.getUniqueId() + ".spawned", null);
            main.getDataFile().save(main.getFile());
        }

        else if (!main.getDataFile().getConfigurationSection("players").getKeys(false).contains(String.valueOf(p.getUniqueId()))) {
            main.getDataFile().set("players."+p.getUniqueId()+".spawned", null);
            main.getDataFile().save(main.getFile());
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (storage.hasSpawnedEntity(p)) {
            storage.killPet(p);
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        Player p = e.getPlayer();
        if (storage.hasSpawnedEntity(p)) {
            storage.killPet(p);
            storage.spawnPet(p,storage.getEntityToSpawn(p));
        }

    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent e) {
        Player p = e.getPlayer();
        if (storage.hasSpawnedEntity(p)) {
            storage.killPet(p);
            storage.spawnPet(p,storage.getEntityToSpawn(p));
        }
    }

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        if (e.getHand().equals(EquipmentSlot.HAND) && e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
            if (e.getClickedBlock().getType()!=null || e.getClickedBlock().getType()!=Material.AIR) {
                Block b = e.getClickedBlock();
                if (b.getType()==Material.PURPUR_BLOCK) {
                    Player p = e.getPlayer();

                    e.setCancelled(true);
                    new PetsGUI(p,1,main,storage);
                }
            }
    }
}
