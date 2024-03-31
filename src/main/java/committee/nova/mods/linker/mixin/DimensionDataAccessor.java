package committee.nova.mods.linker.mixin;

import com.google.common.collect.Maps;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

/**
 * DimensionDataAccesser
 *
 * @author cnlimiter
 * @version 1.0
 * @description
 * @date 2024/3/31 10:42
 */
@Mixin(DimensionDataStorage.class)
public interface DimensionDataAccessor {
    @Accessor("cache")
    Map<String, SavedData> linker$cache();
}
