package Ora000.customfishing.data;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * サーバー全体の魚レコード管理
 * ・最大サイズ
 * ・最小サイズ
 * ・記録者
 * ・更新日時
 */
public class ServerRecordData {

    /* ==================================================
     * 内部データクラス
     * ================================================== */
    public static class FishRecord {

        public int maxSize = Integer.MIN_VALUE;
        public int minSize = Integer.MAX_VALUE;

        public String maxOwner = "NONE";
        public String minOwner = "NONE";

        public String maxUpdatedAt = "";
        public String minUpdatedAt = "";

        public boolean hasMax() {
            return maxSize != Integer.MIN_VALUE;
        }

        public boolean hasMin() {
            return minSize != Integer.MAX_VALUE;
        }
    }

    /* ==================================================
     * フィールド
     * ================================================== */
    private final Map<String, FishRecord> records = new HashMap<>();
    private final File file;
    private FileConfiguration config;

    /* ==================================================
     * コンストラクタ
     * ================================================== */
    public ServerRecordData(File file) {
        this.file = file;
        this.config = YamlConfiguration.loadConfiguration(file);
        load();
    }

    /* ==================================================
     * load / save
     * ================================================== */
    public void load() {
        records.clear();

        if (!file.exists()) return;

        config = YamlConfiguration.loadConfiguration(file);

        for (String fishName : config.getKeys(false)) {
            FishRecord r = new FishRecord();
            r.maxSize = config.getInt(fishName + ".max.size", Integer.MIN_VALUE);
            r.minSize = config.getInt(fishName + ".min.size", Integer.MAX_VALUE);
            r.maxOwner = config.getString(fishName + ".max.owner", "NONE");
            r.minOwner = config.getString(fishName + ".min.owner", "NONE");
            r.maxUpdatedAt = config.getString(fishName + ".max.time", "");
            r.minUpdatedAt = config.getString(fishName + ".min.time", "");
            records.put(fishName, r);
        }
    }

    public void save() {
        config = YamlConfiguration.loadConfiguration(file);

        for (Map.Entry<String, FishRecord> e : records.entrySet()) {
            String fish = e.getKey();
            FishRecord r = e.getValue();

            config.set(fish + ".max.size", r.maxSize);
            config.set(fish + ".max.owner", r.maxOwner);
            config.set(fish + ".max.time", r.maxUpdatedAt);

            config.set(fish + ".min.size", r.minSize);
            config.set(fish + ".min.owner", r.minOwner);
            config.set(fish + ".min.time", r.minUpdatedAt);
        }

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* ==================================================
     * 取得API
     * ================================================== */
    public FishRecord getRecord(String fishName) {
        return records.computeIfAbsent(fishName, k -> new FishRecord());
    }

    public Map<String, FishRecord> getAllRecords() {
        return records;
    }

    /* ==================================================
     * 記録更新API（核心）
     * ================================================== */

    /**
     * 最大サイズ更新
     *
     * @return true = 新記録
     */
    public boolean checkMaxRecord(String fishName, int size, String playerName) {
        FishRecord r = getRecord(fishName);

        if (!r.hasMax() || size > r.maxSize) {
            r.maxSize = size;
            r.maxOwner = playerName;
            r.maxUpdatedAt = LocalDateTime.now().toString();
            save();
            return true;
        }
        return false;
    }

    /**
     * 最小サイズ更新
     *
     * @return true = 新記録
     */
    public boolean checkMinRecord(String fishName, int size, String playerName) {
        FishRecord r = getRecord(fishName);

        if (!r.hasMin() || size < r.minSize) {
            r.minSize = size;
            r.minOwner = playerName;
            r.minUpdatedAt = LocalDateTime.now().toString();
            save();
            return true;
        }
        return false;
    }

    /* ==================================================
     * 図鑑表示用 Lore API
     * ================================================== */
    public String getMaxRecordLore(String fishName) {
        FishRecord r = getRecord(fishName);
        if (!r.hasMax()) return "§7未記録";
        return "§e最大: §6" + r.maxSize + "cm §7(" + r.maxOwner + ")";
    }

    public String getMinRecordLore(String fishName) {
        FishRecord r = getRecord(fishName);
        if (!r.hasMin()) return "§7未記録";
        return "§e最小: §b" + r.minSize + "cm §7(" + r.minOwner + ")";
    }
}
