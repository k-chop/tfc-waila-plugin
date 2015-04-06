package com.github.whelmaze.tfcwailaplugin

import java.util.{List => JList}

import com.bioxx.tfc.TileEntities.TEIngotPile
import mcp.mobius.waila.api.{IWailaConfigHandler, IWailaDataAccessor}
import net.minecraft.item.ItemStack

object IngotPileProvider extends TileEntityProviderBase[TEIngotPile] with EphemeralCache[ItemStack, ItemStack] {

  override def getWailaStack(accessor: IWailaDataAccessor, config: IWailaConfigHandler): ItemStack = {
    accessor.getTileEntity match {
      case ip: TEIngotPile =>
        val is = ip.getStackInSlot(0)
        val item = is.getItem
        cache.getOrElseUpdate(is, new ItemStack(item, 1, is.getItemDamage))
      case _ => null
    }
  }

  override def getWailaHead(stack: ItemStack,
                   tooltip: JList[String],
                   accessor: IWailaDataAccessor,
                   config: IWailaConfigHandler): JList[String] = {
    import net.minecraft.util.EnumChatFormatting._

    Option(stack) foreach { is =>
      val n = is.getItem.getUnlocalizedName
      val metalRawName = n.replace("item.","").split(" Ingot").headOption.getOrElse("Unknown").replace(" ","")
      val metalStr = util.translate(s"gui.metal.$metalRawName")
      val pileStr = util.translate("tile.ingotpile.name")
      if (!tooltip.isEmpty) {
        tooltip.set(0, s"$WHITE$metalStr $pileStr")
      }
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
