package committee.nova.mods.linker;

import committee.nova.mods.linker.config.ModConfig;
import committee.nova.mods.linker.utils.FileUtils;
import me.melontini.dark_matter.api.base.config.ConfigManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

/**
 * BoatLinker
 *
 * @author cnlimiter
 * @version 1.0
 * @description
 * @date 2024/3/30 21:52
 */
public class Linker implements ModInitializer {
    public static final String ID = "linker";
    public static final Logger LOGGER = LoggerFactory.getLogger("Linker");
    public static final ConfigManager<ModConfig> CONFIG_MANAGER = ConfigManager.of(ModConfig.class, ID, ModConfig::new);
    public static final TagKey<Item> LINKERS = TagKey.create(itemKey(), new ResourceLocation(ID, "linkers"));
    public static MinecraftServer SERVER = null;
    public static Path CONFIG_FOLDER;
    public static ModConfig config;

    static {
        loadConfig();
    }

    public static void loadConfig() {
        config = CONFIG_MANAGER.load(FabricLoader.getInstance().getConfigDir());
        CONFIG_MANAGER.save(FabricLoader.getInstance().getConfigDir(), config);
    }

    private static ResourceKey<? extends Registry<Item>> itemKey() {
        return ResourceKey.createRegistryKey(ResourceLocation.tryParse("item"));
    }

    @Override
    public void onInitialize() {
        CONFIG_FOLDER = FabricLoader.getInstance().getConfigDir().resolve(ID);
        FileUtils.checkFolder(CONFIG_FOLDER);
    }
}
