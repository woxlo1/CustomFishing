package Ora000.customfishing.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SellLogger {

    private static File logFile;

    public static void init(File dataFolder) {
        File dir = new File(dataFolder, "logs");
        if (!dir.exists()) dir.mkdirs();

        logFile = new File(dir, "sell.log");
    }

    public static void log(Player p, int totalPrice, int itemCount, String detail) {

        if (logFile == null) return; // 保険

        String time = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        String line = String.format(
                "[%s] %s (%s) price=%d count=%d detail=%s%n",
                time,
                p.getName(),
                p.getUniqueId(),
                totalPrice,
                itemCount,
                detail
        );

        try (FileWriter fw = new FileWriter(logFile, true)) {
            fw.write(line);
        } catch (IOException e) {
            Bukkit.getLogger().severe("Sell log write failed: " + e.getMessage());
        }

        Bukkit.getLogger().info("[CustomFishing] " + p.getName() + " sold fish: " + totalPrice);
    }
}
