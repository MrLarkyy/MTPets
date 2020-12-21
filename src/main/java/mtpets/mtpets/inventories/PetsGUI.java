package mtpets.mtpets.inventories;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import mtpets.mtpets.MTPets;
import mtpets.mtpets.managers.StorageManager;
import mtpets.mtpets.object.Pet;

import java.util.ArrayList;
import java.util.List;

public class PetsGUI {
    public PetsGUI(Player p, int page, MTPets main, StorageManager storage) {
        Inventory inv = Bukkit.createInventory(null,54, ChatColor.translateAlternateColorCodes('&',main.getConfigString("GUI.title").replace("%page%",String.valueOf(page))));

        List<ItemStack>allItems =new ArrayList<>();
        ItemStack petItem;
        int i = 1;
        if (storage.getPlayerPets(p)!=null)
        for (Pet pet : storage.getPlayerPets(p)) {

            List<String> lore = new ArrayList<>();
            for (String line : main.getConfigStringList("GUI.pet.lore")) {
                lore.add(line.replace("%type%",String.valueOf(pet.getEntityType())).replace("%name%",pet.getName()));
            }
            petItem = GUIUtil.mkItem(Material.valueOf(main.getConfigString("GUI.pet.material")),main.getConfigString("GUI.pet.name").replace("%number%",String.valueOf(i)),pet.getEntityType()+"||"+pet.getName(), lore);
            i++;
            allItems.add(petItem);
        }
        if (allItems.size()==0 || storage.getPlayerPets(p)==null){
            petItem = GUIUtil.mkItem(Material.valueOf(main.getConfigString("GUI.nopets.material")),main.getConfigString("GUI.nopets.name"),"nopets",main.getConfigStringList("GUI.nopets.lore"));
            inv.setItem(22,petItem);
        }

        ItemStack left;
        if (GUIUtil.isPageValid(allItems,page-1,45)) {
            left = GUIUtil.mkItem(Material.valueOf(main.getConfigString("GUI.prevpage.material")),main.getConfigString(main.getConfigString("GUI.prevpage.name")),String.valueOf(page),main.getConfigStringList("GUI.prevpage.lore"));
            inv.setItem(45,left);
        }

        ItemStack right;
        if (GUIUtil.isPageValid(allItems,page+1,45)) {
            right = GUIUtil.mkItem(Material.valueOf(main.getConfigString("GUI.nextpage.material")),main.getConfigString(main.getConfigString("GUI.nextpage.name")),String.valueOf(page),main.getConfigStringList("GUI.nextpage.lore"));
            inv.setItem(53,right);
        }

        for (ItemStack item : GUIUtil.getPageItems(allItems,page,45))
            inv.setItem(inv.firstEmpty(),item);

        ItemStack despawn = GUIUtil.mkItem(Material.valueOf(main.getConfigString("GUI.despawn.material")),main.getConfigString("GUI.despawn.name"),"despawnpet",main.getConfigStringList("GUI.despawn.lore"));
        inv.setItem(49,despawn);

        p.openInventory(inv);
    }
}
