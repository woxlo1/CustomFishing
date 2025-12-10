package Ora000.customfishing.data;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class PlayerCatchData {

    private final JavaPlugin plugin;
    private final File dir;

    public PlayerCatchData(JavaPlugin plugin) {
        this.plugin = plugin;
        this.dir = new File(plugin.getDataFolder(), "playerdata");
        if (!dir.exists()) dir.mkdirs();
    }

    private File getFile(UUID uuid) {
        return new File(dir, uuid + ".yml");
    }

    private YamlConfiguration getConfig(UUID uuid) {
        return YamlConfiguration.loadConfiguration(getFile(uuid));
    }

    // ===============================
    // 記録
    // ===============================
    public void record(UUID uuid, String fish, int size) {
        YamlConfiguration c = getConfig(uuid);
        String p = "fishes." + fish;

        int count = c.getInt(p + ".count", 0) + 1;
        int max = Math.max(c.getInt(p + ".max", 0), size);
        int min = c.contains(p + ".min")
                ? Math.min(c.getInt(p + ".min"), size)
                : size;

        c.set(p + ".count", count);
        c.set(p + ".max", max);
        c.set(p + ".min", min);

        save(uuid, c);
    }

    // ===============================
    // 図鑑取得API
    // ===============================
    public boolean hasCaught(UUID uuid, String fish) {
        return getCount(uuid, fish) > 0;
    }

    public int getCount(UUID uuid, String fish) {
        return getConfig(uuid).getInt("fishes." + fish + ".count", 0);
    }

    public int getMax(UUID uuid, String fish) {
        return getConfig(uuid).getInt("fishes." + fish + ".max", 0);
    }

    public int getMin(UUID uuid, String fish) {
        return getConfig(uuid).getInt("fishes." + fish + ".min", 0);
    }

    private void save(UUID uuid, YamlConfiguration c) {
        try {
            c.save(getFile(uuid));
        } catch (IOException e) {
            plugin.getLogger().severe("PlayerCatchData save failed: " + uuid);
        }
    }
}
