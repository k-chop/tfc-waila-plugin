package tfcwailaplugin

import java.util.{List => JList}

import com.bioxx.tfc.TFCBlocks
import com.bioxx.tfc.TileEntities.TileEntityToolRack
import mcp.mobius.waila.api.{IWailaConfigHandler, IWailaDataAccessor}
import net.minecraft.item.ItemStack

import net.minecraftforge.common.util.ForgeDirection

import scala.collection.mutable

object ToolRackProvider extends ProviderBase[TileEntityToolRack] {

  private[this] val cache = mutable.WeakHashMap.empty[Int, ItemStack]

  override def getWailaStack(accessor: IWailaDataAccessor, config: IWailaConfigHandler): ItemStack = {
    accessor.getTileEntity match {
      case tr: TileEntityToolRack =>
        val pos = accessor.getPosition
        val hitSide = ForgeDirection.getOrientation(pos.sideHit)
        val v = pos.hitVec
        val hitX = v.xCoord - v.xCoord.floor
        val hitY = v.yCoord - v.yCoord.floor
        val dir = accessor.getMetadata

        // https://github.com/Deadrik/TFCraft/blob/master/src/Common/com/bioxx/tfc/Blocks/Devices/BlockToolRack.java#L82
        val result = {
          if (hitX < 0.5 && hitY > 0.5)
            tr.getStackInSlot(0)
          else if (hitX > 0.5 && hitY > 0.5)
            tr.getStackInSlot(1)
          else if (hitX < 0.5)
            tr.getStackInSlot(2)
          else if (hitX > 0.5)
            tr.getStackInSlot(3)
          else null
        }
        if (result == null) { // selecting empty slot on toolRack
          // fix incorrect woodName
          val id = tr.woodType
          cache.getOrElseUpdate(id, new ItemStack(TFCBlocks.ToolRack, 1, id))
        } else result
      case _ => null
    }
  }

}
