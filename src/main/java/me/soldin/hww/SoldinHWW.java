package me.soldin.hww;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class SoldinHWW extends JavaPlugin {

    private BukkitTask displayTask;
    private int index = 0;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        startTask();
        getLogger().info("SoldinHWW запущен! Разработчик: Soldin.jar");
    }

    @Override
    public void onDisable() {
        if (displayTask != null) displayTask.cancel();
        getLogger().info("SoldinHWW выключен!");
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("soldinhww")) {
            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                reloadConfig();
                sender.sendMessage("§a[SoldinHWW] Конфиг перезагружен!");
                restartTask();
                return true;
            } else {
                sender.sendMessage("§eИспользуй: /soldinhww reload");
                return true;
            }
        }
        return false;
    }

    private void startTask() {
        FileConfiguration cfg = getConfig();
        if (!cfg.getBoolean("enabled", true)) return;

        int interval = cfg.getInt("update-interval", 10);
        displayTask = Bukkit.getScheduler().runTaskTimer(this, () -> {
            index++;

            String textKey = (index % 2 == 0) ? "text1" : "text2";
            String colorKey = (index % 2 == 0) ? "color1" : "color2";

            String text = cfg.getString(textKey, "Бу!");
            String colorName = cfg.getString(colorKey, "RED").toUpperCase();

            NamedTextColor color;
            try {
                color = NamedTextColor.valueOf(colorName);
            } catch (IllegalArgumentException e) {
                color = NamedTextColor.RED;
            }

            Component message = Component.text(text).color(color);

            // Выводим как обычное сообщение чата — в нижнем левом углу
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(message);
            }

        }, 0L, interval * 20L);
    }

    private void restartTask() {
        if (displayTask != null) displayTask.cancel();
        startTask();
    }
}
