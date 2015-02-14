package com.github.whelmaze.tfcwailaplugin

import java.util.{List => JList}

import com.bioxx.tfc.TileEntities.TELogPile
import mcp.mobius.waila.api.{IWailaConfigHandler, IWailaDataAccessor}
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World

import implicits.RichItemStack

object LogPileProvider extends TileEntityProviderBase[TELogPile] {

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
    implicit val implicitlyProvideToConfigs = config

    accessor.getTileEntity match {
      case lp: TELogPile if Configs.noLogInfo.isDisabled =>
        val tags = accessor.getNBTData
        val items = NBTUtil.readItemStacks(tags)

        // show number of logs
        if (Configs.numberOfLogOnly.isEnabled) {
          tooltip.add(s"in ${items.foldLeft(0)(_ + _.stackSize)} logs")
        } else {
          // show logs grouped by woodType
          items.groupBy(_.getItemDamage).foreach { case (_, iss) =>
            val stackSizeSum = iss.foldLeft(0)(_ + _.stackSize)
            tooltip.add(s"${iss.head.getDisplayName} x$stackSizeSum")
          }
        }
      case _ =>
    }
    tooltip
  }

}