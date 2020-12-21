package mtpets.mtpets;

import org.bukkit.scheduler.BukkitRunnable;
import mtpets.mtpets.managers.StorageManager;

import java.io.IOException;

public class SaveRunnable extends BukkitRunnable {

    private MTPets main;
    private StorageManager storage;
    public SaveRunnable(MTPets main) {
        this.main=main;
        this.storage=main.storage;
    }

    @Override
    public void run() {
        try {
            storage.saveData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
