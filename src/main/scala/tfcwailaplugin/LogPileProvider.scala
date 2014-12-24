package tfcwailaplugin

import java.util.{List => JList}

import com.bioxx.tfc.TileEntities.TELogPile
import mcp.mobius.waila.api.{IWailaConfigHandler, IWailaDataAccessor}
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World

import implicits.ItemStackAdapter

object LogPileProvider extends ProviderBase[TELogPile] {

  override def getNBTData(player: EntityPlayerMP, te: TileEntity, tag: NBTTagCompound, world: World, x: Int, y: Int, z: Int): NBTTagCompound = {
    te match {
      case lp: TELogPile =>
        tag.setTag("Items", NBTUtil.buildTagList(lp, start = 0, end = lp.getSizeInventory))
      case _ =>
    }
    tag
  }

  override def getWailaBody(stack: ItemStack,
                            tooltip: JList[String],
                            accessor: IWailaDataAccessor,
                            config: IWailaConfigHandler): JList[String] = {

    accessor.getTileEntity match {
      case lp: TELogPile if !config.getConfig("tfcwailaplugin.nologinfo") =>
        val tags = accessor.getNBTData
        val items = NBTUtil.readItemStacks(tags)

        // show number of logs
        if (config.getConfig("tfcwailaplugin.numberoflog")) {
          tooltip.add(s"in ${items.foldLeft(0)(_ + _.stackSize)} logs")
        }
        // show logs each slot
        if (config.getConfig("tfcwailaplugin.logperslot")) {
          items.foreach { is =>
            tooltip.add(is.toInfoString)
          }
        } else {
          // show logs grouped by woodType
          items.groupBy(_.getItemDamage).foreach { case (_, is) =>
            val stackSizeSum = is.foldLeft(0)(_ + _.stackSize)
            tooltip.add(s"${is.head.getDisplayName} x$stackSizeSum")
          }
        }
      case _ =>
    }
    tooltip
  }

}