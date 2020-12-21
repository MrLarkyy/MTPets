package mtpets.mtpets.inventories;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class GUIUtil {

    public static List<ItemStack> getPageItems(List<ItemStack> items, int page, int spaces) {
        int upperBound = page * spaces;
        int lowerBound = upperBound - spaces;

        List<ItemStack> newItems = new ArrayList<>();
        for (int i = lowerBound;i < upperBound;i++) {
            try {
                newItems.add(items.get(i));
            } catch(IndexOutOfBoundsException e) {
                continue;
            }


        }
        return newItems;
    }

    public static boolean isPageValid(List<ItemStack> items,int page,int spaces) {
        if (page <= 0) {
            return false;
        }

        int upperBound = page * spaces;
        int lowerBound = upperBound - spaces;

        return items.size() > lowerBound;

    }


    public static ItemStack mkItem(Material material, String name, String localized, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',name));
        if (lore!=null)
            meta.setLore(mkLore(lore));
        meta.setLocalizedName(localized);
        item.setItemMeta(meta);

        return item;
    }

    public static List<String> mkLore(List<String> oldlore) {
        List<String> lore = new ArrayList<>();
        for (String str : oldlore) {
            lore.add(ChatColor.translateAlternateColorCodes('&',str));
        }
        return lore;
    }
}
