package Ora000.customfishing.data;

import Ora000.customfishing.Customfishing;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ServerRecordData {

    public static class FishRecord {
        public int maxSize = Integer.MIN_VALUE;
        public int minSize = Integer.MAX_VALUE;
        public String maxOwner = "";
        public String minOwner = "";
    }

    private final Map<String, FishRecord> records = new HashMap<>();
    private final File file;
    private FileConfiguration config;

    public ServerRecordData(File file) {
        this.file = file;
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public void load() {
        if (!file.exists()) return;
        for (String fishName : config.getKeys(false)) {
            FishRecord r = new FishRecord();
            r.maxSize = config.getInt(fishName + ".maxSize", Integer.MIN_VALUE);
            r.minSize = config.getInt(fishName + ".minSize", Integer.MAX_VALUE);
            r.maxOwner = config.getString(fishName + ".maxOwner", "");
            r.minOwner = config.getString(fishName + ".minOwner", "");
            records.put(fishName, r);
        }
    }

    public void save() throws IOException {
        for (Map.Entry<String, FishRecord> entry : records.entrySet()) {
            String fishName = entry.getKey();
            FishRecord r = entry.getValue();
            config.set(fishName + ".maxSize", r.maxSize);
            config.set(fishName + ".minSize", r.minSize);
            config.set(fishName + ".maxOwner", r.maxOwner);
            config.set(fishName + ".minOwner", r.minOwner);
        }
        config.save(file);
    }

    public FishRecord getRecord(String fishName) {
        return records.computeIfAbsent(fishName, k -> new FishRecord());
    }

    /**
     * 最大サイズ記録を更新
     * @return trueなら新記録
     */
    public boolean checkMaxRecord(String fishName, int size, String playerName) {
        FishRecord r = getRecord(fishName);
        if (size > r.maxSize) {
            r.maxSize = size;
            r.maxOwner = playerName;
            saveSilently(); // 保存のみ
            return true;
        }
        return false;
    }

    /**
     * 最小サイズ記録を更新
     * @return trueなら新記録
     */
    public boolean checkMinRecord(String fishName, int size, String playerName) {
        FishRecord r = getRecord(fishName);
        if (size < r.minSize) {
            r.minSize = size;
            r.minOwner = playerName;
            saveSilently(); // 保存のみ
            return true;
        }
        return false;
    }

    // 保存は例：内部用
    private void saveSilently() {
        try { save(); } catch (IOException e) { e.printStackTrace(); }
    }
}
