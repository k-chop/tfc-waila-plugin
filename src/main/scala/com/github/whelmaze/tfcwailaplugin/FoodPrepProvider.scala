package com.github.whelmaze.tfcwailaplugin

import java.util.{List => JList}

import com.bioxx.tfc.TileEntities.TEFoodPrep
import mcp.mobius.waila.api.{IWailaConfigHandler, IWailaDataAccessor}
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World

import implicits.RichItemStack

object FoodPrepProvider extends ProviderBase[TEFoodPrep] {

  override def getNBTData(player: EntityPlayerMP, te: TileEntity, tag: NBTTagCompound, world: World, x: Int, y: Int, z: Int): NBTTagCompound = {
    te match {
      case fp: TEFoodPrep =>
        val tagList = NBTUtil.buildTagList(fp, start = 0, end = 5)
        tag.setTag("Items", tagList)
      case _ =>
    }
    tag
  }

  override def getWailaBody(stack: ItemStack,
                   tooltip: JList[String],
                   accessor: IWailaDataAccessor,
                   config: IWailaConfigHandler): JList[String] = {
    val nbt = accessor.getNBTData
    val items = NBTUtil.readItemStacks(nbt)
    items.foreach { is =>
      tooltip.add(is.toInfoString)
    }

    tooltip
  }
}