package committee.nova.mods.linker.config;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import committee.nova.mods.linker.Linker;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
/**
 * ConfigHandler
 *
 * @author cnlimiter
 * @version 1.0
 * @description
 * @date 2024/3/30 22:36
 */
public class ConfigHandler {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static ModConfig load() {
        ModConfig config = new ModConfig();

        if (!Linker.CONFIG_FOLDER.toFile().isDirectory()) {
            try {
                Files.createDirectories(Linker.CONFIG_FOLDER);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Path configPath = Linker.CONFIG_FOLDER.resolve(config.getConfigName() + ".json");
        if (configPath.toFile().isFile()) {
            try {
                config = GSON.fromJson(FileUtils.readFileToString(configPath.toFile(), StandardCharsets.UTF_8),
                        ModConfig.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                FileUtils.write(configPath.toFile(), GSON.toJson(config), StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return config;
    }

    public static void save(ModConfig config) {
        if (!Linker.CONFIG_FOLDER.toFile().isDirectory()) {
            try {
                Files.createDirectories(Linker.CONFIG_FOLDER);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Path configPath = Linker.CONFIG_FOLDER.resolve(config.getConfigName() + ".json");
        try {
            FileUtils.write(configPath.toFile(), GSON.toJson(config), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void onChange(){
        ConfigHandler.save(Linker.config);
        Linker.config = ConfigHandler.load();
    }
}
