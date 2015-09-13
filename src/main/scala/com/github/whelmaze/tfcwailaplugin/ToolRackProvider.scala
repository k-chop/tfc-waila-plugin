package com.github.whelmaze.tfcwailaplugin

import com.bioxx.tfc.TileEntities.TEToolRack
import mcp.mobius.waila.api.{IWailaConfigHandler, IWailaDataAccessor}
import net.minecraft.item.ItemStack

object ToolRackProvider extends TileEntityProviderBase[TEToolRack] {

  override def getWailaStack(accessor: IWailaDataAccessor, config: IWailaConfigHandler): ItemStack = {
    accessor.getTileEntity match {
      case tr: TEToolRack =>

        val pos = accessor.getPosition
        val v = pos.hitVec
        val hitX = v.xCoord - v.xCoord.floor
        val hitY = v.yCoord - v.yCoord.floor
        val hitZ = v.zCoord - v.zCoord.floor
        val dir = accessor.getMetadata

        // 0(south) | 2(north) => X
        // 1(west) | 3(east)  => Z
        val localX = if (dir % 2 == 0) hitX else hitZ

        // https://github.com/Deadrik/TFCraft/blob/f5d99e045398ff44a8410f5ad309583b293cacee/src/Common/com/bioxx/tfc/Blocks/Devices/BlockToolRack.java#L82
        val result = {
          if (localX < 0.5 && hitY > 0.5)
            tr.getStackInSlot(0)
          else if (localX > 0.5 && hitY > 0.5)
            tr.getStackInSlot(1)
          else if (localX < 0.5)
            tr.getStackInSlot(2)
          else if (localX > 0.5)
            tr.getStackInSlot(3)
          else null
        }
        result
      case _ => null
    }
  }

}
