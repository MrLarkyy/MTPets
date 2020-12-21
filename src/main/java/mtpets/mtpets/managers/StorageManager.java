package mtpets.mtpets.managers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import mtpets.mtpets.MTPets;
import mtpets.mtpets.object.Pet;

import java.io.IOException;
import java.util.*;

public class StorageManager {

    private MTPets main;
    private HashMap<UUID,List<Pet>> pets = new HashMap<>();
    private HashMap<Player,UUID> spawnedPets = new HashMap<>();
    private HashMap<UUID,Pet> cachedPetsToSpawn = new HashMap<>();

    public StorageManager(MTPets main) {
        this.main = main;
    }

    public void loadPlayerPets(){
        HashMap<UUID,List<Pet>> loaded = new HashMap<>();
        if (main.getDataFile().getConfigurationSection("players")!=null)
            if (main.getDataFile().getConfigurationSection("players").getKeys(false)!=null)
                for (String playeruuid : main.getDataFile().getConfigurationSection("players").getKeys(false)) {
                    if (main.getDataFile().getStringList("players."+playeruuid+".pets")!=null) {
                        List<Pet> petlist = new ArrayList<>();
                        for (String petstring : main.getDataFile().getStringList("players."+playeruuid+".pets")) {
                            String[] value = petstring.split("\\|\\|");
                            Pet pet = new Pet(EntityType.valueOf(value[0]),String.valueOf(value[1]));
                            petlist.add(pet);
                        }
                        loaded.put(UUID.fromString(playeruuid),petlist);
                    }
                }
            pets = loaded;
    }

    public void loadSpawnedPets(){
        HashMap<UUID,Pet> loadedSpawnPets = new HashMap<>();
        if (main.getDataFile().getConfigurationSection("players")!=null)
            if (main.getDataFile().getConfigurationSection("players").getKeys(false)!=null)
                for (String playeruuid : main.getDataFile().getConfigurationSection("players").getKeys(false)) {
                    if (main.getDataFile().getString("players."+playeruuid+".spawned")!=null) {
                        String[] value = main.getDataFile().getString("players." + playeruuid + ".spawned").split("\\|\\|");
                        Pet pet = new Pet(EntityType.valueOf(value[0]),String.valueOf(value[1]));
                        loadedSpawnPets.put(UUID.fromString(playeruuid),pet);
                    }
                }
        cachedPetsToSpawn = loadedSpawnPets;
    }

    public void saveData() throws IOException {
        if (cachedPetsToSpawn!=null) {
            for (UUID uuid : cachedPetsToSpawn.keySet()) {
                main.getDataFile().set("players."+uuid+".spawned",cachedPetsToSpawn.get(uuid).getEntityType()+"||"+cachedPetsToSpawn.get(uuid).getName());
                main.getDataFile().save(main.getFile());
            }
        }
    }


    public List<Pet> getPlayerPets(Player p) {
        if (main.getDataFile().getStringList("players."+p.getUniqueId()+".pets")!=null)
            if (pets.get(p.getUniqueId())!=null)
                return pets.get(p.getUniqueId());
        return null;
    }

    public void removePlayerPet(Player p, EntityType entity, String name) throws IOException {
        List<Pet> petslist = getPlayerPets(p);
        Pet pet = new Pet(entity,name);
        petslist.remove(pet);
        p.sendMessage("Pet Removed");
        pets.put(p.getUniqueId(),petslist);
        if (cachedPetsToSpawn.get(p.getUniqueId()).getName().equals(pet.getName()) && cachedPetsToSpawn.get(p.getUniqueId()).getEntityType().equals(pet.getEntityType())) {
            cachedPetsToSpawn.remove(p.getUniqueId());
            killPet(p);
            p.sendMessage("Pet Killed");
        }
        saveData();
    }

    public void addPetToCache(Player p, EntityType entity, String name) {
        List<Pet> cachedpets = pets.get(p.getUniqueId());
        Pet pet = new Pet(entity,name);
        if (cachedpets!=null) {
            cachedpets.add(pet);
            pets.put(p.getUniqueId(),cachedpets);
        } else
            pets.put(p.getUniqueId(),Arrays.asList(pet));
    }


    public void addPetData(Player p, EntityType entity, String name) {
        addPetToCache(p,entity,name);

        List<String> pet = new ArrayList<>();
        if (main.getDataFile().getStringList("players."+p.getUniqueId()+".pets")!=null)
            pet = main.getDataFile().getStringList("players."+p.getUniqueId()+".pets");
        pet.add(entity.toString()+"||"+name);
        main.getDataFile().set("players."+p.getUniqueId()+".pets",pet);
    }

    public boolean hasPet(Player p,String name,EntityType entity) {
        for (String str : main.getDataFile().getStringList("players."+p.getUniqueId()+".pets")) {
            String[] values = str.split("\\|\\|");

            if (values[1].equals(name) && values[0].equals(entity.toString()))
                return true;
        }

        return false;
    }

    public void addSpawnedPet(Player p,EntityType entity, String name) {
        Pet pet = new Pet(entity,name);
        cachedPetsToSpawn.put(p.getUniqueId(),pet);
        spawnPet(p, pet);
    }

    public boolean isSpawned(Player p,EntityType entity, String name){
        if (cachedPetsToSpawn.containsKey(p.getUniqueId())) {
            Pet pet = cachedPetsToSpawn.get(p.getUniqueId());
            return pet.getName().equals(name) && pet.getEntityType().equals(entity);
        }
        return false;
    }

    public boolean doesEntityExist(String argument) {
        try {
            EntityType entity = EntityType.valueOf(argument.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public void spawnPet(Player p, Pet pet) {
        Location loc = p.getLocation();
        Creature entity = (Creature) loc.getWorld().spawnEntity(loc,pet.getEntityType());
        entity.setInvulnerable(true);
        entity.setCustomName(ChatColor.translateAlternateColorCodes('&',pet.getName()));
        entity.setCustomNameVisible(true);
        UUID entityuuid = entity.getUniqueId();
        spawnedPets.put(p,entityuuid);
    }

    public void killPet(Player p) {
        Entity entity = Bukkit.getEntity(spawnedPets.get(p));
        entity.remove();
        spawnedPets.remove(p);
    }

    public boolean hasEntityToSpawn(Player p) {
        return cachedPetsToSpawn.containsKey(p.getUniqueId());
    }

    public Pet getEntityToSpawn(Player p) {
        return cachedPetsToSpawn.get(p.getUniqueId());
    }

    public boolean hasSpawnedEntity(Player p) {
        return spawnedPets.containsKey(p);
    }

    public void removeEntityToSpawn(Player p) {
        cachedPetsToSpawn.remove(p.getUniqueId());
    }

    public HashMap<Player,UUID> getSpawnedPets(){
        return (HashMap<Player, UUID>) spawnedPets.clone();
    }

    public void removePets() {
        if (Bukkit.getOnlinePlayers()!=null)
            for (Player p : Bukkit.getOnlinePlayers())
                if (hasSpawnedEntity(p)) {
                    killPet(p);
        }
    }

}
