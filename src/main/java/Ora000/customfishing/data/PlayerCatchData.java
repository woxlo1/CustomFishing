package Ora000.customfishing.data;

import java.util.HashMap;
import java.util.Map;

public class PlayerCatchData {

    // 魚ごとの最大サイズ・釣った回数
    public static class FishRecord {
        public int maxSize;
        public int totalCaught;

        public FishRecord(int maxSize, int totalCaught) {
            this.maxSize = maxSize;
            this.totalCaught = totalCaught;
        }
    }

    // プレイヤーUUID -> 魚名 -> FishRecord
    private final Map<String, Map<String, FishRecord>> playerData = new HashMap<>();

    public Map<String, FishRecord> getPlayer(String uuid) {
        return playerData.computeIfAbsent(uuid, k -> new HashMap<>());
    }

    public void recordCatch(String uuid, String fishName, int size) {
        Map<String, FishRecord> fishMap = getPlayer(uuid);
        FishRecord record = fishMap.get(fishName);

        if (record == null) {
            fishMap.put(fishName, new FishRecord(size, 1));
        } else {
            record.totalCaught++;
            if (size > record.maxSize) {
                record.maxSize = size;
            }
        }
    }

    public Map<String, Map<String, FishRecord>> getAllData() {
        return playerData;
    }
}
