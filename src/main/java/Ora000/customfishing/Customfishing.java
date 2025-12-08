package Ora000.customfishing;

import Ora000.customfishing.antimacro.AntiMacroListener;
import Ora000.customfishing.antimacro.AntiMacroManager;
import Ora000.customfishing.command.CustomFishCommand;
import Ora000.customfishing.command.FilletingCommand;
import Ora000.customfishing.data.FishDataManager;
import Ora000.customfishing.data.ServerRecordData;
import Ora000.customfishing.listener.FishingListener;
import Ora000.customfishing.util.FishingTabCompleter;
import Ora000.customfishing.util.SellLogger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class Customfishing extends JavaPlugin {

    public static final String PREFIX =
            "§f§l[§b§lCustom§c§lFishing§f§l]§r ";

    private FishDataManager dataManager;
    private VaultManager vaultManager;

    // ✅ 釣りON / OFF
    private boolean fishingEnabled = true;

    // ✅ マクロ対策
    private AntiMacroManager antiMacroManager;

    // ===============================
    // サーバーレコード管理
    // ===============================
    private ServerRecordData serverRecordData;

    @Override
    public void onEnable() {

        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        // ===============================
        // データ
        // ===============================
        dataManager = new FishDataManager(this);
        dataManager.loadFishData();

        // ===============================
        // サーバーレコード
        // ===============================
        serverRecordData = new ServerRecordData(new File(getDataFolder(), "server-records.yml"));
        serverRecordData.load();

        // ===============================
        // コマンド
        // ===============================
        getCommand("customfish")
                .setExecutor(new CustomFishCommand(this, dataManager));
        getCommand("filleting")
                .setExecutor(new FilletingCommand(this, dataManager));

        FishingTabCompleter tab = new FishingTabCompleter(this, dataManager);
        getCommand("customfish").setTabCompleter(tab);
        getCommand("filleting").setTabCompleter(tab);

        // ===============================
        // リスナー
        // ===============================
        getServer().getPluginManager().registerEvents(
                new FishingListener(this, dataManager), this);

        // ✅ マクロ対策 初期化 & 登録
        antiMacroManager = new AntiMacroManager(this);
        getServer().getPluginManager().registerEvents(
                new AntiMacroListener(this), this);

        // ===============================
        // Vault
        // ===============================
        vaultManager = new VaultManager();

        // ===============================
        // 売却ログ
        // ===============================
        SellLogger.init(getDataFolder());

        getLogger().info("CustomFishing enabled!");
    }

    @Override
    public void onDisable() {
        if (dataManager != null) {
            dataManager.saveFishData();
        }

        if (serverRecordData != null) {
            try {
                serverRecordData.save();
            } catch (IOException e) {
                getLogger().warning("ServerRecordData の保存に失敗しました: " + e.getMessage());
            }
        }
    }

    // ===============================
    // Getter
    // ===============================
    public FishDataManager getDataManager() {
        return dataManager;
    }

    public VaultManager getVaultManager() {
        return vaultManager;
    }

    public AntiMacroManager getAntiMacroManager() {
        return antiMacroManager;
    }

    public ServerRecordData getServerRecordData() {
        return serverRecordData;
    }

    // ===============================
    // Fishing ON / OFF API
    // ===============================
    public boolean isFishingEnabled() {
        return fishingEnabled;
    }

    public void setFishingEnabled(boolean enabled) {
        this.fishingEnabled = enabled;
    }
}
