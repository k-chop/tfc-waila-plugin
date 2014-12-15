package tfcwailaplugin

import java.util.{List => JList}

import com.bioxx.tfc.TileEntities.TELogPile
import mcp.mobius.waila.api.{IWailaConfigHandler, IWailaDataAccessor}
import net.minecraft.item.ItemStack

object LogPileProvider extends ProviderBase[TELogPile] {

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