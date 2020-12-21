package mtpets.mtpets;

import mtpets.mtpets.managers.StorageManager;
import net.minecraft.server.v1_12_R1.EntityInsentient;
import net.minecraft.server.v1_12_R1.NavigationAbstract;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PetRunnable extends BukkitRunnable {

    private MTPets main;
    private StorageManager storage;
    public PetRunnable (MTPets main) {
        this.main = main;
        this.storage = main.storage;
    }
    @Override
    public void run() {
        if (Bukkit.getOnlinePlayers() !=null) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                World w = player.getWorld();
                if (w!=null)
                    if (w.getEntities()!=null)
                        for (Entity entity : w.getEntities()) {
                            if (entity!=null)
                                if (storage.getSpawnedPets()!=null && storage.getSpawnedPets().get(player)!=null)
                                    if (storage.getSpawnedPets().get(player).equals(entity.getUniqueId())) {
                                        Location loc = player.getLocation();
                                        LivingEntity livingEntity = (LivingEntity) entity;
                                        double distance = entity.getLocation().distance(player.getLocation());
                                        if (!(distance <= 2.5)) {
                                            if (!livingEntity.hasAI())
                                                livingEntity.setAI(true);

                                            if (distance >= 15)
                                                entity.teleport(player.getLocation());

                                            net.minecraft.server.v1_12_R1.Entity nmsEntity = ((CraftEntity) entity).getHandle();
                                            NavigationAbstract nav = ((EntityInsentient) nmsEntity).getNavigation();
                                            nav.a(loc.getX(), loc.getY(), loc.getZ(), 1.5);
                                        } else {
                                            livingEntity.setAI(false);
                                        }
                        }
                }
            }
        }
    }
}
