package tfcwailaplugin

import java.util.{List => JList}

import com.bioxx.tfc.TileEntities.TELogPile
import mcp.mobius.waila.api.{IWailaConfigHandler, IWailaDataAccessor}
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.{NBTTagList, NBTTagCompound}
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World
import net.minecraftforge.common.util.Constants.NBT

object LogPileProvider extends ProviderBase[TELogPile] {

  override def getNBTData(te: TileEntity, tag: NBTTagCompound, world: World, x: Int, y: Int, z: Int): NBTTagCompound = {
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
      case lp: TELogPile =>
        val tags = accessor.getNBTData
        val items = NBTUtil.readItemStacks(tags)

        // TODO: config
        // 1. show number of logs
        /* tooltip.add(s"in ${items.foldLeft(0)(_ + _.stackSize)} logs") */
        // 2. show logs each slot
        /* items.foreach { is =>
          tooltip.add(s"${is.getDisplayName} x${is.stackSize}")
        } */
        // 3. show logs each woodType
        items.groupBy(_.getItemDamage).foreach { case (i, iss) =>
          val stackSizeSum = iss.foldLeft(0){ (acc, is) => acc + is.stackSize }
          tooltip.add(s"${iss.head.getDisplayName} x$stackSizeSum")
        }
      case _ =>
    }
    tooltip
  }

}