package Ora000.customfishing;

import Ora000.customfishing.antimacro.AntiMacroListener;
import Ora000.customfishing.antimacro.AntiMacroManager;
import Ora000.customfishing.command.CustomFishCommand;
import Ora000.customfishing.command.FilletingCommand;
import Ora000.customfishing.data.FishDataManager;
import Ora000.customfishing.data.PlayerCatchData;
import Ora000.customfishing.data.ServerRecordData;
import Ora000.customfishing.listener.FishingListener;
import Ora000.customfishing.menu.itemindex.ItemIndexListener;
import Ora000.customfishing.util.FishingTabCompleter;
import Ora000.customfishing.util.SellLogger;
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

    // ✅ サーバーレコード
    private ServerRecordData serverRecordData;

    private PlayerCatchData playerCatchData;

    private FishDataManager fishDataManager;

    @Override
    public void onEnable() {

        // フォルダ作成
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        // 魚データ読み込み
        fishDataManager = new FishDataManager(this);
        fishDataManager.loadFishData();

        // サーバーレコード初期化（ファイルは plugin ディレクトリ配下）
        serverRecordData = new ServerRecordData(new File(getDataFolder(), "server-records.yml"));
        serverRecordData.load();

        // プレイヤーデータ初期化
        playerCatchData = new PlayerCatchData(this);

        // コマンド登録
        getCommand("customfish").setExecutor(new CustomFishCommand(this, fishDataManager));
        getCommand("filleting").setExecutor(new FilletingCommand(this, fishDataManager));

        // タブ補完
        FishingTabCompleter tab = new FishingTabCompleter(this, fishDataManager);
        getCommand("customfish").setTabCompleter(tab);
        getCommand("filleting").setTabCompleter(tab);

        // リスナー登録（釣り処理）
        getServer().getPluginManager().registerEvents(new FishingListener(this, fishDataManager), this);

        getServer().getPluginManager()
                .registerEvents(new ItemIndexListener(this), this);

        // マクロ対策初期化＆登録
        antiMacroManager = new AntiMacroManager(this);
        getServer().getPluginManager().registerEvents(new AntiMacroListener(this), this);

        // Vault 初期化（VaultManager 実装に依存）
        vaultManager = new VaultManager();

        // 売却ログ初期化
        SellLogger.init(getDataFolder());

        getLogger().info("CustomFishing enabled!");
    }

    @Override
    public void onDisable() {

        // 魚データ保存
        if (dataManager != null) {
            dataManager.saveFishData();
        }

        // サーバーレコード保存（例外安全）
        if (serverRecordData != null) {
            serverRecordData.save();
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

    public PlayerCatchData getPlayerFishData() {
        return playerCatchData;
    }

    public FishDataManager getFishDataManager() {
        return fishDataManager;
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
