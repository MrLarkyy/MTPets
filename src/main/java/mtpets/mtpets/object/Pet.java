package mtpets.mtpets.object;

import org.bukkit.entity.EntityType;

import java.util.UUID;

public class Pet {

    private EntityType entityType;

    private String name;

    public Pet (EntityType entityType, String name) {
        this.entityType = entityType;
        this.name = name;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public String getName() {
        return name;
    }
}
