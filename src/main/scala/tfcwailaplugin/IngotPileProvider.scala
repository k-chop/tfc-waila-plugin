package tfcwailaplugin

import java.util.{List => JList}

import com.bioxx.tfc.TileEntities.TEIngotPile
import mcp.mobius.waila.api.{IWailaConfigHandler, IWailaDataAccessor}
import net.minecraft.item.{Item, ItemStack}

object IngotPileProvider extends ProviderBase[TEIngotPile] with EphemeralCache[Symbol, ItemStack] {

  override def getWailaStack(accessor: IWailaDataAccessor, config: IWailaConfigHandler): ItemStack = {
    accessor.getTileEntity match {
      case ip: TEIngotPile =>
        val is = ip.getStackInSlot(0)
        val item = is.getItem
        cache.getOrElseUpdate(Symbol(is.getDisplayName), new ItemStack(item, 1, is.getItemDamage))
      case _ => null
    }
  }

  override def getWailaHead(stack: ItemStack,
                   tooltip: JList[String],
                   accessor: IWailaDataAccessor,
                   config: IWailaConfigHandler): JList[String] = {
    if (!tooltip.isEmpty) {
      tooltip.set(0, tooltip.get(0) + " Pile")
    }
    tooltip
  }

  @inline final def calcWholeStackIngot(base: TEIngotPile, accessor: IWailaDataAccessor): Int = {
    val baseX = base.xCoord
    val baseY = base.yCoord
    val baseZ = base.zCoord

    @annotation.tailrec
    def searchGT(y: Int, count: Int = 0): Int = accessor.getWorld.getTileEntity(baseX, y+1, baseZ) match {
      case e: TEIngotPile =>
        searchGT(y+1, count + e.getStack)
      case _ => count
    }

    @annotation.tailrec
    def searchLT(y: Int, count: Int = 0): Int = accessor.getWorld.getTileEntity(baseX, y-1, baseZ) match {
      case e: TEIngotPile =>
        searchLT(y-1, count + e.getStack)
      case _ => count
    }

    base.getStack + searchGT(baseY) + searchLT(baseY)
  }

  override def getWailaBody(stack: ItemStack,
                   tooltip: JList[String],
                   accessor: IWailaDataAccessor,
                   config: IWailaConfigHandler): JList[String] = {
    accessor.getTileEntity match {
      case ingots: TEIngotPile =>
        tooltip.add(s"${calcWholeStackIngot(ingots, accessor)} stack")
      case _ =>
    }
    tooltip
  }

}
