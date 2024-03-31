package committee.nova.mods.linker;

import committee.nova.mods.linker.config.ConfigHandler;
import committee.nova.mods.linker.config.ModConfig;
import committee.nova.mods.linker.mixin.DimensionDataAccessor;
import committee.nova.mods.linker.utils.FileUtils;
import committee.nova.mods.linker.utils.LinkerSave;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
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
    public static Linker INSTANCE = new Linker();
    public static final String ID = "linker";
    public static final Logger LOGGER = LoggerFactory.getLogger("Linker");
    public static final TagKey<Item> LINKERS = TagKey.create(itemKey(), new ResourceLocation(ID, "linkers"));
    public static Path CONFIG_FOLDER;
    public static ModConfig config;

    static {
        CONFIG_FOLDER = FabricLoader.getInstance().getConfigDir().resolve(ID);
        FileUtils.checkFolder(CONFIG_FOLDER);
        config = ConfigHandler.load();//读取配置
        ConfigHandler.save(config);
    }

    private static ResourceKey<? extends Registry<Item>> itemKey() {
        return ResourceKey.createRegistryKey(ResourceLocation.tryParse("item"));
    }

    @Override
    public void onInitialize() {
        ServerWorldEvents.LOAD.register((server, world) -> {
            if (config.chunkLoading) LinkerSave.getOrCreate(world);
        });

        ServerTickEvents.START_WORLD_TICK.register(world -> {
            if (config.chunkLoading && ((DimensionDataAccessor) world.getDataStorage()).linker$cache().containsKey("loading_linkable")) {
                LinkerSave.getOrCreate(world).tick(world);
            }
        });
    }
}
