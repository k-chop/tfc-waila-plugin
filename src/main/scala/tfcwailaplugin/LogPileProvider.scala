package tfcwailaplugin

import java.util.{List => JList}

import com.bioxx.tfc.TerraFirmaCraft
import com.bioxx.tfc.TileEntities.TELogPile
import mcp.mobius.waila.api.{IWailaConfigHandler, IWailaDataAccessor}
import net.minecraft.item.ItemStack

import implicits.IInventoryAdapter
import net.minecraft.tileentity.TileEntity

object LogPileProvider extends ProviderBase[TELogPile] {

  private[this] var show = false
  private[this] var c: TELogPile = _

  override def getWailaBody(stack: ItemStack,
                            tooltip: JList[String],
                            accessor: IWailaDataAccessor,
                            config: IWailaConfigHandler): JList[String] = {

    accessor.getTileEntity match {
      case lp: TELogPile =>
        tooltip.add(s"in ${lp.getNumberOfLogs} logs")
        /*lp.foreachSlot { is =>
          tooltip.add(s"${is.getDisplayName} x${is.stackSize}")
        }*/
      case _ =>
    }
    tooltip
  }

}