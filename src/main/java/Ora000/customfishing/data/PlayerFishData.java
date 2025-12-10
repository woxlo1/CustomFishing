package Ora000.customfishing.data;

import java.util.Set;
import java.util.UUID;

public class PlayerFishData {

    public final UUID uuid;
    public final Set<String> caughtFish;
    public final int totalCaught;

    public PlayerFishData(UUID uuid, Set<String> caughtFish, int totalCaught) {
        this.uuid = uuid;
        this.caughtFish = caughtFish;
        this.totalCaught = totalCaught;
    }

    public boolean hasCaught(String fishName) {
        return caughtFish.contains(fishName);
    }
}
