package Ora000.customfishing.util;

import Ora000.customfishing.Customfishing;
import Ora000.customfishing.data.FishDataManager;
import org.bukkit.command.*;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class FishingTabCompleter implements TabCompleter {

    private final Customfishing plugin;
    private final FishDataManager data;

    public FishingTabCompleter(Customfishing plugin, FishDataManager data) {
        this.plugin = plugin;
        this.data = data;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        List<String> res = new ArrayList<>();

        if (cmd.getName().equalsIgnoreCase("filleting")) {
            if (args.length == 1) {
                return partial(args[0], List.of("set", "addpotion", "clearpotion", "remove", "info"));
            }
            if (args.length == 2) {
                // 魚番号
                List<String> ids = new ArrayList<>();
                for (int i = 0; i < data.fishes.size(); i++) ids.add(String.valueOf(i));
                return partial(args[1], ids);
            }
            if (args.length == 3 && args[0].equalsIgnoreCase("addpotion")) {
                List<String> effects = new ArrayList<>();
                for (PotionEffectType t : PotionEffectType.values()) {
                    if (t != null && t.getName() != null) effects.add(t.getName());
                }
                return partial(args[2], effects);
            }
        }

        if (cmd.getName().equalsIgnoreCase("customfish")) {
            if (args.length == 1) {
                return partial(args[0],
                        List.of("sell", "list", "add", "remove", "reload", "rod", "on", "off"));
            }

            if (args.length == 2 && args[0].equalsIgnoreCase("rod")) {
                return partial(args[1], List.of("create", "remove"));
            }
        }

        return res;
    }

    private List<String> partial(String token, List<String> options) {
        List<String> out = new ArrayList<>();
        StringUtil.copyPartialMatches(token, options, out);
        return out;
    }
}
