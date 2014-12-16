package tfcwailaplugin

import java.util.{List => JList}

import com.bioxx.tfc.TileEntities.TEFoodPrep
import mcp.mobius.waila.api.{IWailaConfigHandler, IWailaDataAccessor}
import net.minecraft.item.ItemStack
import net.minecraft.nbt.{NBTTagList, NBTTagCompound}
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World

import implicits.{IInventoryAdapter, ItemStackAdapter}
import net.minecraftforge.common.util.Constants.NBT

object FoodPrepProvider extends ProviderBase[TEFoodPrep] {

  override def getNBTData(te: TileEntity, tag: NBTTagCompound, world: World, x: Int, y: Int, z: Int): NBTTagCompound = {
    te match {
      case fp: TEFoodPrep =>
        val tagList = new NBTTagList
        0 to 4 foreach { i =>
          fp.ifNonEmptySlot(i) { is =>
            val addtag = new NBTTagCompound
            addtag.setByte("Slot", i.toByte)
            is.writeToNBT(addtag)
            tagList.appendTag(addtag)
          }
        }
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
    val tagList = nbt.getTagList("Items", NBT.TAG_COMPOUND)
    val items = (0 until tagList.tagCount) map { i =>
      val tagC = tagList.getCompoundTagAt(i)
      ItemStack.loadItemStackFromNBT(tagC)
    }
    items.foreach { is =>
      tooltip.add(is.toInfoString)
    }

    tooltip
  }
}