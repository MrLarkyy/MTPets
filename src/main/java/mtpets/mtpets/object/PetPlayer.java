package mtpets.mtpets.object;

import java.util.List;
import java.util.UUID;

public class PetPlayer {

    private List<Pet> pets;

    private UUID uuid;

    private Pet activePet;

    public PetPlayer(List<Pet> pets,UUID uuid,Pet activePet) {
        this.pets = pets;
        this.uuid = uuid;
        this.activePet = activePet;

    }
}
