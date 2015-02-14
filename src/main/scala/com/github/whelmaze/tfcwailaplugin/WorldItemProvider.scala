package com.github.whelmaze.tfcwailaplugin

import com.bioxx.tfc.TileEntities.TEWorldItem
import mcp.mobius.waila.api.{IWailaConfigHandler, IWailaDataAccessor}
import net.minecraft.item.ItemStack

object WorldItemProvider extends TileEntityProviderBase[TEWorldItem] {

  override def getWailaStack(accessor: IWailaDataAccessor, config: IWailaConfigHandler): ItemStack =
    accessor.getTileEntity match {
      case e: TEWorldItem =>
        e.storage(0)
      case _ =>
        null
    }

}
