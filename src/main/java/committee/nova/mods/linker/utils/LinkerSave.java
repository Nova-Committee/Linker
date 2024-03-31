package committee.nova.mods.linker.utils;

import committee.nova.mods.linker.api.Linkable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * LinkerSave
 *
 * @author cnlimiter
 * @version 1.0
 * @description
 * @date 2024/3/31 10:28
 */
public class LinkerSave extends SavedData {

    public static LinkerSave getOrCreate(ServerLevel world) {
        return world.getDataStorage().computeIfAbsent(compoundTag -> new LinkerSave().read(compoundTag),LinkerSave::new,"loading_linkable");
    }

    private final Set<BlockPos> chunksToReload = new HashSet<>();
    private final Set<Linkable> cartsToBlockPos = new HashSet<>();

    @Override
    public @NotNull CompoundTag save(CompoundTag nbt) {
        ListTag list = new ListTag();
        for (Linkable linkable : cartsToBlockPos) {
            if (!((Entity)linkable).isRemoved()) list.add(LongTag.valueOf(((Entity)linkable).blockPosition().asLong()));
        }
        nbt.put("chunksToSave", list);
        cartsToBlockPos.clear();
        return nbt;
    }

    public LinkerSave read(CompoundTag nbt) {
        ListTag list = nbt.getList("chunksToSave", Tag.TAG_LONG);
        for (Tag element : list) {
            chunksToReload.add(BlockPos.of(((LongTag) element).getAsLong()));
        }
        return this;
    }

    public void tick(ServerLevel world) {
        if (!chunksToReload.isEmpty()) {
            for (BlockPos pos : chunksToReload) {
                ChunkPos chunkPos = new ChunkPos(pos);
                world.getChunkSource().addRegionTicket(TicketType.PORTAL, chunkPos, 8, pos);
            }
            chunksToReload.clear();
            setDirty();
        }
    }

    public void addLinkable(Linkable cart) {
        cartsToBlockPos.add(cart);
        setDirty();
    }

    public void removeLinkable(Linkable cart) {
        cartsToBlockPos.remove(cart);
        setDirty();
    }

}
