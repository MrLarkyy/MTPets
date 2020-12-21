package mtpets.mtpets;

import mtpets.mtpets.inventories.PetsGUI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import mtpets.mtpets.managers.StorageManager;

import java.io.IOException;

public class Commands implements CommandExecutor {
    private MTPets main;
    private StorageManager storage;
    public Commands (MTPets main, StorageManager storage) {
        this.main = main;
        this.storage = storage;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = (Player) sender;

        //Pet cmd
        if (args.length==0 && p.hasPermission("mtpets.pets")) {
            new PetsGUI(p,1,main,storage);
        } else if (args.length > 3) {

            //Add cmd
            if (args[0].equalsIgnoreCase("add") && p.hasPermission("mtpets.admin")) {
                if (Bukkit.getPlayer(args[1])!=null) {
                    Player o = Bukkit.getPlayer(args[1]);
                    if (storage.doesEntityExist(args[2])) {
                        StringBuilder name = new StringBuilder();
                        for (int i = 3; i < args.length; i++) {
                            name.append(args[i]).append(" ");
                        }
                        String namestring = name.substring(0, name.length() - 1);

                        if (!storage.hasPet(o, namestring, EntityType.valueOf(args[2].toUpperCase()))) {
                            storage.addPetData(o, EntityType.valueOf(args[2].toUpperCase()), namestring);
                            main.sendMessage(p,main.getConfigString("messages.petcreated").replace("%type%",args[2].toLowerCase()).replace("%name%",namestring).replace("%player%",o.getName()));
                            main.sendMessage(o, main.getConfigString("messages.petcreatedother").replace("%type%",args[2].toLowerCase()).replace("%name%",namestring));
                            try {
                                main.getDataFile().save(main.getFile());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else
                            main.sendMessage(p,main.getConfigString("messages.alreadyhavepet"));
                    } else
                        main.sendMessage(p,main.getConfigString("messages.unknownentity"));
                } else
                    main.sendMessage(p,main.getConfigString("messages.unknownplayer"));
            } else if (args[0].equalsIgnoreCase("remove") && p.hasPermission("mtpets.admin")) {
                //Remove cmd
                if (Bukkit.getPlayer(args[1])!=null) { if (storage.doesEntityExist(args[2])) {

                    Player o = Bukkit.getPlayer(args[1]);

                    StringBuilder name = new StringBuilder();
                    for (int i = 3; i < args.length; i++) {
                        name.append(args[i]).append(" ");
                    }
                    String namestring = name.substring(0, name.length() - 1);

                    if (storage.hasPet(o,namestring,EntityType.valueOf(args[2].toUpperCase()))) {
                        try {
                            storage.removePlayerPet(o,EntityType.valueOf(args[2].toUpperCase()),namestring);
                            main.sendMessage(p,main.getConfigString("messages.petremoved").replace("%type%",args[2].toLowerCase()).replace("%name%",namestring).replace("%player%",o.getName()));
                            main.sendMessage(o, main.getConfigString("messages.petremovedother").replace("%type%",args[2].toLowerCase()).replace("%name%",namestring));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        main.sendMessage(p,main.getConfigString("messages.doesnthavepet"));
                    }

                } else
                    main.sendMessage(p,main.getConfigString("messages.unknownentity"));
                } else {
                    main.sendMessage(p,main.getConfigString("messages.unknownplayer"));
                }
            } else if (!p.hasPermission("mtpets.admin") || !p.hasPermission("mtpets.pets")) {
                main.sendMessage(p,main.getConfigString("messages.noperm"));
            }
        }
        return false;
    }
}
