package mtpets.mtpets;

import mtpets.mtpets.managers.StorageManager;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public final class MTPets extends JavaPlugin {

    public static HashMap<Player,UUID> pets = new HashMap<>();
    private YamlConfiguration modifyDataFile;
    private YamlConfiguration modifyConfigFile;
    private File dataFile;
    private File configFile;
    StorageManager storage = new StorageManager(this);

    @Override
    public void onEnable() {

        getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',"&e[lPets]&f Plugin was &aEnabled&f!"));

        if (!getDataFolder().exists())
            getDataFolder().mkdir();
        saveDefaultConfig();
        try {
            loadFiles();
        } catch (IOException e) {
            e.printStackTrace();
        }


        storage.loadPlayerPets();
        storage.loadSpawnedPets();

        getCommand("pet").setExecutor(new Commands(this,storage));
        getServer().getPluginManager().registerEvents(new PetListener(this),this);
        new PetRunnable(this).runTaskTimerAsynchronously(this,20,5);
        new SaveRunnable(this).runTaskTimerAsynchronously(this,20,200);

    }

    @Override
    public void onDisable() {
        storage.removePets();
        getServer().getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',"&e[lPets]&f Plugin was &cDisabled&f!"));
        try {
            storage.saveData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public YamlConfiguration getDataFile() {
        return modifyDataFile;
    }
    public YamlConfiguration getConfigFile() {
        return modifyConfigFile;
    }

    public File getFile() {
        return dataFile;
    }
    public File getCFile() {
        return configFile;
    }

    public void loadFiles() throws IOException {
        dataFile = new File(getDataFolder(), "data.yml");
        configFile = new File(getDataFolder(), "config.yml");
        if (!dataFile.exists()) {
            dataFile.createNewFile();
        }
        modifyDataFile = YamlConfiguration.loadConfiguration(dataFile);
        modifyConfigFile = YamlConfiguration.loadConfiguration(configFile);
    }

    public void sendMessage(Player p, String message) {
        p.sendMessage(ChatColor.translateAlternateColorCodes('&',message));
    }

    public String getConfigString(String path) {
        return getConfigFile().getString(path);
    }
    public List<String> getConfigStringList(String path) {
        return getConfigFile().getStringList(path);
    }

}
