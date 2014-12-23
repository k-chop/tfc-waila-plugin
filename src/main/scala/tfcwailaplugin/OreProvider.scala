package tfcwailaplugin

import com.bioxx.tfc.api.Constant.Global
import com.bioxx.tfc.{TFCBlocks, TFCItems}
import com.bioxx.tfc.TileEntities.TEOre
import mcp.mobius.waila.api.{IWailaConfigHandler, IWailaDataAccessor}
import net.minecraft.item.ItemStack


object OreProvider extends ProviderBase[TEOre] with EphemeralCache[Int, ItemStack] {

  override def getWailaStack(accessor: IWailaDataAccessor, config: IWailaConfigHandler): ItemStack = {
    val meta = accessor.getMetadata
    val block = accessor.getBlock
    // https://github.com/Deadrik/TFCraft/blob/2bd2f2286b1682271fd95b4d5a8b6f58fb109f91/src/Common/com/bioxx/tfc/Items/Tools/ItemProPick.java#L113
    val id = {
      if (block == TFCBlocks.Ore)
        meta
      else if (block == TFCBlocks.Ore2)
        meta + Global.ORE_METAL.length
      else if (block == TFCBlocks.Ore3)
        meta + Global.ORE_METAL.length + Global.ORE_MINERAL.length
      else
        0
    }
    cache.getOrElseUpdate(id, new ItemStack(TFCItems.OreChunk, 1, id))
  }

}
