package tfcwailaplugin

import java.util.{List => JList}

import com.bioxx.tfc.Food.{CropIndexPepper, CropIndex, CropManager}
import com.bioxx.tfc.TileEntities.TECrop
import mcp.mobius.waila.api.{IWailaConfigHandler, IWailaDataAccessor}
import net.minecraft.item.ItemStack


object CropProvider extends ProviderBase[TECrop] with CacheableItemStack {

  override def getWailaHead(stack: ItemStack,
                   tooltip: JList[String],
                   accessor: IWailaDataAccessor,
                   config: IWailaConfigHandler): JList[String] = {
    import net.minecraft.util.EnumChatFormatting._

    accessor.getTileEntity match {
      case e: TECrop =>
        val is = CropManager.getInstance.getCropFromId(e.cropId) match {
          case pepper: CropIndexPepper => cache.getOrElseUpdate(e.cropId, new ItemStack(pepper.Output2))
          case others: CropIndex => cache.getOrElseUpdate(e.cropId, new ItemStack(others.Output1))
          case _ => null
        }
        if (is != null && !tooltip.isEmpty) {
          tooltip.set(0, s"${WHITE}Crop (${is.getDisplayName})")
        }
      case _ =>
    }
    tooltip
  }

  override def getWailaBody(stack: ItemStack,
                   tooltip: JList[String],
                   accessor: IWailaDataAccessor,
                   config: IWailaConfigHandler): JList[String] = tooltip

}
