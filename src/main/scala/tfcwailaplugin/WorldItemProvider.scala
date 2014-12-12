package tfcwailaplugin

import java.util.{List => JList}

import com.bioxx.tfc.TFCItems
import com.bioxx.tfc.TileEntities.{TEWorldItem, TECrop}
import mcp.mobius.waila.api.{IWailaConfigHandler, IWailaDataAccessor, IWailaDataProvider}
import net.minecraft.item.ItemStack
import net.minecraft.util.{EnumChatFormatting => EColor, StatCollector}

object WorldItemProvider extends ProviderBase[TEWorldItem] {

  override def getWailaStack(accessor: IWailaDataAccessor, config: IWailaConfigHandler): ItemStack =
    accessor.getTileEntity match {
      case e: TEWorldItem =>
        e.storage(0)
      case _ =>
        null
    }

}
