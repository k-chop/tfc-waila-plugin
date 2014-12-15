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

  @inline private[this] final def buildTagListFromInventory(inv: IInventory): NBTTagList = {
    val tagList = new NBTTagList()
    for (i <- 0 until inv.getSizeInventory) {
      if (inv.getStackInSlot(i) != null) {
        val tag1 = new NBTTagCompound()
        tag1.setByte("Slot", i.toByte)
        inv.getStackInSlot(i).writeToNBT(tag1)
        tagList.appendTag(tag1)
      }
    }
    tagList
  }

  @inline private[this] final def readItemsFromTagList(tag: NBTTagList): Seq[ItemStack] = {
    (0 until tag.tagCount) map { i =>
      val tagC = tag.getCompoundTagAt(i)
      ItemStack.loadItemStackFromNBT(tagC)
    }
  }

  override def getNBTData(te: TileEntity, tag: NBTTagCompound, world: World, x: Int, y: Int, z: Int): NBTTagCompound = {
    te match {
      case lp: TELogPile =>
        tag.setTag("Items", buildTagListFromInventory(lp))
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
        //tooltip.add(s"in ${lp.getNumberOfLogs} logs")
        val items = readItemsFromTagList( tags.getTagList("Items", NBT.TAG_COMPOUND) )
        items.foreach { is =>
          tooltip.add(s"${is.getDisplayName} x${is.stackSize}")
        }
      case _ =>
    }
    tooltip
  }

}