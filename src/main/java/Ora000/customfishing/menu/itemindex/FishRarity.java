package Ora000.customfishing.menu.itemindex;

import org.bukkit.Material;

public enum FishRarity {

    COMMON(
            "COMMON",
            "§aコモン",
            Material.WHITE_STAINED_GLASS_PANE
    ),

    UNCOMMON(
            "UNCOMMON",
            "§bアンコモン",
            Material.LIGHT_BLUE_STAINED_GLASS_PANE
    ),

    RARE(
            "RARE",
            "§9レア",
            Material.BLUE_STAINED_GLASS_PANE
    ),

    EPIC(
            "EPIC",
            "§5エピック",
            Material.PURPLE_STAINED_GLASS_PANE
    ),

    LEGENDARY(
            "LEGENDARY",
            "§6レジェンダリー",
            Material.ORANGE_STAINED_GLASS_PANE
    );

    /** config上のID（CustomFish.rarity と一致） */
    public final String id;

    /** GUI表示名 */
    public final String display;

    /** GUIアイコン */
    public final Material icon;

    FishRarity(String id, String display, Material icon) {
        this.id = id;
        this.display = display;
        this.icon = icon;
    }

    /** rarity文字列 → enum 変換（安全） */
    public static FishRarity fromString(String rarity) {
        for (FishRarity r : values()) {
            if (r.id.equalsIgnoreCase(rarity)) {
                return r;
            }
        }
        return null;
    }
}
